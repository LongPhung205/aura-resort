import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

export interface Collection {
  id?: number;
  name: string;
  slug?: string;
  description?: string;
  imageUrl?: string;
  displayOrder?: number;
  isActive?: boolean;
}

export interface CollectionRequest {
  name: string;
  slug?: string;
  description?: string;
  imageUrl?: string;
  displayOrder?: number;
  isActive?: boolean;
}

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

@Injectable({
  providedIn: 'root'
})
export class CollectionService {
  private readonly API_URL = 'http://localhost:8080/api/v1/collections';

  constructor(private http: HttpClient) {}

  // Public APIs
  getActiveCollections(): Observable<Collection[]> {
    return this.http.get<ApiResponse<Collection[]>>(`${this.API_URL}/active`).pipe(
      map(res => res.data || []),
      catchError(err => {
        console.error('Error fetching active collections', err);
        return of([]);
      })
    );
  }

  getCollectionBySlug(slug: string): Observable<Collection> {
    return this.http.get<ApiResponse<Collection>>(`${this.API_URL}/slug/${slug}`).pipe(
      map(res => res.data)
    );
  }

  // Admin APIs
  getAllCollections(): Observable<Collection[]> {
    return this.http.get<ApiResponse<Collection[]>>(this.API_URL).pipe(
      map(res => res.data || []),
      catchError(err => {
        console.error('Error fetching all collections', err);
        return of([]);
      })
    );
  }

  getCollectionById(id: number): Observable<Collection> {
    return this.http.get<ApiResponse<Collection>>(`${this.API_URL}/${id}`).pipe(
      map(res => res.data)
    );
  }

  createCollection(data: CollectionRequest): Observable<Collection> {
    return this.http.post<ApiResponse<Collection>>(this.API_URL, data).pipe(
      map(res => res.data)
    );
  }

  updateCollection(id: number, data: CollectionRequest): Observable<Collection> {
    return this.http.put<ApiResponse<Collection>>(`${this.API_URL}/${id}`, data).pipe(
      map(res => res.data)
    );
  }

  deleteCollection(id: number): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.API_URL}/${id}`).pipe(
      map(() => void 0)
    );
  }
}
