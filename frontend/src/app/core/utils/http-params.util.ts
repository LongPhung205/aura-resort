import { HttpParams } from '@angular/common/http';

/**
 * Generic Helper chuyển đổi bất kỳ filter/params object nào thành HttpParams sạch.
 * Tự động bỏ qua các giá trị null, undefined, chuỗi rỗng và tùy chọn giá trị 'ALL'.
 */
export function buildHttpParams<T extends object>(
  paramsObject?: T,
  ignoreValues: unknown[] = [null, undefined, '', 'ALL']
): HttpParams {
  let params = new HttpParams();
  if (!paramsObject) {
    return params;
  }

  for (const [key, value] of Object.entries(paramsObject)) {
    if (!ignoreValues.includes(value)) {
      params = params.set(key, String(value).trim());
    }
  }

  return params;
}
