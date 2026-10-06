import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

export interface ComboPackage {
  id?: number;
  name: string;
  description?: string;
  imageUrl?: string;
  price?: number;
  status: string;
  extraServiceIds?: number[];
  extraServices?: any[];
  createdAt?: string;
  updatedAt?: string;
}

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

@Injectable({ providedIn: 'root' })
export class ComboPackageService {
  private readonly API_URL = 'http://localhost:8080/api/v1/combo-packages';

  constructor(private http: HttpClient) {}

  /** Public — trang chủ */
  getActive(): Observable<ComboPackage[]> {
    return this.http.get<ApiResponse<ComboPackage[]>>(`${this.API_URL}/active`).pipe(
      map(res => res.data || []),
      catchError(() => of([]))
    );
  }

  /** Admin — danh sách đầy đủ */
  getAll(): Observable<ComboPackage[]> {
    return this.http.get<ApiResponse<ComboPackage[]>>(this.API_URL).pipe(
      map(res => res.data || []),
      catchError(() => of([]))
    );
  }

  create(combo: ComboPackage): Observable<ComboPackage> {
    return this.http.post<ApiResponse<ComboPackage>>(this.API_URL, combo).pipe(
      map(res => res.data)
    );
  }

  update(id: number, combo: ComboPackage): Observable<ComboPackage> {
    return this.http.put<ApiResponse<ComboPackage>>(`${this.API_URL}/${id}`, combo).pipe(
      map(res => res.data)
    );
  }

  delete(id: number): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.API_URL}/${id}`).pipe(
      map(() => void 0)
    );
  }
}
