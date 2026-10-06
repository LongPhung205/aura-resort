import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { ReviewAnalytics, ReviewItem, ServiceRecoveryTicket } from '../models/admin-review.model';

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

@Injectable({
  providedIn: 'root',
})
export class AdminReviewService {
  private readonly API_URL = `${environment.apiUrl}/admin/reviews`;

  constructor(private http: HttpClient) {}

  getAnalytics(): Observable<ReviewAnalytics> {
    return this.http.get<ApiResponse<ReviewAnalytics>>(`${this.API_URL}/analytics`).pipe(
      map((res) => res.data),
      catchError((err) => {
        console.error('Failed to load review analytics', err);
        return of(this.getMockAnalytics());
      })
    );
  }

  replyReview(id: number, reply: string): Observable<ReviewItem | null> {
    return this.http.post<ApiResponse<ReviewItem>>(`${this.API_URL}/${id}/reply`, { reply }).pipe(
      map((res) => res.data),
      catchError((err) => {
        console.error('Failed to reply review', err);
        return of(null);
      })
    );
  }

  deleteReview(id: number): Observable<boolean> {
    return this.http.delete<ApiResponse<void>>(`${this.API_URL}/${id}`).pipe(
      map((res) => res.success),
      catchError((err) => {
        console.error('Failed to delete review', err);
        return of(false);
      })
    );
  }

  createRecoveryTicket(ticket: Partial<ServiceRecoveryTicket>): Observable<ServiceRecoveryTicket | null> {
    return this.http.post<ApiResponse<ServiceRecoveryTicket>>(`${this.API_URL}/service-recovery`, ticket).pipe(
      map((res) => res.data),
      catchError(() => of(null))
    );
  }

  resolveRecoveryTicket(id: number, resolutionMinutes = 2): Observable<ServiceRecoveryTicket | null> {
    return this.http.patch<ApiResponse<ServiceRecoveryTicket>>(`${this.API_URL}/service-recovery/${id}/resolve`, { resolutionMinutes }).pipe(
      map((res) => res.data),
      catchError(() => of(null))
    );
  }

  private getMockAnalytics(): ReviewAnalytics {
    return {
      csatScore: 0,
      npsScore: 0,
      totalReviews: 0,
      positiveCount: 0,
      neutralCount: 0,
      negativeCount: 0,
      responseRate: 0,
      averageResponseMinutes: 0,
      latestReviews: [],
      openRecoveryTickets: [],
    };
  }
}
