import { Injectable } from '@angular/core';
import { BookingState } from '../models/booking-state.model';

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
