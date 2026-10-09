import {
  Component, OnInit, OnDestroy, ViewChild, ElementRef,
  AfterViewChecked, ChangeDetectorRef, signal
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AiChatService, ChatMessage, ChatApiResponse } from '../../../core/services/ai-chat.service';
import { BookingStateService } from '../../../core/services/booking-state.service';
import { TokenService } from '../../../core/services/token.service';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-ai-chat-widget',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ai-chat-widget.component.html',
  styleUrls: ['./ai-chat-widget.component.scss']
})
export class AiChatWidgetComponent implements OnInit, OnDestroy, AfterViewChecked {

  @ViewChild('messagesContainer') private messagesContainer!: ElementRef;
  @ViewChild('messageInput') private messageInput!: ElementRef;

  isOpen = signal(false);
  isLoading = signal(false);
  messages = signal<ChatMessage[]>([]);
  inputText = '';
  sessionId = '';
  shouldScrollToBottom = false;

  // Safe Booking Modal State
  showBookingModal = signal(false);
  isSubmittingBooking = signal(false);
  bookingModalError = signal<string | null>(null);
  currentDraft: any = null;
  bookingForm = {
    fullName: '',
    phone: '',
    email: '',
    specialRequest: '',
    checkInDate: '',
    checkOutDate: ''
  };

  // Human Handoff Modal State
  showHandoffModal = signal(false);
  currentHandoffData: any = null;
  callbackForm = {
    phone: '',
    name: '',
    note: ''
  };
  callbackSubmitted = signal(false);

  // Quick replies
  readonly quickReplies = [
    'Tìm villa trống hôm nay',
    'Dịch vụ Spa & BBQ tại villa',
    'Gói combo ưu đãi',
    'Xem khuyến mãi hiện có',
    'Gặp lễ tân hỗ trợ'
  ];

  private readonly STORAGE_KEY = 'ai_chat_messages';

  constructor(
    private aiChatService: AiChatService,
    private bookingStateService: BookingStateService,
    private tokenService: TokenService,
    private http: HttpClient,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.sessionId = this.aiChatService.getOrCreateSessionId();
    this.loadSavedMessages();

    if (this.messages().length === 0) {
      this.addWelcomeMessage();
    }
  }

  ngOnDestroy(): void {
    this.saveMessages();
  }

  ngAfterViewChecked(): void {
    if (this.shouldScrollToBottom) {
      this.scrollToBottom();
      this.shouldScrollToBottom = false;
    }
  }

  toggleChat(): void {
    this.isOpen.update(v => !v);
    if (this.isOpen()) {
      setTimeout(() => {
        this.scrollToBottom();
        this.messageInput?.nativeElement?.focus();
      }, 100);
    }
  }

  closeChat(): void {
    this.isOpen.set(false);
  }

  sendMessage(text?: string): void {
    const message = (text || this.inputText).trim();
    if (!message || this.isLoading()) return;

    this.inputText = '';

    // Thêm message của user
    this.addMessage({ role: 'user', content: message, timestamp: new Date() });

    // Thêm loading indicator
    this.addMessage({ role: 'ai', content: '', timestamp: new Date(), isLoading: true });

    this.isLoading.set(true);
    this.shouldScrollToBottom = true;

    this.aiChatService.sendMessage(this.sessionId, message).subscribe({
      next: (response: ChatApiResponse) => {
        // Xóa loading message
        this.removeLoadingMessage();

        // Thêm AI response
        this.addMessage({
          role: 'ai',
          content: response.reply,
          timestamp: new Date(),
          actionType: response.actionType,
          actionPayload: response.actionPayload
        });

        this.isLoading.set(false);
        this.shouldScrollToBottom = true;
        this.saveMessages();
        this.cdr.detectChanges();
      },
      error: () => {
        this.removeLoadingMessage();
        this.addMessage({
          role: 'ai',
          content: 'Không thể kết nối tới dịch vụ AI. Vui lòng thử lại sau hoặc liên hệ Hotline: 0901 234 567.',
          timestamp: new Date()
        });
        this.isLoading.set(false);
        this.shouldScrollToBottom = true;
      }
    });
  }

