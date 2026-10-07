import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AdminBookingService } from '../../core/services/admin-booking.service';
import { AdminHousekeepingService } from '../../core/services/admin-housekeeping.service';
import { VillaService } from '../../core/services/villa.service';
import { Villa } from '../../core/models/villa.model';
import { RoomConsumptionRecord } from '../../core/models/housekeeping.model';
import { AdminBookingItem, GanttRoomAvailability } from '../../core/models/admin-booking.model';
import { environment } from '../../../environments/environment';

export interface LuxuryBooking {
  id: number;
  code: string;
  bookingDate: string;
  bookingTime: string;
  guestName: string;
  guestAvatar: string;
  guestEmail: string;
  country: string;
  phone: string;
  vipTier: string;
  vipBadgeClass: string;
  villaNumber: string;
  villaName: string;
  checkInDate: string;
  checkOutDate: string;
  nights: number;
  occupants: string;
  channel: string;
  channelClass: string;
  totalAmount: string;
  totalAmountNum: number;
  paymentNote: string;
  paymentMethod: string;
  paymentClass: string;
  service1: string;
  service1Icon: string;
  butler: string;
  status: string;
  statusCode: string;
  statusClass: string;
  statusDot: string;
  note: string;
}

export interface GanttDaySlotItem {
  dateLabel: string;
  fullDate: string;
  status: string;
  label: string;
  class: string;
  tooltip: string;
  bookingId?: number;
  bookingCode?: string;
  guestName?: string;
}

export interface GanttRow {
  villaNumber: string;
  villaName: string;
  zoneName?: string;
  status: string;
  statusClass: string;
  statusDot: string;
  slots: GanttDaySlotItem[];
}

export interface TimelineHeader {
  label: string;
  fullDate: string;
  dayName: string;
  isToday: boolean;
}

@Component({
  selector: 'app-booking-management',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './booking-management.component.html',
  styleUrls: ['./booking-management.component.scss'],
})
export class BookingManagementComponent implements OnInit {
  activeQuickFilter = 'ALL'; // 'ALL', 'CONFIRMED', 'CHECKED_IN', 'PENDING', 'CHECKED_OUT', 'CANCELLED'
  searchTerm = '';
  selectedRoomCategory = 'ALL';
  selectedStatus = 'ALL';
  selectedSort = 'checkInAsc';
  dateRangeLabel = '';

  // Gantt state
  ganttStartDate: Date = new Date();
  ganttSearch = '';
  selectedGanttZone = 'ALL';
  selectedGanttStatus = 'ALL';
  timelineHeaders: TimelineHeader[] = [];
  ganttList: GanttRow[] = [];
  filteredGanttList: GanttRow[] = [];

  // Bookings state
  bookings: LuxuryBooking[] = [];
  filteredBookings: LuxuryBooking[] = [];
  paginatedBookings: LuxuryBooking[] = [];
  currentPage = 1;
  pageSize = 10;
  totalFilteredElements = 0;

  isLoadingBookings = false;
  isLoadingGantt = false;

  // Modal State
  selectedBooking: LuxuryBooking | null = null;
  showDetailModal = false;
  showCreateModal = false;
  toastMessage: string | null = null;

  // Check-in verification modal state
  showCheckInModal = false;
  bookingToCheckIn: LuxuryBooking | null = null;
  checkInInputCode = '';
  checkInErrorMessage = '';
  isVerifyingCheckIn = false;

  // Housekeeping Consumptions for Selected Booking
  consumptions: RoomConsumptionRecord[] = [];
  isLoadingConsumptions = false;

  // Real direct booking creation form
  newBookingForm = {
    guestName: '',
    phone: '',
    email: '',
    villaNumber: 'NT-001',
    checkInDate: '',
    checkOutDate: '',
    amount: 5000000,
    paymentMethod: 'CASH',
    requests: '',
  };

  // Dynamic KPI Getters based on real DB data
  get totalBookingsCount(): number {
    return this.bookings.length;
  }

  get confirmedCount(): number {
    return this.bookings.filter((b) => b.statusCode === 'CONFIRMED').length;
  }

  get inHouseCount(): number {
    return this.bookings.filter((b) => b.statusCode === 'CHECKED_IN').length;
  }

  get pendingCount(): number {
    return this.bookings.filter((b) => b.statusCode === 'PENDING').length;
  }

  get checkedOutCount(): number {
    return this.bookings.filter((b) => b.statusCode === 'CHECKED_OUT').length;
  }

