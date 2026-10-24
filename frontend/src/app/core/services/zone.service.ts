import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Zone, ZonePayload, ZoneDetail } from '../models/zone.model';
import { ApiResponse } from '../models/api-response.model';
import { BaseCrudService } from './base-crud.service';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ZoneService extends BaseCrudService<Zone, ZonePayload> {
  private readonly PUBLIC_API_URL = `${environment.apiUrl}/zones`;

  constructor(http: HttpClient) {
    super(http, `${environment.apiUrl}/admin/zones`);
  }

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

  // Admin APIs (Alias methods cho tính tương thích ngược)
  getAllZones(): Observable<Zone[]> {
    return this.getAll();
  }

  getZoneById(id: number): Observable<Zone> {
    return this.getById(id);
  }

  createZone(payload: ZonePayload): Observable<Zone> {
    return this.create(payload);
  }

  updateZone(id: number, payload: ZonePayload): Observable<Zone> {
    return this.update(id, payload);
  }

  deleteZone(id: number): Observable<void> {
    return this.delete(id);
  }

  uploadImage(file: File): Observable<string> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http
      .post<ApiResponse<string>>(`${environment.apiUrl}/villas/upload-image`, formData)
      .pipe(map(res => res.data));
  }
}
