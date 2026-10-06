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

export interface ChildRoomItem {
  id?: number;
  roomNumber: string;
  name?: string;
  description?: string;
  floor?: number;
  status?: string;
  roomTypeId?: number;
  roomTypeName?: string;
  bedType?: string;
  adults?: number;
  children?: number;
  capacity?: number;
  zone?: string;
}

export interface AdminRoomItem {
  id: number;
  roomNumber: string;
  floor: number;
  structureType?: string;
  basePrice?: number;
  status: string;
  roomTypeId?: number;
  roomTypeName?: string;
  zone?: string;
  ozoneStatus?: string;
  amenities?: string[];
  lastCleanedAt?: string;
  currentGuestName?: string;
  villaNumber?: string;
  villaTypeName?: string;
  villaTypeId?: number;
  bedroomCount?: number;
  totalBeds?: number;
  totalAdults?: number;
  totalChildren?: number;
  totalCapacity?: number;
  childRooms?: ChildRoomItem[];
  rooms?: ChildRoomItem[];
  imageUrl?: string;
  images?: string[];
  area?: number;
  viewDirection?: string;
  poolSize?: number;
  overviewDescription?: string;
}

export interface AdminRoomTypeItem {
  id: number;
  name: string;
  description?: string;
  basePrice?: number;
  dynamicPrice?: number;
  capacity?: number;
  adults?: number;
  children?: number;
  bedType?: string;
  imageUrl?: string;
}

export interface UpdateRoomTypePayload {
  name: string;
  description?: string;
  basePrice: number;
  capacity: number;
  adults?: number;
  children?: number;
  bedType?: string;
  imageUrl?: string;
}

export interface ZoneItem {
  id?: number;
  name: string;
  matchKey?: string;
  tag?: string;
  icon?: string;
  badgeClass?: string;
  description?: string;
  isActive?: boolean;
  villaCount?: number;
  createdAt?: string;
}

export interface CreateRoomPayload {
  roomNumber: string;
  villaNumber?: string;
  floor: number;
  structureType?: string;
  roomTypeId?: number;
  villaTypeId?: number;
  zoneId?: number;
  zone?: string;
  status?: string;
  ozoneStatus?: string;
  amenities?: string[];
  basePrice?: number;
  price?: number;
  bedroomCount?: number;
  bedSelections?: { roomTypeId: number; quantity: number }[];
  childRooms?: {
    roomNumber: string;
    name?: string;
    floor?: number;
    roomTypeId: number;
  }[];
  imageUrl?: string;
  images?: string[];
  area?: number;
  viewDirection?: string;
  poolSize?: number;
  overviewDescription?: string;
}

export type UpdateRoomPayload = CreateRoomPayload;

interface ApiResponse<T> {
  status?: string;
  success?: boolean;
  message: string;
  data: T;
}

@Injectable({
  providedIn: 'root',
})
export class AdminHousekeepingService {
  private readonly API_URL = 'http://localhost:8080/api/v1/admin/housekeeping';
  private readonly ROOMS_API_URL = 'http://localhost:8080/api/v1/villas';
  private readonly ROOM_TYPES_API_URL = 'http://localhost:8080/api/v1/villa-types';
  private readonly ZONES_API_URL = 'http://localhost:8080/api/v1/admin/zones';

  constructor(private http: HttpClient) {}

  getZones(): Observable<ZoneItem[]> {
    return this.http.get<ApiResponse<ZoneItem[]>>(this.ZONES_API_URL).pipe(
      map((res) => res.data || []),
      catchError(() => of([]))
    );
  }

  getZoneById(id: number): Observable<ZoneItem> {
    return this.http
      .get<ApiResponse<ZoneItem>>(`${this.ZONES_API_URL}/${id}`)
      .pipe(map((res) => res.data));
  }

  createZone(payload: Partial<ZoneItem>): Observable<ZoneItem> {
    return this.http
      .post<ApiResponse<ZoneItem>>(this.ZONES_API_URL, payload)
      .pipe(map((res) => res.data));
  }

  updateZone(id: number, payload: Partial<ZoneItem>): Observable<ZoneItem> {
    return this.http
      .put<ApiResponse<ZoneItem>>(`${this.ZONES_API_URL}/${id}`, payload)
      .pipe(map((res) => res.data));
  }

