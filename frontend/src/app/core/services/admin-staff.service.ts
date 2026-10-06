import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { StaffRosterResponse, ShiftSwapRequest, WeeklyShiftRegistration } from '../models/admin-staff.model';

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

@Injectable({
  providedIn: 'root',
})
export class AdminStaffService {
  private readonly API_URL = `${environment.apiUrl}/admin/staff`;

  constructor(private http: HttpClient) {}

  getRoster(startDate?: string): Observable<StaffRosterResponse> {
    let params = new HttpParams();
    if (startDate) {
      params = params.set('startDate', startDate);
    }

    return this.http.get<ApiResponse<StaffRosterResponse>>(`${this.API_URL}/roster`, { params }).pipe(
      map((res) => res.data),
      catchError((err) => {
        console.error('Failed to load roster:', err);
        return of(this.getMockRoster(startDate));
      })
    );
  }

  getSwapRequests(): Observable<ShiftSwapRequest[]> {
    return this.http.get<ApiResponse<ShiftSwapRequest[]>>(`${this.API_URL}/swap-requests`).pipe(
      map((res) => res.data),
      catchError((err) => {
        console.error('Failed to load swap requests:', err);
        return of([]);
      })
    );
  }

  processSwap(requestId: number, approved: boolean, rejectReason?: string): Observable<ShiftSwapRequest | null> {
    return this.http.post<ApiResponse<ShiftSwapRequest>>(`${this.API_URL}/swap-requests/action`, {
      requestId,
      approved,
      rejectReason,
    }).pipe(
      map((res) => res.data),
      catchError((err) => {
        console.error('Failed to process swap:', err);
        return of(null);
      })
    );
  }

  updateScheduleCell(payload: { staffId: number; workDate: string; shiftType: string; note?: string }): Observable<any> {
    return this.http.post<ApiResponse<any>>(`${this.API_URL}/schedule/cell`, payload).pipe(
      map((res) => res.data),
      catchError((err) => {
        console.error('Failed to update schedule cell:', err);
        return of(null);
      })
    );
  }

  generateAiRoster(startDate?: string): Observable<StaffRosterResponse | null> {
    let params = new HttpParams();
    if (startDate) {
      params = params.set('startDate', startDate);
    }
    return this.http.post<ApiResponse<StaffRosterResponse>>(`${this.API_URL}/schedule/ai-generate`, {}, { params }).pipe(
      map((res) => res.data),
      catchError((err) => {
        console.error('Failed to generate AI roster:', err);
        return of(null);
      })
    );
  }

  getWeeklyRegistrations(): Observable<WeeklyShiftRegistration[]> {
    return this.http.get<ApiResponse<WeeklyShiftRegistration[]>>(`${this.API_URL}/weekly-registrations`).pipe(
      map((res) => res.data || []),
      catchError((err) => {
        console.error('Failed to load weekly registrations:', err);
        return of([]);
      })
    );
  }

  processWeeklyRegistration(registrationId: number, approved: boolean, rejectionReason?: string): Observable<WeeklyShiftRegistration | null> {
    return this.http.post<ApiResponse<WeeklyShiftRegistration>>(`${this.API_URL}/weekly-registrations/action`, {
      registrationId,
      approved,
      rejectionReason,
    }).pipe(
      map((res) => res.data),
      catchError((err) => {
        console.error('Failed to process weekly registration:', err);
        return of(null);
      })
    );
  }

  createStaff(payload: any): Observable<any> {
    return this.http.post<ApiResponse<any>>(`${environment.apiUrl}/admin/users`, payload).pipe(
      map((res) => res.data),
      catchError((err) => {
        console.error('Failed to create staff:', err);
        return of(null);
      })
    );
  }

  private getMockRoster(startDate?: string): StaffRosterResponse {
    const start = startDate ? new Date(startDate) : new Date();
    const days = [0, 1, 2, 3, 4, 5, 6].map((offset) => {
      const d = new Date(start);
      d.setDate(d.getDate() + offset);
      return d.toISOString().split('T')[0];
    });

    return {
      weekStartDate: days[0],
      weekEndDate: days[6],
      totalStaff: 0,
      onDutyToday: 0,
      lateToday: 0,
      onLeaveToday: 0,
      staffMembers: [],
    };
  }
}
