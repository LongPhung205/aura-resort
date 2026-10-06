import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ZoneService } from '../../core/services/zone.service';
import { Zone, ZonePayload } from '../../core/models/zone.model';

@Component({
  selector: 'app-zone-management',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './zone-management.component.html',
  styleUrls: ['./zone-management.component.scss']
})
export class ZoneManagementComponent implements OnInit {
  zones: Zone[] = [];
  loading: boolean = false;
  saving: boolean = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  // Modal State
  showModal: boolean = false;
  isEditing: boolean = false;
  currentId: number | null = null;

  // Form State
  formModel: ZonePayload = {
    name: '',
    slug: '',
    tag: '',
    icon: 'holiday_village',
    badgeClass: 'bg-emerald-50 text-emerald-700 border-emerald-200',
    description: '',
    bannerUrl: '/assets/images/rooms/villa-beachfront.jpg',
    highlights: '',
    displayOrder: 1,
    isActive: true
  };

  // File Upload State
  isUploadingImage: boolean = false;
  selectedFileName: string = '';

  // Preset icon choices
  readonly iconPresets = [
    { label: 'Kim Cương', icon: 'diamond' },
    { label: 'Ngôi Sao', icon: 'star' },
    { label: 'Làn Sóng', icon: 'waves' },
    { label: 'Biệt Thự', icon: 'holiday_village' },
    { label: 'Hồ Bơi', icon: 'pool' },
    { label: 'Vương Miện', icon: 'crown' }
  ];

  constructor(private zoneService: ZoneService) {}

  ngOnInit(): void {
    this.loadZones();
  }

  loadZones(): void {
    this.loading = true;
    this.errorMessage = null;
    this.zoneService.getAllZones().subscribe({
      next: (data) => {
        this.zones = data || [];
        this.loading = false;
      },
      error: (err) => {
        console.error('Error fetching zones', err);
        this.errorMessage = 'Không thể tải danh sách phân khu. Vui lòng thử lại!';
        this.loading = false;
      }
    });
  }

  get totalVillas(): number {
    return this.zones.reduce((sum, z) => sum + (z.villaCount || 0), 0);
  }

  openCreateModal(): void {
    this.isEditing = false;
    this.currentId = null;
    this.formModel = {
      name: '',
      slug: '',
      tag: '',
      icon: 'holiday_village',
      badgeClass: 'bg-emerald-50 text-emerald-700 border-emerald-200',
      description: '',
      bannerUrl: '/assets/images/rooms/villa-beachfront.jpg',
      highlights: 'Gần bãi biển riêng,Hồ bơi vô cực riêng,Quản gia 24/7,Sân BBQ ngoài trời',
      displayOrder: this.zones.length + 1,
      isActive: true
    };
    this.showModal = true;
  }

  openEditModal(zone: Zone): void {
    this.isEditing = true;
    this.currentId = zone.id;
    this.formModel = {
      name: zone.name,
      slug: zone.slug || '',
      tag: zone.tag || zone.name.toUpperCase(),
      icon: zone.icon || 'holiday_village',
      badgeClass: zone.badgeClass || 'bg-emerald-50 text-emerald-700 border-emerald-200',
      description: zone.description || '',
      bannerUrl: zone.bannerUrl || '/assets/images/rooms/villa-beachfront.jpg',
      highlights: zone.highlights || '',
      displayOrder: zone.displayOrder || 1,
      isActive: zone.isActive !== false
    };
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
  }

