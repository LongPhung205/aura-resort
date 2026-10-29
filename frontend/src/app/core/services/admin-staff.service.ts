import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { StaffRosterResponse, ShiftSwapRequest, WeeklyShiftRegistration } from '../models/admin-staff.model';
import { ApiResponse } from '../models/api-response.model';

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
      map((res) => res.data)
    );
  }

  getSwapRequests(): Observable<ShiftSwapRequest[]> {
    return this.http.get<ApiResponse<ShiftSwapRequest[]>>(`${this.API_URL}/swap-requests`).pipe(
      map((res) => res.data || [])
    );
  }

  processSwap(requestId: number, approved: boolean, rejectReason?: string): Observable<ShiftSwapRequest> {
    return this.http.post<ApiResponse<ShiftSwapRequest>>(`${this.API_URL}/swap-requests/action`, {
      requestId,
      approved,
      rejectReason,
    }).pipe(
      map((res) => res.data)
    );
  }

  updateScheduleCell(payload: { staffId: number; workDate: string; shiftType: string; note?: string }): Observable<unknown> {
    return this.http.post<ApiResponse<unknown>>(`${this.API_URL}/schedule/cell`, payload).pipe(
      map((res) => res.data)
    );
  }

  generateAiRoster(startDate?: string): Observable<StaffRosterResponse> {
    let params = new HttpParams();
    if (startDate) {
      params = params.set('startDate', startDate);
    }
    return this.http.post<ApiResponse<StaffRosterResponse>>(`${this.API_URL}/schedule/ai-generate`, {}, { params }).pipe(
      map((res) => res.data)
    );
  }

  getWeeklyRegistrations(): Observable<WeeklyShiftRegistration[]> {
    return this.http.get<ApiResponse<WeeklyShiftRegistration[]>>(`${this.API_URL}/weekly-registrations`).pipe(
      map((res) => res.data || [])
    );
  }

  processWeeklyRegistration(registrationId: number, approved: boolean, rejectionReason?: string): Observable<WeeklyShiftRegistration> {
    return this.http.post<ApiResponse<WeeklyShiftRegistration>>(`${this.API_URL}/weekly-registrations/action`, {
      registrationId,
      approved,
      rejectionReason,
    }).pipe(
      map((res) => res.data)
    );
  }

  createStaff(payload: unknown): Observable<unknown> {
    return this.http.post<ApiResponse<unknown>>(`${environment.apiUrl}/admin/users`, payload).pipe(
      map((res) => res.data)
    );
  }
}