  get cancelledCount(): number {
    return this.bookings.filter((b) => b.statusCode === 'CANCELLED').length;
  }

  get arrivalsTodayCount(): number {
    const today = new Date();
    const todayVi = `${String(today.getDate()).padStart(2, '0')}/${String(today.getMonth() + 1).padStart(2, '0')}`;
    return this.bookings.filter(
      (b) =>
        (b.statusCode === 'CONFIRMED' || b.statusCode === 'CHECKED_IN') &&
        b.checkInDate.includes(todayVi)
    ).length;
  }

  get departuresTodayCount(): number {
    const today = new Date();
    const todayVi = `${String(today.getDate()).padStart(2, '0')}/${String(today.getMonth() + 1).padStart(2, '0')}`;
    return this.bookings.filter(
      (b) =>
        b.statusCode === 'CHECKED_IN' &&
        b.checkOutDate.includes(todayVi)
    ).length;
  }

  get totalRevenueDisplay(): string {
    const sum = this.bookings
      .filter((b) => b.statusCode === 'CONFIRMED' || b.statusCode === 'CHECKED_IN' || b.statusCode === 'CHECKED_OUT')
      .reduce((acc, b) => acc + (b.totalAmountNum || 0), 0);
    return sum > 0 ? `${sum.toLocaleString('vi-VN')}₫` : '0₫';
  }

  get totalPages(): number {
    return Math.max(1, Math.ceil(this.totalFilteredElements / this.pageSize));
  }

  get pagesArray(): number[] {
    const pages: number[] = [];
    for (let i = 1; i <= this.totalPages; i++) {
      pages.push(i);
    }
    return pages;
  }

  // Danh sách toàn bộ biệt thự lấy từ Database
  availableVillas: Villa[] = [];

  constructor(
    private route: ActivatedRoute,
    private bookingService: AdminBookingService,
    private hkService: AdminHousekeepingService,
    private villaService: VillaService
  ) {}

  ngOnInit(): void {
    const today = new Date();
    const tomorrow = new Date(today);
    tomorrow.setDate(tomorrow.getDate() + 2);

    this.newBookingForm.checkInDate = this.formatDateIso(today);
    this.newBookingForm.checkOutDate = this.formatDateIso(tomorrow);

    this.loadBookings();
    this.loadGantt();
    this.loadAvailableVillas();
    this.setupRealTimeUpdates();

    this.route.queryParams.subscribe((params) => {
      if (params['action'] === 'new') {
        this.showCreateModal = true;
      }
    });
  }

  loadAvailableVillas(): void {
    this.villaService.getVillas().subscribe({
      next: (villas) => {
        if (villas && villas.length > 0) {
          this.availableVillas = villas;
          // Nếu villa đang chọn chưa có trong danh sách, đặt lại theo villa đầu tiên
          const exists = this.availableVillas.some(
            (v) => v.villaNumber === this.newBookingForm.villaNumber
          );
          if (!exists) {
            this.newBookingForm.villaNumber = this.availableVillas[0].villaNumber;
          }
          this.calculateTotalAmount();
        }
      },
      error: (err) => {
        console.warn('Không thể tải danh sách Villa cho modal đặt phòng:', err);
      },
    });
  }

  calculateNights(): number {
    if (!this.newBookingForm.checkInDate || !this.newBookingForm.checkOutDate) {
      return 1;
    }
    const checkIn = new Date(this.newBookingForm.checkInDate);
    const checkOut = new Date(this.newBookingForm.checkOutDate);
    const diffTime = checkOut.getTime() - checkIn.getTime();
    const diffDays = Math.round(diffTime / (1000 * 60 * 60 * 24));
    return diffDays > 0 ? diffDays : 1;
  }

  getSelectedVillaPrice(): number {
    const selected = this.availableVillas.find(
      (v) => v.villaNumber === this.newBookingForm.villaNumber
    );
    return selected?.basePrice || selected?.dynamicPrice || 5000000;
  }

  calculateTotalAmount(): void {
    const pricePerNight = this.getSelectedVillaPrice();
    const nights = this.calculateNights();
    this.newBookingForm.amount = pricePerNight * nights;
  }

  onVillaChange(): void {
    this.calculateTotalAmount();
  }

  onDateChange(): void {
    this.calculateTotalAmount();
  }

