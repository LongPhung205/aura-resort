import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiResponse } from '../models/api-response.model';

/**
 * Generic Base CRUD Service cung cấp sẵn các thao tác RESTful cơ bản:
 * - getAll(): Lấy toàn bộ danh sách T[]
 * - getById(id): Lấy chi tiết T theo ID
 * - create(payload): Tạo mới với DTO REQ
 * - update(id, payload): Cập nhật với DTO REQ
 * - delete(id): Xóa theo ID
 */
export abstract class BaseCrudService<T, REQ = Partial<T>, ID = number> {
  protected constructor(
    protected http: HttpClient,
    protected readonly endpointUrl: string
  ) {}

  getAll(): Observable<T[]> {
    return this.http.get<ApiResponse<T[]>>(this.endpointUrl).pipe(
      map((res) => res.data || [])
    );
  }

  getById(id: ID): Observable<T> {
    return this.http.get<ApiResponse<T>>(`${this.endpointUrl}/${id}`).pipe(
      map((res) => res.data)
    );
  }

  create(payload: REQ): Observable<T> {
    return this.http.post<ApiResponse<T>>(this.endpointUrl, payload).pipe(
      map((res) => res.data)
    );
  }

  update(id: ID, payload: REQ): Observable<T> {
    return this.http.put<ApiResponse<T>>(`${this.endpointUrl}/${id}`, payload).pipe(
      map((res) => res.data)
    );
  }

  delete(id: ID): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.endpointUrl}/${id}`).pipe(
      map(() => undefined)
    );
  }
}
