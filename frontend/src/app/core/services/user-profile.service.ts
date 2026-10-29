import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { UserProfile } from '../models/user-profile.model';
import { ApiResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root'
})
export class UserProfileService {
  private readonly API_URL = `${environment.apiUrl}/users/me`;

  constructor(private http: HttpClient) {}

  getProfile(): Observable<UserProfile> {
    return this.http.get<ApiResponse<UserProfile>>(this.API_URL).pipe(
      map(res => res.data)
    );
  }

  updateProfile(data: { fullName: string; phone?: string; avatar?: string }): Observable<UserProfile> {
    return this.http.put<ApiResponse<UserProfile>>(this.API_URL, data).pipe(
      map(res => res.data)
    );
  }

  changePassword(data: { currentPassword: string; newPassword: string }): Observable<void> {
    return this.http.put<ApiResponse<void>>(`${this.API_URL}/password`, data).pipe(
      map(() => void 0)
    );
  }
}
