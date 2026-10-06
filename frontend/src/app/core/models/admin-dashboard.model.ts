export interface RevenueTrendPoint {
  day: string;
  revenueMillion: number;
  occupancyPercent: number;
  isToday: boolean;
}

export interface FieldButlerDispatch {
  id: string;
  initials: string;
  butlerName: string;
  villaAssignment: string;
  task: string;
  statusBadge: string;
  badgeColor: string;
  isGpsActive: boolean;
}

export interface ModuleMatrixStatus {
  moduleCode?: string;
  title?: string;
  description?: string;
  metricValue?: string;
  metricLabel?: string;
  statusBadge?: string;
  badgeClass?: string;
  iconName?: string;
  actionRoute?: string;
  index?: number;
  name?: string;
  statusText?: string;
  badge?: string;
  badgeColor?: string;
  icon?: string;
  highlightInfo?: string;
}

export interface VipArrivalDeparture {
  id: string;
  initials: string;
  guestName: string;
  tier: string;
  tierBadgeColor: string;
  assignedVilla: string;
  flightOrRoute: string;
  transport: string;
  butler: string;
  specialRequest: string;
  status: string;
  statusColor: string;
  isArrival: boolean;
}

export interface DashboardStatsResponse {
  occupancyRate: number;
  occupancyMoM: string;
  occupiedVillas: number;
  totalVillas: number;
  seasonStatus: string;
  todayRevenue: string;
  revenueTargetPercent: number;
  adr: string;
  revpar: string;
  vipInHouseCount: number;
  anniversaryCouplesCount: number;
  butlerCoverage: string;
  csatRating: number;
  fiveStarReviewsCount: number;
  unresolvedComplaintsCount: number;
  forecastNext3Days: string;
  highestSegment: string;
  primaryChannel: string;
  weatherCondition: string;
  revenueTrend: RevenueTrendPoint[];
  fieldDispatches: FieldButlerDispatch[];
  moduleStatuses: ModuleMatrixStatus[];
  vipArrivalDepartures: VipArrivalDeparture[];
}
