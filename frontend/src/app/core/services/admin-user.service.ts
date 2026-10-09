import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import {
  AdminUser,
  ApiResponse,
  CreateUserRequest,
  PageResponse,
  ResetPasswordRequest,
  UpdateUserRequest,
  UserFilter,
  UserSummaryStats,
} from '../models/admin-user.model';

import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AdminUserService {
  private readonly baseUrl = `${environment.apiUrl}/admin/users`;

  constructor(private http: HttpClient) {}

  getUsers(filter: UserFilter): Observable<PageResponse<AdminUser>> {
    let params = new HttpParams()
      .set('tab', filter.tab)
      .set('page', filter.page.toString())
      .set('size', filter.size.toString());

    if (filter.search && filter.search.trim()) {
      params = params.set('search', filter.search.trim());
    }
    if (filter.role) {
      params = params.set('role', filter.role);
    }
    if (filter.isActive !== '' && filter.isActive !== undefined && filter.isActive !== null) {
      params = params.set('isActive', filter.isActive.toString());
    }

    return this.http
      .get<ApiResponse<PageResponse<AdminUser>>>(this.baseUrl, { params })
      .pipe(map((res) => res.data));
  }

  getStats(): Observable<UserSummaryStats> {
    return this.http
      .get<ApiResponse<UserSummaryStats>>(`${this.baseUrl}/stats`)
      .pipe(map((res) => res.data));
  }

  getUserById(id: number): Observable<AdminUser> {
    return this.http
      .get<ApiResponse<AdminUser>>(`${this.baseUrl}/${id}`)
      .pipe(map((res) => res.data));
  }

  createUser(request: CreateUserRequest): Observable<AdminUser> {
    return this.http
      .post<ApiResponse<AdminUser>>(this.baseUrl, request)
      .pipe(map((res) => res.data));
  }

  updateUser(id: number, request: UpdateUserRequest): Observable<AdminUser> {
    return this.http
      .put<ApiResponse<AdminUser>>(`${this.baseUrl}/${id}`, request)
      .pipe(map((res) => res.data));
  }

  toggleUserStatus(id: number): Observable<AdminUser> {
    return this.http
      .patch<ApiResponse<AdminUser>>(`${this.baseUrl}/${id}/status`, {})
      .pipe(map((res) => res.data));
  }

  resetPassword(id: number, request: ResetPasswordRequest): Observable<void> {
    return this.http
      .patch<ApiResponse<void>>(`${this.baseUrl}/${id}/reset-password`, request)
      .pipe(map(() => void 0));
  }

  deleteUser(id: number): Observable<void> {
    return this.http
      .delete<ApiResponse<void>>(`${this.baseUrl}/${id}`)
      .pipe(map(() => void 0));
  }
}

