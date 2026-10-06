import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { ServiceDispatch, ServiceDispatchRequest } from '../models/admin-service-dispatch.model';

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

@Injectable({
  providedIn: 'root',
})
export class AdminServiceDispatchService {
  private readonly API_URL = 'http://localhost:8080/api/v1/admin/services/dispatches';

  constructor(private http: HttpClient) {}

  getDispatches(status = 'ALL'): Observable<ServiceDispatch[]> {
    const params = new HttpParams().set('status', status);
    return this.http.get<ApiResponse<ServiceDispatch[]>>(this.API_URL, { params }).pipe(
      map((res) => res.data),
      catchError(() => of(this.getMockDispatches(status)))
    );
  }

  createDispatch(request: ServiceDispatchRequest): Observable<ServiceDispatch> {
    return this.http.post<ApiResponse<ServiceDispatch>>(this.API_URL, request).pipe(
      map((res) => res.data),
      catchError(() => of({} as ServiceDispatch))
    );
  }

  updateStatus(id: number, status: ServiceDispatch['status']): Observable<ServiceDispatch> {
    const params = new HttpParams().set('status', status);
    return this.http.patch<ApiResponse<ServiceDispatch>>(`${this.API_URL}/${id}/status`, null, { params }).pipe(
      map((res) => res.data),
      catchError(() => of({} as ServiceDispatch))
    );
  }

  private getMockDispatches(status: string): ServiceDispatch[] {
    return [];
  }
}
