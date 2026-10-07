import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { HomeBanner, HomeBannerPayload } from '../models/banner.model';
import { ApiResponse } from '../models/auth.model';

import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AdminBannerService {
  private readonly API_URL = `${environment.apiUrl}/admin/banners`;

  constructor(private http: HttpClient) {}

  getAllBanners(): Observable<HomeBanner[]> {
    return this.http.get<ApiResponse<HomeBanner[]>>(this.API_URL).pipe(
      map(res => res.data || [])
    );
  }

  getBannerById(id: number): Observable<HomeBanner> {
    return this.http.get<ApiResponse<HomeBanner>>(`${this.API_URL}/${id}`).pipe(
      map(res => res.data)
    );
  }

  createBanner(payload: HomeBannerPayload): Observable<HomeBanner> {
    return this.http.post<ApiResponse<HomeBanner>>(this.API_URL, payload).pipe(
      map(res => res.data)
    );
  }

  updateBanner(id: number, payload: HomeBannerPayload): Observable<HomeBanner> {
    return this.http.put<ApiResponse<HomeBanner>>(`${this.API_URL}/${id}`, payload).pipe(
      map(res => res.data)
    );
  }

  toggleBannerStatus(id: number): Observable<void> {
    return this.http.patch<ApiResponse<void>>(`${this.API_URL}/${id}/toggle`, {}).pipe(
      map(() => void 0)
    );
  }

  deleteBanner(id: number): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.API_URL}/${id}`).pipe(
      map(() => void 0)
    );
  }

  uploadImage(file: File): Observable<string> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http
      .post<ApiResponse<string>>(`${environment.apiUrl}/villas/upload-image`, formData)
      .pipe(map(res => res.data));
  }
}
