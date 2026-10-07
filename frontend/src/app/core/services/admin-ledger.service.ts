import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { LedgerItem, LedgerItemRequest, DayEndClosingReport, DayEndClosingRequest, PaymentDashboardStatsResponse, ReconcileRequest } from '../models/admin-ledger.model';

import { environment } from '../../../environments/environment';

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

@Injectable({
  providedIn: 'root',
})
export class AdminLedgerService {
  private readonly API_URL = `${environment.apiUrl}/admin/payments`;

  constructor(private http: HttpClient) {}

  getLedger(ledgerType = 'ALL', method = 'ALL'): Observable<LedgerItem[]> {
    const params = new HttpParams()
      .set('ledgerType', ledgerType)
      .set('method', method);

    return this.http.get<ApiResponse<LedgerItem[]>>(`${this.API_URL}/ledger`, { params }).pipe(
      map((res) => res.data),
      catchError((err) => {
        console.error('Failed to fetch ledger:', err);
        return of(this.getMockLedger(ledgerType, method));
      })
    );
  }

  createTransaction(payload: LedgerItemRequest): Observable<LedgerItem> {
    return this.http.post<ApiResponse<LedgerItem>>(`${this.API_URL}/transactions`, payload).pipe(
      map((res) => res.data)
    );
  }

  executeDayEndClosing(request?: DayEndClosingRequest): Observable<DayEndClosingReport> {
    return this.http.post<ApiResponse<DayEndClosingReport>>(`${this.API_URL}/day-end-closing`, request || {}).pipe(
      map((res) => res.data),
      catchError(() => of(this.getMockClosing()))
    );
  }

  getDashboardStats(): Observable<PaymentDashboardStatsResponse> {
    return this.http.get<ApiResponse<PaymentDashboardStatsResponse>>(`${this.API_URL}/dashboard-stats`).pipe(
      map((res) => res.data)
    );
  }

  reconcileTransaction(id: number, request: ReconcileRequest): Observable<LedgerItem> {
    return this.http.put<ApiResponse<LedgerItem>>(`${this.API_URL}/transactions/${id}/reconcile`, request).pipe(
      map((res) => res.data)
    );
  }

  getLatestClosing(): Observable<DayEndClosingReport> {
    return this.http.get<ApiResponse<DayEndClosingReport>>(`${this.API_URL}/latest-closing`).pipe(
      map((res) => res.data),
      catchError(() => of(this.getMockClosing()))
    );
  }

  private getMockLedger(type: string, method: string): LedgerItem[] {
    return [];
  }

  private getMockClosing(): DayEndClosingReport {
    return {
      id: 0,
      closingDate: new Date().toISOString().split('T')[0],
      totalRevenue: 0,
      roomRevenue: 0,
      serviceRevenue: 0,
      totalOpex: 0,
      netCash: 0,
      occupancyRate: 0,
      adr: 0,
      revPar: 0,
      totalBookings: 0,
      occupiedRooms: 0,
      closedByName: '',
      closedAt: new Date().toISOString(),
      status: 'OPEN',
      notes: 'Chưa có dữ liệu chốt sổ',
    };
  }
}
