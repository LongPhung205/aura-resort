export interface ClientReview {
  id: number;
  rating: number;
  comment: string;
  userEmail: string;
  userName: string;
  createdAt: string;
  bookingId?: number;
  bookingCode?: string;
  villaName?: string;
  sentiment?: string;
  managementReply?: string;
  repliedAt?: string;
}

export interface CreateReviewRequest {
  bookingId: number;
  rating: number;
  comment: string;
}

