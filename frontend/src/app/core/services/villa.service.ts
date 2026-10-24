import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { Villa, VillaRequest } from '../models/villa.model';
import { ApiResponse } from '../models/api-response.model';
import { BaseCrudService } from './base-crud.service';
import { buildHttpParams } from '../utils/http-params.util';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class VillaService extends BaseCrudService<Villa, VillaRequest> {
  private readonly API_URL = `${environment.apiUrl}/villas`;

  constructor(http: HttpClient) {
    super(http, `${environment.apiUrl}/villas`);
  }

  getVillas(typeId?: number, status?: string, zone?: string): Observable<Villa[]> {
    const params = buildHttpParams({ typeId, status, zone });

    return this.http.get<ApiResponse<Villa[]>>(this.API_URL, { params }).pipe(
      map((res) => res.data || []),
      catchError(() => of([]))
    );
  }

  // Alias methods cho tính tương thích ngược
  getVillaById(id: number): Observable<Villa> {
    return this.getById(id);
  }

  createVilla(payload: VillaRequest): Observable<Villa> {
    return this.create(payload);
  }

  updateVilla(id: number, payload: VillaRequest): Observable<Villa> {
    return this.update(id, payload);
  }

  deleteVilla(id: number): Observable<void> {
    return this.delete(id);
  }

  updateVillaStatus(id: number, status: string): Observable<Villa> {
    const params = buildHttpParams({ status });
    return this.http.patch<ApiResponse<Villa>>(`${this.API_URL}/${id}/status`, {}, { params }).pipe(
      map((res) => res.data)
    );
  }

  searchAvailableVillas(zone: string, checkInDate: string, checkOutDate: string, adults?: number): Observable<Villa[]> {
    const params = buildHttpParams({ zone, checkInDate, checkOutDate, adults });

    return this.http.get<ApiResponse<Villa[]>>(`${environment.apiUrl}/public/villas/search`, { params }).pipe(
      map(res => res.data)
    );
  }
}
