import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { HomeBanner, HomeBannerPayload } from '../models/banner.model';
import { ApiResponse } from '../models/api-response.model';
import { BaseCrudService } from './base-crud.service';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AdminBannerService extends BaseCrudService<HomeBanner, HomeBannerPayload> {
  private readonly BANNER_API_URL = `${environment.apiUrl}/admin/banners`;

  constructor(http: HttpClient) {
    super(http, `${environment.apiUrl}/admin/banners`);
  }

  // Alias methods cho tính tương thích ngược
  getAllBanners(): Observable<HomeBanner[]> {
    return this.getAll();
  }

  getBannerById(id: number): Observable<HomeBanner> {
    return this.getById(id);
  }

  createBanner(payload: HomeBannerPayload): Observable<HomeBanner> {
    return this.create(payload);
  }

  updateBanner(id: number, payload: HomeBannerPayload): Observable<HomeBanner> {
    return this.update(id, payload);
  }

  deleteBanner(id: number): Observable<void> {
    return this.delete(id);
  }

  toggleBannerStatus(id: number): Observable<void> {
    return this.http.patch<ApiResponse<void>>(`${this.BANNER_API_URL}/${id}/toggle`, {}).pipe(
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
