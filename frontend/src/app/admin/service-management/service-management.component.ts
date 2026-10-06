import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  AdminExtraServiceService,
  ExtraServiceItem,
  ExtraServicePayload,
} from '../../core/services/admin-extra-service.service';
import { BodyPortalDirective } from '../../shared/directives/body-portal.directive';

type ServiceType = 'ALL' | 'DINING' | 'TRANSPORT' | 'SPA' | 'ENTERTAINMENT' | 'CLEANING' | 'OTHER';

@Component({
  selector: 'app-service-management',
  standalone: true,
  imports: [CommonModule, FormsModule, BodyPortalDirective],
  templateUrl: './service-management.component.html',
  styleUrls: ['./service-management.component.scss'],
})
export class ServiceManagementComponent implements OnInit {
  services: ExtraServiceItem[] = [];
  filteredServices: ExtraServiceItem[] = [];
  activeFilter: ServiceType = 'ALL';

  // Toast
  toastMessage: string | null = null;
  toastType: 'success' | 'error' = 'success';

  // Modal
  showFormModal = false;
  showDeleteModal = false;
  isEditMode = false;
  isSaving = false;
  serviceToDelete: ExtraServiceItem | null = null;

  // Form
  formData: ExtraServicePayload = {
    name: '',
    description: '',
    price: 0,
    type: 'OTHER',
    unit: 'lần',
    icon: '',
    imageUrl: '',
    isActive: true,
  };
  editingId: number | null = null;

  // Filter type tabs
  typeFilters: { key: ServiceType; label: string; icon: string }[] = [
    { key: 'ALL', label: 'Tất Cả', icon: 'apps' },
    { key: 'DINING', label: 'Ẩm Thực', icon: 'restaurant' },
    { key: 'TRANSPORT', label: 'Vận Chuyển', icon: 'directions_car' },
    { key: 'SPA', label: 'Spa & Wellness', icon: 'spa' },
    { key: 'ENTERTAINMENT', label: 'Giải Trí', icon: 'celebration' },
    { key: 'CLEANING', label: 'Vệ Sinh', icon: 'cleaning_services' },
    { key: 'OTHER', label: 'Khác', icon: 'more_horiz' },
  ];

  // Service type options for form
  typeOptions = [
    { value: 'DINING', label: 'Ẩm Thực' },
    { value: 'TRANSPORT', label: 'Vận Chuyển' },
    { value: 'SPA', label: 'Spa & Wellness' },
    { value: 'ENTERTAINMENT', label: 'Giải Trí' },
    { value: 'CLEANING', label: 'Vệ Sinh' },
    { value: 'OTHER', label: 'Khác' },
  ];

  unitOptions = ['người', 'gói', 'ngày', 'lần', 'chuyến', 'giờ', 'phòng'];

  constructor(private svc: AdminExtraServiceService) {}

  ngOnInit(): void {
    this.loadServices();
  }

  loadServices(): void {
    this.svc.getAll().subscribe((data) => {
      this.services = data;
      this.applyFilter();
    });
  }

  // ─── KPI Getters ───
  get totalCount(): number {
    return this.services.length;
  }
  get activeCount(): number {
    return this.services.filter((s) => s.isActive).length;
  }
  get inactiveCount(): number {
    return this.services.filter((s) => !s.isActive).length;
  }
  get totalVillaAssignments(): number {
    return this.services.reduce((sum, s) => sum + (s.villaCount || 0), 0);
  }

  // ─── Filter ───
  setFilter(type: ServiceType): void {
    this.activeFilter = type;
    this.applyFilter();
  }

  applyFilter(): void {
    if (this.activeFilter === 'ALL') {
      this.filteredServices = [...this.services];
    } else {
      this.filteredServices = this.services.filter((s) => s.type === this.activeFilter);
    }
  }

  getTypeLabel(type: string): string {
    const found = this.typeOptions.find((t) => t.value === type);
    return found ? found.label : type;
  }

