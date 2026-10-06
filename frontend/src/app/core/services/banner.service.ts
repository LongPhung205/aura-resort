import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { HomeBanner } from '../models/banner.model';
import { ApiResponse } from '../models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class BannerService {
  private readonly API_URL = 'http://localhost:8080/api/v1/banners';

  constructor(private http: HttpClient) {}

  getActiveBanners(placement?: string): Observable<HomeBanner[]> {
    let url = this.API_URL;
    if (placement) {
      url += `?placement=${placement}`;
    }
    return this.http.get<ApiResponse<HomeBanner[]>>(url).pipe(
      map(res => res.data || [])
    );
  }
}
