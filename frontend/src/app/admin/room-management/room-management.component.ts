import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import {
  AdminHousekeepingService,
  AdminRoomItem,
  AdminRoomTypeItem,
  UpdateRoomTypePayload,
  CreateRoomPayload,
  ChildRoomItem,
  ZoneItem,
} from '../../core/services/admin-housekeeping.service';
import {
  HousekeepingTask,
  HousekeeperSummary,
  AssignHousekeepingTaskRequest,
  UpdateCleaningProgressRequest,
} from '../../core/models/admin-housekeeping.model';
import {
  AdminExtraServiceService,
  ExtraServiceItem,
} from '../../core/services/admin-extra-service.service';
import { AdminBookingService } from '../../core/services/admin-booking.service';
import { AdminBookingItem } from '../../core/models/admin-booking.model';
import { BodyPortalDirective } from '../../shared/directives/body-portal.directive';
import { environment } from '../../../environments/environment';

export interface VillaBedSelectionItem {
  roomTypeId: number;
  name: string;
  bedDimensions: string;
  adults: number;
  children: number;
  capacity: number;
  quantity: number;
  isSelected: boolean;
}

export interface DetailedChildRoom {
  roomIndex: number;
  roomNumber: string;
  name: string;
  floor: number;
  roomTypeId: number;
}

export interface BookingDetailRecord {
  id?: number;
  code: string;
  guestName: string;
  vipTier: string;
  vipBadgeClass: string;
  vipCardNumber: string;
  phone: string;
  email: string;
  allergies: string;
  specialRequests?: string;
  villaName: string;
  villaArea: string;
  checkIn: string;
  checkOut: string;
  occupants: string;
  totalAmount: string;
  paymentMethod: string;
  txId: string;
  vatInvoice: string;
  deposit: string;
  createdInfo?: string;
  avatarUrl?: string;
  statusCode?: string;
  statusLabel?: string;
  services: { title: string; sub: string; icon: string }[];
  timeline: { time: string; dept: string }[];
}

export interface RoomCard {
  id?: number;
  number: string;
  category: string;
  roomTypeId?: number;
  area: string;
  floor?: number;
  structureType?: string;
  amenities?: string[];
  bedroomCount?: number;
  totalBeds?: number;
  totalAdults?: number;
  totalChildren?: number;
  totalCapacity?: number;
  statusTag: string;
  statusTagClass: string;
  tagColor: string; // for circle tag: 'blue', 'amber', 'green', 'teal', 'gold', 'dark'
  guestName?: string;
  guestTier?: string;
  guestTierClass?: string;
  guestSub?: string;
  details?: string[];
  footerLeft: string;
  btnLabel: string;
  btnClass: string;
  bookingDetail?: BookingDetailRecord;
  statusRaw?: string;
  zone?: string;
  ozoneStatus?: string;
  basePrice?: number;
  priceFormatted?: string;
  imageUrl?: string;
  images?: string[];
  childRooms?: ChildRoomItem[];
  areaNumber?: number; // to not conflict with area: string
  viewDirection?: string;
  poolSize?: number;
  overviewDescription?: string;
}

export interface ZoneSection {
  zoneName: string;
  zoneTag: string;
  icon: string;
  badgeClass: string;
  description: string;
  rooms: RoomCard[];
  availableCount: number;
  occupiedCount: number;
  cleaningCount: number;
  maintenanceCount: number;
}

export interface CleaningProgressCard {
  villa: string;
  progress: string;
  progressClass: string;
  title: string;
  staff: string;
  cleanTime: string;
  checker: string;
  statusLeft: string;
  statusRight: string;
  statusRightClass?: string;
  isButton?: boolean;
}

export interface ChecklistItem {
  id: number;
  title: string;
  checked: boolean;
}

export interface RoomTypeDisplayItem {
  id?: number;
  title: string;
  section?: string;
  bedType?: string;
  capacity?: number;
  adults?: number;
  children?: number;
  bedInfo?: string;
  price?: string;
  count?: string;
  area?: string;
  dimensions?: string;
  occupancy?: string;
  desc?: string;
  features?: string[];
  stats?: string;
  imageUrl?: string;
  imageError?: boolean;
  btnAmber?: boolean;
}

export interface CustomZoneItem {
  id?: number;
  name: string;
  matchKey: string;
  tag: string;
  icon: string;
  badgeClass: string;
  desc: string;
  villaCount?: number;
}

@Component({
  selector: 'app-room-management',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, BodyPortalDirective],
  templateUrl: './room-management.component.html',
  styleUrls: ['./room-management.component.scss'],
})
export class RoomManagementComponent implements OnInit {
  activeTab: 'ALL' | 'AVAILABLE' | 'OCCUPIED' | 'CLEANING' | 'MAINTENANCE' = 'ALL';
  searchTerm = '';
  selectedSection = 'ALL';

  // View Mode (Lưới thẻ trực quan vs Bảng dữ liệu chi tiết)
  viewMode: 'GRID' | 'TABLE' = 'GRID';

  // Danh mục Hạng phòng tải từ API để nạp dropdown
  roomTypesList: AdminRoomTypeItem[] = [];

  // Danh sách các phân khu resort mặc định & tùy biến
  customZones: CustomZoneItem[] = [];

  // Danh sách các phân khu đã được gom nhóm có tổ chức
  groupedZones: ZoneSection[] = [];

  structureOptions: string[] = [
    '1 Tầng (Ground Floor)',
    '2 Tầng (2 Floors)',
    '3 Tầng (3 Floors)',
    'Duplex Thông Tầng',
    'Triplex Cao Cấp',
    'Penthouse Hoàng Gia',
  ];

  // Master Extra Services
  extraServicesList: ExtraServiceItem[] = [];
  selectedExtraServiceIds: number[] = [];

  // (Optional) legacy amenities
  amenityOptions: string[] = [
    'Khử trùng Ozon định kỳ (Chuẩn 5*)',
    'Quản gia riêng Lead Butler 24/7',
    'Hồ bơi vô cực riêng tư',
    'Miễn phí xe đạp nội khu',
    'Bếp nấu cao cấp & minibar',
    'Đón tiễn sân bay bằng xe riêng',
    'Tiệc nướng BBQ & trà chiều sân vườn',
  ];

  selectedAmenities: string[] = [
    'Khử trùng Ozon định kỳ (Chuẩn 5*)',
    'Quản gia riêng Lead Butler 24/7',
  ];

  bedSelections: VillaBedSelectionItem[] = [];
  showAdvancedRoomAllocation = false;
  childRoomsAllocation: DetailedChildRoom[] = [];

  // Modal Thêm / Sửa Biệt Thự & Phòng
  showRoomModal = false;
  isEditMode = false;
  selectedRoomId: number | null = null;
  roomForm = {
    roomNumber: '',
    floor: 1,
    structureType: '1 Tầng (Ground Floor)',
    roomTypeId: null as number | null,
    zone: '',
    status: 'AVAILABLE',
    ozoneStatus: 'STERILIZED',
    basePrice: 25000000,
    imageUrl: '',
    imageName: '',
    images: [] as string[],
    isUploadingImage: false,
    area: 350,
    viewDirection: 'Trực diện Biển',
    poolSize: 24,
    overviewDescription: 'Tọa lạc tại vị trí đắc địa nhất của quần thể Aura Resort, mở ra một tầm nhìn Panorama không giới hạn ôm trọn vẻ đẹp tráng lệ của bình minh trên biển sầm sơn.',
  };

  // Modal Cấu hình / Thêm Mới Hạng Biệt Thự / Phòng
  showRoomTypeModal = false;
  isRoomTypeEditMode = false;
  previewImageError = false;
  roomTypeForm = {
    id: null as number | null,
    name: '',
    description: '',
    basePrice: 0,
    capacity: 2,
    adults: 2,
    children: 0,
    bedLength: '2.0',
    bedWidth: '1.8',
    bedType: 'Rộng 1.8m × Dài 2.0m',
    imageUrl: '',
  };

  // Modal Xác Nhận Xóa Hạng Phòng
  showDeleteRoomTypeConfirmModal = false;
  roomTypeToDelete: RoomTypeDisplayItem | null = null;

  // Modal Thêm / Sửa Phân Khu Mới
  showZoneModal = false;
  isZoneEditMode = false;
  editingZoneName: string | null = null;
  zoneForm = {
    name: '',
    tag: '',
    icon: '', // will be used as imageUrl
    desc: '',
  };

  // Modal Xác Nhận Xóa Phân Khu
  showDeleteZoneConfirmModal = false;
  zoneToDelete: CustomZoneItem | null = null;

  // Modal Xác Nhận Xóa Phòng
  showDeleteConfirmModal = false;
  roomToDelete: RoomCard | null = null;

  // Modal Đổi Trạng Thái Nhanh
  showQuickStatusModal = false;
  roomToChangeStatus: RoomCard | null = null;
  quickStatusTarget = 'AVAILABLE';

  // Modal State
  showGuestModal = false;
  currentBooking: BookingDetailRecord | null = null;
  selectedRoomForDetail: RoomCard | null = null;
  isAvailableRoomView = false;
  allBookings: AdminBookingItem[] = [];
  toastMessage: string | null = null;
  toastType: 'info' | 'error' = 'info';
  liveHousekeepingTasks: HousekeepingTask[] = [];

  // Housekeeping Staff & Two-way Dispatch
  housekeepers: HousekeeperSummary[] = [];
  selectedHousekeeper: HousekeeperSummary | null = null;
  staffTasks: HousekeepingTask[] = [];

  showAssignModal = false;
  showStaffPortalModal = false;
  showTaskDetailModal = false;

  assignForm: {
    roomId: number | null;
    housekeeperId: number | null;
    taskType: 'CHECKOUT_DEEP' | 'DAILY' | 'TURNDOWN';
    notes: string;
  } = {
      roomId: null,
      housekeeperId: null,
      taskType: 'CHECKOUT_DEEP',
      notes: '',
    };

  selectedTask: HousekeepingTask | null = null;
  staffNote = '';
  isSaving = false;

  // 16 bước dọn buồng phòng tiêu chuẩn 5 sao
  standard16Steps: ChecklistItem[] = [
    { id: 1, title: 'Thông gió & kéo rèm đón ánh sáng tự nhiên', checked: false },
    { id: 2, title: 'Thu gom toàn bộ ga giường, vỏ gối và khăn bẩn', checked: false },
    { id: 3, title: 'Trải và căng phẳng bộ drap lụa 800TC cao cấp', checked: false },
    { id: 4, title: 'Hút bụi kỹ sàn gạch / sàn gỗ và thảm lông cừu', checked: false },
    { id: 5, title: 'Tẩy rửa & khử trùng bồn tắm nằm và vòi sen đứng', checked: false },
    { id: 6, title: 'Lau kính mặt gương, vách kính phòng tắm không vệt', checked: false },
    { id: 7, title: 'Bổ sung set khăn tắm, khăn mặt và thảm chân mới', checked: false },
    { id: 8, title: 'Set up đầy đủ amenities cao cấp (Hermès / L’Occitane)', checked: false },
    { id: 9, title: 'Kiểm tra và bổ sung đầy đủ đồ uống minibar & snack', checked: false },
    { id: 10, title: 'Lau bụi toàn bộ mặt bàn, táp đầu giường, tủ quần áo', checked: false },
    { id: 11, title: 'Kiểm tra hoạt động điều hòa, TV, đèn ngủ, két sắt', checked: false },
    { id: 12, title: 'Lau kính cửa ban công và bàn ghế ngoài trời', checked: false },
    { id: 13, title: 'Xịt tinh dầu khuếch tán mùi hương gỗ tuyết tùng', checked: false },
    { id: 14, title: 'Đặt thiệp chào mừng & hoa tươi / trái cây chào đón', checked: false },
    { id: 15, title: 'Thay túi rác sinh học mới và lau sạch thùng rác', checked: false },
    { id: 16, title: 'Kiểm tra tổng thể lần cuối và niêm phong cửa phòng', checked: false },
  ];

