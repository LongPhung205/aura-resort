import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { DashboardStatsResponse } from '../models/admin-dashboard.model';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root',
})
export class AdminDashboardService {
  private readonly API_URL = `${environment.apiUrl}/admin/dashboard`;

  constructor(private http: HttpClient) {}

  getDashboardStats(resortId = 'all', period = '7d'): Observable<DashboardStatsResponse> {
    const params = new HttpParams()
      .set('resortId', resortId)
      .set('period', period);

    return this.http.get<ApiResponse<DashboardStatsResponse>>(`${this.API_URL}/stats`, { params }).pipe(
      map((res) => res.data)
    );
  }
}
