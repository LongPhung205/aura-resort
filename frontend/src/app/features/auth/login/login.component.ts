import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { SocialAuthService, GoogleSigninButtonModule } from '@abacritt/angularx-social-login';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../../core/services/auth.service';
import { TokenService } from '../../../core/services/token.service';
import { ApiResponse, AuthResponse } from '../../../core/models/auth.model';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, GoogleSigninButtonModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit {
  loginForm = new FormGroup({
    identifier: new FormControl('', [Validators.required]),
    password: new FormControl('', [Validators.required]),
    rememberMe: new FormControl(false),
  });

  notification = { show: false, message: '', type: 'success' as 'success' | 'error' | 'warning' };
  private notifTimeout: ReturnType<typeof setTimeout> | null = null;

  constructor(
    private socialAuthService: SocialAuthService,
    private http: HttpClient,
    private router: Router,
    private authService: AuthService,
    private tokenService: TokenService
  ) {}

  showNotification(message: string, type: 'success' | 'error' | 'warning' = 'success') {
    this.notification = { show: true, message, type };
    if (this.notifTimeout) clearTimeout(this.notifTimeout);
    this.notifTimeout = setTimeout(() => {
      this.notification.show = false;
    }, 4000);
  }

  ngOnInit() {
    this.socialAuthService.authState.subscribe((user) => {
      if (user) {
        console.log('Google User:', user);
        this.http
          .post<ApiResponse<AuthResponse>>(`${environment.apiUrl}/auth/google`, {
            idToken: user.idToken,
          })
          .subscribe({
            next: (res) => {
              console.log('Backend response:', res);
              const token = res.data?.accessToken;
              const role = res.data?.role;
              if (token) {
                this.tokenService.saveToken(token);
              }
              if (role) {
                this.tokenService.saveRole(role);
              } else {
                this.tokenService.saveRole('ROLE_CUSTOMER');
              }
              this.showNotification('Đăng nhập bằng Google thành công', 'success');
              setTimeout(() => {
                if (role === 'ROLE_HOUSEKEEPING') {
                  this.router.navigate(['/housekeeping']);
                } else if (
                  role === 'ROLE_ADMIN' ||
                  role === 'ROLE_STAFF' ||
                  role === 'ROLE_RECEPTIONIST' ||
                  role === 'ROLE_BUTLER' ||
                  role === 'ROLE_ACCOUNTANT'
                ) {
                  this.router.navigate(['/admin/dashboard']);
                } else {
                  this.router.navigate(['/']);
                }
              }, 1000);
            },
            error: (err) => {
              console.error('Lỗi xác thực Google:', err);
              this.showNotification('Lỗi đăng nhập Google', 'error');
            },
          });
      }
    });
  }

  submit() {
    if (this.loginForm.valid) {
      const { identifier, password } = this.loginForm.value;
      this.authService.login({ email: identifier || '', password: password || '' }).subscribe({
        next: (res) => {
          if (res.data?.accessToken) {
            this.tokenService.saveToken(res.data.accessToken);
            if (res.data.refreshToken) {
              this.tokenService.saveRefreshToken(res.data.refreshToken);
            }
            if (res.data.role) {
              this.tokenService.saveRole(res.data.role);
            } else {
              this.tokenService.saveRole('ROLE_CUSTOMER');
            }
            localStorage.setItem(
              'user',
              JSON.stringify({
                ...res.data,
                email: identifier,
              })
            );
          }
          const role = res.data?.role;
          this.showNotification(res.message || 'Đăng nhập thành công', 'success');
          setTimeout(() => {
            if (
              role === 'ROLE_HOUSEKEEPING' ||
              identifier?.toLowerCase().includes('housekeeping')
            ) {
              this.router.navigate(['/housekeeping']);
            } else if (
              role === 'ROLE_ADMIN' ||
              role === 'ROLE_STAFF' ||
              role === 'ROLE_RECEPTIONIST' ||
              role === 'ROLE_BUTLER' ||
              role === 'ROLE_ACCOUNTANT' ||
              identifier?.toLowerCase().includes('admin')
            ) {
              this.router.navigate(['/admin/dashboard']);
            } else {
              this.router.navigate(['/']);
            }
          }, 1000);
        },
        error: (err) => {
          const msg = err.error?.message || 'Tài khoản hoặc mật khẩu không chính xác';
          this.showNotification(msg, 'error');
        },
      });
    } else {
      this.loginForm.markAllAsTouched();
    }
  }

  socialLogin(provider: string) {
    console.log(`Login with ${provider}`);
  }
}