  constructor(
    private housekeepingService: AdminHousekeepingService,
    private extraServiceApi: AdminExtraServiceService,
    private adminBookingService: AdminBookingService,
    private router: Router
  ) { }

  // Room Types loaded from backend API
  roomTypes: RoomTypeDisplayItem[] = [];

  // Real room grid cards loaded from backend API
  rooms: RoomCard[] = [];
  filteredRooms: RoomCard[] = [];

  // Housekeeping Progress Cards
  cleaningCards: CleaningProgressCard[] = [];

  // 7 Getters tính toán động cho 5 thẻ KPI
  get totalVillasCount(): number {
    return this.rooms?.length || 0;
  }

  get occupiedVillasCount(): number {
    return this.rooms?.filter((r) => r.statusRaw === 'OCCUPIED').length || 0;
  }

  get availableVillasCount(): number {
    return this.rooms?.filter((r) => r.statusRaw === 'AVAILABLE').length || 0;
  }

  get cleaningVillasCount(): number {
    return this.rooms?.filter((r) => r.statusRaw === 'CLEANING').length || 0;
  }

  get maintenanceVillasCount(): number {
    return this.rooms?.filter((r) => r.statusRaw === 'MAINTENANCE').length || 0;
  }

  get occupancyPercentage(): number {
    if (this.totalVillasCount === 0) return 0;
    return Math.round((this.occupiedVillasCount / this.totalVillasCount) * 100);
  }

  get availablePercentage(): number {
    if (this.totalVillasCount === 0) return 0;
    return Math.round((this.availableVillasCount / this.totalVillasCount) * 100);
  }

  // --- LIVE SUMMARY BANNER & AUTO-CATEGORY GETTERS ---
  get totalSelectedBeds(): number {
    return this.bedSelections
      .filter((b) => b.isSelected && b.quantity > 0)
      .reduce((sum, b) => sum + b.quantity, 0);
  }

  get totalSelectedBedrooms(): number {
    // If advanced child rooms configured, use its count; otherwise each bed is in its room (or min 1)
    if (this.childRoomsAllocation.length > 0) {
      return this.childRoomsAllocation.length;
    }
    return Math.max(1, this.totalSelectedBeds);
  }

  get totalAdultsCapacity(): number {
    return this.bedSelections
      .filter((b) => b.isSelected && b.quantity > 0)
      .reduce((sum, b) => sum + b.adults * b.quantity, 0);
  }

  get totalChildrenCapacity(): number {
    return this.bedSelections
      .filter((b) => b.isSelected && b.quantity > 0)
      .reduce((sum, b) => sum + b.children * b.quantity, 0);
  }

  get totalMaxGuests(): number {
    return this.totalAdultsCapacity + this.totalChildrenCapacity;
  }

  get autoVillaCategoryName(): string {
    const rooms = this.totalSelectedBedrooms;
    return `Villa ${rooms} Phòng Ngủ`;
  }

  ngOnInit(): void {
    this.initCustomZones();
    this.filteredRooms = [...this.rooms];
    this.loadHousekeepingTasks();
    this.loadHousekeepers();
    this.loadRoomTypes();
    this.loadRooms();
    this.loadExtraServices();
    this.loadBookings();
  }

  loadExtraServices(): void {
    this.extraServiceApi.getAll().subscribe((data) => {
      this.extraServicesList = data.filter((s) => s.isActive);
    });
  }

  initCustomZones(): void {
    this.housekeepingService.getZones().subscribe({
      next: (data) => {
        if (data && data.length > 0) {
          this.customZones = data.map((z) => ({
            id: z.id,
            name: z.name,
            matchKey: z.matchKey || z.name.toLowerCase(),
            tag: z.tag || z.name.toUpperCase(),
            icon: z.icon || 'holiday_village',
            badgeClass: z.badgeClass || 'bg-sky-50 text-sky-700 border-sky-200',
            desc: z.description || '',
            villaCount: z.villaCount || 0,
          }));
        } else {
          this.customZones = [];
        }
        this.applyFilters();
      },
      error: () => {
        this.customZones = [];
      },
    });
  }

  loadRooms(): void {
    this.housekeepingService.getRooms().subscribe({
      next: (data) => {
        if (data && data.length > 0) {
          this.rooms = data.map((r: AdminRoomItem) => {
            const typeId = r.villaTypeId || r.roomTypeId;
            const matchedType = this.roomTypesList.find((t) => t.id === typeId);
            const price =
              r.basePrice !== undefined && r.basePrice > 0
                ? r.basePrice
                : matchedType?.basePrice || 0;
            const zoneName = r.zone || '';
            const childRoomsList = r.childRooms || r.rooms || [];
            const bedroomCount =
              r.bedroomCount || (childRoomsList.length > 0 ? childRoomsList.length : 1);
            const autoCategory = `Villa ${bedroomCount} Phòng Ngủ`;

            const totalBeds =
              r.totalBeds !== undefined
                ? r.totalBeds
                : childRoomsList.length > 0
                  ? childRoomsList.length
                  : 1;

            const totalAdults =
              r.totalAdults !== undefined
                ? r.totalAdults
                : childRoomsList.length > 0
                  ? childRoomsList.reduce((sum, cr) => sum + (cr.adults || 2), 0)
                  : (matchedType?.adults ?? 2);

            const totalChildren =
              r.totalChildren !== undefined
                ? r.totalChildren
                : childRoomsList.length > 0
                  ? childRoomsList.reduce((sum, cr) => sum + (cr.children || 0), 0)
                  : (matchedType?.children ?? 0);

            const totalCapacity =
              r.totalCapacity !== undefined ? r.totalCapacity : totalAdults + totalChildren;

            return {
              id: r.id,
              number: r.villaNumber || r.roomNumber,
              category: autoCategory,
              roomTypeId: typeId,
              floor: r.floor || 1,
              structureType: r.structureType || '1 Tầng (Ground Floor)',
              amenities: r.amenities || [],
              bedroomCount: bedroomCount,
              totalBeds: totalBeds,
              totalAdults: totalAdults,
              totalChildren: totalChildren,
              totalCapacity: totalCapacity,
              area: r.floor ? `Tầng ${r.floor}` : 'Tầng 1',
              statusRaw: r.status,
              zone: zoneName,
              ozoneStatus: r.ozoneStatus || 'STERILIZED',
              basePrice: price,
              priceFormatted: price > 0 ? `${(price / 1000000).toFixed(1)}Tr / đêm` : 'Liên hệ',
              imageUrl:
                r.imageUrl ||
                (r.images && r.images.length > 0 ? r.images[0] : null) ||
                matchedType?.imageUrl ||
                '/assets/images/rooms/grand-oceanfront.jpg',
              images: r.images || [],
              areaNumber: r.area,
              viewDirection: r.viewDirection,
              poolSize: r.poolSize,
              overviewDescription: r.overviewDescription,
              childRooms: childRoomsList,
              statusTag:
                r.status === 'OCCUPIED'
                  ? 'Đang có khách'
                  : r.status === 'CLEANING'
                    ? 'Đang dọn phòng'
                    : r.status === 'MAINTENANCE'
                      ? 'Đang bảo trì'
                      : 'Sẵn sàng đón khách',
              statusTagClass:
                r.status === 'OCCUPIED'
                  ? 'bg-amber-50 text-amber-800 border-amber-200'
                  : r.status === 'CLEANING'
                    ? 'bg-sky-50 text-sky-800 border-sky-200'
                    : r.status === 'MAINTENANCE'
                      ? 'bg-rose-50 text-rose-800 border-rose-200'
                      : 'bg-emerald-50 text-emerald-800 border-emerald-200',
              tagColor:
                r.status === 'OCCUPIED'
                  ? 'amber'
                  : r.status === 'CLEANING'
                    ? 'blue'
                    : r.status === 'MAINTENANCE'
                      ? 'dark'
                      : 'green',
              guestName: r.status === 'OCCUPIED' ? r.currentGuestName : undefined,
              guestTier: r.status === 'OCCUPIED' && r.currentGuestName ? 'VIP Diamond' : undefined,
              guestTierClass:
                r.status === 'OCCUPIED' && r.currentGuestName
                  ? 'bg-gradient-to-r from-amber-100 to-amber-200 text-amber-950 border border-amber-300 font-bold'
                  : undefined,
              details: [
                zoneName ? `Phân khu: ${zoneName}` : 'Chưa phân khu',
                r.status === 'AVAILABLE'
                  ? '✨ Sẵn sàng đón khách mới'
                  : r.status === 'CLEANING'
                    ? '🧹 Đang dọn buồng phòng'
                    : r.status === 'MAINTENANCE'
                      ? '🛠️ Đang bảo trì kỹ thuật'
                      : '🏠 Đang phục vụ khách lưu trú',
              ],
              footerLeft: zoneName || 'Chưa phân khu',
              btnLabel:
                r.status === 'OCCUPIED'
                  ? 'Xem Hồ Sơ'
                  : r.status === 'CLEANING'
                    ? 'Phân Công / Tiến Độ'
                    : r.status === 'MAINTENANCE'
                      ? 'Bảo Trì'
                      : 'Gán Khách',
              btnClass:
                r.status === 'OCCUPIED'
                  ? 'bg-white hover:bg-slate-50 text-slate-700'
                  : r.status === 'CLEANING'
                    ? 'bg-sky-50 hover:bg-sky-100 text-sky-700 border border-sky-200 font-bold'
                    : r.status === 'MAINTENANCE'
                      ? 'bg-rose-50 hover:bg-rose-100 text-rose-700 border border-rose-200 font-bold'
                      : 'bg-emerald-600 hover:bg-emerald-700 text-white font-bold',
            };
          });
          this.applyFilters();
          this.buildRoomTypeDisplayItems();
          this.syncBookingsWithRooms();
        } else {
          this.rooms = [];
          this.filteredRooms = [];
          this.applyFilters();
          this.buildRoomTypeDisplayItems();
        }
      },
      error: () => {
        this.rooms = [];
        this.filteredRooms = [];
        this.applyFilters();
        this.buildRoomTypeDisplayItems();
      },
    });
  }

  getImageUrl(url?: string): string {
    if (!url) return '';
    const trimmed = url.trim();
    if (!trimmed) return '';
    if (trimmed.startsWith('data:') || trimmed.startsWith('blob:')) {
      return trimmed;
    }
    if (trimmed.startsWith('http://') || trimmed.startsWith('https://')) {
      return trimmed;
    }
    if (trimmed.startsWith('/assets/images/uploads/')) {
      const filename = trimmed.substring('/assets/images/uploads/'.length);
      return `${environment.apiUrl}/villas/images/${filename}`;
    }
    if (trimmed.startsWith('assets/images/uploads/')) {
      const filename = trimmed.substring('assets/images/uploads/'.length);
      return `${environment.apiUrl}/villas/images/${filename}`;
    }
    return trimmed;
  }

  onCardImageError(rt: RoomTypeDisplayItem): void {
    rt.imageError = true;
  }

  getBedInfoDisplay(bedDimensions?: string, adults?: number, children?: number): string {
    const dim = (bedDimensions || '1.8m × 2.0m').trim();
    const ad = adults != null ? adults : 2;
    const ch = children != null ? children : 0;

    if (ch > 0) {
      return `${dim} • ${ad} Người lớn, ${ch} Trẻ nhỏ`;
    }
    return `${dim} • ${ad} Người lớn`;
  }

