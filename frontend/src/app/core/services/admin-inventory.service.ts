import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import {
  InventoryItem,
  InventoryItemPayload,
  InventorySummary,
  InventoryTransaction,
  InventoryTransactionPayload,
  VillaAsset,
  VillaAssetPayload,
  VillaAssetStatus,
} from '../models/inventory.model';

interface ApiResponse<T> {
  status?: string;
  message: string;
  data: T;
}

export interface VillaOption {
  id: number;
  villaNumber: string;
  zone?: string;
  capacity?: number;
}

@Injectable({
  providedIn: 'root',
})
export class AdminInventoryService {
  private readonly API_URL = 'http://localhost:8080/api/v1/admin/inventory';
  private readonly VILLAS_URL = 'http://localhost:8080/api/v1/villas';

  constructor(private http: HttpClient) {}

  getSummary(): Observable<InventorySummary> {
    return this.http.get<ApiResponse<InventorySummary>>(`${this.API_URL}/summary`).pipe(
      map((res) => res.data),
      catchError((err) => {
        console.warn('Error fetching inventory summary:', err);
        return of({
          totalItems: 0,
          lowStockCount: 0,
          totalInventoryValue: 0,
          totalTransactions: 0,
          totalVillaAssets: 0,
        });
      })
    );
  }

  getItems(category?: string, keyword?: string): Observable<InventoryItem[]> {
    let params = new HttpParams();
    if (category && category !== 'ALL') {
      params = params.set('category', category);
    }
    if (keyword && keyword.trim()) {
      params = params.set('keyword', keyword.trim());
    }

    return this.http.get<ApiResponse<InventoryItem[]>>(`${this.API_URL}/items`, { params }).pipe(
      map((res) => res.data || []),
      catchError((err) => {
        console.warn('Error fetching inventory items:', err);
        return of([]);
      })
    );
  }

  getItemById(id: number): Observable<InventoryItem | null> {
    return this.http.get<ApiResponse<InventoryItem>>(`${this.API_URL}/items/${id}`).pipe(
      map((res) => res.data),
      catchError((err) => {
        console.error('Error fetching inventory item by id:', err);
        return of(null);
      })
    );
  }

  createItem(payload: InventoryItemPayload): Observable<InventoryItem> {
    return this.http
      .post<ApiResponse<InventoryItem>>(`${this.API_URL}/items`, payload)
      .pipe(map((res) => res.data));
  }

  updateItem(id: number, payload: InventoryItemPayload): Observable<InventoryItem> {
    return this.http
      .put<ApiResponse<InventoryItem>>(`${this.API_URL}/items/${id}`, payload)
      .pipe(map((res) => res.data));
  }

  deleteItem(id: number): Observable<void> {
    return this.http
      .delete<ApiResponse<void>>(`${this.API_URL}/items/${id}`)
      .pipe(map(() => void 0));
  }

  getTransactions(itemId?: number): Observable<InventoryTransaction[]> {
    let params = new HttpParams();
    if (itemId) {
      params = params.set('itemId', itemId.toString());
    }

    return this.http
      .get<ApiResponse<InventoryTransaction[]>>(`${this.API_URL}/transactions`, { params })
      .pipe(
        map((res) => res.data || []),
        catchError((err) => {
          console.warn('Error fetching inventory transactions:', err);
          return of([]);
        })
      );
  }

  recordTransaction(payload: InventoryTransactionPayload): Observable<InventoryTransaction> {
    return this.http
      .post<ApiResponse<InventoryTransaction>>(`${this.API_URL}/transactions`, payload)
      .pipe(map((res) => res.data));
  }

  getVillaAssets(villaId?: number, status?: VillaAssetStatus): Observable<VillaAsset[]> {
    let params = new HttpParams();
    if (villaId) {
      params = params.set('villaId', villaId.toString());
    }
    if (status) {
      params = params.set('status', status);
    }

    return this.http
      .get<ApiResponse<VillaAsset[]>>(`${this.API_URL}/villa-assets`, { params })
      .pipe(
        map((res) => res.data || []),
        catchError((err) => {
          console.warn('Error fetching villa assets:', err);
          return of([]);
        })
      );
  }

  createVillaAsset(payload: VillaAssetPayload): Observable<VillaAsset> {
    return this.http
      .post<ApiResponse<VillaAsset>>(`${this.API_URL}/villa-assets`, payload)
      .pipe(map((res) => res.data));
  }

  updateVillaAsset(id: number, payload: VillaAssetPayload): Observable<VillaAsset> {
    return this.http
      .put<ApiResponse<VillaAsset>>(`${this.API_URL}/villa-assets/${id}`, payload)
      .pipe(map((res) => res.data));
  }

  deleteVillaAsset(id: number): Observable<void> {
    return this.http
      .delete<ApiResponse<void>>(`${this.API_URL}/villa-assets/${id}`)
      .pipe(map(() => void 0));
  }

  getVillasList(): Observable<VillaOption[]> {
    interface VillaApiResponseItem {
      id: number;
      villaNumber?: string;
      zoneName?: string;
      zone?: { name?: string } | string;
      totalCapacity?: number;
    }

    return this.http
      .get<ApiResponse<VillaApiResponseItem[]> | VillaApiResponseItem[]>(this.VILLAS_URL)
      .pipe(
        map((res) => {
          const raw = Array.isArray(res) ? res : res?.data || [];
          return raw.map((v: VillaApiResponseItem) => {
            let zoneStr = '';
            if (typeof v.zone === 'object' && v.zone?.name) {
              zoneStr = v.zone.name;
            } else if (typeof v.zone === 'string') {
              zoneStr = v.zone;
            } else if (v.zoneName) {
              zoneStr = v.zoneName;
            }

            return {
              id: v.id,
              villaNumber: v.villaNumber || `Villa #${v.id}`,
              zone: zoneStr,
              capacity: v.totalCapacity || 4,
            };
          });
        }),
        catchError(() => of([]))
      );
  }
}
