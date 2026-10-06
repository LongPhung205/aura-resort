export interface AdminBookingItem {
  id: number;
  bookingCode: string;
  bookingDateFormatted?: string;
  bookingDate?: string;
  guestName: string;
  guestEmail?: string;
  guestPhone?: string;
  avatarUrl?: string;
  guestCountry?: string;
  guestTier?: string;
  tierBadgeColor?: string;
  villaNumber?: string;
  villaTypeName?: string;
  roomTypeName?: string;
  checkInFormatted?: string;
  checkInDate?: string;
  checkOutFormatted?: string;
  checkOutDate?: string;
  nights?: number;
  guestSummary?: string;
  channel?: string;
  channelBadgeColor?: string;
  totalAmount?: number;
  totalAmountDisplay?: string;
  paymentStatusDisplay?: string;
  paymentMethod?: string;
  isFullyPaid?: boolean;
  extraServiceName?: string;
  extraServiceIcon?: string;
  assignedButler?: string;
  statusCode?: string;
  statusLabel?: string;
  statusBadgeColor?: string;
  note?: string;

  // Legacy aliases
  vipTier?: string;
  vipBadgeClass?: string;
  villaName?: string;
  totalAmountVnd?: number;
  totalAmountFormatted?: string;
  paymentStatus?: string;
  paymentBadgeClass?: string;
  butlerName?: string;
  butlerAvatar?: string;
  bookingStatus?: string;
  statusBadgeClass?: string;
  specialRequests?: string;
}

export interface GanttDaySlot {
  dateLabel: string;
  status: 'OCCUPIED' | 'CONFIRMED' | 'AVAILABLE' | 'CLEANING' | 'MAINTENANCE' | string;
  guestName?: string | null;
  spanDays?: number;
  nightsSpan?: number;
  isSpanStart?: boolean;
  isStartOfSpan?: boolean;
  blockLabel?: string;
  barLabel?: string;
  colorClass?: string;
  badgeClass?: string;
  bookingId?: number;
  bookingCode?: string;
}

export interface GanttVillaAvailability {
  villaId?: number;
  villaNumber: string;
  villaName?: string;
  villaTypeName?: string;
  villaCategory?: string;
  zone?: string;
  statusTag: string;
  statusTagClass: string;
  daySlots: GanttDaySlot[];
  dailySlots?: GanttDaySlot[];
  roomId?: number;
  roomNumber?: string;
  roomName?: string;
  roomCategory?: string;
}

export interface GanttRoomAvailability {
  roomId?: number;
  roomNumber?: string;
  roomName?: string;
  roomCategory?: string;
  villaId?: number;
  villaNumber: string;
  villaName?: string;
  villaTypeName?: string;
  roomTypeName?: string;
  statusTag: string;
  statusTagClass: string;
  daySlots?: GanttDaySlot[];
  dailySlots?: GanttDaySlot[];
  zone?: string;
  zoneName?: string;
}

export interface AdminBookingFilterParams {
  tab?: string;
  search?: string;
  status?: string;
  startDate?: string;
  endDate?: string;
  page?: number;
  size?: number;
}

export interface AdminPageResponse<T> {
  content: T[];
  pageNo: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}
