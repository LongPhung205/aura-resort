import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { Villa, VillaRequest } from '../models/villa.model';

interface ApiResponse<T> {
  status?: string;
  success?: boolean;
  message: string;
  data: T;
}

@Injectable({
  providedIn: 'root',
})
export class VillaService {
  private readonly API_URL = 'http://localhost:8080/api/v1/villas';

  constructor(private http: HttpClient) {}

  getVillas(typeId?: number, status?: string, zone?: string): Observable<Villa[]> {
    let params = new HttpParams();
    if (typeId) params = params.set('typeId', typeId.toString());
    if (status && status !== 'ALL') params = params.set('status', status);
    if (zone && zone !== 'ALL') params = params.set('zone', zone);

    return this.http.get<ApiResponse<Villa[]>>(this.API_URL, { params }).pipe(
      map((res) => res.data),
      catchError(() => of([]))
    );
  }

  getVillaById(id: number): Observable<Villa> {
    return this.http.get<ApiResponse<Villa>>(`${this.API_URL}/${id}`).pipe(
      map((res) => res.data)
    );
  }

  createVilla(payload: VillaRequest): Observable<Villa> {
    return this.http.post<ApiResponse<Villa>>(this.API_URL, payload).pipe(
      map((res) => res.data)
    );
  }

  updateVilla(id: number, payload: VillaRequest): Observable<Villa> {
    return this.http.put<ApiResponse<Villa>>(`${this.API_URL}/${id}`, payload).pipe(
      map((res) => res.data)
    );
  }

  deleteVilla(id: number): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.API_URL}/${id}`).pipe(
      map(() => undefined)
    );
  }

  updateVillaStatus(id: number, status: string): Observable<Villa> {
    const params = new HttpParams().set('status', status);
    return this.http.patch<ApiResponse<Villa>>(`${this.API_URL}/${id}/status`, {}, { params }).pipe(
      map((res) => res.data)
    );
  }

  searchAvailableVillas(zone: string, checkInDate: string, checkOutDate: string, adults?: number): Observable<Villa[]> {
    let params = new HttpParams()
      .set('checkInDate', checkInDate)
      .set('checkOutDate', checkOutDate);
    
    if (zone) params = params.set('zone', zone);
    if (adults) params = params.set('adults', adults.toString());

    return this.http.get<ApiResponse<Villa[]>>(`http://localhost:8080/api/v1/public/villas/search`, { params }).pipe(
      map(res => res.data)
    );
  }
}
