import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { ApiResponse } from '../models/api-response.model';
import { BaseCrudService } from './base-crud.service';
import { environment } from '../../../environments/environment';

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

@Injectable({
  providedIn: 'root'
})
export class CollectionService extends BaseCrudService<Collection, CollectionRequest> {
  private readonly BASE_COLLECTION_URL = `${environment.apiUrl}/collections`;

  constructor(http: HttpClient) {
    super(http, `${environment.apiUrl}/collections`);
  }

  // Public APIs
  getActiveCollections(): Observable<Collection[]> {
    return this.http.get<ApiResponse<Collection[]>>(`${this.BASE_COLLECTION_URL}/active`).pipe(
      map(res => res.data || []),
      catchError(err => {
        console.error('Error fetching active collections', err);
        return of([]);
      })
    );
  }

  getCollectionBySlug(slug: string): Observable<Collection> {
    return this.http.get<ApiResponse<Collection>>(`${this.BASE_COLLECTION_URL}/slug/${slug}`).pipe(
      map(res => res.data)
    );
  }

  // Alias methods cho tính tương thích ngược
  getAllCollections(): Observable<Collection[]> {
    return this.getAll().pipe(catchError(() => of([])));
  }

  getCollectionById(id: number): Observable<Collection> {
    return this.getById(id);
  }

  createCollection(data: CollectionRequest): Observable<Collection> {
    return this.create(data);
  }

  updateCollection(id: number, data: CollectionRequest): Observable<Collection> {
    return this.update(id, data);
  }

  deleteCollection(id: number): Observable<void> {
    return this.delete(id);
  }
}
