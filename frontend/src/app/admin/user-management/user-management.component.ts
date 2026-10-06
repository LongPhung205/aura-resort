import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AdminUserService } from '../../core/services/admin-user.service';
import {
  AdminUser,
  CreateUserRequest,
  UpdateUserRequest,
  UserFilter,
  UserRole,
  UserSummaryStats,
} from '../../core/models/admin-user.model';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.css'],
})
export class UserManagementComponent implements OnInit {
  currentTab: 'CUSTOMER' | 'STAFF' = 'CUSTOMER';

  users: AdminUser[] = [];
  stats: UserSummaryStats = {
    totalUsers: 0,
    totalCustomers: 0,
    totalStaff: 0,
    totalLocked: 0,
  };

  isLoading = false;
  isSaving = false;
  toastMessage: string | null = null;
  toastType: 'success' | 'error' = 'success';

  // Filters
  searchTerm = '';
  selectedRole: UserRole | '' = '';
  selectedStatus: '' | 'true' | 'false' = '';

  // Pagination
  pageNo = 0;
  pageSize = 10;
  totalElements = 0;
  totalPages = 0;

  // Modals
  showCreateModal = false;
  showEditModal = false;
  showResetPasswordModal = false;
  showDetailModal = false;
  showConfirmStatusModal = false;

  selectedUser: AdminUser | null = null;

  // Forms
  createForm: CreateUserRequest = {
    email: '',
    password: '',
    fullName: '',
    phone: '',
    role: 'ROLE_CUSTOMER',
  };

  editForm: UpdateUserRequest = {
    fullName: '',
    phone: '',
    role: 'ROLE_CUSTOMER',
  };

  resetPasswordForm = {
    newPassword: '',
    confirmPassword: '',
  };

  staffRoles: { value: UserRole; label: string }[] = [
    { value: 'ROLE_RECEPTIONIST', label: 'Lễ Tân (Front Desk)' },
    { value: 'ROLE_BUTLER', label: 'Quản Gia (Butler)' },
    { value: 'ROLE_HOUSEKEEPING', label: 'Buồng Phòng (Housekeeping)' },
    { value: 'ROLE_ACCOUNTANT', label: 'Kế Toán (Accountant)' },
    { value: 'ROLE_STAFF', label: 'Nhân Viên Tiêu Chuẩn' },
    { value: 'ROLE_ADMIN', label: 'Ban Quản Trị (Admin)' },
  ];

  constructor(private userService: AdminUserService) {}

  ngOnInit(): void {
    this.loadStats();
    this.loadUsers();
  }

  loadStats(): void {
    this.userService.getStats().subscribe({
      next: (res) => {
        if (res) {
          this.stats = res;
        }
      },
      error: (err) => console.error('Lỗi khi tải thống kê:', err),
    });
  }

  loadUsers(): void {
    this.isLoading = true;
    const filter: UserFilter = {
      tab: this.currentTab,
      search: this.searchTerm.trim() || undefined,
      role: this.selectedRole || undefined,
      isActive:
        this.selectedStatus === 'true'
          ? true
          : this.selectedStatus === 'false'
          ? false
          : '',
      page: this.pageNo,
      size: this.pageSize,
    };

    this.userService.getUsers(filter).subscribe({
      next: (res) => {
        this.isLoading = false;
        if (res) {
          this.users = res.content || [];
          this.pageNo = res.pageNo;
          this.pageSize = res.pageSize;
          this.totalElements = res.totalElements;
          this.totalPages = res.totalPages;
        }
      },
      error: (err) => {
        this.isLoading = false;
        this.showToast('Không thể tải danh sách tài khoản: ' + (err.error?.message || err.message), 'error');
      },
    });
  }

  switchTab(tab: 'CUSTOMER' | 'STAFF'): void {
    if (this.currentTab === tab) return;
    this.currentTab = tab;
    this.pageNo = 0;
    this.searchTerm = '';
    this.selectedRole = '';
    this.selectedStatus = '';
    this.loadUsers();
  }

  onSearchChange(): void {
    this.pageNo = 0;
    this.loadUsers();
  }