  deleteZone(id: number): Observable<void> {
    return this.http
      .delete<ApiResponse<void>>(`${this.ZONES_API_URL}/${id}`)
      .pipe(map(() => void 0));
  }

  getRooms(typeId?: number, status?: string, zone?: string): Observable<AdminRoomItem[]> {
    let params = new HttpParams();
    if (typeId) params = params.set('typeId', typeId.toString());
    if (status) params = params.set('status', status);
    if (zone && zone !== 'ALL') params = params.set('zone', zone);

    return this.http.get<ApiResponse<AdminRoomItem[]>>(this.ROOMS_API_URL, { params }).pipe(
      map((res) => res.data),
      catchError(() => of([]))
    );
  }

  getRoomTypes(): Observable<AdminRoomTypeItem[]> {
    return this.http.get<ApiResponse<AdminRoomTypeItem[]>>(this.ROOM_TYPES_API_URL).pipe(
      map((res) => res.data),
      catchError(() => of([]))
    );
  }

  createRoomType(payload: UpdateRoomTypePayload): Observable<AdminRoomTypeItem> {
    return this.http
      .post<ApiResponse<AdminRoomTypeItem>>(this.ROOM_TYPES_API_URL, payload)
      .pipe(map((res) => res.data));
  }

  updateRoomType(id: number, payload: UpdateRoomTypePayload): Observable<AdminRoomTypeItem> {
    return this.http
      .put<ApiResponse<AdminRoomTypeItem>>(`${this.ROOM_TYPES_API_URL}/${id}`, payload)
      .pipe(map((res) => res.data));
  }

  deleteRoomType(id: number): Observable<void> {
    return this.http
      .delete<ApiResponse<void>>(`${this.ROOM_TYPES_API_URL}/${id}`)
      .pipe(map(() => undefined));
  }

  createRoom(payload: CreateRoomPayload): Observable<AdminRoomItem> {
    return this.http
      .post<ApiResponse<AdminRoomItem>>(this.ROOMS_API_URL, payload)
      .pipe(map((res) => res.data));
  }

  updateRoom(id: number, payload: UpdateRoomPayload): Observable<AdminRoomItem> {
    return this.http
      .put<ApiResponse<AdminRoomItem>>(`${this.ROOMS_API_URL}/${id}`, payload)
      .pipe(map((res) => res.data));
  }

  deleteRoom(id: number): Observable<void> {
    return this.http
      .delete<ApiResponse<void>>(`${this.ROOMS_API_URL}/${id}`)
      .pipe(map(() => undefined));
  }

  uploadImage(file: File): Observable<string> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http
      .post<ApiResponse<string>>(`${this.ROOMS_API_URL}/upload-image`, formData)
      .pipe(map((res) => res.data));
  }

  updateRoomStatus(id: number, status: string): Observable<AdminRoomItem> {
    const params = new HttpParams().set('status', status);
    return this.http
      .patch<ApiResponse<AdminRoomItem>>(`${this.ROOMS_API_URL}/${id}/status`, {}, { params })
      .pipe(map((res) => res.data));
  }

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
        `http://localhost:8080/api/v1/admin/billing/consumptions/pending?bookingId=${bookingId}`
      )
      .pipe(
        map((res) => res.data || []),
        catchError(() => of([]))
      );
  }

  approveConsumption(id: number): Observable<RoomConsumptionRecord> {
    return this.http
      .post<ApiResponse<RoomConsumptionRecord>>(
        `http://localhost:8080/api/v1/admin/billing/consumptions/${id}/approve`,
        {}
      )
      .pipe(map((res) => res.data));
  }

  waiveConsumption(id: number, reason?: string): Observable<RoomConsumptionRecord> {
    const url = reason
      ? `http://localhost:8080/api/v1/admin/billing/consumptions/${id}/waive?reason=${encodeURIComponent(reason)}`
      : `http://localhost:8080/api/v1/admin/billing/consumptions/${id}/waive`;
    return this.http.post<ApiResponse<RoomConsumptionRecord>>(url, {}).pipe(map((res) => res.data));
  }
}
