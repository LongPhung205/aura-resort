import {
  Component, OnInit, OnDestroy, ViewChild, ElementRef,
  AfterViewChecked, ChangeDetectorRef, signal
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AiChatService, ChatMessage, ChatApiResponse } from '../../../core/services/ai-chat.service';

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

  // Quick replies
  readonly quickReplies = [
    'Tìm villa trống hôm nay',
    'Villa khu vực biển',
    'Xem khuyến mãi hiện có',
    'Gói combo ưu đãi',
  ];

  private readonly STORAGE_KEY = 'ai_chat_messages';

  constructor(
    private aiChatService: AiChatService,
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
    const loadingId = Date.now();
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
      error: (err) => {
        this.removeLoadingMessage();
        this.addMessage({
          role: 'ai',
          content: 'Không thể kết nối. Vui lòng thử lại sau.',
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
    return 'assets/images/villa-placeholder.jpg';
  }

  clearChat(): void {
    this.aiChatService.resetSession();
    this.sessionId = this.aiChatService.getOrCreateSessionId();
    this.messages.set([]);
    localStorage.removeItem(this.STORAGE_KEY);
    this.addWelcomeMessage();
  }

  formatPrice(price: number): string {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);
  }

  getSearchResults(payload: any): any[] {
    if (!payload) return [];
    return Array.isArray(payload) ? payload.slice(0, 4) : [];
  }

  getBookingCode(payload: any): string {
    return payload?.bookingCode || payload?.id || '';
  }

  // ======================== PRIVATE ========================

  private addWelcomeMessage(): void {
    this.addMessage({
      role: 'ai',
      content: 'Xin chào! Tôi là **Villa AI** - trợ lý đặt phòng thông minh của Villa Paradise.\n\nTôi có thể giúp bạn:\n- Tìm biệt thự phù hợp\n- Kiểm tra khuyến mãi\n- Đặt phòng nhanh chóng\n\nHãy cho tôi biết bạn muốn làm gì!',
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
    } catch (e) {}
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
    } catch (e) {
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
