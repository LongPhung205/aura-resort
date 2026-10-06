import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import {
  CreateRefillTaskPayload,
  RefillTask,
  RefillTaskStatus,
  VillaInventoryStock,
  VillaSupplyStandard,
  VillaSupplyStandardPayload,
} from '../models/refill.model';

interface ApiResponse<T> {
  status?: string;
  message: string;
  data: T;
}

@Injectable({
  providedIn: 'root',
})
export class AdminRefillService {
  private readonly API_URL = 'http://localhost:8080/api/v1/admin/inventory/refill';

  constructor(private http: HttpClient) {}

  getStandards(villaId?: number): Observable<VillaSupplyStandard[]> {
    let params = new HttpParams();
    if (villaId) {
      params = params.set('villaId', villaId.toString());
    }

    return this.http
      .get<ApiResponse<VillaSupplyStandard[]>>(`${this.API_URL}/standards`, { params })
      .pipe(
        map((res) => res.data || []),
        catchError((err) => {
          console.warn('Lỗi lấy định mức tiêu chuẩn:', err);
          return of([]);
        })
      );
  }

  saveStandard(payload: VillaSupplyStandardPayload): Observable<VillaSupplyStandard> {
    return this.http
      .post<ApiResponse<VillaSupplyStandard>>(`${this.API_URL}/standards`, payload)
      .pipe(map((res) => res.data));
  }

  deleteStandard(id: number): Observable<void> {
    return this.http
      .delete<ApiResponse<void>>(`${this.API_URL}/standards/${id}`)
      .pipe(map(() => void 0));
  }

  applyDefaultStandards(villaId: number): Observable<void> {
    return this.http
      .post<ApiResponse<void>>(`${this.API_URL}/standards/apply-defaults/${villaId}`, {})
      .pipe(map(() => void 0));
  }

  applyDefaultStandardsToAllVillas(): Observable<void> {
    return this.http
      .post<ApiResponse<void>>(`${this.API_URL}/standards/apply-defaults-all`, {})
      .pipe(map(() => void 0));
  }

  getVillaStock(villaId: number): Observable<VillaInventoryStock[]> {
    const params = new HttpParams().set('villaId', villaId.toString());
    return this.http
      .get<ApiResponse<VillaInventoryStock[]>>(`${this.API_URL}/villa-stock`, { params })
      .pipe(
        map((res) => res.data || []),
        catchError((err) => {
          console.warn('Lỗi lấy tồn kho villa:', err);
          return of([]);
        })
      );
  }

  getTasks(status?: RefillTaskStatus, villaId?: number): Observable<RefillTask[]> {
    let params = new HttpParams();
    if (status) {
      params = params.set('status', status);
    }
    if (villaId) {
      params = params.set('villaId', villaId.toString());
    }

    return this.http.get<ApiResponse<RefillTask[]>>(`${this.API_URL}/tasks`, { params }).pipe(
      map((res) => res.data || []),
      catchError((err) => {
        console.warn('Lỗi lấy danh sách nhiệm vụ refill:', err);
        return of([]);
      })
    );
  }

  getTaskById(id: number): Observable<RefillTask | null> {
    return this.http.get<ApiResponse<RefillTask>>(`${this.API_URL}/tasks/${id}`).pipe(
      map((res) => res.data),
      catchError(() => of(null))
    );
  }

  createRefillTask(payload: CreateRefillTaskPayload): Observable<RefillTask> {
    return this.http
      .post<ApiResponse<RefillTask>>(`${this.API_URL}/tasks`, payload)
      .pipe(map((res) => res.data));
  }

  fulfillRefillTask(taskId: number): Observable<RefillTask> {
    return this.http
      .post<ApiResponse<RefillTask>>(`${this.API_URL}/tasks/${taskId}/fulfill`, {})
      .pipe(map((res) => res.data));
  }
}