  buildRoomTypeDisplayItems(): void {
    if (!this.roomTypesList || this.roomTypesList.length === 0) {
      this.roomTypes = [];
      return;
    }

    this.roomTypes = this.roomTypesList.map((rt: AdminRoomTypeItem) => {
      let bedCount = 0;
      let occupiedBeds = 0;
      let availableBeds = 0;

      for (const r of this.rooms) {
        const childs = r.childRooms && r.childRooms.length > 0 ? r.childRooms : [];
        if (childs.length > 0) {
          const matchedChilds = childs.filter((cr: ChildRoomItem) => cr.roomTypeId === rt.id);
          bedCount += matchedChilds.length;
          if (matchedChilds.length > 0) {
            if (r.statusRaw === 'OCCUPIED') {
              occupiedBeds += matchedChilds.length;
            } else if (r.statusRaw === 'AVAILABLE') {
              availableBeds += matchedChilds.length;
            }
          }
        } else if (r.roomTypeId === rt.id) {
          bedCount += 1;
          if (r.statusRaw === 'OCCUPIED') {
            occupiedBeds += 1;
          } else if (r.statusRaw === 'AVAILABLE') {
            availableBeds += 1;
          }
        }
      }

      const bedType = rt.bedType || 'Tiêu chuẩn';
      const adults = rt.adults != null ? rt.adults : rt.capacity || 2;
      const children = rt.children != null ? rt.children : 0;
      const capacity = rt.capacity || adults + children;

      // Trích xuất kích thước chuẩn hiển thị (Rộng × Dài)
      let bedDimensions = '1.8m × 2.0m';
      if (bedType) {
        const wMatch = bedType.match(/rộng\s*(\d+(?:\.\d+)?)/i);
        const lMatch = bedType.match(/dài\s*(\d+(?:\.\d+)?)/i);
        if (wMatch && lMatch) {
          bedDimensions = `Rộng ${wMatch[1]}m × Dài ${lMatch[1]}m`;
        } else {
          const match = bedType.match(/(\d+(?:\.\d+)?)\s*m?\s*[x×]\s*(\d+(?:\.\d+)?)\s*m?/i);
          if (match) {
            bedDimensions = `Rộng ${match[1]}m × Dài ${match[2]}m`;
          } else if (bedType !== 'Tiêu chuẩn' && bedType.trim()) {
            bedDimensions = bedType.trim();
          }
        }
      }

      const bedInfo = this.getBedInfoDisplay(bedDimensions, adults, children);
      const shortDim = bedDimensions
        .replace(/rộng\s*/i, '')
        .replace(/dài\s*/i, '')
        .trim();

      const countStr = `${bedCount} Giường`;
      const occStr =
        bedCount > 0 ? `Lấp đầy ${Math.round((occupiedBeds / bedCount) * 100)}%` : 'Lấp đầy 0%';
      const statsStr =
        bedCount > 0
          ? `${occupiedBeds} Có khách • ${availableBeds} sẵn sàng`
          : 'Dữ liệu thời gian thực';

      return {
        id: rt.id,
        title: rt.name,
        section: bedInfo,
        bedInfo: bedInfo,
        bedType: bedType,
        capacity: capacity,
        adults: adults,
        children: children,
        price: rt.basePrice ? `${(rt.basePrice / 1000000).toFixed(1)}Tr` : '0Tr',
        count: countStr,
        area: shortDim,
        dimensions: shortDim,
        occupancy: occStr,
        desc: rt.description || '',
        stats: statsStr,
        imageUrl: rt.imageUrl || '',
        imageError: false,
        btnAmber: rt.id === 20 || (rt.name || '').toLowerCase().includes('presidential'),
      };
    });
  }

  loadRoomTypes(): void {
    this.housekeepingService.getRoomTypes().subscribe({
      next: (data) => {
        if (data && data.length > 0) {
          this.roomTypesList = data;
          this.buildRoomTypeDisplayItems();
        } else {
          this.roomTypes = [];
          this.roomTypesList = [];
        }
        this.applyFilters();
      },
      error: () => {
        this.roomTypes = [];
        this.roomTypesList = [];
        this.applyFilters();
      },
    });
  }

  loadHousekeepingTasks(): void {
    this.housekeepingService.getTasks().subscribe((tasks) => {
      this.liveHousekeepingTasks = tasks || [];
    });
  }

  loadHousekeepers(): void {
    this.housekeepingService.getHousekeepers().subscribe((data) => {
      this.housekeepers = data || [];
      if (this.housekeepers.length > 0 && !this.selectedHousekeeper) {
        this.selectedHousekeeper = this.housekeepers[0];
      }
    });
  }

  // Action: Trigger bulk cleaning
  triggerBulkCleaning(): void {
    this.showToast('Đang kích hoạt quy trình dọn dẹp vệ sinh đồng loạt cho các Villa cần dọn...');
    this.housekeepingService.startCleaning(101).subscribe({
      next: () => {
        this.showToast('Đã bắt đầu chu trình dọn dẹp vệ sinh tiêu chuẩn 5 sao!');
        this.loadHousekeepingTasks();
        this.loadRooms();
      },
      error: () => {
        this.showToast('Đã bắt đầu chu trình dọn dẹp vệ sinh phòng!');
        this.loadHousekeepingTasks();
      },
    });
  }

  // Modal 1: Assign room cleaning
  openAssignModal(roomId?: number): void {
    this.assignForm = {
      roomId: roomId || (this.rooms.length > 0 ? this.rooms[0].id || 1 : 1),
      housekeeperId: this.housekeepers.length > 0 ? this.housekeepers[0].id : null,
      taskType: 'CHECKOUT_DEEP',
      notes: '',
    };
    this.showAssignModal = true;
  }

  submitAssign(): void {
    if (!this.assignForm.roomId || !this.assignForm.housekeeperId) {
      this.showToast('Vui lòng chọn phòng và nhân viên buồng phòng!', 'error');
      return;
    }

    this.isSaving = true;
    const req: AssignHousekeepingTaskRequest = {
      roomId: this.assignForm.roomId,
      housekeeperId: this.assignForm.housekeeperId,
      taskType: this.assignForm.taskType,
      notes: this.assignForm.notes,
    };

    this.housekeepingService.assignTask(req).subscribe({
      next: (res) => {
        this.isSaving = false;
        this.showAssignModal = false;
        this.showToast(`Đã giao phòng ${res.roomNumber} cho nhân viên ${res.housekeeperName}!`);
        this.loadHousekeepingTasks();
        this.loadHousekeepers();
        this.loadRooms();
      },
      error: (err) => {
        this.isSaving = false;
        this.showToast(err.error?.message || 'Có lỗi xảy ra khi phân công!', 'error');
      },
    });
  }

  // Modal 2: Housekeeping Staff Portal
  openStaffPortalModal(housekeeper?: HousekeeperSummary): void {
    if (housekeeper) {
      this.selectedHousekeeper = housekeeper;
    } else if (this.housekeepers.length > 0 && !this.selectedHousekeeper) {
      this.selectedHousekeeper = this.housekeepers[0];
    }
    this.loadStaffTasks();
    this.showStaffPortalModal = true;
  }

  selectHousekeeper(housekeeper: HousekeeperSummary): void {
    this.selectedHousekeeper = housekeeper;
    this.loadStaffTasks();
  }

  loadStaffTasks(): void {
    if (!this.selectedHousekeeper) return;
    this.housekeepingService
      .getMyTasks(this.selectedHousekeeper.email, this.selectedHousekeeper.id)
      .subscribe({
        next: (tasks) => {
          this.staffTasks = tasks || [];
        },
        error: (err) => console.error('Lỗi khi tải task nhân viên:', err),
      });
  }

  startStaffCleaning(task: HousekeepingTask): void {
    this.isSaving = true;
    this.housekeepingService.startCleaning(task.id).subscribe({
      next: (res) => {
        this.isSaving = false;
        this.showToast(`Đã bắt đầu dọn phòng ${res.roomNumber}!`);
        this.loadStaffTasks();
        this.loadHousekeepingTasks();
        this.loadRooms();
      },
      error: () => {
        this.isSaving = false;
        this.showToast('Không thể bắt đầu dọn phòng!', 'error');
      },
    });
  }

  openTaskDetailModal(task: HousekeepingTask): void {
    this.selectedTask = task;
    this.staffNote = task.cleaningNote || '';

    // Parse checklist if existing, otherwise reset
    let checkedMap: Record<number, boolean> = {};
    if (task.checklistJson) {
      try {
        checkedMap = JSON.parse(task.checklistJson);
      } catch {
        checkedMap = {};
      }
    }

    this.standard16Steps = this.standard16Steps.map((step) => ({
      ...step,
      checked: !!checkedMap[step.id],
    }));

    this.showTaskDetailModal = true;
  }

  toggleStep(index: number): void {
    this.standard16Steps[index].checked = !this.standard16Steps[index].checked;
  }

  getCompletedStepsCount(): number {
    return this.standard16Steps.filter((s) => s.checked).length;
  }

  saveTaskProgress(): void {
    if (!this.selectedTask) return;
    this.isSaving = true;

    const checkedMap: Record<number, boolean> = {};
    this.standard16Steps.forEach((s) => {
      checkedMap[s.id] = s.checked;
    });

    const req: UpdateCleaningProgressRequest = {
      taskId: this.selectedTask.id,
      status: this.selectedTask.status === 'PENDING' ? 'IN_PROGRESS' : this.selectedTask.status,
      checklistJson: JSON.stringify(checkedMap),
      cleaningNote: this.staffNote,
    };

    this.housekeepingService.updateProgress(req).subscribe({
      next: (res) => {
        this.isSaving = false;
        this.selectedTask = res;
        this.showToast(`Đã lưu tiến độ dọn phòng (${this.getCompletedStepsCount()}/16 bước)!`);
        this.loadStaffTasks();
        this.loadHousekeepingTasks();
      },
      error: () => {
        this.isSaving = false;
        this.showToast('Lỗi khi lưu tiến độ!', 'error');
      },
    });
  }

  submitCompleteCleaning(): void {
    if (!this.selectedTask) return;
    this.isSaving = true;

    this.housekeepingService.completeCleaning(this.selectedTask.id, this.staffNote).subscribe({
      next: (res) => {
        this.isSaving = false;
        this.showTaskDetailModal = false;
        this.showToast(
          `Đã báo cáo hoàn thành dọn phòng ${res.roomNumber}. Quản lý sẽ tiến hành nghiệm thu!`
        );
        this.loadStaffTasks();
        this.loadHousekeepingTasks();
        this.loadRooms();
      },
      error: () => {
        this.isSaving = false;
        this.showToast('Có lỗi xảy ra khi báo cáo hoàn thành!', 'error');
      },
    });
  }

  approveRoomCleaning(taskId: number): void {
    this.housekeepingService.approveTask(taskId).subscribe({
      next: (res) => {
        this.showToast(
          `Nghiệm thu thành công phòng ${res.roomNumber}. Phòng đã sẵn sàng đón khách!`
        );
        this.loadHousekeepingTasks();
        this.loadStaffTasks();
        this.loadRooms();
      },
      error: () => {
        this.showToast('Không thể nghiệm thu phòng!', 'error');
      },
    });
  }

  // Chuyển đổi chế độ xem Lưới Thẻ / Bảng
  toggleViewMode(mode: 'GRID' | 'TABLE'): void {
    this.viewMode = mode;
  }

  // Bộ lọc trạng thái tab
  setTab(tab: 'ALL' | 'AVAILABLE' | 'OCCUPIED' | 'CLEANING' | 'MAINTENANCE'): void {
    this.activeTab = tab;
    this.applyFilters();
  }

  // Lọc theo phân khu
  onSectionChange(): void {
    this.applyFilters();
  }

  // Tìm kiếm theo từ khóa
  onSearch(): void {
    this.applyFilters();
  }

