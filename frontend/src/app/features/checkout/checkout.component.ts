import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { BookingStateService, BookingState } from '../../core/services/booking-state.service';
import { TokenService } from '../../core/services/token.service';
import { AdminExtraServiceService, ExtraServiceItem } from '../../core/services/admin-extra-service.service';
import { ComboPackageService, ComboPackage } from '../../core/services/combo-package.service';
import { PromotionService } from '../../core/services/promotion.service';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, ReactiveFormsModule],
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.scss']
})
export class CheckoutComponent implements OnInit {
  bookingState?: BookingState;
  
  // Customer Info Form
  checkoutForm!: FormGroup;

  // Extra Services & Combo Package state
  extraServicesList: ExtraServiceItem[] = [];
  comboPackagesList: ComboPackage[] = [];
  serviceSelectionMode: 'individual' | 'combo' = 'individual';
  selectedComboId: number | null = null;
  selectedServiceIds: Set<number> = new Set();
  isServicesExpanded = false;

  // Promotion / Coupon code state
  availablePromotions: any[] = [];
  couponCode = '';
  appliedPromotion: any = null;
  isCheckingCoupon = false;
  couponError = '';
  couponSuccess = '';

  // Payment
  paymentMethod = 'pay_at_hotel';
  isSubmitting = false;

  // Toast notification
  toastMessage = '';
  toastType: 'success' | 'error' | 'info' = 'info';
  showToast = false;
  submitError: string | null = null;
  private toastTimeout: any;

  constructor(
    private router: Router,
    private bookingStateService: BookingStateService,
    private tokenService: TokenService,
    private http: HttpClient,
    private extraService: AdminExtraServiceService,
    private comboService: ComboPackageService,
    private promotionService: PromotionService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    const state = this.bookingStateService.getState();
    if (!state || !state.villa) {
      this.router.navigate(['/']);
      return;
    }
    this.bookingState = state;

    if (state.serviceSelectionMode) {
      this.serviceSelectionMode = state.serviceSelectionMode;
    }
    if (state.selectedServiceIds && state.selectedServiceIds.length > 0) {
      this.selectedServiceIds = new Set(state.selectedServiceIds);
      this.isServicesExpanded = true;
    }
    if (state.selectedComboId) {
      this.selectedComboId = state.selectedComboId;
    }

    this.initForm();
    this.loadExtraServicesAndCombos();

    if (state.promoCode) {
      this.couponCode = state.promoCode;
      this.applyCoupon();
    }
  }

  private initForm(): void {
    this.checkoutForm = this.fb.group({
      fullName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', Validators.required],
      estimatedArrivalTime: ['14:00'],
      specialRequest: ['']
    });
  }

  private showToastMessage(msg: string, type: 'success' | 'error' | 'info' = 'info'): void {
    this.toastMessage = msg;
    this.toastType = type;
    this.showToast = true;
    if (this.toastTimeout) {
      clearTimeout(this.toastTimeout);
    }
    const duration = type === 'error' ? 7000 : 4000;
    this.toastTimeout = setTimeout(() => { this.showToast = false; }, duration);
  }

  formatPrice(price?: number): string {
    if (price === undefined || price === null || isNaN(price)) return '0';
    return new Intl.NumberFormat('vi-VN', { maximumFractionDigits: 0 }).format(price);
  }

  loadExtraServicesAndCombos(): void {
    this.extraService.getAll().subscribe({
      next: (services) => {
        this.extraServicesList = (services || []).filter(s => s.isActive !== false);
      },
      error: (err) => console.warn('Could not load extra services', err)
    });

    this.comboService.getActive().subscribe({
      next: (combos) => {
        this.comboPackagesList = combos || [];
      },
      error: (err) => console.warn('Could not load combo packages', err)
    });

    this.promotionService.getActivePromotions().subscribe({
      next: (promos) => {
        this.availablePromotions = promos || [];
      },
      error: (err) => console.warn('Could not load active promotions', err)
    });
  }

  setServiceMode(mode: 'individual' | 'combo'): void {
    this.serviceSelectionMode = mode;
  }

  toggleService(serviceId: number): void {
    if (this.selectedServiceIds.has(serviceId)) {
      this.selectedServiceIds.delete(serviceId);
    } else {
      this.selectedServiceIds.add(serviceId);
    }
  }

  isServiceSelected(serviceId: number): boolean {
    return this.selectedServiceIds.has(serviceId);
  }

  selectCombo(combo: ComboPackage): void {
    if (this.selectedComboId === combo.id) {
      this.selectedComboId = null;
    } else {
      this.selectedComboId = combo.id || null;
    }
  }

  get selectedCombo(): ComboPackage | undefined {
    return this.comboPackagesList.find(c => c.id === this.selectedComboId);
  }

  get extraServicesTotal(): number {
    if (this.serviceSelectionMode === 'combo') {
      return this.selectedCombo?.price ? Number(this.selectedCombo.price) : 0;
    } else {
      let total = 0;
      for (const s of this.extraServicesList) {
        if (this.selectedServiceIds.has(s.id)) {
          total += Number(s.price || 0);
        }
      }
      return total;
    }
  }

  applyCoupon(): void {
    const code = (this.couponCode || '').trim();
    if (!code) {
      this.couponError = 'Vui lòng nhập mã giảm giá!';
      return;
    }

    this.isCheckingCoupon = true;
    this.couponError = '';
    this.couponSuccess = '';

    this.promotionService.validateCode(code).subscribe({
      next: (promo) => {
        this.isCheckingCoupon = false;
        this.appliedPromotion = promo;
        this.couponSuccess = `Áp dụng thành công: ${promo.code}`;
        this.showToastMessage(`Áp dụng mã giảm giá ${promo.code} thành công!`, 'success');
      },
      error: (err) => {
        this.isCheckingCoupon = false;
        this.appliedPromotion = null;
        this.couponError = err.error?.message || 'Mã giảm giá không hợp lệ hoặc đã hết hạn!';
        this.showToastMessage(this.couponError, 'error');
      }
    });
  }

