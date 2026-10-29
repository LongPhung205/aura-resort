export interface ClientBooking {
  id: number;
  bookingCode: string;
  checkInDate: string;
  checkOutDate: string;
  totalAmount: number;
  status: 'PENDING' | 'CONFIRMED' | 'CHECKED_IN' | 'CHECKED_OUT' | 'CANCELLED';
  expireAt?: string;
  note?: string;
  userEmail?: string;
  guestName?: string;
  guestPhone?: string;
  bookedVillaNumbers?: string[];
  bookedRoomNumbers?: string[];
  // Extended UI fields
  villaName?: string;
  villaImage?: string;
  totalNights?: number;
  hasReview?: boolean;
}

