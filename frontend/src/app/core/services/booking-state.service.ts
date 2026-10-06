import { Injectable } from '@angular/core';
import { Villa } from '../models/villa.model';

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

@Injectable({
  providedIn: 'root'
})
export class BookingStateService {
  private state: BookingState | null = null;

  setState(state: BookingState): void {
    this.state = state;
  }

  getState(): BookingState | null {
    return this.state;
  }

  clearState(): void {
    this.state = null;
  }
}
