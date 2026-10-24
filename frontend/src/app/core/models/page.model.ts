/**
 * Chuẩn phân trang chung (Page Response) đồng bộ với Spring Data PageResponse<T> từ Backend.
 */
export interface PageResponse<T> {
  content: T[];
  pageNo: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
  first?: boolean;
  empty?: boolean;
}

/**
 * Chuẩn query params cho các API phân trang.
 */
export interface PageRequest {
  page?: number;
  size?: number;
  sort?: string;
  direction?: 'asc' | 'desc';
}

/**
 * Alias tương thích ngược cho AdminPageResponse
 */
export type AdminPageResponse<T> = PageResponse<T>;