  onNameChange(): void {
    if (!this.isEditing && (!this.formModel.slug || this.formModel.slug.startsWith('villa-'))) {
      const slugVal = this.toSlug(this.formModel.name);
      this.formModel.slug = slugVal ? `villa-${slugVal}` : '';
      this.formModel.tag = this.formModel.name.toUpperCase();
    }
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const file = input.files[0];
    this.selectedFileName = file.name;
    this.isUploadingImage = true;

    // Immediate preview with FileReader
    const reader = new FileReader();
    reader.onload = (e: ProgressEvent<FileReader>) => {
      this.formModel.bannerUrl = (e.target?.result as string) || '';
    };
    reader.readAsDataURL(file);

    // Upload to server
    this.zoneService.uploadImage(file).subscribe({
      next: (url) => {
        this.isUploadingImage = false;
        if (url) {
          this.formModel.bannerUrl = url;
        }
        this.showFlash(`Đã tải lên tệp "${file.name}" thành công!`);
      },
      error: (err) => {
        console.error('Upload error', err);
        this.isUploadingImage = false;
        this.showFlash(`Đã chọn ảnh từ máy tính (lưu dữ liệu cục bộ)!`);
      }
    });
  }

  removeSelectedImage(): void {
    this.formModel.bannerUrl = '';
    this.selectedFileName = '';
  }

  selectIcon(icon: string): void {
    this.formModel.icon = icon;
  }

  saveZone(): void {
    if (this.isUploadingImage) {
      alert('Đang tải ảnh lên máy chủ, vui lòng đợi trong giây lát...');
      return;
    }

    if (!this.formModel.name?.trim()) {
      alert('Vui lòng nhập tên phân khu!');
      return;
    }

    this.saving = true;
    const payload: ZonePayload = {
      ...this.formModel,
      name: this.formModel.name.trim(),
      slug: this.formModel.slug ? this.toSlug(this.formModel.slug) : `villa-${this.toSlug(this.formModel.name)}`,
      tag: this.formModel.tag?.trim() || this.formModel.name.toUpperCase(),
      bannerUrl: this.formModel.bannerUrl?.trim() || null as any,
      highlights: this.formModel.highlights?.trim() || null as any,
      description: this.formModel.description?.trim() || null as any
    };

    if (this.isEditing && this.currentId) {
      this.zoneService.updateZone(this.currentId, payload).subscribe({
        next: () => {
          this.saving = false;
          this.showModal = false;
          this.showFlash('Cập nhật phân khu thành công!');
          this.loadZones();
        },
        error: (err) => {
          console.error('Update zone error', err);
          alert(err.error?.message || 'Cập nhật phân khu thất bại!');
          this.saving = false;
        }
      });
    } else {
      this.zoneService.createZone(payload).subscribe({
        next: () => {
          this.saving = false;
          this.showModal = false;
          this.showFlash('Tạo mới phân khu thành công!');
          this.loadZones();
        },
        error: (err) => {
          console.error('Create zone error', err);
          alert(err.error?.message || 'Tạo phân khu thất bại!');
          this.saving = false;
        }
      });
    }
  }

  deleteZone(zone: Zone, event: Event): void {
    event.stopPropagation();
    if (zone.villaCount && zone.villaCount > 0) {
      alert(`Không thể xóa phân khu "${zone.name}" vì đang có ${zone.villaCount} biệt thự trực thuộc!`);
      return;
    }

    if (confirm(`Bạn có chắc chắn muốn xóa phân khu "${zone.name}"?`)) {
      this.zoneService.deleteZone(zone.id).subscribe({
        next: () => {
          this.showFlash('Đã xóa phân khu thành công!');
          this.loadZones();
        },
        error: (err) => {
          console.error('Delete zone error', err);
          alert(err.error?.message || 'Không thể xóa phân khu!');
        }
      });
    }
  }

  parseHighlights(highlights?: string): string[] {
    if (!highlights) return [];
    return highlights.split(',').map(s => s.trim()).filter(s => s.length > 0);
  }

  private toSlug(str: string): string {
    if (!str) return '';
    return str
      .toLowerCase()
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .replace(/đ/g, 'd')
      .replace(/[^a-z0-9 -]/g, '')
      .replace(/\s+/g, '-')
      .replace(/-+/g, '-')
      .replace(/^-+|-+$/g, '');
  }

  private showFlash(msg: string): void {
    this.successMessage = msg;
    setTimeout(() => {
      this.successMessage = null;
    }, 3500);
  }
}
