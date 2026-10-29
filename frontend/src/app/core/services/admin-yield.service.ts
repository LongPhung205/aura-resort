import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { YieldMatrixResponse, YieldRule } from '../models/admin-yield.model';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root',
})
export class AdminYieldService {
  private readonly API_URL = `${environment.apiUrl}/admin/yield`;

  constructor(private http: HttpClient) {}

  getRateMatrix(): Observable<YieldMatrixResponse> {
    return this.http.get<ApiResponse<YieldMatrixResponse>>(`${this.API_URL}/rate-matrix`).pipe(
      map((res) => res.data)
    );
  }

  getPromotions(): Observable<unknown[]> {
    return this.http.get<ApiResponse<unknown[]>>(`${environment.apiUrl}/promotions`).pipe(
      map((res) => res.data || [])
    );
  }

  createPromotion(data: unknown): Observable<unknown> {
    return this.http.post<ApiResponse<unknown>>(`${environment.apiUrl}/promotions`, data).pipe(
      map((res) => res.data)
    );
  }

  updatePromotion(id: string | number, data: unknown): Observable<unknown> {
    return this.http.put<ApiResponse<unknown>>(`${environment.apiUrl}/promotions/${id}`, data).pipe(
      map((res) => res.data)
    );
  }

  deletePromotion(id: string | number): Observable<boolean | undefined> {
    return this.http.delete<ApiResponse<void>>(`${environment.apiUrl}/promotions/${id}`).pipe(
      map((res) => res.success)
    );
  }

  getRules(): Observable<YieldRule[]> {
    return this.http.get<ApiResponse<YieldRule[]>>(`${this.API_URL}/rules`).pipe(
      map((res) => res.data || [])
    );
  }

  applyDynamicPricing(enableAi = true): Observable<YieldMatrixResponse> {
    const params = new HttpParams().set('enableAi', enableAi.toString());
    return this.http.post<ApiResponse<YieldMatrixResponse>>(`${this.API_URL}/apply`, null, { params }).pipe(
      map((res) => res.data)
    );
  }
}
