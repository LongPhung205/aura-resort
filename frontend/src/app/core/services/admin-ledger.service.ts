import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import {
  LedgerItem,
  LedgerItemRequest,
  DayEndClosingReport,
  DayEndClosingRequest,
  PaymentDashboardStatsResponse,
  ReconcileRequest,
} from '../models/admin-ledger.model';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';

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
      map((res) => res.data)
    );
  }

  createTransaction(payload: LedgerItemRequest): Observable<LedgerItem> {
    return this.http.post<ApiResponse<LedgerItem>>(`${this.API_URL}/transactions`, payload).pipe(
      map((res) => res.data)
    );
  }

  executeDayEndClosing(request?: DayEndClosingRequest): Observable<DayEndClosingReport> {
    return this.http.post<ApiResponse<DayEndClosingReport>>(`${this.API_URL}/day-end-closing`, request || {}).pipe(
      map((res) => res.data)
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
      map((res) => res.data)
    );
  }
}
