import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import {
  AdminBookingFilterParams,
  AdminBookingItem,
  GanttRoomAvailability,
} from '../models/admin-booking.model';
import { PageResponse } from '../models/page.model';
import { ApiResponse } from '../models/api-response.model';
import { buildHttpParams } from '../utils/http-params.util';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AdminBookingService {
  private readonly API_URL = `${environment.apiUrl}/admin/bookings`;

  constructor(private http: HttpClient) {}

  getBookings(filter: AdminBookingFilterParams): Observable<PageResponse<AdminBookingItem>> {
    const params = buildHttpParams(filter);

    return this.http
      .get<ApiResponse<PageResponse<AdminBookingItem>>>(this.API_URL, { params })
      .pipe(map((res) => res.data));
  }

  getGanttAvailability(startDate?: string, days = 7): Observable<GanttRoomAvailability[]> {
    let params = new HttpParams().set('days', days.toString());
    if (startDate) params = params.set('startDate', startDate);

    return this.http
      .get<ApiResponse<GanttRoomAvailability[]>>(`${this.API_URL}/gantt`, { params })
      .pipe(map((res) => res.data));
  }

  checkInBooking(id: number, verifyCode?: string): Observable<unknown> {
    let params = new HttpParams();
    if (verifyCode) {
      params = params.set('verifyCode', verifyCode.trim());
    }
    return this.http.post<ApiResponse<unknown>>(`${this.API_URL}/${id}/check-in`, {}, { params });
  }

  checkOutBooking(id: number): Observable<unknown> {
    return this.http.post<ApiResponse<unknown>>(`${this.API_URL}/${id}/check-out`, {});
  }

  createDirectBooking(data: {
    guestName: string;
    guestPhone?: string;
    guestEmail?: string;
    villaNumber?: string;
    checkInDate: string;
    checkOutDate: string;
    amount?: number;
    paymentMethod?: string;
    note?: string;
  }): Observable<unknown> {
    return this.http.post<ApiResponse<unknown>>(`${this.API_URL}/direct`, data);
  }
}
