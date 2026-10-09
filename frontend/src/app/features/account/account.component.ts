/* eslint-disable @typescript-eslint/no-explicit-any */
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { UserProfileService, UserProfile } from '../../core/services/user-profile.service';
import { ClientBookingService, ClientBooking } from '../../core/services/client-booking.service';
import { ClientReviewService, ClientReview, CreateReviewRequest } from '../../core/services/client-review.service';
import { TokenService } from '../../core/services/token.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-account',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.scss']
})
export class AccountComponent implements OnInit {
  activeTab: 'bookings' | 'reviews' | 'profile' = 'bookings';
  reviewSubTab: 'reviewed' | 'pending' = 'reviewed';
  bookingFilter: 'ALL' | 'PENDING' | 'CONFIRMED' | 'CHECKED_OUT' | 'CANCELLED' = 'ALL';

  // User Profile State
  profile: UserProfile | null = null;
  profileLoading = false;
  profileForm = {
    fullName: '',
    phone: '',
    avatar: ''
  };
  passwordForm = {
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  };
  passwordLoading = false;

  // Bookings State
  bookings: ClientBooking[] = [];
  bookingsLoading = false;
  selectedBooking: ClientBooking | null = null;
  showDetailModal = false;
  showCancelModal = false;
  cancellingBookingId: number | null = null;
  cancelLoading = false;

  // Reviews State
  reviews: ClientReview[] = [];
  reviewsLoading = false;
  showReviewModal = false;
  reviewForm: CreateReviewRequest = {
    bookingId: 0,
    rating: 5,
    comment: ''
  };
  reviewingBooking: ClientBooking | null = null;
  reviewSubmitting = false;

  // Notification Toast
  toastMessage: string | null = null;
  toastType: 'success' | 'error' = 'success';

