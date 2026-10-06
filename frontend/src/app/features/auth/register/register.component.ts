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
import { RouterLink } from '@angular/router';
import {
  TuiInputModule,
  TuiInputPasswordModule,
  TuiTextfieldControllerModule,
} from '@taiga-ui/legacy';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

export function passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
  const password = control.get('password')?.value;
  const confirmPassword = control.get('confirmPassword')?.value;

  if (password !== confirmPassword) {
    control.get('confirmPassword')?.setErrors({ passwordMismatch: true });
    return { passwordMismatch: true };
  } else {
    return null;
  }
}

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    TuiInputModule,
    TuiInputPasswordModule,
    TuiTextfieldControllerModule,
  ],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
})
export class RegisterComponent {
  registerForm = new FormGroup(
    {
      fullName: new FormControl('', [Validators.required]),
      email: new FormControl('', [Validators.required, Validators.email]),
      otp: new FormControl('', [Validators.required, Validators.minLength(6), Validators.maxLength(6)]),
      password: new FormControl('', [Validators.required, Validators.minLength(8)]),
      confirmPassword: new FormControl('', [Validators.required]),
    },
    { validators: passwordMatchValidator }
  );

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

  requestOtp() {
    const emailControl = this.registerForm.get('email');

    // Mark email as touched to show errors if any
    emailControl?.markAsTouched();

    if (emailControl?.valid) {
      const payload = {
        email: emailControl.value || ''
      };
      
      this.authService.register(payload).subscribe({
        next: (res) => {
          const msg = res.message || 'Mã OTP đã được gửi đến email của bạn';
          this.showNotification(msg, 'success');
        },
        error: (err) => {
          const msg = err.error?.message || 'Có lỗi xảy ra khi gửi OTP';
          this.showNotification(msg, 'error');
        }
      });
    } else {
      const msg = 'Vui lòng điền đúng thông tin Email để nhận mã OTP';
      this.showNotification(msg, 'warning');
    }
  }

  submit() {
    if (this.registerForm.valid) {
      const payload = {
        email: this.registerForm.get('email')?.value || '',
        otp: this.registerForm.get('otp')?.value || '',
        fullName: this.registerForm.get('fullName')?.value || '',
        password: this.registerForm.get('password')?.value || ''
      };

      this.authService.verifyOtp(payload).subscribe({
        next: () => {
          this.showNotification('Đăng ký thành công! Đang điều hướng đến trang đăng nhập...', 'success');
          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 2500);
        },
        error: (err) => {
          const msg = err.error?.message || 'Có lỗi xảy ra khi xác thực OTP';
          this.showNotification(msg, 'error');
        }
      });
    } else {
      this.registerForm.markAllAsTouched();
      
      if (this.registerForm.hasError('passwordMismatch')) {
        this.showNotification('Mật khẩu xác nhận không khớp!', 'error');
      } else {
        this.showNotification('Vui lòng điền đầy đủ và chính xác các thông tin đăng ký!', 'warning');
      }
    }
  }
}
