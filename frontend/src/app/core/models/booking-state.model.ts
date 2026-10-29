/* eslint-disable @typescript-eslint/no-explicit-any */
import { Villa } from './villa.model';

export interface BookingState {
  villa: Villa | null;
  checkInDate: string;
  checkOutDate: string;
  numberOfGuests?: number;
  serviceSelectionMode?: 'individual' | 'combo';
  selectedServiceIds?: number[];
  selectedComboId?: number | null;
  promoCode?: string;
  discountPercent?: number;
  appliedPromotion?: any;
}
