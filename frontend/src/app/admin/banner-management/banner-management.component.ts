import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AdminBannerService } from '../../core/services/admin-banner.service';
import { HomeBanner, HomeBannerPayload } from '../../core/models/banner.model';

@Component({
  selector: 'app-banner-management',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './banner-management.component.html',
  styleUrls: ['./banner-management.component.scss']
})
export class BannerManagementComponent implements OnInit {
  banners: HomeBanner[] = [];
  loading: boolean = false;
  saving: boolean = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  // Modal State
  showModal: boolean = false;
  isEditing: boolean = false;
  currentId: number | null = null;

  // Form State
  formModel: HomeBannerPayload = {
    title: '',
    subtitle: '',
    description: '',
    imageUrl: '',
    mobileImageUrl: '',
    ctaText: 'Khám Phá Ngay',
    ctaLink: '/villas',
    placement: 'HOME',
    displayOrder: 1,
    isActive: true
  };

  // Preview Modal
  previewBanner: HomeBanner | null = null;

  // File Upload State
  isUploadingImage: boolean = false;
  selectedFileName: string = '';

  constructor(private adminBannerService: AdminBannerService) {}

  ngOnInit(): void {
    this.loadBanners();
  }

  loadBanners(): void {
    this.loading = true;
    this.errorMessage = null;
    this.adminBannerService.getAllBanners().subscribe({
      next: (data) => {
        this.banners = data || [];
        this.loading = false;
      },
      error: (err) => {
        console.error('Error fetching banners', err);
        this.errorMessage = 'Không thể tải danh sách banner. Vui lòng thử lại!';
        this.loading = false;
      }
    });
  }

  get activeCount(): number {
    return this.banners.filter(b => b.isActive).length;
  }

  get inactiveCount(): number {
    return this.banners.filter(b => !b.isActive).length;
  }

  openCreateModal(): void {
    this.isEditing = false;
    this.currentId = null;
    this.selectedFileName = '';
    this.formModel = {
      title: '',
      subtitle: '',
      description: '',
      imageUrl: '',
      mobileImageUrl: '',
      ctaText: 'Khám Phá Ngay',
      ctaLink: '/villas',
      placement: 'HOME',
      displayOrder: this.banners.length + 1,
      isActive: true
    };
    this.showModal = true;
  }

  openEditModal(banner: HomeBanner): void {
    this.isEditing = true;
    this.currentId = banner.id;
    this.selectedFileName = '';
    this.formModel = {
      title: banner.title,
      subtitle: banner.subtitle || '',
      description: banner.description || '',
      imageUrl: banner.imageUrl,
      mobileImageUrl: banner.mobileImageUrl || '',
      ctaText: banner.ctaText || 'Khám Phá Ngay',
      ctaLink: banner.ctaLink || '/villas',
      placement: banner.placement || 'HOME',
      displayOrder: banner.displayOrder || 1,
      isActive: banner.isActive !== false
    };
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
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
      this.formModel.imageUrl = (e.target?.result as string) || '';
      this.formModel.mobileImageUrl = this.formModel.imageUrl;
    };
    reader.readAsDataURL(file);

    // Upload to server
    this.adminBannerService.uploadImage(file).subscribe({
      next: (url) => {
        this.isUploadingImage = false;
        if (url) {
          this.formModel.imageUrl = url;
          this.formModel.mobileImageUrl = url;
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
    this.formModel.imageUrl = '';
    this.formModel.mobileImageUrl = '';
    this.selectedFileName = '';
  }

  saveBanner(): void {
    if (this.isUploadingImage) {
      alert('Đang tải ảnh lên máy chủ, vui lòng đợi trong giây lát...');
      return;
    }

    if (!this.formModel.title?.trim() || !this.formModel.imageUrl?.trim()) {
      alert('Vui lòng nhập tiêu đề và chọn ảnh banner!');
      return;
    }

    this.saving = true;
    const payload: HomeBannerPayload = {
      ...this.formModel,
      title: this.formModel.title.trim(),
      subtitle: this.formModel.subtitle?.trim() || '',
      description: this.formModel.description?.trim() || '',
      imageUrl: this.formModel.imageUrl.trim(),
      mobileImageUrl: this.formModel.mobileImageUrl?.trim() || this.formModel.imageUrl.trim(),
      ctaText: this.formModel.ctaText?.trim() || 'Khám Phá Ngay',
      ctaLink: this.formModel.ctaLink?.trim() || '/villas',
      placement: this.formModel.placement?.trim() || 'HOME'
    };

    if (this.isEditing && this.currentId) {
      this.adminBannerService.updateBanner(this.currentId, payload).subscribe({
        next: () => {
          this.saving = false;
          this.showModal = false;
          this.showFlash('Cập nhật banner thành công!');
          this.loadBanners();
        },
        error: (err) => {
          console.error('Update banner error', err);
          const msg = err.error?.message || err.message || 'Cập nhật thất bại, vui lòng kiểm tra lại!';
          alert(msg);
          this.saving = false;
        }
      });
    } else {
      this.adminBannerService.createBanner(payload).subscribe({
        next: () => {
          this.saving = false;
          this.showModal = false;
          this.showFlash('Thêm mới banner thành công!');
          this.loadBanners();
        },
        error: (err) => {
          console.error('Create banner error', err);
          const msg = err.error?.message || err.message || 'Thêm banner thất bại, vui lòng kiểm tra lại!';
          alert(msg);
          this.saving = false;
        }
      });
    }
  }

  toggleStatus(banner: HomeBanner, event: Event): void {
    event.stopPropagation();
    this.adminBannerService.toggleBannerStatus(banner.id).subscribe({
      next: () => {
        banner.isActive = !banner.isActive;
        this.showFlash(`Đã ${banner.isActive ? 'kích hoạt' : 'tạm ẩn'} banner thành công!`);
      },
      error: (err) => {
        console.error('Toggle status error', err);
        alert('Không thể đổi trạng thái banner.');
      }
    });
  }

  deleteBanner(banner: HomeBanner, event: Event): void {
    event.stopPropagation();
    if (confirm(`Bạn có chắc chắn muốn xóa banner "${banner.title}"?`)) {
      this.adminBannerService.deleteBanner(banner.id).subscribe({
        next: () => {
          this.showFlash('Đã xóa banner thành công!');
          this.loadBanners();
        },
        error: (err) => {
          console.error('Delete banner error', err);
          alert('Không thể xóa banner này!');
        }
      });
    }
  }

  openPreview(banner: HomeBanner, event: Event): void {
    event.stopPropagation();
    this.previewBanner = banner;
  }

  closePreview(): void {
    this.previewBanner = null;
  }

  private showFlash(msg: string): void {
    this.successMessage = msg;
    setTimeout(() => {
      this.successMessage = null;
    }, 3500);
  }
}