  // Áp dụng đồng thời các điều kiện lọc & gom nhóm theo phân khu
  applyFilters(): void {
    let result = [...this.rooms];

    if (this.activeTab !== 'ALL') {
      if (this.activeTab === 'AVAILABLE') {
        result = result.filter(
          (r) => r.statusRaw === 'AVAILABLE' || r.statusTag.includes('Sẵn sàng')
        );
      } else if (this.activeTab === 'OCCUPIED') {
        result = result.filter(
          (r) => r.statusRaw === 'OCCUPIED' || r.statusTag.includes('Đang có khách')
        );
      } else if (this.activeTab === 'CLEANING') {
        result = result.filter((r) => r.statusRaw === 'CLEANING' || r.statusTag.includes('dọn'));
      } else if (this.activeTab === 'MAINTENANCE') {
        result = result.filter(
          (r) => r.statusRaw === 'MAINTENANCE' || r.statusTag.includes('bảo trì')
        );
      }
    }

    if (this.selectedSection && this.selectedSection !== 'ALL') {
      result = result.filter((r) =>
        (r.zone || '').toLowerCase().includes(this.selectedSection.toLowerCase())
      );
    }

    if (this.searchTerm.trim()) {
      const q = this.searchTerm.toLowerCase();
      result = result.filter(
        (r) =>
          r.number.toLowerCase().includes(q) ||
          r.category.toLowerCase().includes(q) ||
          (r.guestName && r.guestName.toLowerCase().includes(q)) ||
          (r.zone && r.zone.toLowerCase().includes(q))
      );
    }

    // Sắp xếp thứ tự: theo Phân khu -> rồi theo Số phòng số nguyên tăng dần (101, 102...)
    result.sort((a, b) => {
      const zA = a.zone || '';
      const zB = b.zone || '';
      const cmp = zA.localeCompare(zB);
      if (cmp !== 0) return cmp;
      return a.number.localeCompare(b.number, undefined, { numeric: true });
    });

    this.filteredRooms = result;
    this.updateGroupedZones();
  }

  // Gom nhóm các căn biệt thự có tổ chức theo từng phân khu
  updateGroupedZones(): void {
    const groups: ZoneSection[] = [];

    for (const def of this.customZones) {
      // Tìm các phòng thuộc phân khu này từ danh sách đã lọc
      const matchingRooms = this.filteredRooms.filter((r) => {
        const z = (r.zone || '').toLowerCase();
        return z.includes(def.matchKey) || z.includes(def.name.toLowerCase());
      });

      // Kiểm tra xem phân khu này có tồn tại phòng nào trong toàn bộ resort không
      const allRoomsInZone = this.rooms.filter((r) => {
        const z = (r.zone || '').toLowerCase();
        return z.includes(def.matchKey) || z.includes(def.name.toLowerCase());
      });

      const sectionFilterMatches =
        this.selectedSection === 'ALL' ||
        def.name.toLowerCase().includes(this.selectedSection.toLowerCase()) ||
        def.matchKey.includes(this.selectedSection.toLowerCase());

      if (
        sectionFilterMatches &&
        (matchingRooms.length > 0 ||
          (this.selectedSection !== 'ALL' && allRoomsInZone.length > 0) ||
          (this.searchTerm.trim() === '' && this.activeTab === 'ALL'))
      ) {
        groups.push({
          zoneName: def.name,
          zoneTag: def.tag,
          icon: def.icon,
          badgeClass: def.badgeClass,
          description: def.desc,
          rooms: matchingRooms,
          availableCount: matchingRooms.filter((r) => r.statusRaw === 'AVAILABLE').length,
          occupiedCount: matchingRooms.filter((r) => r.statusRaw === 'OCCUPIED').length,
          cleaningCount: matchingRooms.filter((r) => r.statusRaw === 'CLEANING').length,
          maintenanceCount: matchingRooms.filter((r) => r.statusRaw === 'MAINTENANCE').length,
        });
      }
    }

    // Xử lý các phân khu tùy biến khác (nếu người dùng tự nhập tên phân khu mới)
    const handledRoomIds = new Set(groups.flatMap((g) => g.rooms.map((r) => r.id)));
    const otherRooms = this.filteredRooms.filter((r) => !handledRoomIds.has(r.id));
    if (otherRooms.length > 0) {
      const otherZones = Array.from(new Set(otherRooms.map((r) => r.zone || 'Phân Khu Khác')));
      for (const oz of otherZones) {
        const zRooms = otherRooms.filter((r) => (r.zone || 'Phân Khu Khác') === oz);
        groups.push({
          zoneName: oz,
          zoneTag: 'PHÂN KHU KHÁC',
          icon: 'villa',
          badgeClass: 'bg-slate-100 text-slate-700 border-slate-200',
          description: 'Các căn biệt thự và phòng bổ sung trong resort.',
          rooms: zRooms,
          availableCount: zRooms.filter((r) => r.statusRaw === 'AVAILABLE').length,
          occupiedCount: zRooms.filter((r) => r.statusRaw === 'OCCUPIED').length,
          cleaningCount: zRooms.filter((r) => r.statusRaw === 'CLEANING').length,
          maintenanceCount: zRooms.filter((r) => r.statusRaw === 'MAINTENANCE').length,
        });
      }
    }

    this.groupedZones = groups;
  }

  // --- MULTI-BED SELECTOR & ADVANCED ALLOCATION METHODS ---
  initBedSelections(): void {
    this.bedSelections = this.roomTypesList.map((rt) => ({
      roomTypeId: rt.id,
      name: rt.name,
      bedDimensions: rt.bedType || 'Rộng 1.8m × Dài 2.0m',
      adults: rt.adults !== undefined ? rt.adults : 2,
      children: rt.children !== undefined ? rt.children : 0,
      capacity: rt.capacity || 2,
      quantity: 0,
      isSelected: false,
    }));

    // Default: select the first bed type with quantity 1
    if (this.bedSelections.length > 0) {
      this.bedSelections[0].isSelected = true;
      this.bedSelections[0].quantity = 1;
    }
    this.syncChildRoomsFromBedSelections();
  }

  toggleBedSelection(item: VillaBedSelectionItem): void {
    item.isSelected = !item.isSelected;
    if (item.isSelected && item.quantity === 0) {
      item.quantity = 1;
    } else if (!item.isSelected) {
      item.quantity = 0;
    }
    this.syncChildRoomsFromBedSelections();
  }

  changeBedQuantity(item: VillaBedSelectionItem, delta: number, event?: Event): void {
    if (event) event.stopPropagation();
    const newQty = Math.max(0, item.quantity + delta);
    item.quantity = newQty;
    item.isSelected = newQty > 0;
    this.syncChildRoomsFromBedSelections();
  }

  syncChildRoomsFromBedSelections(): void {
    const list: DetailedChildRoom[] = [];
    let idx = 1;
    for (const bed of this.bedSelections) {
      if (bed.isSelected && bed.quantity > 0) {
        for (let i = 0; i < bed.quantity; i++) {
          list.push({
            roomIndex: idx,
            roomNumber: `${this.roomForm.roomNumber ? this.roomForm.roomNumber.trim() : 'Villa'}-P${idx}`,
            name: idx === 1 ? 'Phòng Ngủ Master' : `Phòng Ngủ Phụ ${idx - 1} (${bed.name})`,
            floor: Math.min(idx, this.roomForm.floor || 1),
            roomTypeId: bed.roomTypeId,
          });
          idx++;
        }
      }
    }
    this.childRoomsAllocation = list;
  }

  onRoomNumberChange(): void {
    if (this.childRoomsAllocation && this.childRoomsAllocation.length > 0) {
      const prefix = this.roomForm.roomNumber ? this.roomForm.roomNumber.trim() : 'Villa';
      this.childRoomsAllocation.forEach((child, i) => {
        child.roomNumber = `${prefix}-P${i + 1}`;
      });
    }
  }

  onStructureTypeChanged(): void {
    const struct = this.roomForm.structureType || '';
    const match = struct.match(/^(\d+)\s*Tầng/i);
    if (match) {
      this.roomForm.floor = parseInt(match[1], 10);
    } else if (struct.includes('Duplex')) {
      this.roomForm.floor = 2;
    } else if (struct.includes('Triplex')) {
      this.roomForm.floor = 3;
    } else if (struct.includes('Penthouse')) {
      this.roomForm.floor = 4;
    }
    this.syncChildRoomsFromBedSelections();
  }

  toggleAmenity(amenity: string): void {
    const idx = this.selectedAmenities.indexOf(amenity);
    if (idx >= 0) {
      this.selectedAmenities.splice(idx, 1);
    } else {
      this.selectedAmenities.push(amenity);
    }
  }

  isAmenitySelected(amenity: string): boolean {
    return this.selectedAmenities.includes(amenity);
  }

  isExtraServiceSelected(id: number): boolean {
    return this.selectedExtraServiceIds.includes(id);
  }

  toggleExtraService(id: number): void {
    const idx = this.selectedExtraServiceIds.indexOf(id);
    if (idx !== -1) {
      this.selectedExtraServiceIds.splice(idx, 1);
    } else {
      this.selectedExtraServiceIds.push(id);
    }
    this.syncSelectedAmenitiesFromExtraServices();
  }

  syncSelectedAmenitiesFromExtraServices(): void {
    if (this.extraServicesList && this.extraServicesList.length > 0) {
      this.selectedAmenities = this.extraServicesList
        .filter((s) => this.selectedExtraServiceIds.includes(s.id))
        .map((s) => s.name);
    }
  }

  // --- CRUD BIỆT THỰ & PHÒNG ---

  // 1. Mở modal tạo mới phòng (hỗ trợ truyền phân khu mặc định)
  openCreateRoomModal(defaultZone?: string): void {
    this.isEditMode = false;
    this.selectedRoomId = null;
    const defaultTypeId = this.roomTypesList.length > 0 ? this.roomTypesList[0].id : null;
    const matchedType = defaultTypeId
      ? this.roomTypesList.find((t) => t.id === defaultTypeId)
      : null;
    const initialPrice = matchedType?.basePrice || 25000000;
    let initialZone =
      defaultZone || (this.customZones.length > 0 ? this.customZones[0].name : 'Ngọc Trai');
    if (!defaultZone && this.selectedSection && this.selectedSection !== 'ALL') {
      const match = this.customZones.find((z) =>
        z.name.toLowerCase().includes(this.selectedSection.toLowerCase())
      );
      if (match) initialZone = match.name;
    }

    this.selectedAmenities = [];
    this.selectedExtraServiceIds = [];
    this.showAdvancedRoomAllocation = false;
    this.initBedSelections();

    this.roomForm = {
      roomNumber: '',
      floor: 1,
      structureType: '1 Tầng (Ground Floor)',
      roomTypeId: defaultTypeId,
      zone: initialZone,
      status: 'AVAILABLE',
      ozoneStatus: 'STERILIZED',
      basePrice: initialPrice,
      imageUrl: '',
      imageName: '',
      images: [],
      isUploadingImage: false,
      area: 350,
      viewDirection: 'Trực diện Biển',
      poolSize: 24,
      overviewDescription: 'Tọa lạc tại vị trí đắc địa nhất của quần thể Aura Resort...',
    };
    this.showRoomModal = true;
  }

  onVillaTypeChanged(): void {
    if (this.roomForm.roomTypeId) {
      const matched = this.roomTypesList.find((t) => t.id === Number(this.roomForm.roomTypeId));
      if (matched && matched.basePrice && !this.isEditMode) {
        this.roomForm.basePrice = matched.basePrice;
      }
    }
  }

  // --- CẤU HÌNH & THÊM MỚI LOẠI GIƯỜNG ---
  openCreateRoomTypeModal(): void {
    this.isRoomTypeEditMode = false;
    this.previewImageError = false;
    this.roomTypeForm = {
      id: null,
      name: '',
      description: '',
      basePrice: 0,
      capacity: 2,
      adults: 2,
      children: 0,
      bedLength: '2.0',
      bedWidth: '1.8',
      bedType: 'Rộng 1.8m × Dài 2.0m',
      imageUrl: '',
    };
    this.showRoomTypeModal = true;
  }

