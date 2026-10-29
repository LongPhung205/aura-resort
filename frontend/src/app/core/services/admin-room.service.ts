import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import {
  AdminRoomItem,
  AdminRoomTypeItem,
  UpdateRoomTypePayload,
  ZoneItem,
  CreateRoomPayload,
  UpdateRoomPayload,
} from '../models/admin-room.model';
import { ApiResponse } from '../models/api-response.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AdminRoomService {
  private readonly ROOMS_API_URL = `${environment.apiUrl}/villas`;
  private readonly ROOM_TYPES_API_URL = `${environment.apiUrl}/villa-types`;
  private readonly ZONES_API_URL = `${environment.apiUrl}/admin/zones`;

  constructor(private http: HttpClient) {}

  // --- ZONES ---
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

  // --- ROOMS / VILLAS ---
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

  updateRoomStatus(id: number, status: string): Observable<AdminRoomItem> {
    const params = new HttpParams().set('status', status);
    return this.http
      .patch<ApiResponse<AdminRoomItem>>(`${this.ROOMS_API_URL}/${id}/status`, {}, { params })
      .pipe(map((res) => res.data));
  }

  uploadImage(file: File): Observable<string> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http
      .post<ApiResponse<string>>(`${this.ROOMS_API_URL}/upload-image`, formData)
      .pipe(map((res) => res.data));
  }

  // --- ROOM TYPES ---
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
}

