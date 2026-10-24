import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { VillaType, VillaTypeRequest } from '../models/villa-type.model';
import { BaseCrudService } from './base-crud.service';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class VillaTypeService extends BaseCrudService<VillaType, VillaTypeRequest> {
  constructor(http: HttpClient) {
    super(http, `${environment.apiUrl}/villa-types`);
  }

  // Alias methods cho tính tương thích ngược tuyệt đối
  getVillaTypes(): Observable<VillaType[]> {
    return this.getAll();
  }

  getVillaTypeById(id: number): Observable<VillaType> {
    return this.getById(id);
  }

  createVillaType(payload: VillaTypeRequest): Observable<VillaType> {
    return this.create(payload);
  }

  updateVillaType(id: number, payload: VillaTypeRequest): Observable<VillaType> {
    return this.update(id, payload);
  }

  deleteVillaType(id: number): Observable<void> {
    return this.delete(id);
  }
}