  setupRealTimeUpdates(): void {
    try {
      const eventSource = new EventSource(`${environment.apiUrl}/notifications/stream`);
      eventSource.addEventListener('status_update', (event) => {
        if (event.data === 'REFRESH_GANTT') {
          this.loadGantt();
          this.loadBookings();
        }
      });
      eventSource.onerror = (err) => {
        console.warn('SSE stream error/offline:', err);
      };
    } catch (e) {
      console.warn('Cannot init SSE eventSource:', e);
    }
  }

  formatDateIso(d: Date): string {
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  formatDateVi(d: Date): string {
    const day = String(d.getDate()).padStart(2, '0');
    const month = String(d.getMonth() + 1).padStart(2, '0');
    return `${day}/${month}`;
  }

  buildTimelineHeaders(): void {
    const headers: TimelineHeader[] = [];
    const today = new Date();
    const todayIso = this.formatDateIso(today);

    for (let i = 0; i < 7; i++) {
      const d = new Date(this.ganttStartDate);
      d.setDate(d.getDate() + i);
      const iso = this.formatDateIso(d);
      const isToday = iso === todayIso;

      const daysOfWeek = ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7'];
      const dayName = daysOfWeek[d.getDay()];
      const dayMonth = this.formatDateVi(d);
      const label = (isToday ? 'H.Nay' : dayName) + ` (${dayMonth})`;

      headers.push({
        label,
        dayName,
        fullDate: `${dayMonth}/${d.getFullYear()}`,
        isToday,
      });
    }

    this.timelineHeaders = headers;
    const startStr = headers[0]?.fullDate || '';
    const endStr = headers[6]?.fullDate || '';
    this.dateRangeLabel = `${startStr} - ${endStr}`;
  }

  prevWeek(): void {
    const d = new Date(this.ganttStartDate);
    d.setDate(d.getDate() - 7);
    this.ganttStartDate = d;
    this.loadGantt();
  }

  nextWeek(): void {
    const d = new Date(this.ganttStartDate);
    d.setDate(d.getDate() + 7);
    this.ganttStartDate = d;
    this.loadGantt();
  }

  todayWeek(): void {
    this.ganttStartDate = new Date();
    this.loadGantt();
  }

  loadBookings(): void {
    this.isLoadingBookings = true;
    this.bookingService.getBookings({ size: 200 }).subscribe({
      next: (res) => {
        this.isLoadingBookings = false;
        if (res && res.content && res.content.length > 0) {
          this.bookings = res.content.map((b: AdminBookingItem) => {
            const raw = b as unknown as Record<string, unknown>;
            const id = Number(raw['id'] || b.id || 0);
            const code = String(raw['bookingCode'] || b.bookingCode || `#BK-${id}`);
            const dateParts =
              typeof raw['bookingDateFormatted'] === 'string'
                ? (raw['bookingDateFormatted'] as string).split(' ')
                : [];
            const bookingDate = dateParts[0] || String(raw['bookingDate'] || b.bookingDate || '');
            const bookingTime = dateParts[1] || '';
            const guestName = String(raw['guestName'] || b.guestName || 'Khách lưu trú');
            const guestAvatar =
              typeof raw['avatarUrl'] === 'string' && (raw['avatarUrl'] as string).length > 5
                ? (raw['avatarUrl'] as string)
                : '/assets/images/staff/avatar-nam.jpg';
            const country = String(raw['guestCountry'] || 'Việt Nam');
            const phone = String(raw['guestPhone'] || b.guestPhone || 'Chưa cập nhật');
            const guestEmail = String(raw['guestEmail'] || b.guestEmail || '');
            const vipTier = String(raw['guestTier'] || raw['vipTier'] || 'Thành viên');
            const vipBadgeClass = String(
              raw['tierBadgeColor'] ||
                raw['vipBadgeClass'] ||
                'bg-slate-100 text-slate-700 border border-slate-200'
            );
            const villaNumber = String(raw['villaNumber'] || b.villaNumber || 'Chưa gán');
            const villaName = String(
              raw['villaTypeName'] || raw['roomTypeName'] || b.villaName || 'Villa Resort'
            );
            const checkInDate = String(
              raw['checkInFormatted'] || raw['checkInDate'] || b.checkInDate || ''
            );
            const checkOutDate = String(
              raw['checkOutFormatted'] || raw['checkOutDate'] || b.checkOutDate || ''
            );
            const nights = Number(raw['nights'] || b.nights || 1);
            const occupants = String(raw['guestSummary'] || `${nights} đêm`);
            const channel = String(raw['channel'] || 'Website Trực Tiếp');
            const channelClass = String(
              raw['channelBadgeColor'] || 'bg-slate-50 text-slate-700 border border-slate-200'
            );
            const totalAmountNum = Number(raw['totalAmount'] || b.totalAmountVnd || 0);
            const totalAmount = String(
              raw['totalAmountDisplay'] ||
                raw['totalAmountFormatted'] ||
                (totalAmountNum > 0 ? `${totalAmountNum.toLocaleString('vi-VN')}₫` : '0₫')
            );
            const paymentNote = String(
              raw['paymentStatusDisplay'] ||
                (raw['isFullyPaid'] ? 'Đã thanh toán' : 'Chờ thanh toán')
            );
            const isPaid =
              Boolean(raw['isFullyPaid']) ||
              paymentNote.toLowerCase().includes('đã thanh toán');
            const paymentClass = isPaid ? 'text-emerald-700 font-bold' : 'text-amber-700 font-bold';
            const paymentMethod = String(raw['paymentMethod'] || '');

            const service1 = raw['extraServiceName'] ? String(raw['extraServiceName']) : '';
            const service1Icon = raw['extraServiceIcon']
              ? String(raw['extraServiceIcon'])
              : 'room_service';
            const butler = raw['assignedButler'] ? String(raw['assignedButler']) : '';

            const statusCode = String(raw['statusCode'] || 'PENDING');
            const status = String(
              raw['statusLabel'] ||
                (statusCode === 'CONFIRMED'
                  ? 'Đã Xác Nhận'
                  : statusCode === 'CHECKED_IN'
                    ? 'Đang Lưu Trú'
                    : statusCode === 'PENDING'
                      ? 'Chờ Thanh Toán'
                      : statusCode === 'CHECKED_OUT'
                        ? 'Đã Trả Phòng'
                        : statusCode === 'CANCELLED'
                          ? 'Đã Hủy'
                          : statusCode)
            );
            const statusClass = String(
              raw['statusBadgeColor'] ||
                (statusCode === 'CHECKED_IN'
                  ? 'bg-sky-50 text-sky-700 border-sky-300'
                  : statusCode === 'CONFIRMED'
                    ? 'bg-emerald-50 text-emerald-700 border-emerald-300'
                    : statusCode === 'PENDING'
                      ? 'bg-amber-50 text-amber-700 border-amber-300'
                      : statusCode === 'CHECKED_OUT'
                        ? 'bg-slate-100 text-slate-700 border-slate-300'
                        : 'bg-rose-50 text-rose-700 border-rose-300')
            );
            const statusDot =
              statusCode === 'CHECKED_IN'
                ? 'bg-sky-500'
                : statusCode === 'CONFIRMED'
                  ? 'bg-emerald-500'
                  : statusCode === 'PENDING'
                    ? 'bg-amber-500'
                    : statusCode === 'CHECKED_OUT'
                      ? 'bg-slate-400'
                      : 'bg-rose-500';

            const note = String(raw['note'] || '');

            return {
              id,
              code,
              bookingDate,
              bookingTime,
              guestName,
              guestAvatar,
              guestEmail,
              country,
              phone,
              vipTier,
              vipBadgeClass,
              villaNumber,
              villaName,
              checkInDate,
              checkOutDate,
              nights,
              occupants,
              channel,
              channelClass,
              totalAmount,
              totalAmountNum,
              paymentNote,
              paymentMethod,
              paymentClass,
              service1,
              service1Icon,
              butler,
              status,
              statusCode,
              statusClass,
              statusDot,
              note,
            };
          });
        } else {
          this.bookings = [];
        }
        this.applyFilter();
      },
      error: (err) => {
        console.error('Error loading admin bookings:', err);
        this.isLoadingBookings = false;
        this.bookings = [];
        this.applyFilter();
      },
    });
  }

  loadGantt(): void {
    this.buildTimelineHeaders();
    this.isLoadingGantt = true;
    const startDateStr = this.formatDateIso(this.ganttStartDate);

    this.bookingService.getGanttAvailability(startDateStr, 7).subscribe({
      next: (data) => {
        this.isLoadingGantt = false;
        if (data && data.length > 0) {
          this.ganttList = data.map((item: GanttRoomAvailability) => {
            const rawItem = item as unknown as Record<string, unknown>;
            const rawSlots = (rawItem['daySlots'] ||
              rawItem['dailySlots'] ||
              item.dailySlots ||
              []) as Record<string, unknown>[];

            const slots: GanttDaySlotItem[] = [];
            for (let i = 0; i < 7; i++) {
              const th = this.timelineHeaders[i];
              const s = rawSlots[i] || {};
              const status = String(s['status'] || 'AVAILABLE');
              const guestName = s['guestName'] ? String(s['guestName']) : null;
              const blockLabel = s['blockLabel'] ? String(s['blockLabel']) : null;

              let label = 'Trống';
              let cssClass = 'bg-slate-50 text-slate-400 hover:bg-slate-100';

              if (status === 'OCCUPIED') {
                label = blockLabel || guestName || 'Đang ở';
                cssClass = 'bg-[#0284c7] text-white font-bold shadow-xs';
              } else if (status === 'CONFIRMED') {
                label = blockLabel || guestName || 'Đã đặt';
                cssClass = 'bg-emerald-600 text-white font-bold shadow-xs';
              } else if (status === 'CLEANING') {
                label = 'Đang dọn';
                cssClass = 'bg-amber-100 text-amber-900 border border-amber-300 font-bold';
              } else if (status === 'MAINTENANCE') {
                label = 'Bảo trì';
                cssClass = 'bg-rose-100 text-rose-700 border border-rose-300 font-bold';
              }

              const bookingId = s['bookingId'] ? Number(s['bookingId']) : undefined;
              const bookingCode = s['bookingCode'] ? String(s['bookingCode']) : undefined;

              slots.push({
                dateLabel: th?.label || '',
                fullDate: th?.fullDate || '',
                status,
                label,
                class: cssClass,
                tooltip: `${label} (${th?.fullDate})${bookingCode ? ' - Nhấp để xem hồ sơ' : ''}`,
                bookingId,
                bookingCode,
                guestName: guestName || undefined,
              });
            }

            const statusTag = String(rawItem['statusTag'] || item.statusTag || 'Sẵn Sàng');
            const statusTagClass = String(
              rawItem['statusTagClass'] ||
                item.statusTagClass ||
                'bg-emerald-50 text-emerald-700 border-emerald-200'
            );
            const statusDot =
              statusTag === 'Đang Có Khách'
                ? 'bg-sky-500'
                : statusTag === 'Đã Đặt'
                  ? 'bg-amber-500'
                  : statusTag === 'Sẵn Sàng'
                    ? 'bg-emerald-500'
                    : statusTag === 'Đang Dọn'
                      ? 'bg-amber-500'
                      : 'bg-rose-500';

            const zoneName = String(
              rawItem['zoneName'] || rawItem['zone'] || item.zoneName || item.zone || ''
            );

            return {
              villaNumber: String(
                rawItem['villaNumber'] || item.villaNumber || item.roomNumber || 'Villa'
              ),
              villaName: String(
                rawItem['roomTypeName'] || item.villaTypeName || item.roomName || 'Villa Resort'
              ),
              zoneName,
              status: statusTag,
              statusClass: statusTagClass,
              statusDot,
              slots,
            };
          });
        } else {
          this.ganttList = [];
        }
        this.applyGanttFilter();
      },
      error: (err) => {
        console.error('Error loading gantt:', err);
        this.isLoadingGantt = false;
        this.ganttList = [];
        this.applyGanttFilter();
      },
    });
  }

  applyGanttFilter(): void {
    let list = [...this.ganttList];

    // Filter by Zone
    if (this.selectedGanttZone !== 'ALL') {
      const z = this.selectedGanttZone.toLowerCase();
      list = list.filter(
        (g) =>
          (g.zoneName && g.zoneName.toLowerCase().includes(z)) ||
          g.villaNumber.toLowerCase().includes(z) ||
          (z.includes('ngọc trai') && g.villaNumber.startsWith('NT')) ||
          (z.includes('sao biển') && g.villaNumber.startsWith('SB')) ||
          (z.includes('san hô') && g.villaNumber.startsWith('SH'))
      );
    }

    // Filter by Status
    if (this.selectedGanttStatus !== 'ALL') {
      list = list.filter((g) => g.status === this.selectedGanttStatus);
    }

    // Search query
    if (this.ganttSearch.trim()) {
      const q = this.ganttSearch.toLowerCase().trim();
      list = list.filter(
        (g) =>
          g.villaNumber.toLowerCase().includes(q) ||
          g.villaName.toLowerCase().includes(q) ||
          (g.zoneName && g.zoneName.toLowerCase().includes(q)) ||
          g.status.toLowerCase().includes(q) ||
          g.slots.some((s) => s.guestName && s.guestName.toLowerCase().includes(q))
      );
    }

    this.filteredGanttList = list;
  }

  onGanttSlotClick(slot: GanttDaySlotItem): void {
    if (slot.bookingCode || slot.bookingId) {
      const found = this.bookings.find(
        (b) =>
          (slot.bookingCode && b.code === slot.bookingCode) ||
          (slot.bookingId && b.id === slot.bookingId)
      );
      if (found) {
        this.openDetail(found);
      }
    }
  }

  setQuickFilter(filter: string): void {
    this.activeQuickFilter = filter;
    this.currentPage = 1;
    this.applyFilter();
  }

  applyFilter(): void {
    let list = [...this.bookings];

    // Quick Filter by status
    if (this.activeQuickFilter !== 'ALL') {
      list = list.filter((b) => b.statusCode === this.activeQuickFilter);
    }

    // Room Category Filter
    if (this.selectedRoomCategory !== 'ALL') {
      list = list.filter(
        (b) =>
          b.villaName.toLowerCase().includes(this.selectedRoomCategory.toLowerCase()) ||
          b.villaNumber.toLowerCase().includes(this.selectedRoomCategory.toLowerCase())
      );
    }

    // Status dropdown filter
    if (this.selectedStatus !== 'ALL') {
      list = list.filter((b) => b.statusCode === this.selectedStatus);
    }

    // Text Search
    if (this.searchTerm.trim()) {
      const q = this.searchTerm.toLowerCase().trim();
      list = list.filter(
        (b) =>
          b.code.toLowerCase().includes(q) ||
          b.guestName.toLowerCase().includes(q) ||
          b.phone.toLowerCase().includes(q) ||
          b.guestEmail.toLowerCase().includes(q) ||
          b.villaNumber.toLowerCase().includes(q)
      );
    }

    // Sorting
    if (this.selectedSort === 'checkInAsc') {
      list.sort((a, b) => a.checkInDate.localeCompare(b.checkInDate));
    } else if (this.selectedSort === 'checkInDesc') {
      list.sort((a, b) => b.checkInDate.localeCompare(a.checkInDate));
    } else if (this.selectedSort === 'amountDesc') {
      list.sort((a, b) => b.totalAmountNum - a.totalAmountNum);
    } else if (this.selectedSort === 'newest') {
      list.sort((a, b) => b.id - a.id);
    }

    this.filteredBookings = list;
    this.totalFilteredElements = list.length;
    this.updatePagination();
  }

  onSearch(): void {
    this.currentPage = 1;
    this.applyFilter();
  }

  onCategoryChange(): void {
    this.currentPage = 1;
    this.applyFilter();
  }

  onStatusChange(): void {
    this.currentPage = 1;
    this.applyFilter();
  }

  onSortChange(): void {
    this.currentPage = 1;
    this.applyFilter();
  }

  updatePagination(): void {
    const start = (this.currentPage - 1) * this.pageSize;
    this.paginatedBookings = this.filteredBookings.slice(start, start + this.pageSize);
  }

  goToPage(p: number): void {
    if (p >= 1 && p <= this.totalPages) {
      this.currentPage = p;
      this.updatePagination();
    }
  }

  openDetail(booking: LuxuryBooking): void {
    this.selectedBooking = booking;
    this.showDetailModal = true;
    this.consumptions = [];
    this.isLoadingConsumptions = true;

    this.hkService.getPendingConsumptions(booking.id).subscribe({
      next: (list) => {
        this.consumptions = list;
        this.isLoadingConsumptions = false;
      },
      error: () => {
        this.isLoadingConsumptions = false;
      },
    });
  }

  get pendingConsumptions(): RoomConsumptionRecord[] {
    return this.consumptions.filter((c) => c.status === 'PENDING_RECEPTION_APPROVAL');
  }

  get pendingConsumptionsCount(): number {
    return this.pendingConsumptions.length;
  }

  get pendingConsumptionsTotal(): number {
    return this.pendingConsumptions.reduce((sum, c) => sum + (c.totalPrice || 0), 0);
  }

  get approvedConsumptionsTotal(): number {
    return this.consumptions
      .filter((c) => c.status === 'APPROVED_CHARGED')
      .reduce((sum, c) => sum + (c.totalPrice || 0), 0);
  }

  approveConsumptionItem(item: RoomConsumptionRecord): void {
    this.hkService.approveConsumption(item.id).subscribe({
      next: (updated) => {
        item.status = updated.status;
        this.showToast(
          `Đã tính phí món "${item.itemName}" (${item.totalPrice.toLocaleString('vi-VN')}₫) vào hóa đơn!`
        );
      },
      error: () => this.showToast('Lỗi khi tính phí vào hóa đơn'),
    });
  }

  waiveConsumptionItem(item: RoomConsumptionRecord): void {
    const reason =
      prompt('Nhập lý do miễn phí / bỏ qua (VD: Quà tặng VIP, khách không dùng):') ||
      'Miễn phí theo chính sách CSKH';
    this.hkService.waiveConsumption(item.id, reason).subscribe({
      next: (updated) => {
        item.status = updated.status;
        this.showToast(`Đã miễn phí món "${item.itemName}"!`);
      },
      error: () => this.showToast('Lỗi khi cập nhật miễn phí'),
    });
  }

  performCheckOut(b: LuxuryBooking): void {
    if (b.statusCode !== 'CHECKED_IN') {
      if (b.statusCode === 'CONFIRMED') {
        if (
          confirm(
            `Đơn ${b.code} đang ở trạng thái "Đã Xác Nhận" (Chưa làm thủ tục nhận phòng). Bạn có muốn xác nhận Check-in và tiến hành Trả phòng ngay không?`
          )
        ) {
          this.bookingService.checkInBooking(b.id).subscribe({
            next: () => {
              this.proceedCheckOutApi(b);
            },
            error: (err) => {
              const errorMsg =
                err?.error?.message || `Lỗi khi làm thủ tục Check-in cho ${b.guestName}`;
              alert(errorMsg);
            },
          });
          return;
        }
        return;
      } else {
        alert(
          `Đơn đặt phòng đang ở trạng thái "${b.status}". Chỉ đơn "Đang Lưu Trú (Checked-In)" mới thực hiện Trả phòng được.`
        );
        return;
      }
    }

    if (this.pendingConsumptionsCount > 0 && this.selectedBooking?.id === b.id) {
      if (
        !confirm(
          `CẢNH BÁO: Còn ${this.pendingConsumptionsCount} món Minibar / Hỏng hóc chưa chốt tính phí (${this.pendingConsumptionsTotal.toLocaleString('vi-VN')}₫). Bạn có chắc chắn muốn bỏ qua và làm thủ tục trả phòng không?`
        )
      ) {
        return;
      }
    }

    this.proceedCheckOutApi(b);
  }

  private proceedCheckOutApi(b: LuxuryBooking): void {
    this.bookingService.checkOutBooking(b.id).subscribe({
      next: () => {
        this.showToast(`Đã hoàn tất thủ tục trả phòng cho khách ${b.guestName} (${b.villaNumber})!`);
        this.closeDetail();
        this.loadBookings();
        this.loadGantt();
      },
      error: (err) => {
        const errorMsg =
          err?.error?.message || `Lỗi khi làm thủ tục trả phòng cho ${b.guestName}`;
        alert(errorMsg);
      },
    });
  }

  closeDetail(): void {
    this.showDetailModal = false;
    this.selectedBooking = null;
    this.consumptions = [];
  }

  openCheckInModal(b: LuxuryBooking): void {
    this.bookingToCheckIn = b;
    this.checkInInputCode = '';
    this.checkInErrorMessage = '';
    this.isVerifyingCheckIn = false;
    this.showCheckInModal = true;
  }

  closeCheckInModal(): void {
    this.showCheckInModal = false;
    this.bookingToCheckIn = null;
    this.checkInInputCode = '';
    this.checkInErrorMessage = '';
    this.isVerifyingCheckIn = false;
  }

  confirmCheckIn(): void {
    if (!this.bookingToCheckIn) return;
    const b = this.bookingToCheckIn;
    const input = (this.checkInInputCode || '').trim();

    if (!input) {
      this.checkInErrorMessage = 'Vui lòng nhập mã Check-in hoặc số điện thoại của khách hàng!';
      return;
    }

    // Client-side quick check
    const cleanInput = input.replace(/[\s\-\.]/g, '');
    const cleanBookingCode = (b.code || '').replace(/[\s\-\.]/g, '');
    
    // Normalize phone
    const cleanPhone = (b.phone || '').replace(/[^0-9]/g, '');
    const normPhone = cleanPhone.startsWith('84') ? '0' + cleanPhone.substring(2) : cleanPhone;
    const cleanInputPhone = cleanInput.replace(/[^0-9]/g, '');
    const normInputPhone = cleanInputPhone.startsWith('84') ? '0' + cleanInputPhone.substring(2) : cleanInputPhone;

    let matched = false;

    // 1. So khớp mã Check-in đầy đủ
    if (
      cleanBookingCode.toLowerCase() === cleanInput.toLowerCase() ||
      (b.code && b.code.toLowerCase() === input.toLowerCase())
    ) {
      matched = true;
    } else if (b.code && b.code.includes('-')) {
      // Cho phép nhập phần đuôi của mã Check-in (ví dụ phần ngẫu nhiên)
      const suffix = b.code.substring(b.code.lastIndexOf('-') + 1);
      if (suffix.toLowerCase() === input.toLowerCase() || suffix.toLowerCase() === cleanInput.toLowerCase()) {
        matched = true;
      }
    }

    // 2. So khớp số điện thoại của khách
    if (!matched && normPhone && normInputPhone) {
      if (normPhone === normInputPhone || normPhone.endsWith(normInputPhone) || normInputPhone.endsWith(normPhone)) {
        matched = true;
      }
    }

    if (!matched) {
      this.checkInErrorMessage = 'Mã Check-in hoặc số điện thoại không trùng khớp với đơn này! Vui lòng kiểm tra lại với khách hàng.';
      return;
    }

    // Đã khớp -> Gọi API xác nhận check-in
    this.isVerifyingCheckIn = true;
    this.checkInErrorMessage = '';

    this.bookingService.checkInBooking(b.id, input).subscribe({
      next: () => {
        this.isVerifyingCheckIn = false;
        this.showToast(`Xác thực thành công! Đã hoàn tất thủ tục nhận phòng cho khách ${b.guestName} (${b.villaNumber})!`);
        this.closeCheckInModal();
        if (this.selectedBooking && this.selectedBooking.id === b.id) {
          this.closeDetail();
        }
        this.loadBookings();
        this.loadGantt();
      },
      error: (err) => {
        this.isVerifyingCheckIn = false;
        const errorMsg =
          err?.error?.message || `Lỗi khi lưu trạng thái Check-in cho ${b.guestName}`;
        this.checkInErrorMessage = errorMsg;
      },
    });
  }

  performCheckIn(b: LuxuryBooking): void {
    this.openCheckInModal(b);
  }

  submitCreate(): void {
    if (!this.newBookingForm.guestName.trim()) {
      alert('Vui lòng nhập họ tên khách hàng!');
      return;
    }
    if (!this.newBookingForm.checkInDate || !this.newBookingForm.checkOutDate) {
      alert('Vui lòng chọn ngày nhận phòng và ngày trả phòng!');
      return;
    }

    this.bookingService
      .createDirectBooking({
        guestName: this.newBookingForm.guestName,
        guestPhone: this.newBookingForm.phone,
        guestEmail: this.newBookingForm.email,
        villaNumber: this.newBookingForm.villaNumber,
        checkInDate: this.newBookingForm.checkInDate,
        checkOutDate: this.newBookingForm.checkOutDate,
        amount: this.newBookingForm.amount,
        paymentMethod: this.newBookingForm.paymentMethod,
        note: this.newBookingForm.requests,
      })
      .subscribe({
        next: () => {
          this.showToast(`Đã tạo thành công đơn đặt phòng cho khách ${this.newBookingForm.guestName}!`);
          this.showCreateModal = false;
          this.newBookingForm.guestName = '';
          this.newBookingForm.phone = '';
          this.newBookingForm.email = '';
          this.newBookingForm.requests = '';
          this.loadBookings();
          this.loadGantt();
        },
        error: (err) => {
          const msg = err?.error?.message || 'Lỗi khi tạo đơn đặt phòng trực tiếp';
          alert(msg);
        },
      });
  }

  showToast(msg: string): void {
    this.toastMessage = msg;
    setTimeout(() => {
      this.toastMessage = null;
    }, 4500);
  }

  importOta(): void {
    alert('Đang đồng bộ đặt phòng từ các cổng OTA (Agoda, Booking.com, Traveloka)...');
  }

  exportTodayCheckIn(): void {
    alert('Đang xuất báo cáo danh sách khách Check-in hôm nay từ hệ thống PMS...');
  }
}
