import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import {
  HousekeepingTask,
  HousekeeperSummary,
  AssignHousekeepingTaskRequest,
  UpdateCleaningProgressRequest,
  HousekeepingChecklistRequest,
} from '../models/admin-housekeeping.model';
import {
  LostAndFoundItem,
  MaintenanceTicket,
  RoomConsumptionRecord,
} from '../models/housekeeping.model';
import { ApiResponse } from '../models/api-response.model';
import { environment } from '../../../environments/environment';

// Re-export models for backwards compatibility
export type {
  ChildRoomItem,
  AdminRoomItem,
  AdminRoomTypeItem,
  UpdateRoomTypePayload,
  ZoneItem,
  CreateRoomPayload,
  UpdateRoomPayload,
} from '../models/admin-room.model';

@Injectable({
  providedIn: 'root',
})
export class AdminHousekeepingService {
  private readonly API_URL = `${environment.apiUrl}/admin/housekeeping`;

  constructor(private http: HttpClient) {}

  getTasks(status = 'ALL'): Observable<HousekeepingTask[]> {
    const params = new HttpParams().set('status', status);
    return this.http.get<ApiResponse<HousekeepingTask[]>>(`${this.API_URL}/tasks`, { params }).pipe(
      map((res) => res.data),
      catchError(() => of([]))
    );
  }

  getHousekeepers(): Observable<HousekeeperSummary[]> {
    return this.http.get<ApiResponse<HousekeeperSummary[]>>(`${this.API_URL}/housekeepers`).pipe(
      map((res) => res.data),
      catchError(() => of([]))
    );
  }

  getMyTasks(email?: string, housekeeperId?: number): Observable<HousekeepingTask[]> {
    let params = new HttpParams();
    if (email) params = params.set('email', email);
    if (housekeeperId) params = params.set('housekeeperId', housekeeperId.toString());

    return this.http
      .get<ApiResponse<HousekeepingTask[]>>(`${this.API_URL}/my-tasks`, { params })
      .pipe(
        map((res) => res.data),
        catchError(() => of([]))
      );
  }

  assignTask(request: AssignHousekeepingTaskRequest): Observable<HousekeepingTask> {
    return this.http
      .post<ApiResponse<HousekeepingTask>>(`${this.API_URL}/tasks/assign`, request)
      .pipe(map((res) => res.data));
  }

  startCleaning(taskId: number): Observable<HousekeepingTask> {
    return this.http
      .post<ApiResponse<HousekeepingTask>>(`${this.API_URL}/tasks/${taskId}/start-cleaning`, {})
      .pipe(map((res) => res.data));
  }

  startOzone(taskId: number): Observable<HousekeepingTask> {
    return this.startCleaning(taskId);
  }

  updateProgress(request: UpdateCleaningProgressRequest): Observable<HousekeepingTask> {
    return this.http
      .post<ApiResponse<HousekeepingTask>>(`${this.API_URL}/tasks/progress`, request)
      .pipe(map((res) => res.data));
  }

  completeCleaning(taskId: number, note?: string): Observable<HousekeepingTask> {
    let params = new HttpParams();
    if (note) params = params.set('note', note);
    return this.http
      .post<ApiResponse<HousekeepingTask>>(
        `${this.API_URL}/tasks/${taskId}/complete`,
        {},
        { params }
      )
      .pipe(map((res) => res.data));
  }

  submitChecklist(request: HousekeepingChecklistRequest): Observable<HousekeepingTask> {
    return this.http
      .post<ApiResponse<HousekeepingTask>>(`${this.API_URL}/tasks/checklist`, request)
      .pipe(map((res) => res.data));
  }

  approveTask(taskId: number): Observable<HousekeepingTask> {
    return this.http
      .post<ApiResponse<HousekeepingTask>>(`${this.API_URL}/tasks/${taskId}/approve`, {})
      .pipe(map((res) => res.data));
  }

  rejectTask(taskId: number, reason: string): Observable<HousekeepingTask> {
    return this.http
      .post<ApiResponse<HousekeepingTask>>(
        `${this.API_URL}/tasks/${taskId}/reject?reason=${encodeURIComponent(reason)}`,
        {}
      )
      .pipe(map((res) => res.data));
  }

  getLostAndFoundList(status?: string, villaId?: number): Observable<LostAndFoundItem[]> {
    let params = new HttpParams();
    if (status) params = params.set('status', status);
    if (villaId) params = params.set('villaId', villaId.toString());
    return this.http
      .get<ApiResponse<LostAndFoundItem[]>>(`${this.API_URL}/lost-found`, { params })
      .pipe(
        map((res) => res.data || []),
        catchError(() => of([]))
      );
  }

  updateLostAndFoundStatus(
    id: number,
    status: string,
    note?: string
  ): Observable<LostAndFoundItem> {
    let url = `${this.API_URL}/lost-found/${id}/status?status=${status}`;
    if (note) url += `&note=${encodeURIComponent(note)}`;
    return this.http.put<ApiResponse<LostAndFoundItem>>(url, {}).pipe(map((res) => res.data));
  }

  getMaintenanceTickets(status?: string, villaId?: number): Observable<MaintenanceTicket[]> {
    let params = new HttpParams();
    if (status) params = params.set('status', status);
    if (villaId) params = params.set('villaId', villaId.toString());
    return this.http
      .get<ApiResponse<MaintenanceTicket[]>>(`${this.API_URL}/maintenance-tickets`, { params })
      .pipe(
        map((res) => res.data || []),
        catchError(() => of([]))
      );
  }

  updateMaintenanceStatus(
    id: number,
    status: string,
    note?: string,
    technicianName?: string
  ): Observable<MaintenanceTicket> {
    let url = `${this.API_URL}/maintenance-tickets/${id}/status?status=${status}`;
    if (note) url += `&note=${encodeURIComponent(note)}`;
    if (technicianName) url += `&technicianName=${encodeURIComponent(technicianName)}`;
    return this.http.put<ApiResponse<MaintenanceTicket>>(url, {}).pipe(map((res) => res.data));
  }

  getPendingConsumptions(bookingId: number): Observable<RoomConsumptionRecord[]> {
    return this.http
      .get<ApiResponse<RoomConsumptionRecord[]>>(
        `${environment.apiUrl}/admin/billing/consumptions/pending?bookingId=${bookingId}`
      )
      .pipe(
        map((res) => res.data || []),
        catchError(() => of([]))
      );
  }

  approveConsumption(id: number): Observable<RoomConsumptionRecord> {
    return this.http
      .post<ApiResponse<RoomConsumptionRecord>>(
        `${environment.apiUrl}/admin/billing/consumptions/${id}/approve`,
        {}
      )
      .pipe(map((res) => res.data));
  }

  waiveConsumption(id: number, reason?: string): Observable<RoomConsumptionRecord> {
    const url = reason
      ? `${environment.apiUrl}/admin/billing/consumptions/${id}/waive?reason=${encodeURIComponent(reason)}`
      : `${environment.apiUrl}/admin/billing/consumptions/${id}/waive`;
    return this.http.post<ApiResponse<RoomConsumptionRecord>>(url, {}).pipe(map((res) => res.data));
  }
}
