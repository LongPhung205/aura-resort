import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  ReactiveFormsModule,
  FormGroup,
  FormControl,
  Validators,
  AbstractControl,
  ValidationErrors,
} from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

export function passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
  const password = control.get('newPassword')?.value;
  const confirmPassword = control.get('confirmPassword')?.value;

  if (password !== confirmPassword) {
    control.get('confirmPassword')?.setErrors({ passwordMismatch: true });
    return { passwordMismatch: true };
  } else {
    return null;
  }
}

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
  ],
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss'],
})
export class ForgotPasswordComponent {
  step = 1;
  emailForReset = '';

  requestOtpForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
  });

  notification = { show: false, message: '', type: 'success' as 'success' | 'error' | 'warning' };
  private notifTimeout: ReturnType<typeof setTimeout> | null = null;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  showNotification(message: string, type: 'success' | 'error' | 'warning' = 'success') {
    this.notification = { show: true, message, type };
    if (this.notifTimeout) clearTimeout(this.notifTimeout);
    this.notifTimeout = setTimeout(() => {
      this.notification.show = false;
    }, 4000);
  }

  resetPasswordForm = new FormGroup(
    {
      otp: new FormControl('', [Validators.required, Validators.minLength(6), Validators.maxLength(6)]),
      newPassword: new FormControl('', [Validators.required, Validators.minLength(8)]),
      confirmPassword: new FormControl('', [Validators.required]),
    },
    { validators: passwordMatchValidator }
  );

  requestOtp() {
    if (this.requestOtpForm.valid) {
      this.emailForReset = this.requestOtpForm.value.email || '';
      
      this.authService.forgotPassword({ email: this.emailForReset }).subscribe({
        next: (res) => {
          this.showNotification(res.message || 'Mã OTP đã được gửi đến email của bạn', 'success');
          this.step = 2;
        },
        error: (err) => {
          const msg = err.error?.message || 'Có lỗi xảy ra khi yêu cầu gửi OTP';
          this.showNotification(msg, 'error');
        }
      });
    } else {
      this.requestOtpForm.markAllAsTouched();
    }
  }

  resetPassword() {
    if (this.resetPasswordForm.valid) {
      const payload = {
        email: this.emailForReset,
        otp: this.resetPasswordForm.value.otp || '',
        newPassword: this.resetPasswordForm.value.newPassword || ''
      };
      
      this.authService.resetPassword(payload).subscribe({
        next: (res) => {
          this.showNotification(res.message || 'Đổi mật khẩu thành công', 'success');
          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 1500);
        },
        error: (err) => {
          const msg = err.error?.message || 'Có lỗi xảy ra khi đổi mật khẩu';
          this.showNotification(msg, 'error');
        }
      });
    } else {
      this.resetPasswordForm.markAllAsTouched();
    }
  }
}
