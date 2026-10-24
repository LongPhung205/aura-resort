/**
 * Cấu trúc API Response chuẩn từ Spring Boot backend.
 */
export interface ApiResponse<T> {
  status?: string;
  message: string;
  data: T;
  errorCode?: string;
  success?: boolean;
}