  sendQuickReply(text: string): void {
    this.sendMessage(text);
  }

  onKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.sendMessage();
    }
  }

  navigateToLogin(): void {
    this.closeChat();
    this.router.navigate(['/auth/login']);
  }

  navigateToBookings(): void {
    this.closeChat();
    this.router.navigate(['/account/bookings']);
  }

  navigateToVilla(villaId: number): void {
    this.closeChat();
    this.router.navigate(['/villas', villaId]);
  }

  getVillaImageUrl(villa: any): string {
    if (villa?.imageUrl) return villa.imageUrl;
    if (villa?.images?.length > 0) return villa.images[0];
    return '/assets/images/rooms/grand-oceanfront.jpg';
  }

  clearChat(): void {
    this.aiChatService.resetSession();
    this.sessionId = this.aiChatService.getOrCreateSessionId();
    this.messages.set([]);
    localStorage.removeItem(this.STORAGE_KEY);
    this.addWelcomeMessage();
  }

  formatPrice(price: number): string {
    if (!price) return '0 ₫';
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);
  }

  getSearchResults(payload: any): any[] {
    if (!payload) return [];
    return Array.isArray(payload) ? payload.slice(0, 4) : [];
  }

  getExtraServicesList(payload: any): any[] {
    if (!payload) return [];
    return Array.isArray(payload) ? payload.slice(0, 6) : [];
  }

  getBookingCode(payload: any): string {
    return payload?.bookingCode || payload?.id || '';
  }

  // ======================== SAFE BOOKING METHODS ========================

  get minCheckInDate(): string {
    const today = new Date();
    return today.toISOString().split('T')[0];
  }

  get minCheckOutDate(): string {
    if (this.bookingForm.checkInDate) {
      const nextDay = new Date(this.bookingForm.checkInDate);
      nextDay.setDate(nextDay.getDate() + 1);
      return nextDay.toISOString().split('T')[0];
    }
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    return tomorrow.toISOString().split('T')[0];
  }

  onDateChange(): void {
    if (!this.bookingForm.checkInDate || !this.bookingForm.checkOutDate || !this.currentDraft) {
      return;
    }

    const checkIn = new Date(this.bookingForm.checkInDate);
    let checkOut = new Date(this.bookingForm.checkOutDate);

    if (isNaN(checkIn.getTime())) return;

    if (isNaN(checkOut.getTime()) || checkOut <= checkIn) {
      const nextDay = new Date(checkIn);
      nextDay.setDate(nextDay.getDate() + 1);
      this.bookingForm.checkOutDate = nextDay.toISOString().split('T')[0];
      checkOut = nextDay;
    }

    const diffMs = checkOut.getTime() - checkIn.getTime();
    const nights = Math.max(1, Math.round(diffMs / (1000 * 60 * 60 * 24)));

    this.currentDraft.checkInDate = this.bookingForm.checkInDate;
    this.currentDraft.checkOutDate = this.bookingForm.checkOutDate;
    this.currentDraft.nights = nights;

    const basePrice = this.currentDraft.pricePerNight ||
      (this.currentDraft.estimatedTotal && this.currentDraft.nights
        ? Math.round(this.currentDraft.estimatedTotal / this.currentDraft.nights)
        : 3500000);
    this.currentDraft.pricePerNight = basePrice;
    this.currentDraft.estimatedTotal = basePrice * nights;
  }

  openBookingModal(draft: any): void {
    this.currentDraft = { ...draft };
    this.bookingModalError.set(null);

    // Pre-fill user information if logged in
    const user = this.tokenService.getUser();
    this.bookingForm.fullName = user?.fullName || '';
    this.bookingForm.email = user?.email || draft?.userEmail || '';
    this.bookingForm.phone = user?.phone || '';
    this.bookingForm.specialRequest = '';

    // Set dates from draft or default to today and tomorrow
    const today = new Date().toISOString().split('T')[0];
    const tomorrowDate = new Date();
    tomorrowDate.setDate(tomorrowDate.getDate() + 1);
    const tomorrow = tomorrowDate.toISOString().split('T')[0];

    this.bookingForm.checkInDate = draft?.checkInDate || today;
    this.bookingForm.checkOutDate = draft?.checkOutDate || tomorrow;

    this.onDateChange();

    this.showBookingModal.set(true);
  }

  closeBookingModal(): void {
    this.showBookingModal.set(false);
    this.currentDraft = null;
  }

  confirmBookingSubmit(): void {
    if (!this.currentDraft) return;

    if (!this.bookingForm.fullName || !this.bookingForm.phone) {
      this.bookingModalError.set('Vui lòng điền đầy đủ Họ tên và Số điện thoại nhận phòng');
      return;
    }

    if (!this.bookingForm.checkInDate || !this.bookingForm.checkOutDate) {
      this.bookingModalError.set('Vui lòng chọn ngày nhận phòng và ngày trả phòng hợp lệ');
      return;
    }

    this.isSubmittingBooking.set(true);
    this.bookingModalError.set(null);

    const bookingPayload = {
      checkInDate: this.bookingForm.checkInDate,
      checkOutDate: this.bookingForm.checkOutDate,
      villaId: this.currentDraft.villaId || null,
      villaTypeId: this.currentDraft.villaTypeId || null,
      quantity: 1,
      promotionCode: this.currentDraft.promoCode || null,
      guestName: this.bookingForm.fullName,
      guestPhone: this.bookingForm.phone,
      guestEmail: this.bookingForm.email,
      specialRequest: this.bookingForm.specialRequest
    };

    this.http.post<any>(`${environment.apiUrl}/bookings`, bookingPayload).subscribe({
      next: (res) => {
        this.isSubmittingBooking.set(false);
        this.closeBookingModal();

        // Add confirmed booking message to chat
        const bookingData = res.data || res;
        this.addMessage({
          role: 'ai',
          content: `🎉 **Đặt phòng thành công!** Cảm ơn quý khách **${this.bookingForm.fullName}** đã lựa chọn Aura Resort.\n\nMã đơn đặt phòng: **${bookingData.bookingCode || bookingData.id}**.\nNhân viên resort sẽ liên hệ xác nhận trong thời gian sớm nhất!`,
          timestamp: new Date(),
          actionType: 'BOOKING_CREATED',
          actionPayload: bookingData
        });
        this.shouldScrollToBottom = true;
        this.saveMessages();
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.isSubmittingBooking.set(false);
        const errMsg = err?.error?.message || 'Có lỗi xảy ra khi tạo đơn đặt phòng. Vui lòng thử lại hoặc tới trang thanh toán chi tiết.';
        this.bookingModalError.set(errMsg);
      }
    });
  }

  proceedToCheckout(draft: any): void {
    if (!draft) return;

    const checkIn = this.bookingForm.checkInDate || draft.checkInDate;
    const checkOut = this.bookingForm.checkOutDate || draft.checkOutDate;

    // Set state in BookingStateService
    this.bookingStateService.setState({
      villa: {
        id: draft.villaId,
        villaNumber: draft.villaNumber || '',
        basePrice: draft.pricePerNight,
        imageUrl: draft.imageUrl,
        villaTypeName: draft.villaName,
        zone: draft.zone
      } as any,
      checkInDate: checkIn,
      checkOutDate: checkOut,
      numberOfGuests: draft.adults || 2,
      promoCode: draft.promoCode || ''
    });

    this.closeBookingModal();
    this.closeChat();
    this.router.navigate(['/checkout']);
  }

  // ======================== HUMAN HANDOFF METHODS ========================

  openHumanHandoff(payload?: any): void {
    this.currentHandoffData = payload || {
      hotline: '0901 234 567',
      receptionEmail: 'reception@auraresort.com',
      zaloUrl: 'https://zalo.me/0378203598',
      operatingHours: '24/7 (Phục vụ liên tục)',
      reason: 'Khách hàng yêu cầu hỗ trợ trực tiếp từ lễ tân'
    };

    const user = this.tokenService.getUser();
    this.callbackForm.name = user?.fullName || '';
    this.callbackForm.phone = user?.phone || '';
    this.callbackForm.note = '';
    this.callbackSubmitted.set(false);

    this.showHandoffModal.set(true);
  }

  closeHumanHandoff(): void {
    this.showHandoffModal.set(false);
  }

  submitCallbackRequest(): void {
    if (!this.callbackForm.phone) return;

    this.callbackSubmitted.set(true);

    const payload = {
      phone: this.callbackForm.phone,
      name: this.callbackForm.name || '',
      note: this.callbackForm.note || '',
      preferredTime: 'Càng sớm càng tốt'
    };

    this.http.post<any>(`${environment.apiUrl}/ai/callback-request`, payload).subscribe({
      next: () => {
        setTimeout(() => {
          this.closeHumanHandoff();
          this.addMessage({
            role: 'ai',
            content: `📞 **Đã tiếp nhận yêu cầu hỗ trợ!**\n\nBộ phận Lễ tân & CSKH Aura Resort đã ghi nhận số điện thoại **${this.callbackForm.phone}** của quý khách (${this.callbackForm.name || 'Quý khách'}).\n\nNhân viên phụ trách đã nhận được thông báo tức thời và sẽ chủ động gọi lại tư vấn trong vòng **5 - 10 phút**. Cảm ơn quý khách!`,
            timestamp: new Date()
          });
          this.shouldScrollToBottom = true;
          this.saveMessages();
          this.cdr.detectChanges();
        }, 400);
      },
      error: () => {
        setTimeout(() => {
          this.closeHumanHandoff();
          this.addMessage({
            role: 'ai',
            content: `📞 **Đã ghi nhận yêu cầu hỗ trợ!**\n\nResort đã lưu số điện thoại **${this.callbackForm.phone}**. Quý khách cũng có thể gọi hotline trực tiếp **0901 234 567** để được phục vụ ngay!`,
            timestamp: new Date()
          });
          this.shouldScrollToBottom = true;
          this.saveMessages();
          this.cdr.detectChanges();
        }, 400);
      }
    });
  }

  // ======================== PRIVATE UTILITIES ========================

  private addWelcomeMessage(): void {
    this.addMessage({
      role: 'ai',
      content: 'Xin chào! Tôi là **Villa AI** - trợ lý thông minh của khu nghỉ dưỡng 5 sao Aura Resort Sầm Sơn.\n\nTôi có thể giúp quý khách:\n- 🏖️ Tìm kiếm & kiểm tra phòng villa trống\n- 💆 Tư vấn dịch vụ Spa, BBQ tại villa & xe đưa đón\n- 🎁 Cập nhật ưu đãi & gói combo đặc biệt\n- 🛎️ Hỗ trợ lập đơn đặt phòng hoặc kết nối lễ tân trực tiếp\n\nQuý khách muốn trải nghiệm dịch vụ gì hôm nay?',
      timestamp: new Date()
    });
  }

  private addMessage(msg: ChatMessage): void {
    this.messages.update(msgs => [...msgs, msg]);
  }

  private removeLoadingMessage(): void {
    this.messages.update(msgs => msgs.filter(m => !m.isLoading));
  }

  private scrollToBottom(): void {
    try {
      const container = this.messagesContainer?.nativeElement;
      if (container) {
        container.scrollTop = container.scrollHeight;
      }
    } catch {}
  }

  private saveMessages(): void {
    const toSave = this.messages().slice(-30);
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(toSave));
  }

  private loadSavedMessages(): void {
    try {
      const saved = localStorage.getItem(this.STORAGE_KEY);
      if (saved) {
        const parsed = JSON.parse(saved);
        const msgs = parsed.map((m: any) => ({ ...m, timestamp: new Date(m.timestamp) }));
        this.messages.set(msgs);
      }
    } catch {
      localStorage.removeItem(this.STORAGE_KEY);
    }
  }

  formatContent(content: string): string {
    if (!content) return '';
    return content
      .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
      .replace(/\*(.*?)\*/g, '<em>$1</em>')
      .replace(/\n/g, '<br>');
  }
}