  getTypeBadgeClass(type: string): string {
    switch (type) {
      case 'DINING': return 'bg-amber-50 text-amber-700 border-amber-200';
      case 'TRANSPORT': return 'bg-sky-50 text-sky-700 border-sky-200';
      case 'SPA': return 'bg-purple-50 text-purple-700 border-purple-200';
      case 'ENTERTAINMENT': return 'bg-pink-50 text-pink-700 border-pink-200';
      case 'CLEANING': return 'bg-emerald-50 text-emerald-700 border-emerald-200';
      default: return 'bg-slate-50 text-slate-600 border-slate-200';
    }
  }

  formatPrice(price: number): string {
    if (price >= 1000000) {
      return (price / 1000000).toFixed(1).replace(/\.0$/, '') + 'tr';
    }
    return new Intl.NumberFormat('vi-VN').format(price) + 'đ';
  }

  // ─── Modal: Create / Edit ───
  openCreateModal(): void {
    this.isEditMode = false;
    this.editingId = null;
    this.formData = {
      name: '',
      description: '',
      price: 0,
      type: 'OTHER',
      unit: 'lần',
      icon: '',
      imageUrl: '',
      isActive: true,
    };
    this.showFormModal = true;
  }

  openEditModal(s: ExtraServiceItem): void {
    this.isEditMode = true;
    this.editingId = s.id;
    this.formData = {
      name: s.name,
      description: s.description || '',
      price: s.price,
      type: s.type,
      unit: s.unit,
      icon: s.icon || '',
      imageUrl: s.imageUrl || '',
      isActive: s.isActive,
    };
    this.showFormModal = true;
  }

  closeFormModal(): void {
    this.showFormModal = false;
  }

  saveService(): void {
    if (!this.formData.name.trim()) {
      this.showToast('Tên dịch vụ không được để trống!', 'error');
      return;
    }
    if (this.formData.price < 0) {
      this.showToast('Giá dịch vụ phải >= 0!', 'error');
      return;
    }

    this.isSaving = true;

    if (this.isEditMode && this.editingId) {
      this.svc.update(this.editingId, this.formData).subscribe({
        next: () => {
          this.isSaving = false;
          this.showFormModal = false;
          this.showToast(`Cập nhật dịch vụ "${this.formData.name}" thành công!`);
          this.loadServices();
        },
        error: (err) => {
          this.isSaving = false;
          this.showToast(err?.error?.message || 'Lỗi khi cập nhật dịch vụ!', 'error');
        },
      });
    } else {
      this.svc.create(this.formData).subscribe({
        next: () => {
          this.isSaving = false;
          this.showFormModal = false;
          this.showToast(`Tạo dịch vụ "${this.formData.name}" thành công!`);
          this.loadServices();
        },
        error: (err) => {
          this.isSaving = false;
          this.showToast(err?.error?.message || 'Lỗi khi tạo dịch vụ!', 'error');
        },
      });
    }
  }

  // ─── Toggle Active ───
  toggleActive(s: ExtraServiceItem): void {
    this.svc.toggleActive(s.id).subscribe({
      next: (updated) => {
        const idx = this.services.findIndex((x) => x.id === s.id);
        if (idx !== -1) {
          this.services[idx] = updated;
          this.applyFilter();
        }
        this.showToast(
          updated.isActive
            ? `Đã kích hoạt dịch vụ "${updated.name}"`
            : `Đã tạm ẩn dịch vụ "${updated.name}"`
        );
      },
      error: () => this.showToast('Lỗi khi cập nhật trạng thái!', 'error'),
    });
  }

  // ─── Delete ───
  openDeleteConfirm(s: ExtraServiceItem): void {
    this.serviceToDelete = s;
    this.showDeleteModal = true;
  }

  confirmDelete(): void {
    if (!this.serviceToDelete) return;
    const name = this.serviceToDelete.name;
    this.svc.delete(this.serviceToDelete.id).subscribe({
      next: () => {
        this.showDeleteModal = false;
        this.serviceToDelete = null;
        this.showToast(`Đã xóa dịch vụ "${name}" thành công!`);
        this.loadServices();
      },
      error: (err) => {
        this.showToast(err?.error?.message || `Không thể xóa dịch vụ "${name}"!`, 'error');
      },
    });
  }

  // ─── Toast ───
  showToast(msg: string, type: 'success' | 'error' = 'success'): void {
    this.toastMessage = msg;
    this.toastType = type;
    setTimeout(() => {
      this.toastMessage = null;
    }, 4000);
  }
}