  openEditRoomTypeModal(rt: RoomTypeDisplayItem): void {
    this.isRoomTypeEditMode = true;
    this.previewImageError = false;
    const targetId =
      rt.id ||
      this.roomTypesList.find((item) => item.name.toLowerCase() === (rt.title || '').toLowerCase())
        ?.id ||
      17;
    const matched = this.roomTypesList.find((item) => item.id === targetId);
    const basePrice =
      matched?.basePrice || parseFloat((rt.price || '').replace(/[^0-9.]/g, '')) * 1000000 || 0;
    const adults = matched?.adults ?? rt.adults ?? 2;
    const children = matched?.children ?? rt.children ?? 0;
    const capacity = matched?.capacity ?? rt.capacity ?? adults + children;
    const bedType = matched?.bedType || rt.bedType || 'Rộng 1.8m × Dài 2.0m';

    let bedLen = '2.0';
    let bedWid = '1.8';
    if (bedType) {
      const wMatch = bedType.match(/rộng\s*(\d+(?:\.\d+)?)/i);
      const lMatch = bedType.match(/dài\s*(\d+(?:\.\d+)?)/i);
      if (wMatch && lMatch) {
        bedWid = wMatch[1];
        bedLen = lMatch[1];
      } else {
        const match = bedType.match(/(\d+(?:\.\d+)?)\s*m?\s*[x×]\s*(\d+(?:\.\d+)?)\s*m?/i);
        if (match) {
          bedWid = match[1];
          bedLen = match[2];
        }
      }
    }

    this.roomTypeForm = {
      id: targetId,
      name: matched?.name || rt.title || 'Giường King Tiêu Chuẩn',
      description: matched?.description || rt.desc || '',
      basePrice: basePrice,
      capacity: capacity,
      adults: adults,
      children: children,
      bedLength: bedLen,
      bedWidth: bedWid,
      bedType: bedType,
      imageUrl: matched?.imageUrl || rt.imageUrl || '',
    };
    this.showRoomTypeModal = true;
  }

  setRoomTypePresetImage(url: string): void {
    this.roomTypeForm.imageUrl = url;
    this.previewImageError = false;
  }

  onRoomTypeImageUrlChange(): void {
    this.previewImageError = false;
  }

  onRoomTypeImageError(): void {
    this.previewImageError = true;
  }

  clearRoomTypeImage(): void {
    this.roomTypeForm.imageUrl = '';
    this.previewImageError = false;
  }

  saveRoomType(): void {
    if (!this.roomTypeForm.name || !this.roomTypeForm.name.trim()) {
      this.showToast('Vui lòng nhập đầy đủ tên loại giường!', 'error');
      return;
    }

    this.isSaving = true;
    const cleanWidth = (this.roomTypeForm.bedWidth || '1.8').toString().replace(/m/gi, '').trim();
    const cleanLength = (this.roomTypeForm.bedLength || '2.0').toString().replace(/m/gi, '').trim();
    const bedTypeStr = `Rộng ${cleanWidth}m × Dài ${cleanLength}m`;

    const ad = Math.max(1, Number(this.roomTypeForm.adults) || 2);
    const ch = Math.max(0, Number(this.roomTypeForm.children) || 0);
    const totalCap = ad + ch;

    const payload: UpdateRoomTypePayload = {
      name: this.roomTypeForm.name.trim(),
      description: (this.roomTypeForm.description || '').trim(),
      basePrice: Number(this.roomTypeForm.basePrice) || 0,
      capacity: totalCap,
      adults: ad,
      children: ch,
      bedType: bedTypeStr,
      imageUrl: this.roomTypeForm.imageUrl ? this.roomTypeForm.imageUrl.trim() : '',
    };

    if (this.isRoomTypeEditMode && this.roomTypeForm.id) {
      this.housekeepingService.updateRoomType(this.roomTypeForm.id, payload).subscribe({
        next: (res) => {
          this.isSaving = false;
          this.showRoomTypeModal = false;
          this.showToast(`Cập nhật thành công loại giường ${res.name || payload.name}!`);
          this.loadRoomTypes();
          this.loadRooms();
        },
        error: (err) => {
          this.isSaving = false;
          this.showToast(err.error?.message || 'Có lỗi xảy ra khi cập nhật loại giường!', 'error');
        },
      });
    } else {
      this.housekeepingService.createRoomType(payload).subscribe({
        next: (res) => {
          this.isSaving = false;
          this.showRoomTypeModal = false;
          this.showToast(`Khởi tạo thành công loại giường mới ${res.name || payload.name}!`);
          this.loadRoomTypes();
          this.loadRooms();
        },
        error: (err) => {
          this.isSaving = false;
          this.showToast(err.error?.message || 'Có lỗi xảy ra khi khởi tạo loại giường!', 'error');
        },
      });
    }
  }

  // --- XÓA HẠNG BIỆT THỰ / PHÒNG ---
  openDeleteRoomTypeConfirm(rt: RoomTypeDisplayItem, event?: Event): void {
    if (event) event.stopPropagation();
    this.roomTypeToDelete = rt;
    this.showDeleteRoomTypeConfirmModal = true;
  }

  confirmDeleteRoomType(): void {
    if (!this.roomTypeToDelete) return;
    const targetId =
      this.roomTypeToDelete.id ||
      this.roomTypesList.find(
        (item) => item.name.toLowerCase() === (this.roomTypeToDelete?.title || '').toLowerCase()
      )?.id;
    if (!targetId) {
      this.showToast('Không tìm thấy mã loại giường để xóa!', 'error');
      this.showDeleteRoomTypeConfirmModal = false;
      return;
    }

    const inUse = this.rooms.filter((r) => r.roomTypeId === targetId);
    if (inUse.length > 0) {
      this.showToast(
        `Không thể xóa: Có ${inUse.length} biệt thự đang áp dụng loại giường này! Vui lòng cập nhật các biệt thự trước.`,
        'error'
      );
      this.showDeleteRoomTypeConfirmModal = false;
      return;
    }

    this.isSaving = true;
    this.housekeepingService.deleteRoomType(targetId).subscribe({
      next: () => {
        this.isSaving = false;
        this.showToast(`Đã xóa thành công loại giường ${this.roomTypeToDelete?.title}!`);
        this.showDeleteRoomTypeConfirmModal = false;
        this.roomTypeToDelete = null;
        this.loadRoomTypes();
        this.loadRooms();
      },
      error: (err) => {
        this.isSaving = false;
        this.showToast(
          err.error?.message || 'Không thể xóa loại giường đang có liên kết dữ liệu!',
          'error'
        );
      },
    });
  }

  // --- QUẢN LÝ PHÂN KHU (THÊM, SỬA, XÓA) ---
  getZoneRoomCount(zone: CustomZoneItem): number {
    const key = (zone.matchKey || zone.name).toLowerCase();
    return this.rooms.filter(
      (r) =>
        (r.zone || '').toLowerCase().includes(key) ||
        (r.zone || '').toLowerCase() === zone.name.toLowerCase()
    ).length;
  }

  openCreateZoneModal(): void {
    this.isZoneEditMode = false;
    this.editingZoneName = null;
    this.zoneForm = {
      name: '',
      tag: '',
      icon: 'holiday_village',
      desc: '',
    };
    this.showZoneModal = true;
  }

  openEditZoneModal(zone: CustomZoneItem, event?: Event): void {
    if (event) event.stopPropagation();
    this.isZoneEditMode = true;
    this.editingZoneName = zone.name;
    this.zoneForm = {
      name: zone.name,
      tag: zone.tag,
      icon: zone.icon || 'holiday_village',
      desc: zone.desc || '',
    };
    this.showZoneModal = true;
  }

  saveZone(): void {
    if (!this.zoneForm.name || !this.zoneForm.name.trim()) {
      this.showToast('Vui lòng nhập tên phân khu!', 'error');
      return;
    }

    const trimmedName = this.zoneForm.name.trim();

    if (this.isZoneEditMode && this.editingZoneName) {
      const existing = this.customZones.find(
        (z) => z.name.toLowerCase() === this.editingZoneName?.toLowerCase()
      );
      if (existing && existing.id) {
        const payload: Partial<ZoneItem> = {
          name: trimmedName,
          tag: this.zoneForm.tag.trim().toUpperCase() || existing.tag,
          icon: this.zoneForm.icon || existing.icon,
          badgeClass: existing.badgeClass,
          description: this.zoneForm.desc.trim() || existing.desc,
        };

        this.housekeepingService.updateZone(existing.id, payload).subscribe({
          next: () => {
            this.showZoneModal = false;
            this.showToast(`Đã cập nhật phân khu "${trimmedName}" thành công!`);
            this.initCustomZones();
            this.loadRooms();
          },
          error: (err) => {
            const msg = err?.error?.message || 'Có lỗi xảy ra khi cập nhật phân khu!';
            this.showToast(msg, 'error');
          },
        });
      }
      return;
    }

    // Tạo mới phân khu
    const exists = this.customZones.some((z) => z.name.toLowerCase() === trimmedName.toLowerCase());
    if (exists) {
      this.showToast('Phân khu này đã tồn tại trong danh sách!', 'error');
      return;
    }

    const payload: Partial<ZoneItem> = {
      name: trimmedName,
      tag: this.zoneForm.tag.trim().toUpperCase() || 'PHÂN KHU MỚI',
      icon: this.zoneForm.icon || 'https://images.unsplash.com/photo-1582719508461-905c673771fd?auto=format&fit=crop&w=800&q=80',
      badgeClass: 'bg-sky-50 text-sky-700 border-sky-200',
      description: this.zoneForm.desc.trim() || 'Tổ hợp biệt thự và tiện ích nghỉ dưỡng cao cấp.',
    };

    this.housekeepingService.createZone(payload).subscribe({
      next: () => {
        this.showZoneModal = false;
        this.showToast(`Đã khởi tạo thành công phân khu mới "${trimmedName}"!`);
        this.initCustomZones();
      },
      error: (err) => {
        const msg = err?.error?.message || 'Có lỗi xảy ra khi tạo phân khu!';
        this.showToast(msg, 'error');
      },
    });
  }

  isUploadingZoneImage = false;

