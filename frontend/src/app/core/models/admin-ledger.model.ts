export interface LedgerItem {
  id: number;
  transactionId?: string;
  referenceNo?: string;
  bookingCode?: string;
  bookingStatus?: string;
  guestName: string;
  guestPhone?: string;
  guestEmail?: string;
  roomName?: string;
  amount: number;
  paymentMethod: string;
  ledgerType: string;
  status: string;
  paymentTime: string;
  reconciliationNote?: string;
  reconciliationTime?: string;
  reconciledBy?: string;
}

export interface LedgerItemRequest {
  title?: string;
  guestName: string;
  amount: number;
  paymentMethod: string;
  ledgerType: string;
  referenceNo?: string;
  notes?: string;
}

export interface DayEndClosingReport {
  id: number;
  closingDate: string;
  totalRevenue: number;
  roomRevenue: number;
  serviceRevenue: number;
  totalOpex: number;
  netCash: number;
  occupancyRate: number;
  adr?: number;
  revPar?: number;
  totalBookings?: number;
  occupiedRooms?: number;
  closedByName?: string;
  closedAt?: string;
  status: string;
  notes?: string;
}

export interface DayEndClosingRequest {
  closingDate?: string;
  notes?: string;
}

export interface ReconcileRequest {
  status: string;
  note: string;
}

export interface DailyRevenue {
  date: string;
  revenue: number;
  roomRevenue?: number;
  serviceRevenue?: number;
}

export interface MonthlyRevenue {
  month: string;
  revenue: number;
  roomRevenue?: number;
  serviceRevenue?: number;
}

export interface PaymentMethodStat {
  method: string;
  transactionCount: number;
  actualRevenue: number;
  pendingRevenue: number;
  percentage?: number;
}

export interface PaymentDashboardStatsResponse {
  totalRevenue: number;
  roomRevenue: number;
  serviceRevenue: number;
  collectedAmount: number;
  pendingAmount: number;
  refundedAmount: number;
  successRate: number;
  aov: number;
  growthRate: number;
  totalTransactions: number;
  lineChartData: DailyRevenue[];
  monthlyChartData: MonthlyRevenue[];
  donutChartData: { [key: string]: number };
  methodStats: PaymentMethodStat[];
}
