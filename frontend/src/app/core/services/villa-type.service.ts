import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { VillaType, VillaTypeRequest } from '../models/villa-type.model';

import { environment } from '../../../environments/environment';

interface ApiResponse<T> {
  status?: string;
  success?: boolean;
  message: string;
  data: T;
}

@Injectable({
  providedIn: 'root',
})
export class VillaTypeService {
  private readonly API_URL = `${environment.apiUrl}/villa-types`;

  constructor(private http: HttpClient) {}

  getVillaTypes(): Observable<VillaType[]> {
    return this.http.get<ApiResponse<VillaType[]>>(this.API_URL).pipe(
      map((res) => res.data),
      catchError(() => of([]))
    );
  }

  getVillaTypeById(id: number): Observable<VillaType> {
    return this.http.get<ApiResponse<VillaType>>(`${this.API_URL}/${id}`).pipe(
      map((res) => res.data)
    );
  }

  createVillaType(payload: VillaTypeRequest): Observable<VillaType> {
    return this.http.post<ApiResponse<VillaType>>(this.API_URL, payload).pipe(
      map((res) => res.data)
    );
  }

  updateVillaType(id: number, payload: VillaTypeRequest): Observable<VillaType> {
    return this.http.put<ApiResponse<VillaType>>(`${this.API_URL}/${id}`, payload).pipe(
      map((res) => res.data)
    );
  }

  deleteVillaType(id: number): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.API_URL}/${id}`).pipe(
      map(() => undefined)
    );
  }
}
