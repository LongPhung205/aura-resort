import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { ClientReview, CreateReviewRequest } from '../models/client-review.model';
import { ApiResponse } from '../models/api-response.model';

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

  getReviewsByVillaId(villaId: number): Observable<ClientReview[]> {
    return this.http.get<ApiResponse<ClientReview[]>>(`${this.API_URL}/villa/${villaId}`).pipe(
      map(res => res.data || [])
    );
  }
}
