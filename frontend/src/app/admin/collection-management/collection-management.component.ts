import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CollectionService, Collection, CollectionRequest } from '../../core/services/collection.service';

@Component({
  selector: 'app-collection-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './collection-management.component.html',
  styles: []
})
export class CollectionManagementComponent implements OnInit {
  collections: Collection[] = [];
  loading = false;
  saving = false;

  showModal = false;
  isEditing = false;
  currentId: number | null = null;
  
  formModel: CollectionRequest = {
    name: '',
    slug: '',
    description: '',
    imageUrl: '',
    displayOrder: 1,
    isActive: true
  };

  constructor(private collectionService: CollectionService) {}

  ngOnInit(): void {
    this.loadCollections();
  }

  loadCollections(): void {
    this.loading = true;
    this.collectionService.getAllCollections().subscribe({
      next: (data) => {
        this.collections = data || [];
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading collections', err);
        this.loading = false;
      }
    });
  }

  openCreateModal(): void {
    this.isEditing = false;
    this.currentId = null;
    this.formModel = {
      name: '',
      slug: '',
      description: '',
      imageUrl: 'https://images.unsplash.com/photo-1571896349842-33c89424de2d?auto=format&fit=crop&w=400&q=80',
      displayOrder: this.collections.length + 1,
      isActive: true
    };
    this.showModal = true;
  }

  openEditModal(col: Collection): void {
    this.isEditing = true;
    this.currentId = col.id || null;
    this.formModel = {
      name: col.name,
      slug: col.slug || '',
      description: col.description || '',
      imageUrl: col.imageUrl || '',
      displayOrder: col.displayOrder || 1,
      isActive: col.isActive !== false
    };
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
  }

  saveCollection(): void {
    if (!this.formModel.name) {
      alert('Vui lòng nhập tên bộ sưu tập');
      return;
    }
    this.saving = true;

    if (this.isEditing && this.currentId) {
      this.collectionService.updateCollection(this.currentId, this.formModel).subscribe({
        next: () => {
          this.saving = false;
          this.showModal = false;
          this.loadCollections();
        },
        error: (err) => {
          console.error(err);
          this.saving = false;
          alert(err.error?.message || 'Cập nhật thất bại');
        }
      });
    } else {
      this.collectionService.createCollection(this.formModel).subscribe({
        next: () => {
          this.saving = false;
          this.showModal = false;
          this.loadCollections();
        },
        error: (err) => {
          console.error(err);
          this.saving = false;
          alert(err.error?.message || 'Tạo mới thất bại');
        }
      });
    }
  }

  deleteCollection(col: Collection): void {
    if (confirm(`Bạn có chắc chắn muốn xóa "${col.name}"?`)) {
      this.collectionService.deleteCollection(col.id!).subscribe({
        next: () => {
          this.loadCollections();
        },
        error: (err) => {
          console.error(err);
          alert('Không thể xóa bộ sưu tập này!');
        }
      });
    }
  }
}