  removeCoupon(): void {
    this.appliedPromotion = null;
    this.couponCode = '';
    this.couponError = '';
    this.couponSuccess = '';
  }

  selectPromoCode(code: string): void {
    this.couponCode = code;
    this.applyCoupon();
  }

  get numberOfNights(): number {
    if (!this.bookingState) return 1;
    const checkIn = new Date(this.bookingState.checkInDate);
    const checkOut = new Date(this.bookingState.checkOutDate);
    const diffTime = Math.abs(checkOut.getTime() - checkIn.getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return diffDays > 0 ? diffDays : 1;
  }

  get roomTotal(): number {
    if (!this.bookingState || !this.bookingState.villa?.basePrice) return 0;
    return this.bookingState.villa.basePrice * this.numberOfNights;
  }

  get discountAmount(): number {
    if (!this.appliedPromotion) return 0;
    const subtotal = this.roomTotal + this.extraServicesTotal;
    if (this.appliedPromotion.discountType === 'PERCENTAGE') {
      return (subtotal * Number(this.appliedPromotion.discountValue)) / 100;
    } else {
      return Math.min(Number(this.appliedPromotion.discountValue || 0), subtotal);
    }
  }

  get taxableAmount(): number {
    return Math.max(0, this.roomTotal + this.extraServicesTotal - this.discountAmount);
  }

  get taxAmount(): number {
    return this.taxableAmount * 0.1;
  }

  get finalTotal(): number {
    return this.taxableAmount + this.taxAmount;
  }

  submitBooking(): void {
    if (this.checkoutForm.invalid) {
      this.checkoutForm.markAllAsTouched();
      this.submitError = 'Vui lòng điền đầy đủ thông tin bắt buộc!';
      this.showToastMessage('Vui lòng điền đầy đủ thông tin bắt buộc!', 'error');
      return;
    }

    if (!this.bookingState?.villa) return;

    this.submitError = null;
    this.isSubmitting = true;

    const formValues = this.checkoutForm.value;

    /* eslint-disable @typescript-eslint/no-explicit-any */
    const payload: any = {
      checkInDate: this.bookingState.checkInDate,
      checkOutDate: this.bookingState.checkOutDate,
      villaId: this.bookingState.villa.id || undefined,
      villaTypeId: this.bookingState.villa.villaTypeId,
      roomTypeId: this.bookingState.villa.villaTypeId,
      quantity: 1,
      promotionCode: this.appliedPromotion?.code || undefined,
      note: formValues.specialRequest,
      specialRequest: formValues.specialRequest,
      guestName: formValues.fullName,
      guestEmail: formValues.email,
      guestPhone: formValues.phone,
      estimatedArrivalTime: formValues.estimatedArrivalTime,
      paymentMethod: this.paymentMethod
    };

    if (this.serviceSelectionMode === 'individual' && this.selectedServiceIds.size > 0) {
      payload.extraServices = Array.from(this.selectedServiceIds).map(id => ({
        serviceId: id,
        quantity: 1
      }));
    }

    /* eslint-disable @typescript-eslint/no-explicit-any */
    this.http.post<any>(`${environment.apiUrl}/bookings`, payload).subscribe({
      next: (res) => {
        const bookingId = res.data?.id;

        if (this.paymentMethod === 'momo' && bookingId) {
          this.http.post<any>(`${environment.apiUrl}/payments/momo/${bookingId}`, {}).subscribe({
            next: (momoRes) => {
              if (momoRes.data) {
                window.location.href = momoRes.data;
              } else {
                this.submitError = 'Không lấy được link thanh toán MoMo!';
                this.showToastMessage('Không lấy được link thanh toán MoMo!', 'error');
                this.isSubmitting = false;
              }
            },
            error: (err) => {
              console.error('Lỗi MoMo', err);
              this.submitError = 'Lỗi kết nối MoMo. Vui lòng thử lại!';
              this.showToastMessage('Lỗi kết nối MoMo. Vui lòng thử lại!', 'error');
              this.isSubmitting = false;
            }
          });
        } else {
          this.showToastMessage('Đặt phòng thành công! Chúng tôi đã gửi email xác nhận.', 'success');
          this.isSubmitting = false;
          this.bookingStateService.clearState();
          const code = res.data?.bookingCode || '';
          setTimeout(() => this.router.navigate(['/booking-success'], { 
            queryParams: { 
              code: code,
              email: formValues.email 
            } 
          }), 1500);
        }
      },
      error: (err) => {
        console.error('Lỗi đặt phòng', err);
        const status = err.status;
        if (status === 401 || status === 403) {
          this.submitError = 'Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại!';
          this.showToastMessage('Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại!', 'info');
          setTimeout(() => this.router.navigate(['/login']), 1500);
        } else if (status === 400) {
          const errMsg = err.error?.message || 'Thông tin đặt phòng không hợp lệ!';
          this.submitError = errMsg;
          this.showToastMessage(errMsg, 'error');
        } else {
          const errMsg = 'Đã có lỗi xảy ra. Vui lòng thử lại!';
          this.submitError = errMsg;
          this.showToastMessage(errMsg, 'error');
        }
        this.isSubmitting = false;
      }
    });
  }
}
