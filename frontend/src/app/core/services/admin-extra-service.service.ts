import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, forkJoin, from } from 'rxjs';
import { map, catchError, switchMap } from 'rxjs/operators';

export interface ExtraServiceItem {
  id: number;
  name: string;
  description?: string;
  price: number;
  type: string;
  unit: string;
  icon?: string;
  imageUrl?: string;
  isActive: boolean;
  villaCount?: number;
}

export interface ExtraServicePayload {
  name: string;
  description?: string;
  price: number;
  type: string;
  unit: string;
  icon?: string;
  imageUrl?: string;
  isActive?: boolean;
}

export interface VillaServiceItem {
  id: number;
  villaId: number;
  villaNumber: string;
  serviceId: number;
  serviceName: string;
  serviceType: string;
  serviceIcon?: string;
  serviceUnit?: string;
  defaultPrice: number;
  priceOverride?: number;
  effectivePrice: number;
  isAvailable: boolean;
  note?: string;
}

export interface VillaServicePayload {
  villaId: number;
  serviceId: number;
  priceOverride?: number;
  isAvailable?: boolean;
  note?: string;
}

interface ApiResponse<T> {
  status?: string;
  success?: boolean;
  message: string;
  data: T;
}

import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AdminExtraServiceService {
  private readonly API_URL = `${environment.apiUrl}/extra-services`;
  private readonly VILLA_SERVICE_URL = `${environment.apiUrl}/admin/villa-services`;

  constructor(private http: HttpClient) {}

  // ─── Extra Services (Master List) ───

  getAll(): Observable<ExtraServiceItem[]> {
    return this.http.get<ApiResponse<ExtraServiceItem[]>>(this.API_URL).pipe(
      map((res) => res.data || []),
      catchError(() => of([]))
    );
  }

  create(payload: ExtraServicePayload): Observable<ExtraServiceItem> {
    return this.http
      .post<ApiResponse<ExtraServiceItem>>(this.API_URL, payload)
      .pipe(map((res) => res.data));
  }

  update(id: number, payload: ExtraServicePayload): Observable<ExtraServiceItem> {
    return this.http
      .put<ApiResponse<ExtraServiceItem>>(`${this.API_URL}/${id}`, payload)
      .pipe(map((res) => res.data));
  }

  delete(id: number): Observable<void> {
    return this.http
      .delete<ApiResponse<void>>(`${this.API_URL}/${id}`)
      .pipe(map(() => void 0));
  }

  toggleActive(id: number): Observable<ExtraServiceItem> {
    return this.http
      .patch<ApiResponse<ExtraServiceItem>>(`${this.API_URL}/${id}/toggle-active`, {})
      .pipe(map((res) => res.data));
  }

  // ─── Villa Services (N:N Assignment) ───

  getVillaServices(villaId: number): Observable<VillaServiceItem[]> {
    return this.http
      .get<ApiResponse<VillaServiceItem[]>>(this.VILLA_SERVICE_URL, {
        params: { villaId: villaId.toString() },
      })
      .pipe(
        map((res) => res.data || []),
        catchError(() => of([]))
      );
  }

  assignService(payload: VillaServicePayload): Observable<VillaServiceItem> {
    return this.http
      .post<ApiResponse<VillaServiceItem>>(this.VILLA_SERVICE_URL, payload)
      .pipe(map((res) => res.data));
  }

  bulkAssign(villaId: number, payloads: VillaServicePayload[]): Observable<VillaServiceItem[]> {
    return this.http
      .post<ApiResponse<VillaServiceItem[]>>(`${this.VILLA_SERVICE_URL}/bulk/${villaId}`, payloads)
      .pipe(map((res) => res.data));
  }

  updateVillaService(id: number, payload: VillaServicePayload): Observable<VillaServiceItem> {
    return this.http
      .put<ApiResponse<VillaServiceItem>>(`${this.VILLA_SERVICE_URL}/${id}`, payload)
      .pipe(map((res) => res.data));
  }

  removeVillaService(id: number): Observable<void> {
    return this.http
      .delete<ApiResponse<void>>(`${this.VILLA_SERVICE_URL}/${id}`)
      .pipe(map(() => void 0));
  }

  toggleVillaService(id: number): Observable<VillaServiceItem> {
    return this.http
      .patch<ApiResponse<VillaServiceItem>>(`${this.VILLA_SERVICE_URL}/${id}/toggle`, {})
      .pipe(map((res) => res.data));
  }

  syncVillaServices(villaId: number, selectedServiceIds: number[]): Observable<any> {
    return this.getVillaServices(villaId).pipe(
      switchMap((existingServices) => {
        const existingIds = existingServices.map((es) => es.serviceId);
        
        const toRemove = existingServices.filter((es) => !selectedServiceIds.includes(es.serviceId));
        const toAddIds = selectedServiceIds.filter((id) => !existingIds.includes(id));
        
        // Use any to bypass strict typing for forkJoin arrays of different return types if needed,
        // but here they are all Observables. We'll import forkJoin dynamically or just use it.
        const removeObs = toRemove.map((es) => this.removeVillaService(es.id));
        const addObs = toAddIds.map((serviceId) => this.assignService({ villaId, serviceId, isAvailable: true }));
        
        const allObs = [...removeObs, ...addObs];
        if (allObs.length === 0) return of(null);
        return forkJoin(allObs);
      })
    );
  }
}