  onFilterChange(): void {
    this.pageNo = 0;
    this.loadUsers();
  }

  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.pageNo = page;
      this.loadUsers();
    }
  }

  // CREATE USER
  openCreateModal(): void {
    this.createForm = {
      email: '',
      password: '',
      fullName: '',
      phone: '',
      role: this.currentTab === 'CUSTOMER' ? 'ROLE_CUSTOMER' : 'ROLE_RECEPTIONIST',
    };
    this.showCreateModal = true;
  }

  submitCreate(): void {
    if (!this.createForm.email || !this.createForm.fullName || !this.createForm.password) {
      this.showToast('Vui lòng điền đầy đủ Email, Họ tên và Mật khẩu!', 'error');
      return;
    }
    if (this.createForm.password.length < 6) {
      this.showToast('Mật khẩu phải từ 6 ký tự trở lên!', 'error');
      return;
    }

    this.isSaving = true;
    this.userService.createUser(this.createForm).subscribe({
      next: () => {
        this.isSaving = false;
        this.showCreateModal = false;
        this.showToast('Tạo tài khoản mới thành công!', 'success');
        this.loadStats();
        this.loadUsers();
      },
      error: (err) => {
        this.isSaving = false;
        this.showToast(err.error?.message || 'Có lỗi xảy ra khi tạo tài khoản!', 'error');
      },
    });
  }

  // EDIT USER
  openEditModal(user: AdminUser): void {
    this.selectedUser = user;
    this.editForm = {
      fullName: user.fullName,
      phone: user.phone || '',
      role: user.role,
    };
    this.showEditModal = true;
  }

  submitEdit(): void {
    if (!this.selectedUser) return;
    if (!this.editForm.fullName) {
      this.showToast('Họ tên không được để trống!', 'error');
      return;
    }

    this.isSaving = true;
    this.userService.updateUser(this.selectedUser.id, this.editForm).subscribe({
      next: () => {
        this.isSaving = false;
        this.showEditModal = false;
        this.showToast('Cập nhật tài khoản thành công!', 'success');
        this.loadUsers();
      },
      error: (err) => {
        this.isSaving = false;
        this.showToast(err.error?.message || 'Có lỗi xảy ra khi cập nhật!', 'error');
      },
    });
  }

  // RESET PASSWORD
  openResetPasswordModal(user: AdminUser): void {
    this.selectedUser = user;
    this.resetPasswordForm = {
      newPassword: '',
      confirmPassword: '',
    };
    this.showResetPasswordModal = true;
  }

  submitResetPassword(): void {
    if (!this.selectedUser) return;
    if (!this.resetPasswordForm.newPassword || this.resetPasswordForm.newPassword.length < 6) {
      this.showToast('Mật khẩu mới phải từ 6 ký tự trở lên!', 'error');
      return;
    }
    if (this.resetPasswordForm.newPassword !== this.resetPasswordForm.confirmPassword) {
      this.showToast('Xác nhận mật khẩu không trùng khớp!', 'error');
      return;
    }

    this.isSaving = true;
    this.userService.resetPassword(this.selectedUser.id, { newPassword: this.resetPasswordForm.newPassword }).subscribe({
      next: () => {
        this.isSaving = false;
        this.showResetPasswordModal = false;
        this.showToast('Đặt lại mật khẩu mới thành công!', 'success');
      },
      error: (err) => {
        this.isSaving = false;
        this.showToast(err.error?.message || 'Lỗi khi đặt lại mật khẩu!', 'error');
      },
    });
  }

  // DETAIL MODAL
  openDetailModal(user: AdminUser): void {
    this.selectedUser = user;
    this.showDetailModal = true;
  }

  // TOGGLE STATUS
  openConfirmStatusModal(user: AdminUser): void {
    this.selectedUser = user;
    this.showConfirmStatusModal = true;
  }

  confirmToggleStatus(): void {
    if (!this.selectedUser) return;

    this.isSaving = true;
    this.userService.toggleUserStatus(this.selectedUser.id).subscribe({
      next: (res) => {
        this.isSaving = false;
        this.showConfirmStatusModal = false;
        const action = res.isActive ? 'Kích hoạt' : 'Khóa';
        this.showToast(`${action} tài khoản ${res.fullName} thành công!`, 'success');
        this.loadStats();
        this.loadUsers();
      },
      error: (err) => {
        this.isSaving = false;
        this.showToast(err.error?.message || 'Không thể thay đổi trạng thái tài khoản!', 'error');
      },
    });
  }

  // HELPERS
  showToast(message: string, type: 'success' | 'error' = 'success'): void {
    this.toastMessage = message;
    this.toastType = type;
    setTimeout(() => {
      this.toastMessage = null;
    }, 3500);
  }

  getRoleLabel(role: string): string {
    switch (role) {
      case 'ROLE_CUSTOMER':
        return 'Khách Hàng VIP';
      case 'ROLE_ADMIN':
        return 'Quản Trị Viên (Admin)';
      case 'ROLE_RECEPTIONIST':
        return 'Lễ Tân (Front Desk)';
      case 'ROLE_BUTLER':
        return 'Quản Gia (Butler)';
      case 'ROLE_HOUSEKEEPING':
        return 'Buồng Phòng (Housekeeping)';
      case 'ROLE_ACCOUNTANT':
        return 'Kế Toán (Accountant)';
      case 'ROLE_STAFF':
        return 'Nhân Viên Tiêu Chuẩn';
      default:
        return role;
    }
  }

  getRoleBadgeClass(role: string): string {
    switch (role) {
      case 'ROLE_ADMIN':
        return 'bg-purple-50 text-purple-700 border-purple-200';
      case 'ROLE_CUSTOMER':
        return 'bg-emerald-50 text-emerald-700 border-emerald-200';
      case 'ROLE_RECEPTIONIST':
        return 'bg-sky-50 text-[#0284c7] border-sky-200';
      case 'ROLE_BUTLER':
        return 'bg-amber-50 text-amber-700 border-amber-200';
      case 'ROLE_HOUSEKEEPING':
        return 'bg-teal-50 text-teal-700 border-teal-200';
      case 'ROLE_ACCOUNTANT':
        return 'bg-indigo-50 text-indigo-700 border-indigo-200';
      default:
        return 'bg-slate-50 text-slate-700 border-slate-200';
    }
  }

  getInitials(name: string): string {
    if (!name) return 'U';
    const parts = name.trim().split(' ');
    if (parts.length === 1) return parts[0].substring(0, 2).toUpperCase();
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
  }

  getAvatarBg(name: string): string {
    const colors = [
      'bg-gradient-to-tr from-sky-500 to-blue-600',
      'bg-gradient-to-tr from-emerald-500 to-teal-600',
      'bg-gradient-to-tr from-purple-500 to-indigo-600',
      'bg-gradient-to-tr from-amber-500 to-orange-600',
      'bg-gradient-to-tr from-rose-500 to-pink-600',
    ];
    let hash = 0;
    for (let i = 0; i < (name || '').length; i++) {
      hash = name.charCodeAt(i) + ((hash << 5) - hash);
    }
    const index = Math.abs(hash) % colors.length;
    return colors[index];
  }
}