  constructor(
    private profileService: UserProfileService,
    private bookingService: ClientBookingService,
    private reviewService: ClientReviewService,
    private tokenService: TokenService,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      if (params['tab']) {
        const t = params['tab'].toLowerCase();
        if (t === 'bookings' || t === 'reviews' || t === 'profile') {
          this.activeTab = t;
        }
      }
    });

    this.loadProfile();
    this.loadBookings();
    this.loadReviews();
  }

  setTab(tab: 'bookings' | 'reviews' | 'profile'): void {
    this.activeTab = tab;
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { tab },
      queryParamsHandling: 'merge'
    });
  }

  // --- Profile Methods ---
  loadProfile(): void {
    this.profileLoading = true;
    this.profileService.getProfile().subscribe({
      next: (data) => {
        this.profile = data;
        this.profileForm.fullName = data.fullName || '';
        this.profileForm.phone = data.phone || '';
        this.profileForm.avatar = data.avatar || '';
        this.profileLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.profileLoading = false;
        console.error('Failed to load user profile', err);
      }
    });
  }

  saveProfile(): void {
    if (!this.profileForm.fullName.trim()) {
      return this.showToast('Vui lòng nhập họ và tên!', 'error');
    }
    this.profileLoading = true;
    this.profileService.updateProfile(this.profileForm).subscribe({
      next: (updated) => {
        this.profile = updated;
        this.profileLoading = false;
        
        // Cập nhật lại thông tin trong localStorage để header nhận diện được
        const raw = localStorage.getItem('user');
        if (raw) {
          try {
            const userSession = JSON.parse(raw);
            userSession.fullName = updated.fullName;
            userSession.avatarUrl = updated.avatar;
            localStorage.setItem('user', JSON.stringify(userSession));
          } catch (e) {}
        }
        
        this.showToast('Cập nhật thông tin cá nhân thành công!', 'success');
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.profileLoading = false;
        const msg = err.error?.message || 'Có lỗi xảy ra khi cập nhật thông tin!';
        this.showToast(msg, 'error');
      }
    });
  }

  onAvatarSelect(event: any): void {
    const file = event.target.files?.[0];
    if (!file) return;

    if (file.size > 2 * 1024 * 1024) {
      this.showToast('Ảnh đại diện không được vượt quá 2MB', 'error');
      return;
    }

    const reader = new FileReader();
    reader.onload = (e: any) => {
      this.profileForm.avatar = e.target.result;
      this.cdr.detectChanges();
    };
    reader.readAsDataURL(file);
  }

  changePassword(): void {
    if (!this.passwordForm.currentPassword) {
      return this.showToast('Vui lòng nhập mật khẩu hiện tại!', 'error');
    }
    if (!this.passwordForm.newPassword || this.passwordForm.newPassword.length < 6) {
      return this.showToast('Mật khẩu mới phải từ 6 ký tự trở lên!', 'error');
    }
    if (this.passwordForm.newPassword !== this.passwordForm.confirmPassword) {
      return this.showToast('Xác nhận mật khẩu mới không khớp!', 'error');
    }

    this.passwordLoading = true;
    this.profileService.changePassword({
      currentPassword: this.passwordForm.currentPassword,
      newPassword: this.passwordForm.newPassword
    }).subscribe({
      next: () => {
        this.passwordLoading = false;
        this.passwordForm = { currentPassword: '', newPassword: '', confirmPassword: '' };
        this.showToast('Đổi mật khẩu thành công!', 'success');
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.passwordLoading = false;
        const msg = err.error?.message || 'Đổi mật khẩu thất bại. Vui lòng kiểm tra mật khẩu hiện tại!';
        this.showToast(msg, 'error');
      }
    });
  }

  // --- Bookings Methods ---
  loadBookings(): void {
    this.bookingsLoading = true;
    this.bookingService.getMyBookings().subscribe({
      next: (data) => {
        this.bookings = (data || []).map(b => this.enrichBookingData(b));
        this.bookingsLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load bookings', err);
        this.bookingsLoading = false;
      }
    });
  }

  private enrichBookingData(b: ClientBooking): ClientBooking {
    const checkIn = new Date(b.checkInDate);
    const checkOut = new Date(b.checkOutDate);
    const nights = Math.max(1, Math.round((checkOut.getTime() - checkIn.getTime()) / (1000 * 3600 * 24)));
    
    let villaName = 'Biệt Thự Nghỉ Dưỡng Aura';
    if (b.bookedVillaNumbers && b.bookedVillaNumbers.length > 0) {
      villaName = `Villa ${b.bookedVillaNumbers.join(', ')}`;
    } else if (b.bookedRoomNumbers && b.bookedRoomNumbers.length > 0) {
      villaName = `Phòng ${b.bookedRoomNumbers.join(', ')}`;
    }

    // Default luxury villa image placeholder
    const villaImage = 'https://images.unsplash.com/photo-1580587771525-78b9dba3b914?auto=format&fit=crop&w=800&q=80';

    return {
      ...b,
      totalNights: nights,
      villaName: villaName,
      villaImage: villaImage
    };
  }

  get filteredBookings(): ClientBooking[] {
    if (this.bookingFilter === 'ALL') return this.bookings;
    if (this.bookingFilter === 'CONFIRMED') {
      return this.bookings.filter(b => b.status === 'CONFIRMED' || b.status === 'CHECKED_IN');
    }
    return this.bookings.filter(b => b.status === this.bookingFilter);
  }

  get pendingBookingsCount(): number {
    return this.bookings.filter(b => b.status === 'PENDING').length;
  }

  get confirmedBookingsCount(): number {
    return this.bookings.filter(b => b.status === 'CONFIRMED' || b.status === 'CHECKED_IN').length;
  }

  get completedBookingsCount(): number {
    return this.bookings.filter(b => b.status === 'CHECKED_OUT').length;
  }

  openBookingDetail(booking: ClientBooking): void {
    this.selectedBooking = booking;
    this.showDetailModal = true;
  }

  closeBookingDetail(): void {
    this.selectedBooking = null;
    this.showDetailModal = false;
  }

  promptCancelBooking(bookingId: number): void {
    this.cancellingBookingId = bookingId;
    this.showCancelModal = true;
  }

  confirmCancelBooking(): void {
    if (!this.cancellingBookingId) return;
    this.cancelLoading = true;
    this.bookingService.cancelBooking(this.cancellingBookingId).subscribe({
      next: () => {
        this.cancelLoading = false;
        this.showCancelModal = false;
        this.cancellingBookingId = null;
        this.showToast('Hủy đơn đặt phòng thành công!', 'success');
        this.loadBookings();
      },
      error: (err) => {
        this.cancelLoading = false;
        const msg = err.error?.message || 'Có lỗi xảy ra khi hủy đơn!';
        this.showToast(msg, 'error');
      }
    });
  }

  // --- Reviews Methods ---
  loadReviews(): void {
    this.reviewsLoading = true;
    this.reviewService.getMyReviews().subscribe({
      next: (data) => {
        this.reviews = data || [];
        this.reviewsLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load reviews', err);
        this.reviewsLoading = false;
      }
    });
  }

  get completedUnreviewedBookings(): ClientBooking[] {
    const reviewedBookingIds = new Set(this.reviews.map(r => r.bookingId));
    return this.bookings.filter(b => b.status === 'CHECKED_OUT' && !reviewedBookingIds.has(b.id));
  }

  openWriteReview(booking: ClientBooking): void {
    this.reviewingBooking = booking;
    this.reviewForm = {
      bookingId: booking.id,
      rating: 5,
      comment: ''
    };
    this.showReviewModal = true;
  }

  closeReviewModal(): void {
    this.showReviewModal = false;
    this.reviewingBooking = null;
    this.reviewForm = { bookingId: 0, rating: 5, comment: '' };
  }

  setRating(stars: number): void {
    this.reviewForm.rating = stars;
  }

  submitReview(): void {
    if (!this.reviewForm.comment.trim()) {
      return this.showToast('Vui lòng chia sẻ cảm nhận của bạn về chuyến đi!', 'error');
    }
    this.reviewSubmitting = true;
    this.reviewService.createReview(this.reviewForm).subscribe({
      next: () => {
        this.reviewSubmitting = false;
        this.closeReviewModal();
        this.showToast('Gửi đánh giá thành công! Cảm ơn ý kiến quý giá của bạn.', 'success');
        this.loadReviews();
        this.loadBookings();
      },
      error: (err) => {
        this.reviewSubmitting = false;
        const msg = err.error?.message || 'Có lỗi xảy ra khi gửi đánh giá!';
        this.showToast(msg, 'error');
      }
    });
  }

  // --- Utility Methods ---
  logout(): void {
    const refreshToken = this.tokenService.getRefreshToken() || undefined;
    this.authService.logout(refreshToken).subscribe({
      next: () => {},
      error: () => {}
    });
    this.tokenService.clearAll();
    window.location.href = '/login';
  }

  showToast(msg: string, type: 'success' | 'error' = 'success'): void {
    this.toastMessage = msg;
    this.toastType = type;
    setTimeout(() => {
      this.toastMessage = null;
    }, 3500);
  }

  getInitials(name: string): string {
    if (!name) return 'A';
    const parts = name.trim().split(' ');
    if (parts.length >= 2) {
      return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
    }
    return name.substring(0, 2).toUpperCase();
  }
}
