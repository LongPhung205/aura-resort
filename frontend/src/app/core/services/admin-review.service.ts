import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { ReviewAnalytics, ReviewItem, ServiceRecoveryTicket } from '../models/admin-review.model';
import { ApiResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root',
})
export class AdminReviewService {
  private readonly API_URL = `${environment.apiUrl}/admin/reviews`;

  constructor(private http: HttpClient) {}

  getAnalytics(): Observable<ReviewAnalytics> {
    return this.http.get<ApiResponse<ReviewAnalytics>>(`${this.API_URL}/analytics`).pipe(
      map((res) => res.data)
    );
  }

  replyReview(id: number, reply: string): Observable<ReviewItem> {
    return this.http.post<ApiResponse<ReviewItem>>(`${this.API_URL}/${id}/reply`, { reply }).pipe(
      map((res) => res.data)
    );
  }

  deleteReview(id: number): Observable<boolean> {
    return this.http.delete<ApiResponse<void>>(`${this.API_URL}/${id}`).pipe(
      map((res) => (res.success !== undefined ? res.success : res.status === 'SUCCESS'))
    );
  }

  createRecoveryTicket(ticket: Partial<ServiceRecoveryTicket>): Observable<ServiceRecoveryTicket> {
    return this.http.post<ApiResponse<ServiceRecoveryTicket>>(`${this.API_URL}/service-recovery`, ticket).pipe(
      map((res) => res.data)
    );
  }

  resolveRecoveryTicket(id: number, resolutionMinutes = 2): Observable<ServiceRecoveryTicket> {
    return this.http.patch<ApiResponse<ServiceRecoveryTicket>>(`${this.API_URL}/service-recovery/${id}/resolve`, { resolutionMinutes }).pipe(
      map((res) => res.data)
    );
  }
}
