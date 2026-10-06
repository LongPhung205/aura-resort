import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Zone, ZonePayload, ZoneDetail } from '../models/zone.model';
import { ApiResponse } from '../models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class ZoneService {
  private readonly PUBLIC_API_URL = 'http://localhost:8080/api/v1/zones';
  private readonly ADMIN_API_URL = 'http://localhost:8080/api/v1/admin/zones';

  constructor(private http: HttpClient) {}

  // Public APIs
  getActiveZones(): Observable<Zone[]> {
    return this.http.get<ApiResponse<Zone[]>>(this.PUBLIC_API_URL).pipe(
      map(res => res.data || [])
    );
  }

  getZoneBySlug(slug: string): Observable<ZoneDetail> {
    return this.http.get<ApiResponse<ZoneDetail>>(`${this.PUBLIC_API_URL}/${slug}`).pipe(
      map(res => res.data)
    );
  }

  // Admin APIs
  getAllZones(): Observable<Zone[]> {
    return this.http.get<ApiResponse<Zone[]>>(this.ADMIN_API_URL).pipe(
      map(res => res.data || [])
    );
  }

  getZoneById(id: number): Observable<Zone> {
    return this.http.get<ApiResponse<Zone>>(`${this.ADMIN_API_URL}/${id}`).pipe(
      map(res => res.data)
    );
  }

  createZone(payload: ZonePayload): Observable<Zone> {
    return this.http.post<ApiResponse<Zone>>(this.ADMIN_API_URL, payload).pipe(
      map(res => res.data)
    );
  }

  updateZone(id: number, payload: ZonePayload): Observable<Zone> {
    return this.http.put<ApiResponse<Zone>>(`${this.ADMIN_API_URL}/${id}`, payload).pipe(
      map(res => res.data)
    );
  }

  deleteZone(id: number): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.ADMIN_API_URL}/${id}`).pipe(
      map(() => void 0)
    );
  }

  uploadImage(file: File): Observable<string> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http
      .post<ApiResponse<string>>('http://localhost:8080/api/v1/villas/upload-image', formData)
      .pipe(map(res => res.data));
  }
}