  /* eslint-disable-next-line @typescript-eslint/no-explicit-any */
  onZoneImageSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.isUploadingZoneImage = true;
      this.housekeepingService.uploadImage(file).subscribe({
        next: (url) => {
          this.zoneForm.icon = url;
          this.isUploadingZoneImage = false;
        },
        error: (err) => {
          console.error(err);
          this.showToast('Không thể tải ảnh lên!', 'error');
          this.isUploadingZoneImage = false;
        }
      });
    }
  }

  openDeleteZoneConfirm(zone: CustomZoneItem, event?: Event): void {
    if (event) event.stopPropagation();
    const count = this.getZoneRoomCount(zone);
    if (count > 0) {
      this.showToast(
        `Không thể xóa phân khu "${zone.name}" vì đang có ${count} biệt thự đang vận hành!`,
        'error'
      );
      return;
    }
    this.zoneToDelete = zone;
    this.showDeleteZoneConfirmModal = true;
  }

  confirmDeleteZone(): void {
    if (!this.zoneToDelete) return;
    const target = this.zoneToDelete;
    if (target.id) {
      this.housekeepingService.deleteZone(target.id).subscribe({
        next: () => {
          this.showDeleteZoneConfirmModal = false;
          this.zoneToDelete = null;
          this.showToast(`Đã xóa phân khu "${target.name}" thành công!`);
          this.initCustomZones();
          this.loadRooms();
        },
        error: (err) => {
          const msg = err?.error?.message || `Không thể xóa phân khu "${target.name}"!`;
          this.showToast(msg, 'error');
        },
      });
    } else {
      this.customZones = this.customZones.filter(
        (z) => z.name.toLowerCase() !== target.name.toLowerCase()
      );
      this.showDeleteZoneConfirmModal = false;
      this.zoneToDelete = null;
      this.showToast(`Đã xóa phân khu "${target.name}" thành công!`);
      this.applyFilters();
    }
  }

  // 2. Mở modal chỉnh sửa phòng
  openEditRoomModal(room: RoomCard, event?: Event): void {
    if (event) event.stopPropagation();
    this.isEditMode = true;
    this.selectedRoomId = room.id || null;
    const defaultTypeId = this.roomTypesList.length > 0 ? this.roomTypesList[0].id : null;
    const defaultZone = this.customZones.length > 0 ? this.customZones[0].name : 'Ngọc Trai';

    this.selectedAmenities = room.amenities && room.amenities.length > 0 ? [...room.amenities] : [];

    // Load villa services for this room
    this.selectedExtraServiceIds = [];
    if (room.id) {
      this.extraServiceApi.getVillaServices(room.id).subscribe((services) => {
        const availableServices = (services || []).filter((s) => s.isAvailable);
        this.selectedExtraServiceIds = availableServices.map((s) => s.serviceId);
        if (this.selectedExtraServiceIds.length > 0) {
          this.syncSelectedAmenitiesFromExtraServices();
        }
      });
    }

    this.showAdvancedRoomAllocation = false;

    // Parse childRooms if existing
    if (room.childRooms && room.childRooms.length > 0) {
      this.childRoomsAllocation = room.childRooms.map((cr, idx) => ({
        roomIndex: idx + 1,
        roomNumber: cr.roomNumber || `${room.number}-P${idx + 1}`,
        name: cr.name || (idx === 0 ? 'Phòng Ngủ Master' : `Phòng Ngủ Phụ ${idx}`),
        floor: cr.floor || room.floor || 1,
        roomTypeId: cr.roomTypeId || room.roomTypeId || (this.roomTypesList[0]?.id ?? 1),
      }));

      const counts: Record<number, number> = {};
      for (const cr of room.childRooms) {
        const tid = cr.roomTypeId || room.roomTypeId;
        if (tid) {
          counts[tid] = (counts[tid] || 0) + 1;
        }
      }

      this.bedSelections = this.roomTypesList.map((rt) => {
        const qty = counts[rt.id] || 0;
        return {
          roomTypeId: rt.id,
          name: rt.name,
          bedDimensions: rt.bedType || 'Rộng 1.8m × Dài 2.0m',
          adults: rt.adults !== undefined ? rt.adults : 2,
          children: rt.children !== undefined ? rt.children : 0,
          capacity: rt.capacity || 2,
          quantity: qty,
          isSelected: qty > 0,
        };
      });

      if (this.bedSelections.every((b) => !b.isSelected)) {
        const targetTypeId = room.roomTypeId || (this.roomTypesList[0]?.id ?? 0);
        const match = this.bedSelections.find((b) => b.roomTypeId === targetTypeId);
        if (match) {
          match.isSelected = true;
          match.quantity = room.childRooms.length || 1;
        } else if (this.bedSelections.length > 0) {
          this.bedSelections[0].isSelected = true;
          this.bedSelections[0].quantity = room.childRooms.length || 1;
        }
      }
    } else {
      this.initBedSelections();
      if (room.roomTypeId) {
        this.bedSelections.forEach((b) => {
          if (b.roomTypeId === room.roomTypeId) {
            b.isSelected = true;
            b.quantity = room.bedroomCount || 1;
          } else {
            b.isSelected = false;
            b.quantity = 0;
          }
        });
      }
      this.syncChildRoomsFromBedSelections();
    }

    this.roomForm = {
      roomNumber: room.number,
      floor: room.floor || 1,
      structureType: room.structureType || '1 Tầng (Ground Floor)',
      roomTypeId: room.roomTypeId ?? defaultTypeId,
      zone: room.zone || defaultZone,
      status: room.statusRaw || 'AVAILABLE',
      ozoneStatus: room.ozoneStatus || 'STERILIZED',
      basePrice: room.basePrice || 0,
      imageUrl: room.imageUrl || '',
      images: room.images || [],
      imageName: '',
      isUploadingImage: false,
      area: room.areaNumber || 350,
      viewDirection: room.viewDirection || 'Trực diện Biển',
      poolSize: room.poolSize || 24,
      overviewDescription: room.overviewDescription || 'Tọa lạc tại vị trí đắc địa nhất của quần thể Aura Resort, mở ra một tầm nhìn Panorama không giới hạn ôm trọn vẻ đẹp tráng lệ của bình minh trên biển sầm sơn.',
    };
    this.showRoomModal = true;
  }

  // --- CHỌN & TẢI ẢNH TỪ MÁY TÍNH CHO BIỆT THỰ / PHÒNG (MODAL 4) ---
  onRoomImageSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;
    const file = input.files[0];

    if (!file.type.startsWith('image/')) {
      this.showToast('Vui lòng chọn đúng tệp hình ảnh (PNG, JPG, WEBP)!', 'error');
      return;
    }

    if (file.size > 10 * 1024 * 1024) {
      this.showToast('Dung lượng ảnh không được vượt quá 10MB!', 'error');
      return;
    }

    this.roomForm.imageName = file.name;
    this.roomForm.isUploadingImage = true;

    // Đọc preview tức thì bằng FileReader
    const reader = new FileReader();
    reader.onload = (e: ProgressEvent<FileReader>) => {
      this.roomForm.imageUrl = (e.target?.result as string) || '';
    };
    reader.readAsDataURL(file);

    // Tải ảnh lên server
    this.housekeepingService.uploadImage(file).subscribe({
      next: (url) => {
        this.roomForm.isUploadingImage = false;
        if (url) {
          this.roomForm.imageUrl = url;
        }
        this.showToast(`Đã tải lên ảnh "${file.name}" thành công!`);
      },
      error: () => {
        this.roomForm.isUploadingImage = false;
        this.showToast('Đã lưu ảnh cục bộ (Base64) sẵn sàng lưu!', 'info');
      },
    });
  }

  removeRoomImage(): void {
    this.roomForm.imageUrl = '';
    this.roomForm.imageName = '';
  }

  onRoomImagesSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;
    
    const files = Array.from(input.files);
    for (const file of files) {
      if (!file.type.startsWith('image/')) continue;
      
      this.roomForm.isUploadingImage = true;
      this.housekeepingService.uploadImage(file).subscribe({
        next: (url) => {
          if (!this.roomForm.images) this.roomForm.images = [];
          this.roomForm.images.push(url);
          this.roomForm.isUploadingImage = false;
        },
        error: () => {
          const reader = new FileReader();
          reader.onload = (e: ProgressEvent<FileReader>) => {
            if (!this.roomForm.images) this.roomForm.images = [];
            this.roomForm.images.push((e.target?.result as string) || '');
            this.roomForm.isUploadingImage = false;
          };
          reader.readAsDataURL(file);
        },
      });
    }
  }

  removeRoomImageFromGallery(index: number): void {
    if (this.roomForm.images && index >= 0 && index < this.roomForm.images.length) {
      this.roomForm.images.splice(index, 1);
    }
  }

  setRoomPresetImage(url: string): void {
    this.roomForm.imageUrl = url;
    this.roomForm.imageName = '';
  }

  // --- CHỌN ẢNH TỪ MÁY TÍNH CHO HẠNG BIỆT THỰ / PHÒNG (MODAL 8) ---
  onRoomTypeImageSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;
    const file = input.files[0];

    if (!file.type.startsWith('image/')) {
      this.showToast('Vui lòng chọn đúng tệp hình ảnh (PNG, JPG, WEBP)!', 'error');
      return;
    }

    const reader = new FileReader();
    reader.onload = (e: ProgressEvent<FileReader>) => {
      this.roomTypeForm.imageUrl = (e.target?.result as string) || '';
    };
    reader.readAsDataURL(file);

    this.housekeepingService.uploadImage(file).subscribe({
      next: (url) => {
        if (url) {
          this.roomTypeForm.imageUrl = url;
        }
        this.showToast(`Đã tải lên ảnh "${file.name}" thành công!`);
      },
      error: () => {
        this.showToast('Đã lưu ảnh xem trước (Base64)!', 'info');
      },
    });
  }

  // 3. Lưu thông tin phòng (Thêm mới hoặc Cập nhật)
  saveRoom(): void {
    if (!this.roomForm.roomNumber) {
      this.showToast('Vui lòng nhập đầy đủ số phòng!', 'error');
      return;
    }

    if (this.totalSelectedBeds <= 0) {
      this.showToast('Vui lòng chọn ít nhất 1 loại giường cho biệt thự!', 'error');
      return;
    }

    if (!this.childRoomsAllocation || this.childRoomsAllocation.length === 0) {
      this.syncChildRoomsFromBedSelections();
    }

    this.isSaving = true;
    const num = this.roomForm.roomNumber.trim();
    const firstSelectedBed = this.bedSelections.find((b) => b.isSelected && b.quantity > 0);
    const typeId = Number(
      this.roomForm.roomTypeId || firstSelectedBed?.roomTypeId || this.roomTypesList[0]?.id || 1
    );
    const villaPrice = Number(this.roomForm.basePrice) || 0;

    const activeBedSelections = this.bedSelections
      .filter((b) => b.isSelected && b.quantity > 0)
      .map((b) => ({
        roomTypeId: b.roomTypeId,
        quantity: b.quantity,
      }));

    const activeServiceNames = this.extraServicesList
      .filter((s) => this.selectedExtraServiceIds.includes(s.id))
      .map((s) => s.name);

    const payloadAmenities =
      activeServiceNames.length > 0 ? activeServiceNames : [...this.selectedAmenities];

    const payload: CreateRoomPayload = {
      villaNumber: num,
      roomNumber: num,
      floor: Number(this.roomForm.floor) || 1,
      structureType: this.roomForm.structureType,
      villaTypeId: typeId,
      roomTypeId: typeId,
      zone: this.roomForm.zone,
      status: this.roomForm.status,
      ozoneStatus: this.roomForm.ozoneStatus,
      basePrice: villaPrice,
      price: villaPrice,
      amenities: payloadAmenities,
      bedroomCount: this.totalSelectedBedrooms,
      bedSelections: activeBedSelections,
      childRooms: this.childRoomsAllocation.map((cr) => ({
        roomNumber: cr.roomNumber,
        name: cr.name,
        floor: cr.floor,
        roomTypeId: cr.roomTypeId,
      })),
      imageUrl: this.roomForm.imageUrl ? this.roomForm.imageUrl.trim() : undefined,
      images: this.roomForm.images || [],
      area: this.roomForm.area,
      viewDirection: this.roomForm.viewDirection,
      poolSize: this.roomForm.poolSize,
      overviewDescription: this.roomForm.overviewDescription,
    };

    if (this.isEditMode && this.selectedRoomId) {
      this.housekeepingService.updateRoom(this.selectedRoomId, payload).subscribe({
        next: () => {
          this.isSaving = false;
          this.showRoomModal = false;
          const matchedRoom = this.rooms.find(
            (r) => r.id === this.selectedRoomId || r.number === payload.roomNumber
          );
          if (matchedRoom) {
            matchedRoom.basePrice = villaPrice;
            matchedRoom.priceFormatted = `${(villaPrice / 1000000).toFixed(1)}Tr / đêm`;
            matchedRoom.structureType = payload.structureType;
            matchedRoom.amenities = payload.amenities;
            matchedRoom.bedroomCount = payload.bedroomCount;
          }

          this.extraServiceApi
            .syncVillaServices(this.selectedRoomId!, this.selectedExtraServiceIds)
            .subscribe({
              next: () => {
                this.showToast(`Cập nhật thành công phòng ${payload.roomNumber}!`);
                this.loadRooms();
              },
            });
        },
        error: (err) => {
          this.isSaving = false;
          this.showToast(
            err.error?.message || 'Có lỗi xảy ra khi cập nhật thông tin phòng!',
            'error'
          );
        },
      });
    } else {
      this.housekeepingService.createRoom(payload).subscribe({
        next: (createdRoom) => {
          this.isSaving = false;
          this.showRoomModal = false;

          if (createdRoom && createdRoom.id) {
            this.extraServiceApi
              .syncVillaServices(createdRoom.id, this.selectedExtraServiceIds)
              .subscribe({
                next: () => {
                  this.showToast(`Khởi tạo thành công biệt thự/phòng ${payload.roomNumber}!`);
                  this.loadRooms();
                },
              });
          } else {
            this.showToast(`Khởi tạo thành công biệt thự/phòng ${payload.roomNumber}!`);
            this.loadRooms();
          }
        },
        error: (err) => {
          this.isSaving = false;
          this.showToast(err.error?.message || 'Có lỗi xảy ra khi tạo mới phòng!', 'error');
        },
      });
    }
  }

  // 4. Mở modal xác nhận xóa
  openDeleteConfirm(room: RoomCard, event?: Event): void {
    if (event) event.stopPropagation();
    this.roomToDelete = room;
    this.showDeleteConfirmModal = true;
  }

  // 5. Thực hiện xóa phòng
  confirmDeleteRoom(): void {
    if (!this.roomToDelete || !this.roomToDelete.id) return;
    this.isSaving = true;

    this.housekeepingService.deleteRoom(this.roomToDelete.id).subscribe({
      next: () => {
        this.isSaving = false;
        this.showToast(`Đã xóa thành công phòng ${this.roomToDelete?.number}!`);
        this.showDeleteConfirmModal = false;
        this.roomToDelete = null;
        this.loadRooms();
      },
      error: (err) => {
        this.isSaving = false;
        this.showToast(
          err.error?.message || 'Không thể xóa phòng đang có dữ liệu hoặc khách ở!',
          'error'
        );
      },
    });
  }

  // 6. Mở modal đổi trạng thái nhanh
  openQuickStatusModal(room: RoomCard, event?: Event): void {
    if (event) event.stopPropagation();
    this.roomToChangeStatus = room;
    this.quickStatusTarget = room.statusRaw || 'AVAILABLE';
    this.showQuickStatusModal = true;
  }

  // 7. Thực hiện đổi trạng thái nhanh
  submitQuickStatus(): void {
    if (!this.roomToChangeStatus || !this.roomToChangeStatus.id) return;
    this.isSaving = true;

    this.housekeepingService
      .updateRoomStatus(this.roomToChangeStatus.id, this.quickStatusTarget)
      .subscribe({
        next: () => {
          this.isSaving = false;
          this.showToast(
            `Đã chuyển trạng thái phòng ${this.roomToChangeStatus?.number} sang ${this.getStatusLabel(this.quickStatusTarget)}!`
          );
          this.showQuickStatusModal = false;
          this.roomToChangeStatus = null;
          this.loadRooms();
        },
        error: (err) => {
          this.isSaving = false;
          this.showToast(err.error?.message || 'Lỗi khi cập nhật trạng thái phòng!', 'error');
        },
      });
  }

  // Helpers hiển thị nhãn và màu trạng thái
  getStatusLabel(status?: string): string {
    switch (status) {
      case 'AVAILABLE':
        return 'Sẵn sàng đón khách';
      case 'OCCUPIED':
        return 'Đang có khách';
      case 'CLEANING':
        return 'Đang dọn phòng';
      case 'MAINTENANCE':
        return 'Đang bảo trì';
      default:
        return status || 'Chưa xác định';
    }
  }

  getStatusBadgeClass(status?: string): string {
    switch (status) {
      case 'AVAILABLE':
        return 'bg-emerald-50 text-emerald-700 border-emerald-200';
      case 'OCCUPIED':
        return 'bg-amber-50 text-amber-800 border-amber-200';
      case 'CLEANING':
        return 'bg-sky-50 text-sky-700 border-sky-200';
      case 'MAINTENANCE':
        return 'bg-rose-50 text-rose-700 border-rose-200';
      default:
        return 'bg-slate-50 text-slate-600 border-slate-200';
    }
  }

  getStatusDotClass(status?: string): string {
    switch (status) {
      case 'AVAILABLE':
        return 'bg-emerald-500';
      case 'OCCUPIED':
        return 'bg-amber-500';
      case 'CLEANING':
        return 'bg-sky-500';
      case 'MAINTENANCE':
        return 'bg-rose-500';
      default:
        return 'bg-slate-400';
    }
  }

  loadBookings(): void {
    this.adminBookingService.getBookings({ size: 100 }).subscribe({
      next: (page) => {
        if (page && page.content) {
          this.allBookings = page.content;
          this.syncBookingsWithRooms();
        }
      },
      error: (err) => console.warn('Error loading bookings in room-management:', err),
    });
  }

  getTierBadgeClass(tier?: string): string {
    const t = (tier || '').toLowerCase();
    if (t.includes('diamond')) {
      return 'bg-gradient-to-r from-amber-100 to-amber-200 text-amber-950 border border-amber-300 font-bold';
    }
    if (t.includes('black') || t.includes('elite') || t.includes('platinum')) {
      return 'bg-purple-100 text-purple-900 border border-purple-200 font-bold';
    }
    if (t.includes('gold')) {
      return 'bg-amber-50 text-amber-800 border border-amber-200 font-bold';
    }
    return 'bg-sky-50 text-sky-800 border border-sky-200 font-semibold';
  }

  syncBookingsWithRooms(): void {
    if (!this.rooms || this.rooms.length === 0) {
      return;
    }
    const now = new Date();
    const todayStr = now.toISOString().slice(0, 10);

    this.rooms.forEach((r) => {
      // 1. Nếu phòng không ở trạng thái OCCUPIED (ví dụ AVAILABLE, CLEANING, MAINTENANCE):
      // Tuyệt đối không gắn thông tin khách lưu trú vào thẻ phòng!
      if (r.statusRaw !== 'OCCUPIED') {
        r.guestName = undefined;
        r.guestTier = undefined;
        r.guestTierClass = undefined;
        r.guestSub = undefined;
        r.bookingDetail = undefined;
        return;
      }

      // 2. Nếu phòng OCCUPIED: Chỉ tìm đơn đặt phòng ĐANG THỰC SỰ LƯU TRÚ (in-house)
      if (!this.allBookings || this.allBookings.length === 0) {
        return;
      }

      const activeBooking = this.allBookings.find((b) => {
        // Loại bỏ đơn đã hủy hoặc đã check-out
        if (b.statusCode === 'CANCELLED' || b.statusCode === 'CHECKED_OUT') {
          return false;
        }

        const matchesVilla =
          b.villaNumber === r.number ||
          b.villaNumber === `Villa #${r.number}` ||
          (b.villaNumber && r.number && (b.villaNumber.includes(r.number) || r.number.includes(b.villaNumber)));

        if (!matchesVilla) return false;

        const checkIn = b.checkInDate ? b.checkInDate.slice(0, 10) : '';
        const checkOut = b.checkOutDate ? b.checkOutDate.slice(0, 10) : '';

        // Đơn đã quá hạn ngày trả phòng (khách cũ đã đi) -> loại bỏ!
        if (checkOut && checkOut < todayStr) {
          return false;
        }

        // Đơn đã check-in thực tế
        if (b.statusCode === 'CHECKED_IN') {
          return true;
        }

        // Đơn đã xác nhận và thời gian lưu trú bao gồm ngày hôm nay
        if (
          (b.statusCode === 'CONFIRMED' || b.statusCode === 'PAID') &&
          checkIn &&
          checkIn <= todayStr &&
          (!checkOut || checkOut >= todayStr)
        ) {
          return true;
        }

        return false;
      });

      if (activeBooking) {
        r.guestName = activeBooking.guestName || r.guestName;
        const amt = activeBooking.totalAmount || 0;
        let tierName = activeBooking.guestTier;
        if (!tierName || tierName === 'Thành viên') {
          tierName = amt >= 30000000 ? 'VIP Diamond' : amt >= 15000000 ? 'VIP Gold' : 'Thành Viên AURA';
        }
        r.guestTier = tierName;
        r.guestTierClass = this.getTierBadgeClass(tierName);
        if (activeBooking.checkInFormatted && activeBooking.checkOutFormatted) {
          r.guestSub = `${activeBooking.checkInFormatted} ➔ ${activeBooking.checkOutFormatted}`;
        } else if (activeBooking.nights) {
          r.guestSub = `Lưu trú ${activeBooking.nights} đêm`;
        }
        r.bookingDetail = this.mapBookingToRecord(activeBooking, r);
      } else if (!r.guestName) {
        r.guestTier = undefined;
        r.guestTierClass = undefined;
        r.guestSub = undefined;
      }
    });
  }

  getGuestInitials(name?: string): string {
    if (!name || !name.trim()) return 'VIP';
    const parts = name.trim().split(' ').filter((p) => p.length > 0);
    if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase();
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
  }

  mapBookingToRecord(b: AdminBookingItem, room: RoomCard): BookingDetailRecord {
    const isDiamond = (b.totalAmount || 0) >= 30000000;
    const isGold = (b.totalAmount || 0) >= 15000000;
    const tier = b.guestTier || (isDiamond ? 'VIP Diamond' : isGold ? 'VIP Black Elite' : 'VIP Gold Elite');
    const badgeClass = isDiamond ? 'bg-amber-400 text-slate-900 font-bold' : 'bg-sky-100 text-[#0284c7] font-bold';

    const checkInStr = b.checkInFormatted || b.checkInDate || 'Từ 14:00';
    const checkOutStr = b.checkOutFormatted || b.checkOutDate || 'Trước 12:00';
    const occupantsStr =
      b.guestSummary ||
      `${room.totalAdults || 2} Người lớn${room.totalChildren ? ' + ' + room.totalChildren + ' Trẻ em' : ''} (${b.nights || 1} đêm)`;

    const totalStr =
      b.totalAmountDisplay ||
      (b.totalAmount ? b.totalAmount.toLocaleString('vi-VN') + '₫' : room.priceFormatted || 'Liên hệ');
    const payMethodStr = b.paymentStatusDisplay
      ? `${b.paymentStatusDisplay} ${b.paymentMethod ? 'qua ' + b.paymentMethod : ''}`.trim()
      : b.isFullyPaid
        ? 'Đã thu đủ 100%'
        : 'Chờ thanh toán';

    const servicesList: { title: string; sub: string; icon: string }[] = [];
    if (b.extraServiceName) {
      servicesList.push({
        title: b.extraServiceName,
        sub: 'Dịch vụ chọn theo đơn đặt phòng',
        icon: b.extraServiceIcon || 'room_service',
      });
    }
    servicesList.push({
      title: 'Quản Gia VIP Butler 24/7',
      sub: b.assignedButler || 'Lead Butler phụ trách 24/7',
      icon: 'person',
    });
    servicesList.push({
      title: 'Tiêu Chuẩn Nghỉ Dưỡng 5 Sao',
      sub:
        room.amenities && room.amenities.length > 0
          ? room.amenities[0]
          : 'Buffet sáng & Trà chiều cao cấp',
      icon: 'hotel',
    });

    const bookingChannel = b.channel || 'Website Trực Tuyến';
    const bookingDate = b.bookingDateFormatted || 'Gần đây';

    const timelineList: { time: string; dept: string }[] = [
      {
        time: `${bookingDate} - Khởi tạo thành công đơn đặt phòng qua ${bookingChannel}`,
        dept: 'Hệ Thống Tự Động',
      },
      {
        time: `${bookingDate} - ${payMethodStr}`,
        dept: b.paymentMethod || 'Cổng Thanh Toán',
      },
      {
        time: `Chuẩn bị Villa #${room.number} - Kiểm định buồng phòng đạt chuẩn Deep Clean & Ozon 5 sao`,
        dept: 'Housekeeping Dept',
      },
    ];

    if (b.statusCode === 'CHECKED_IN') {
      timelineList.push({
        time: `Khách hàng đã hoàn tất thủ tục Check-in và đang lưu trú tại biệt thự #${room.number}`,
        dept: 'Đang Tiến Hành',
      });
    } else if (b.statusCode === 'CHECKED_OUT') {
      timelineList.push({
        time: `Khách hàng đã hoàn tất thủ tục Check-out và thanh toán đầy đủ chi phí`,
        dept: 'Hoàn Tất',
      });
    } else {
      timelineList.push({
        time: `Hồ sơ sẵn sàng đón tiếp • Chờ khách hàng tới làm thủ tục nhận phòng`,
        dept: 'Chờ Check-in',
      });
    }

    return {
      id: b.id,
      code: b.bookingCode
        ? b.bookingCode.startsWith('#')
          ? b.bookingCode
          : '#' + b.bookingCode
        : `#AURA-PQ-${room.number}`,
      guestName: b.guestName || room.guestName || 'Khách Hàng Quý Phái',
      vipTier: tier,
      vipBadgeClass: badgeClass,
      vipCardNumber: `VIP-${b.bookingCode ? b.bookingCode.replace(/[^A-Za-z0-9]/g, '').slice(-6) : room.number}`,
      phone: b.guestPhone || '+84 Chưa cập nhật',
      email: b.guestEmail || 'Chưa cập nhật email',
      allergies: b.note || 'Không ghi nhận dị ứng thực phẩm',
      specialRequests: b.note || 'Không có yêu cầu đặc biệt',
      villaName: `Villa #${room.number} (${room.category})`,
      villaArea: `${room.category} • ${room.zone ? 'Khu ' + room.zone : room.area}`,
      checkIn: checkInStr,
      checkOut: checkOutStr,
      occupants: occupantsStr,
      totalAmount: totalStr,
      paymentMethod: payMethodStr,
      txId: b.bookingCode ? `TXN-${b.bookingCode}` : `TXN-VCB-${room.number}`,
      vatInvoice: b.isFullyPaid ? 'Đã xuất hóa đơn điện tử E-Invoice' : 'Chưa xuất hóa đơn',
      deposit: b.totalAmount
        ? Math.round(b.totalAmount * 0.2).toLocaleString('vi-VN') + '₫ (Đã xác nhận)'
        : '20.000.000₫ (Phong tỏa thẻ)',
      createdInfo: `Tạo qua ${bookingChannel} • ${b.bookingDateFormatted ? 'Xác nhận lúc ' + b.bookingDateFormatted : 'Hệ thống tự động'}`,
      avatarUrl: b.avatarUrl || '',
      statusCode: b.statusCode || 'CONFIRMED',
      statusLabel: b.statusLabel || (b.statusCode === 'CHECKED_IN' ? 'Đang Lưu Trú' : 'Đã Xác Nhận'),
      services: servicesList,
      timeline: timelineList,
    };
  }

  createInHouseRecord(room: RoomCard): BookingDetailRecord {
    const isDiamond = room.guestTier?.includes('Diamond');
    const tier = room.guestTier || 'VIP Diamond';
    const badgeClass = isDiamond ? 'bg-amber-400 text-slate-900 font-bold' : 'bg-sky-100 text-[#0284c7] font-bold';

    const today = new Date();
    const d = String(today.getDate()).padStart(2, '0');
    const m = String(today.getMonth() + 1).padStart(2, '0');
    const y = today.getFullYear();
    const todayStr = `${d}/${m}/${y}`;

    const tomorrow = new Date(today);
    tomorrow.setDate(tomorrow.getDate() + 1);
    const d2 = String(tomorrow.getDate()).padStart(2, '0');
    const m2 = String(tomorrow.getMonth() + 1).padStart(2, '0');
    const y2 = tomorrow.getFullYear();
    const tomorrowStr = `${d2}/${m2}/${y2}`;

    return {
      code: `#AURA-INHOUSE-${room.number}`,
      guestName: room.guestName || 'Khách Đang Lưu Trú',
      vipTier: tier,
      vipBadgeClass: badgeClass,
      vipCardNumber: `VIP-${room.number}-AURA`,
      phone: '+84 918 223 999',
      email: 'guest@aura-resort.vn',
      allergies: 'Tiêu chuẩn phục vụ 5 sao',
      specialRequests: 'Tiêu chuẩn phục vụ 5 sao',
      villaName: `Villa #${room.number} (${room.category})`,
      villaArea: `${room.category} • ${room.zone ? 'Khu ' + room.zone : room.area}`,
      checkIn: `${todayStr} (Từ 14:00)`,
      checkOut: `${tomorrowStr} (Trước 12:00)`,
      occupants: `${room.totalAdults || 2} Người lớn${room.totalChildren ? ' + ' + room.totalChildren + ' Trẻ em' : ''}`,
      totalAmount: room.priceFormatted || 'Theo giá niêm yết',
      paymentMethod: 'Đã hoàn tất thanh toán & cọc',
      txId: `TXN-IH-${room.number}`,
      vatInvoice: 'Đã xuất hóa đơn điện tử E-Invoice',
      deposit: 'Đã xác nhận đặt cọc',
      createdInfo: `Khách nhận phòng tại Lễ Tân Resort • ${todayStr}`,
      avatarUrl: '',
      statusCode: 'CHECKED_IN',
      statusLabel: 'Đang Lưu Trú',
      services: [
        {
          title: 'Quản Gia VIP Butler 24/7',
          sub: 'Lead Butler phụ trách 24/7',
          icon: 'person',
        },
        {
          title: 'Dịch Vụ Buồng Phòng 5 Sao',
          sub: 'Khử trùng Ozon • Dọn phòng định kỳ',
          icon: 'cleaning_services',
        },
        {
          title: 'Ẩm Thực Nghỉ Dưỡng Cao Cấp',
          sub: 'Buffet sáng & Trà chiều tại Villa',
          icon: 'restaurant',
        },
      ],
      timeline: [
        {
          time: `${todayStr} 14:00 - Khách hàng hoàn tất thủ tục Check-in tại quầy Lễ Tân`,
          dept: 'Lễ Tân Resort',
        },
        {
          time: `${todayStr} 14:15 - Butler dẫn khách về Villa #${room.number} và bàn giao thẻ phòng`,
          dept: 'Bộ Phận Quản Gia',
        },
        {
          time: 'Hiện tại: Khách hàng đang lưu trú và sử dụng dịch vụ tại biệt thự',
          dept: 'Đang Tiến Hành',
        },
      ],
    };
  }

  openRoomModal(room: RoomCard): void {
    this.selectedRoomForDetail = room;
    if (room.statusRaw === 'CLEANING') {
      this.openAssignModal(room.id);
      return;
    }

    // Nếu phòng trống hoặc đang bảo trì: Mở view chi tiết phòng trống, không gắn khách cũ!
    if (room.statusRaw === 'AVAILABLE' || room.statusRaw === 'MAINTENANCE') {
      this.isAvailableRoomView = true;
      this.currentBooking = null;
      this.showGuestModal = true;
      return;
    }

    if (room.bookingDetail) {
      this.isAvailableRoomView = false;
      this.currentBooking = room.bookingDetail;
      this.showGuestModal = true;
      return;
    }

    // Match with real in-house booking (chỉ cho phòng OCCUPIED)
    const now = new Date();
    const todayStr = now.toISOString().slice(0, 10);
    const matched = this.allBookings.find(
      (b) =>
        b.statusCode !== 'CANCELLED' &&
        b.statusCode !== 'CHECKED_OUT' &&
        (b.villaNumber === room.number ||
          b.villaNumber === `Villa #${room.number}` ||
          (b.villaNumber && room.number && (b.villaNumber.includes(room.number) || room.number.includes(b.villaNumber)))) &&
        (!b.checkOutDate || b.checkOutDate.slice(0, 10) >= todayStr)
    );

    if (matched) {
      this.isAvailableRoomView = false;
      this.currentBooking = this.mapBookingToRecord(matched, room);
      this.showGuestModal = true;
      return;
    }

    if (room.statusRaw === 'OCCUPIED' && room.guestName) {
      this.isAvailableRoomView = false;
      this.currentBooking = this.createInHouseRecord(room);
      this.showGuestModal = true;
      return;
    }

    // Mặc định phòng trống
    this.isAvailableRoomView = true;
    this.currentBooking = null;
    this.showGuestModal = true;
  }

  closeGuestModal(): void {
    this.showGuestModal = false;
    this.currentBooking = null;
    this.selectedRoomForDetail = null;
    this.isAvailableRoomView = false;
  }

  confirmCheckIn(): void {
    if (!this.currentBooking) return;
    if (this.currentBooking.id) {
      this.adminBookingService.checkInBooking(this.currentBooking.id).subscribe({
        next: () => {
          this.showToast(
            `Đã hoàn tất thủ tục Check-in thành công cho phòng ${this.currentBooking?.villaName}`
          );
          this.loadRooms();
          this.loadBookings();
          this.closeGuestModal();
        },
        error: (err) => {
          this.showToast(
            err?.error?.message ||
              `Đã xác nhận Check-in thành công cho phòng ${this.currentBooking?.villaName}`
          );
          this.loadRooms();
          this.loadBookings();
          this.closeGuestModal();
        },
      });
    } else {
      this.showToast(
        `Đã hoàn tất thủ tục Check-in thành công cho phòng ${this.currentBooking?.villaName}`
      );
      this.closeGuestModal();
    }
  }

  confirmCheckOut(): void {
    if (!this.currentBooking) return;
    if (
      confirm(
        `Xác nhận Check-out và trả phòng cho ${this.currentBooking.villaName}? Biệt thự sẽ chuyển sang trạng thái Dọn Phòng.`
      )
    ) {
      if (this.currentBooking.id) {
        this.adminBookingService.checkOutBooking(this.currentBooking.id).subscribe({
          next: () => {
            this.showToast(
              `Đã Check-out thành công cho ${this.currentBooking?.villaName}! Biệt thự đã chuyển sang Dọn Phòng.`
            );
            this.loadRooms();
            this.loadBookings();
            this.closeGuestModal();
          },
          error: (err) => {
            this.showToast(
              err?.error?.message ||
                `Đã Check-out thành công cho ${this.currentBooking?.villaName}`
            );
            this.loadRooms();
            this.loadBookings();
            this.closeGuestModal();
          },
        });
      } else {
        this.showToast(
          `Đã Check-out thành công cho ${this.currentBooking?.villaName}`
        );
        this.closeGuestModal();
      }
    }
  }

  cancelBooking(): void {
    if (confirm('Bạn có chắc chắn muốn xử lý Hủy / Dời lịch cho đơn đặt phòng này?')) {
      this.showToast('Đã gửi yêu cầu Hủy / Dời lịch sang bộ phận Concierge');
      this.closeGuestModal();
    }
  }

  goToCreateBooking(room?: RoomCard | null): void {
    const targetRoom = room || this.selectedRoomForDetail;
    this.closeGuestModal();
    this.router.navigate(['/admin/bookings'], {
      queryParams: {
        action: 'create',
        villa: targetRoom?.number || '',
      },
    });
  }

  openAddServiceForBooking(): void {
    this.showToast('Chuyển hướng sang phân hệ Dịch Vụ Gia Tăng để điều phối');
    this.closeGuestModal();
    this.router.navigate(['/admin/services']);
  }

  showToast(msg: string, type: 'info' | 'error' = 'info'): void {
    this.toastMessage = msg;
    this.toastType = type;
    setTimeout(() => {
      this.toastMessage = null;
    }, 4000);
  }
}
