import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { YieldMatrixResponse, YieldRule } from '../models/admin-yield.model';

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

@Injectable({
  providedIn: 'root',
})
export class AdminYieldService {
  private readonly API_URL = 'http://localhost:8080/api/v1/admin/yield';

  constructor(private http: HttpClient) {}

  getRateMatrix(): Observable<YieldMatrixResponse> {
    return this.http.get<ApiResponse<YieldMatrixResponse>>(`${this.API_URL}/rate-matrix`).pipe(
      map((res) => res.data),
      catchError(() => of(this.getMockMatrix()))
    );
  }

  getPromotions(): Observable<any[]> {
    return this.http.get<ApiResponse<any[]>>('http://localhost:8080/api/v1/promotions').pipe(
      map((res) => res.data || []),
      catchError(() => of([]))
    );
  }

  createPromotion(data: any): Observable<any> {
    return this.http.post<ApiResponse<any>>('http://localhost:8080/api/v1/promotions', data).pipe(
      map((res) => res.data)
    );
  }

  updatePromotion(id: string | number, data: any): Observable<any> {
    return this.http.put<ApiResponse<any>>(`http://localhost:8080/api/v1/promotions/${id}`, data).pipe(
      map((res) => res.data)
    );
  }

  deletePromotion(id: string | number): Observable<boolean> {
    return this.http.delete<ApiResponse<void>>(`http://localhost:8080/api/v1/promotions/${id}`).pipe(
      map((res) => res.success)
    );
  }

  getRules(): Observable<YieldRule[]> {
    return this.http.get<ApiResponse<YieldRule[]>>(`${this.API_URL}/rules`).pipe(
      map((res) => res.data),
      catchError(() => of(this.getMockRules()))
    );
  }

  applyDynamicPricing(enableAi = true): Observable<YieldMatrixResponse> {
    const params = new HttpParams().set('enableAi', enableAi.toString());
    return this.http.post<ApiResponse<YieldMatrixResponse>>(`${this.API_URL}/apply`, null, { params }).pipe(
      map((res) => res.data),
      catchError(() => {
        const matrix = this.getMockMatrix();
        matrix.isAiDynamicPricingActive = enableAi;
        return of(matrix);
      })
    );
  }

  private getMockMatrix(): YieldMatrixResponse {
    return {
      currentOccupancy: 0,
      predictedWeekendOccupancy: 0,
      isAiDynamicPricingActive: false,
      yieldStrategy: 'BALANCED',
      roomTypes: [],
    };
  }

  private getMockRules(): YieldRule[] {
    return [];
  }
}
