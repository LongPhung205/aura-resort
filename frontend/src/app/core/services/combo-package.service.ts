import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { ApiResponse } from '../models/api-response.model';
import { BaseCrudService } from './base-crud.service';
import { environment } from '../../../environments/environment';
import { ComboPackage } from '../models/combo-package.model';

@Injectable({ providedIn: 'root' })
export class ComboPackageService extends BaseCrudService<ComboPackage> {
  private readonly COMBO_API_URL = `${environment.apiUrl}/combo-packages`;

  constructor(http: HttpClient) {
    super(http, `${environment.apiUrl}/combo-packages`);
  }

  /** Public — trang chủ */
  getActive(): Observable<ComboPackage[]> {
    return this.http.get<ApiResponse<ComboPackage[]>>(`${this.COMBO_API_URL}/active`).pipe(
      map(res => res.data || []),
      catchError(() => of([]))
    );
  }
}
