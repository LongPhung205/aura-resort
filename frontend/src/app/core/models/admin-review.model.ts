export interface ServiceRecoveryTicket {
  id: number;
  reviewId?: number;
  guestName: string;
  roomNumber?: string;
  incidentCategory?: string;
  issueSummary: string;
  resolutionAction?: string;
  assignedManagerName?: string;
  slaMinutes: number;
  actualResolutionMinutes?: number;
  status: 'OPEN' | 'IN_PROGRESS' | 'RESOLVED' | 'ESCALATED';
  createdAt?: string;
  resolvedAt?: string;
}

export interface ReviewItem {
  id: number;
  rating: number;
  comment: string;
  sentiment?: 'POSITIVE' | 'NEUTRAL' | 'NEGATIVE' | string;
  managementReply?: string;
  userName?: string;
  guestName?: string;
  userEmail?: string;
  roomTypeName?: string;
  villaName?: string;
  bookingId?: number;
  bookingCode?: string;
  createdAt?: string;
  repliedAt?: string;
}

export interface ReviewAnalytics {
  csatScore: number;
  npsScore: number;
  totalReviews: number;
  positiveCount: number;
  neutralCount: number;
  negativeCount: number;
  responseRate: number;
  averageResponseMinutes: number;
  latestReviews: ReviewItem[];
  openRecoveryTickets: ServiceRecoveryTicket[];
}
