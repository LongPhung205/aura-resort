import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface ClientReview {
  id: number;
  rating: number;
  comment: string;
  userEmail: string;
  userName: string;
  createdAt: string;
  bookingId?: number;
  bookingCode?: string;
  villaName?: string;
  sentiment?: string;
  managementReply?: string;
  repliedAt?: string;
}

export interface CreateReviewRequest {
  bookingId: number;
  rating: number;
  comment: string;
}

interface ApiResponse<T> {
  status: string;
  message: string;
  data: T;
}

@Injectable({
  providedIn: 'root'
})
export class ClientReviewService {
  private readonly API_URL = `${environment.apiUrl}/reviews`;

  constructor(private http: HttpClient) {}

  getMyReviews(): Observable<ClientReview[]> {
    return this.http.get<ApiResponse<ClientReview[]>>(`${this.API_URL}/me`).pipe(
      map(res => res.data || [])
    );
  }

  createReview(payload: CreateReviewRequest): Observable<ClientReview> {
    return this.http.post<ApiResponse<ClientReview>>(this.API_URL, payload).pipe(
      map(res => res.data)
    );
  }

  getReviewsByRoomType(roomTypeId: number): Observable<ClientReview[]> {
    return this.http.get<ApiResponse<ClientReview[]>>(`${this.API_URL}/room-type/${roomTypeId}`).pipe(
      map(res => res.data || [])
    );
  }
}
