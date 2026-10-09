import { Component, OnInit, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { TokenService } from '../../core/services/token.service';
import { AuthService } from '../../core/services/auth.service';
import { AdminUserService } from '../../core/services/admin-user.service';
import { BodyPortalDirective } from '../../shared/directives/body-portal.directive';

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, BodyPortalDirective],
  templateUrl: './admin-layout.component.html',
  styleUrls: ['./admin-layout.component.scss'],
})
export class AdminLayoutComponent implements OnInit {
  sidebarOpen = true;
  selectedResort = 'Phú Quốc Sanctuary';
  resortsList = [
    'Phú Quốc Sanctuary',
    'Aura Ocean Cliff Nha Trang',
    'Aura Heritage Đà Nẵng',
    'Aura Royal Hạ Long',
    'Aura Lotus Đà Lạt',
  ];

  shiftLabel = 'CA TRỰC';
  shiftTime = '08:00 - 17:00';
  userName = 'Ban Quản Trị';
  userRole = 'Quản Trị Viên';
  userEmail = 'admin@auroresort.com';
  userPhone = '0901 234 567';
  userAvatar = '/assets/images/staff/avatar-nam.jpg';
  userId: number | null = null;
  userRawRole = 'ROLE_ADMIN';
  unreadNotificationsCount = 0;

  // Dropdown & Modals state
  userDropdownOpen = false;
  showProfileModal = false;
  showLogoutConfirmModal = false;
  isSavingProfile = false;

  // Profile Edit Form
  profileForm = {
    fullName: '',
    phone: '',
    email: '',
    avatarUrl: '/assets/images/staff/avatar-nam.jpg',
  };

  // Preset Avatars for selection
  availableAvatars = [
    { label: 'Nam Quản Lý 1', url: '/assets/images/staff/avatar-nam.jpg' },
    { label: 'Nữ Quản Lý 1', url: '/assets/images/staff/avatar-hoa.jpg' },
    { label: 'Nam Quản Lý 2', url: '/assets/images/staff/avatar-minh.jpg' },
    { label: 'Nữ Quản Lý 2', url: '/assets/images/staff/avatar-thao.jpg' },
    { label: 'Trưởng Ca 1', url: '/assets/images/staff/avatar-hoang.jpg' },
    { label: 'Trưởng Ca 2', url: '/assets/images/staff/avatar-quang.jpg' },
  ];

  // Toast
  toastMessage: string | null = null;
  toastType: 'success' | 'error' = 'success';

  constructor(
    public router: Router,
    private tokenService: TokenService,
    private authService: AuthService,
    private adminUserService: AdminUserService
  ) {}

  ngOnInit(): void {
    this.loadUserData();
  }

  loadUserData(): void {
    const savedUser = localStorage.getItem('user') || localStorage.getItem('currentUser');
    if (savedUser) {
      try {
        const u = JSON.parse(savedUser);
        if (u.fullName) this.userName = u.fullName;
        if (u.name && !u.fullName) this.userName = u.name;
        if (u.email) this.userEmail = u.email;
        if (u.phone) this.userPhone = u.phone;
        if (u.avatarUrl) this.userAvatar = u.avatarUrl;
        if (u.id) this.userId = u.id;
        if (u.role) {
          this.userRawRole = u.role;
          this.userRole = this.formatRole(u.role);
        }
      } catch {
        // ignore
      }
    }
  }

  formatRole(role: string): string {
    const r = role.replace('ROLE_', '').toUpperCase();
    switch (r) {
      case 'ADMIN':
        return 'Quản Trị Viên';
      case 'STAFF':
        return 'Nhân Viên';
      case 'RECEPTIONIST':
        return 'Lễ Tân';
      case 'BUTLER':
        return 'Quản Gia Butler';
      case 'HOUSEKEEPING':
        return 'Buồng Phòng';
      case 'ACCOUNTANT':
        return 'Kế Toán';
      default:
        return r;
    }
  }

  toggleSidebar(): void {
    this.sidebarOpen = !this.sidebarOpen;
  }

  toggleUserDropdown(event?: Event): void {
    if (event) {
      event.stopPropagation();
    }
    this.userDropdownOpen = !this.userDropdownOpen;
  }

  closeUserDropdown(): void {
    this.userDropdownOpen = false;
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.user-menu-container')) {
      this.userDropdownOpen = false;
    }
  }

  openEditProfileModal(): void {
    this.userDropdownOpen = false;
    this.profileForm = {
      fullName: this.userName,
      phone: this.userPhone,
      email: this.userEmail,
      avatarUrl: this.userAvatar,
    };
    this.showProfileModal = true;
  }

  selectAvatar(url: string): void {
    this.profileForm.avatarUrl = url;
  }

  saveProfile(): void {
    if (!this.profileForm.fullName || !this.profileForm.fullName.trim()) {
      this.showToast('Vui lòng nhập họ và tên!', 'error');
      return;
    }

    this.isSavingProfile = true;
    this.userName = this.profileForm.fullName.trim();
    this.userPhone = this.profileForm.phone ? this.profileForm.phone.trim() : '';
    this.userAvatar = this.profileForm.avatarUrl;

    // Update in localStorage
    const savedUser = localStorage.getItem('user');
    let u: any = {};
    if (savedUser) {
      try {
        u = JSON.parse(savedUser);
      } catch {
        u = {};
      }
    }
    u.fullName = this.userName;
    u.phone = this.userPhone;
    u.avatarUrl = this.userAvatar;
    u.email = this.userEmail;
    u.role = this.userRawRole;
    localStorage.setItem('user', JSON.stringify(u));
    localStorage.setItem('currentUser', JSON.stringify(u));

    // Also update via backend API if userId exists
    if (this.userId) {
      this.adminUserService
        .updateUser(this.userId, {
          fullName: this.userName,
          phone: this.userPhone,
          role: this.userRawRole as any,
        })
        .subscribe({
          next: () => {
            this.isSavingProfile = false;
            this.showProfileModal = false;
            this.showToast('Cập nhật hồ sơ cá nhân thành công!');
          },
          error: () => {
            // Even if server call fails, local state updated
            this.isSavingProfile = false;
            this.showProfileModal = false;
            this.showToast('Đã lưu thông tin hồ sơ!');
          },
        });
    } else {
      this.isSavingProfile = false;
      this.showProfileModal = false;
      this.showToast('Cập nhật hồ sơ cá nhân thành công!');
    }
  }

  logout(): void {
    this.userDropdownOpen = false;
    this.showLogoutConfirmModal = true;
  }

  confirmLogout(): void {
    const refreshToken = this.tokenService.getRefreshToken() || undefined;
    this.authService.logout(refreshToken).subscribe({
      next: () => {},
      error: () => {}
    });
    this.tokenService.clearAll();
    this.showLogoutConfirmModal = false;
    this.showToast('Đã đăng xuất khỏi hệ thống');
    setTimeout(() => {
      this.router.navigate(['/login']);
    }, 300);
  }

  showToast(message: string, type: 'success' | 'error' = 'success'): void {
    this.toastMessage = message;
    this.toastType = type;
    setTimeout(() => {
      this.toastMessage = null;
    }, 3000);
  }
}
