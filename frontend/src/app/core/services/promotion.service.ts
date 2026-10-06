import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

interface ApiResponse<T> {
  status?: string;
  success?: boolean;
  message?: string;
  data: T;
}

@Injectable({
  providedIn: 'root'
})
export class PromotionService {
  private readonly API_URL = `${environment.apiUrl}/promotions`;

  constructor(private http: HttpClient) {}

  /** Public endpoint — dùng cho trang chủ & client check */
  getActivePromotions(): Observable<any[]> {
    return this.http.get<ApiResponse<any[]>>(`${this.API_URL}/active`).pipe(
      map(res => res?.data || []),
      catchError(err => {
        console.error('Error fetching active promotions', err);
        return of([]);
      })
    );
  }

  /** Admin endpoint — cần token ADMIN, fallback sang active nếu chưa có quyền */
  getAllPromotions(): Observable<any[]> {
    return this.http.get<ApiResponse<any[]>>(this.API_URL).pipe(
      map(res => res?.data || []),
      catchError(err => {
        console.warn('getAllPromotions failed (likely auth), falling back to getActivePromotions', err);
        return this.getActivePromotions();
      })
    );
  }

  validateCode(code: string): Observable<any> {
    return this.http.get<ApiResponse<any>>(`${this.API_URL}/check?code=${code}`).pipe(
      map(res => res.data)
    );
  }

  createPromotion(payload: any): Observable<any> {
    return this.http.post<ApiResponse<any>>(this.API_URL, payload).pipe(
      map(res => res.data)
    );
  }

  updatePromotion(id: number, payload: any): Observable<any> {
    return this.http.put<ApiResponse<any>>(`${this.API_URL}/${id}`, payload).pipe(
      map(res => res.data)
    );
  }

  deletePromotion(id: number): Observable<any> {
    return this.http.delete<ApiResponse<any>>(`${this.API_URL}/${id}`).pipe(
      map(res => res.data)
    );
  }
}
