export interface ServiceDispatch {
  id: number;
  bookingId?: number;
  bookingCode?: string;
  serviceType: string;
  assetCode?: string;
  guestName: string;
  roomNumber?: string;
  staffName?: string;
  pickupLocation?: string;
  destination?: string;
  scheduledTime: string;
  flightNumber?: string;
  status: 'SCHEDULED' | 'DISPATCHED' | 'IN_TRANSIT' | 'COMPLETED' | 'CANCELLED';
  cost?: number;
  notes?: string;
}

export interface ServiceDispatchRequest {
  bookingId?: number;
  serviceType: string;
  assetCode?: string;
  guestName?: string;
  roomNumber?: string;
  staffId?: number;
  pickupLocation?: string;
  destination?: string;
  scheduledTime: string;
  flightNumber?: string;
  cost?: number;
  notes?: string;
}
