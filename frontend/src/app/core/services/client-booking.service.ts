import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

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

interface ApiResponse<T> {
  status: string;
  message: string;
  data: T;
}

@Injectable({
  providedIn: 'root'
})
export class ClientBookingService {
  private readonly API_URL = `${environment.apiUrl}/bookings`;

  constructor(private http: HttpClient) {}

  getMyBookings(): Observable<ClientBooking[]> {
    return this.http.get<ApiResponse<ClientBooking[]>>(this.API_URL).pipe(
      map(res => res.data || [])
    );
  }

  getBookingById(id: number): Observable<ClientBooking> {
    return this.http.get<ApiResponse<ClientBooking>>(`${this.API_URL}/${id}`).pipe(
      map(res => res.data)
    );
  }

  cancelBooking(id: number): Observable<void> {
    return this.http.post<ApiResponse<void>>(`${this.API_URL}/${id}/cancel`, {}).pipe(
      map(() => void 0)
    );
  }
}
