import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, catchError, filter, switchMap, take, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { TokenService } from '../services/token.service';

let isRefreshing = false;
const refreshTokenSubject = new BehaviorSubject<string | null>(null);

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const tokenService = inject(TokenService);
  const authService = inject(AuthService);
  const router = inject(Router);

  const token = tokenService.getToken();
  let authReq = req;

  if (token) {
    authReq = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`)
    });
  }

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        // Skip login/refresh/logout to prevent infinite loop
        if (
          req.url.includes('/auth/login') ||
          req.url.includes('/auth/refresh-token') ||
          req.url.includes('/auth/logout')
        ) {
          return throwError(() => error);
        }

        const refreshToken = tokenService.getRefreshToken();
        if (!refreshToken) {
          tokenService.clearAll();
          router.navigate(['/login']);
          return throwError(() => error);
        }

        if (!isRefreshing) {
          isRefreshing = true;
          refreshTokenSubject.next(null);

          return authService.refreshToken(refreshToken).pipe(
            switchMap((res) => {
              isRefreshing = false;
              if (res.data?.accessToken) {
                tokenService.saveToken(res.data.accessToken);
                if (res.data.refreshToken) {
                  tokenService.saveRefreshToken(res.data.refreshToken);
                }
                refreshTokenSubject.next(res.data.accessToken);

                return next(
                  req.clone({
                    headers: req.headers.set('Authorization', `Bearer ${res.data.accessToken}`)
                  })
                );
              } else {
                tokenService.clearAll();
                router.navigate(['/login']);
                return throwError(() => error);
              }
            }),
            catchError((refreshErr) => {
              isRefreshing = false;
              tokenService.clearAll();
              router.navigate(['/login']);
              return throwError(() => refreshErr);
            })
          );
        } else {
          // Refresh is in progress, queue and wait for the new access token
          return refreshTokenSubject.pipe(
            filter((newToken): newToken is string => newToken !== null),
            take(1),
            switchMap((newToken) => {
              return next(
                req.clone({
                  headers: req.headers.set('Authorization', `Bearer ${newToken}`)
                })
              );
            })
          );
        }
      }

      return throwError(() => error);
    })
  );
};
