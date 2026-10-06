import { Component, OnInit, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { VillaService } from '../../../core/services/villa.service';
import { TokenService } from '../../../core/services/token.service';
import { Villa, ChildRoom } from '../../../core/models/villa.model';
import { BookingStateService } from '../../../core/services/booking-state.service';
import { AdminExtraServiceService, ExtraServiceItem } from '../../../core/services/admin-extra-service.service';
import { ComboPackageService, ComboPackage } from '../../../core/services/combo-package.service';
import { PromotionService } from '../../../core/services/promotion.service';

@Component({
  selector: 'app-villa-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './villa-detail.component.html',
  styleUrls: ['./villa-detail.component.scss']
})
export class VillaDetailComponent implements OnInit {
  villaId: string | null = null;
  villa: Villa | null = null;
  loading = true;
  hasError = false;

  // Gallery & Lightbox states
  selectedImageUrl: string = '';
  galleryImages: string[] = [];
  isLightboxOpen = false;
  lightboxIndex = 0;

  // Active Tab state
  activeTab: 'SPECS' | 'OVERVIEW' | 'ROOMS' | 'AMENITIES' | 'SERVICES' | 'LOCATION' = 'SPECS';

  setActiveTab(tab: 'SPECS' | 'OVERVIEW' | 'ROOMS' | 'AMENITIES' | 'SERVICES' | 'LOCATION'): void {
    this.activeTab = tab;
  }

  // Floor filter for child rooms
  selectedFloorFilter: number | 'ALL' = 'ALL';

  // Interaction states
  isFavorite = false;

  // Promo code & discount
  promoCode = '';
  appliedPromotion: any = null;
  isCheckingPromo = false;
  promoApplied = false;
  promoError = '';

  // Extra Services & Combo Packages
  extraServicesList: ExtraServiceItem[] = [];
  comboPackagesList: ComboPackage[] = [];
  serviceSelectionMode: 'individual' | 'combo' = 'individual';
  activeServiceTab: 'none' | 'individual' | 'combo' = 'none';
  selectedComboId: number | null = null;
  selectedServiceIds: Set<number> = new Set();
  isServicesLoading = false;

  // Form states
  checkInDate = '';
  checkOutDate = '';

  // Toast notification
  toastMessage = '';
  toastType: 'success' | 'error' | 'info' = 'info';
  showToast = false;

  // 5-Star Verified Reviews
  reviews = [
    {
      guestName: 'Nguyễn Hoàng Long',
      stayDate: 'Tháng 9/2026',
      stayType: 'Gia đình 8 người • 3 đêm',
      rating: 5,
      initials: 'NL',
      avatarBg: 'bg-emerald-600',
      comment: 'Biệt thự thực sự đẳng cấp vượt kỳ vọng. Không gian sạch bóng không một hạt bụi, đã được khử khuẩn Ozone thơm tho trước khi cả nhà check-in. Hồ bơi riêng nước rất trong và ấm, trẻ con bơi cả ngày không biết chán. Quản gia hỗ trợ 24/7 cực kỳ chu đáo!',
      highlights: ['Khử khuẩn Ozone tuyệt đối', 'Hồ bơi riêng sạch đẹp', 'Quản gia tận tâm']
    },
    {
      guestName: 'Trần Minh Thu & Nhóm Bạn',
      stayDate: 'Tháng 9/2026',
      stayType: 'Kỳ nghỉ bạn bè • 2 đêm',
      rating: 5,
      initials: 'MT',
      avatarBg: 'bg-sky-600',
      comment: 'Vị trí sát bờ biển, đi bộ chỉ hơn 1 phút là ra tới bãi cát riêng. Buổi tối set up tiệc nướng BBQ ngay bên cạnh hồ bơi cực chill. Phòng ngủ Master rộng và đệm êm như khách sạn 6 sao.',
      highlights: ['Sát biển riêng', 'BBQ sân vườn', 'Đệm ngủ 5 sao']
    },
    {
      guestName: 'Phạm Quang Dũng',
      stayDate: 'Tháng 8/2026',
      stayType: 'Doanh nhân & Đối tác • 4 đêm',
      rating: 5,
      initials: 'QD',
      avatarBg: 'bg-amber-600',
      comment: 'Điểm 10 cho sự yên tĩnh và tính riêng tư tuyệt đối. Đưa đón nội khu bằng xe điện rất nhanh, thủ tục nhận trả phòng tức thì không mất thời gian. Chắc chắn sẽ quay lại vào kỳ nghỉ tới!',
      highlights: ['Riêng tư yên tĩnh', 'Xe điện đưa đón', 'Check-in tức thì']
    }
  ];

  // Surroundings / Proximity Highlights
  resortProximities = [
    { name: 'Bãi biển riêng FLC Sầm Sơn', distance: '80m', time: '1 phút đi bộ', icon: 'beach_access', color: 'text-sky-600', bg: 'bg-sky-50' },
    { name: 'Sân Golf FLC Golf Links 18 hố', distance: '400m', time: 'Xe điện 2 phút', icon: 'sports_golf', color: 'text-emerald-600', bg: 'bg-emerald-50' },
    { name: 'Hồ bơi nước mặn 5.100 m²', distance: '250m', time: '3 phút đi bộ', icon: 'pool', color: 'text-blue-600', bg: 'bg-blue-50' },
    { name: 'Nhà hàng Hương Biển & Buffet Á-Âu', distance: '180m', time: '2 phút đi bộ', icon: 'restaurant', color: 'text-amber-600', bg: 'bg-amber-50' },
    { name: 'Spa Maia & Chăm sóc sức khỏe', distance: '300m', time: 'Xe điện miễn phí', icon: 'spa', color: 'text-rose-600', bg: 'bg-rose-50' },
    { name: 'Quảng trường Ánh Sáng & Nhạc Nước', distance: '500m', time: 'Xe điện 3 phút', icon: 'festival', color: 'text-purple-600', bg: 'bg-purple-50' }
  ];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private villaService: VillaService,
    private tokenService: TokenService,
    private bookingStateService: BookingStateService,
    private extraService: AdminExtraServiceService,
    private comboService: ComboPackageService,
    private promotionService: PromotionService
  ) {}

  ngOnInit(): void {
    this.villaId = this.route.snapshot.paramMap.get('id');
    if (this.villaId) {
      this.loadVilla(this.villaId);
    } else {
      this.hasError = true;
      this.loading = false;
    }

    this.loadExtraServicesAndCombos();

    // Default dates or read from query params
    const today = new Date();
    const tomorrow = new Date(today);
    tomorrow.setDate(today.getDate() + 1);
    const dayAfter = new Date(today);
    dayAfter.setDate(today.getDate() + 2);

    this.route.queryParams.subscribe(params => {
      this.checkInDate = params['checkIn'] || this.formatDateLocal(tomorrow);
      this.checkOutDate = params['checkOut'] || this.formatDateLocal(dayAfter);
    });
  }

  private formatDateLocal(date: Date): string {
    const y = date.getFullYear();
    const m = String(date.getMonth() + 1).padStart(2, '0');
    const d = String(date.getDate()).padStart(2, '0');
    return `${y}-${m}-${d}`;
  }

  loadVilla(id: string): void {
    const numId = Number(id);
    if (!isNaN(numId) && numId > 0) {
      this.villaService.getVillaById(numId).subscribe({
        next: (villa: Villa) => {
          if (villa) {
            this.villa = villa;
            this.setupGallery(villa);
            this.loading = false;
          } else {
            this.fallbackLoadVilla(id);
          }
        },
        error: () => {
          this.fallbackLoadVilla(id);
        }
      });
    } else {
      this.fallbackLoadVilla(id);
    }
  }

  private fallbackLoadVilla(id: string): void {
    this.villaService.getVillas().subscribe({
      next: (villas: Villa[]) => {
        this.villa = villas.find((v: Villa) => String(v.id) === id || v.villaNumber === id) || null;
        if (this.villa) {
          this.setupGallery(this.villa);
        } else {
          this.hasError = true;
        }
        this.loading = false;
      },
      error: (err: Error) => {
        console.error(err);
        this.hasError = true;
        this.loading = false;
      }
    });
  }

  private setupGallery(villa: Villa): void {
    const list: string[] = [];
    if (villa.imageUrl && villa.imageUrl.trim()) {
      list.push(villa.imageUrl.trim());
    }
    if (villa.images && Array.isArray(villa.images)) {
      villa.images.forEach((img: string) => {
        if (img && img.trim() && !list.includes(img.trim())) {
          list.push(img.trim());
        }
      });
    }
    if (list.length === 0) {
      list.push('/assets/images/rooms/royal-penthouse.jpg');
    }
    this.galleryImages = list;
    this.selectedImageUrl = list[0];
  }

  selectImage(img: string): void {
    this.selectedImageUrl = img;
  }

  get currentImageIndex(): number {
    const idx = this.galleryImages.indexOf(this.selectedImageUrl);
    return idx >= 0 ? idx : 0;
  }

  prevImage(): void {
    if (this.galleryImages.length <= 1) return;
    const currentIdx = this.currentImageIndex;
    const prevIdx = (currentIdx - 1 + this.galleryImages.length) % this.galleryImages.length;
    this.selectedImageUrl = this.galleryImages[prevIdx];
  }

  nextImage(): void {
    if (this.galleryImages.length <= 1) return;
    const currentIdx = this.currentImageIndex;
    const nextIdx = (currentIdx + 1) % this.galleryImages.length;
    this.selectedImageUrl = this.galleryImages[nextIdx];
  }

  getVillaImageUrl(villa: Villa): string {
    if (villa.imageUrl) return villa.imageUrl;
    return '/assets/images/rooms/royal-penthouse.jpg';
  }

  // Lightbox Handlers
  openLightbox(index?: number): void {
    if (index !== undefined) {
      this.lightboxIndex = index;
    } else {
      this.lightboxIndex = this.currentImageIndex;
    }
    this.isLightboxOpen = true;
    try {
      document.body.style.overflow = 'hidden';
    } catch (e) {}
  }

  closeLightbox(): void {
    this.isLightboxOpen = false;
    try {
      document.body.style.overflow = '';
    } catch (e) {}
  }

  lightboxNext(): void {
    if (this.galleryImages.length <= 1) return;
    this.lightboxIndex = (this.lightboxIndex + 1) % this.galleryImages.length;
    this.selectedImageUrl = this.galleryImages[this.lightboxIndex];
  }

  lightboxPrev(): void {
    if (this.galleryImages.length <= 1) return;
    this.lightboxIndex = (this.lightboxIndex - 1 + this.galleryImages.length) % this.galleryImages.length;
    this.selectedImageUrl = this.galleryImages[this.lightboxIndex];
  }

  selectLightboxImage(idx: number): void {
    this.lightboxIndex = idx;
    this.selectedImageUrl = this.galleryImages[idx];
  }

  @HostListener('window:keydown', ['$event'])
  handleKeyDown(event: KeyboardEvent): void {
    if (!this.isLightboxOpen) return;
    if (event.key === 'Escape') {
      this.closeLightbox();
    } else if (event.key === 'ArrowRight') {
      this.lightboxNext();
    } else if (event.key === 'ArrowLeft') {
      this.lightboxPrev();
    }
  }

  // Favorite & Share
  toggleFavorite(): void {
    this.isFavorite = !this.isFavorite;
    this.showToastMessage(
      this.isFavorite ? 'Đã thêm Villa vào danh sách yêu thích!' : 'Đã bỏ yêu thích Villa.',
      'success'
    );
  }

  shareVilla(): void {
    if (typeof navigator !== 'undefined' && 'share' in navigator) {
      (navigator as any).share({
        title: 'Villa ' + (this.villa?.villaNumber || ''),
        text: 'Nghỉ dưỡng thượng lưu tại FLC Sầm Sơn',
        url: window.location.href
      }).catch(() => {});
    } else if (typeof navigator !== 'undefined' && (navigator as any).clipboard) {
      (navigator as any).clipboard.writeText(window.location.href);
      this.showToastMessage('Đã sao chép liên kết Villa vào bộ nhớ tạm!', 'success');
    } else {
      this.showToastMessage('Đã sao chép liên kết Villa!', 'info');
    }
  }

  scrollToBooking(): void {
    const el = document.getElementById('bookingCard');
    if (el) {
      el.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }

  // Floor Filter for Child Bedrooms
  get floorsAvailable(): number[] {
    if (!this.villa?.rooms) return [];
    const floors = new Set<number>();
    this.villa.rooms.forEach(r => {
      if (r.floor) floors.add(r.floor);
    });
    return Array.from(floors).sort((a, b) => a - b);
  }

  get filteredRooms(): ChildRoom[] {
    if (!this.villa?.rooms) return [];
    if (this.selectedFloorFilter === 'ALL') return this.villa.rooms;
    return this.villa.rooms.filter(r => r.floor === this.selectedFloorFilter);
  }

  // Promo Code
  applyPromoCode(): void {
    const code = (this.promoCode || '').trim().toUpperCase();
    if (!code) {
      this.promoError = 'Vui lòng nhập mã ưu đãi';
      return;
    }

    this.isCheckingPromo = true;
    this.promoError = '';

    this.promotionService.validateCode(code).subscribe({
      next: (promo) => {
        this.isCheckingPromo = false;
        this.appliedPromotion = promo;
        this.promoApplied = true;
        this.promoError = '';
        const discountLabel = promo.discountType === 'PERCENTAGE'
          ? `${promo.discountValue}%`
          : `${this.formatPrice(promo.discountValue)}đ`;
        this.showToastMessage(`Áp dụng mã ưu đãi ${promo.code} (-${discountLabel}) thành công!`, 'success');
      },
      error: (err) => {
        this.isCheckingPromo = false;
        this.appliedPromotion = null;
        this.promoApplied = false;
        this.promoError = err.error?.message || 'Mã ưu đãi không hợp lệ hoặc đã hết hạn';
        this.showToastMessage(this.promoError, 'error');
      }
    });
  }

  removePromo(): void {
    this.promoCode = '';
    this.appliedPromotion = null;
    this.promoApplied = false;
    this.promoError = '';
  }

  // Extra Services & Combo Package Logic
  loadExtraServicesAndCombos(): void {
    this.isServicesLoading = true;
    this.extraService.getAll().subscribe({
      next: (services) => {
        this.extraServicesList = (services || []).filter(s => s.isActive !== false);
        this.isServicesLoading = false;
      },
      error: (err) => {
        console.warn('Could not load extra services', err);
        this.isServicesLoading = false;
      }
    });

    this.comboService.getActive().subscribe({
      next: (combos) => {
        this.comboPackagesList = combos || [];
      },
      error: (err) => console.warn('Could not load combo packages', err)
    });
  }

  selectServiceTab(tab: 'individual' | 'combo'): void {
    if (this.activeServiceTab === tab) {
      this.activeServiceTab = 'none';
    } else {
      this.activeServiceTab = tab;
      this.serviceSelectionMode = tab;
    }
  }

  closeServiceTab(): void {
    this.activeServiceTab = 'none';
  }

  setServiceMode(mode: 'individual' | 'combo'): void {
    this.serviceSelectionMode = mode;
    this.activeServiceTab = mode;
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

  get discountAmount(): number {
    if (!this.appliedPromotion) return 0;
    const subtotal = this.roomTotal + this.extraServicesTotal;
    if (this.appliedPromotion.discountType === 'PERCENTAGE') {
      return Math.round((subtotal * Number(this.appliedPromotion.discountValue)) / 100);
    } else {
      return Math.min(Number(this.appliedPromotion.discountValue || 0), subtotal);
    }
  }

  get discountPercent(): number {
    if (this.appliedPromotion?.discountType === 'PERCENTAGE') {
      return Number(this.appliedPromotion.discountValue || 0);
    }
    return 0;
  }

  get finalTotal(): number {
    return Math.max(0, this.roomTotal + this.extraServicesTotal - this.discountAmount);
  }

  formatPrice(price?: number): string {
    if (price === undefined || price === null || isNaN(price)) {
      return '';
    }
    return new Intl.NumberFormat('vi-VN', {
      maximumFractionDigits: 0,
    }).format(price);
  }

  private showToastMessage(msg: string, type: 'success' | 'error' | 'info' = 'info'): void {
    this.toastMessage = msg;
    this.toastType = type;
    this.showToast = true;
    setTimeout(() => { this.showToast = false; }, 4000);
  }

  get numberOfNights(): number {
    const checkIn = new Date(this.checkInDate);
    const checkOut = new Date(this.checkOutDate);
    const diffTime = Math.abs(checkOut.getTime() - checkIn.getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return diffDays > 0 ? diffDays : 1;
  }

  get roomTotal(): number {
    if (!this.villa || !this.villa.basePrice) return 0;
    return this.villa.basePrice * this.numberOfNights;
  }

  proceedToCheckout(): void {
    if (!this.villa) return;

    // Validate ngày
    const checkIn = new Date(this.checkInDate);
    const checkOut = new Date(this.checkOutDate);
    if (checkOut <= checkIn) {
      this.showToastMessage('Ngày trả phòng phải sau ngày nhận phòng!', 'error');
      return;
    }

    // Ghi nhận state
    this.bookingStateService.setState({
      villa: this.villa,
      checkInDate: this.checkInDate,
      checkOutDate: this.checkOutDate,
      serviceSelectionMode: this.serviceSelectionMode,
      selectedServiceIds: Array.from(this.selectedServiceIds),
      selectedComboId: this.selectedComboId,
      promoCode: this.promoApplied ? (this.appliedPromotion?.code || this.promoCode) : undefined,
      appliedPromotion: this.appliedPromotion,
      discountPercent: this.discountPercent
    });

    this.router.navigate(['/checkout']);
  }
}
