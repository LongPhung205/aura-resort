import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { VillaService } from '../../../../core/services/villa.service';
import { Villa } from '../../../../core/models/villa.model';
import { ZoneService } from '../../../../core/services/zone.service';
import { Zone } from '../../../../core/models/zone.model';

@Component({
  selector: 'app-featured-resorts',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './featured-resorts.component.html',
  styleUrls: ['./featured-resorts.component.scss'],
})
export class FeaturedResortsComponent implements OnInit {
  selectedZone = 'all';
  allVillas: Villa[] = [];
  filteredVillas: Villa[] = [];
  paginatedVillas: Villa[] = [];
  villas: Villa[] = [];
  zones: Zone[] = [];
  loading = true;
  hasError = false;

  // Pagination state
  currentPage = 1;
  pageSize = 9;
  totalPages = 1;
  pageSizeOptions = [9, 18, 27];

  constructor(
    private villaService: VillaService,
    private zoneService: ZoneService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    this.hasError = false;

    this.zoneService.getActiveZones().subscribe({
      next: (zones) => {
        this.zones = zones || [];
      },
      error: (err) => {
        console.warn('Could not load zones for featured villas', err);
      },
    });

    this.villaService.getVillas().subscribe({
      next: (villas) => {
        this.allVillas = villas || [];
        this.filterByZone(this.selectedZone);
        this.loading = false;
      },
      error: (err) => {
        console.error('Error fetching villas from API', err);
        this.hasError = true;
        this.loading = false;
      },
    });
  }

  filterByZone(zoneName: string): void {
    this.selectedZone = zoneName;
    if (zoneName === 'all') {
      this.filteredVillas = this.allVillas;
    } else {
      this.filteredVillas = this.allVillas.filter((v) =>
        (v.zone || '').toLowerCase().includes(zoneName.toLowerCase())
      );
    }
    this.currentPage = 1;
    this.updatePagination();
  }

  updatePagination(): void {
    this.totalPages = Math.ceil(this.filteredVillas.length / this.pageSize) || 1;
    if (this.currentPage > this.totalPages) {
      this.currentPage = 1;
    }
    const start = (this.currentPage - 1) * this.pageSize;
    this.paginatedVillas = this.filteredVillas.slice(start, start + this.pageSize);
    this.villas = this.paginatedVillas;
  }

  changePage(page: number): void {
    if (page < 1 || page > this.totalPages || page === this.currentPage) return;
    this.currentPage = page;
    this.updatePagination();
    const el = document.getElementById('resorts');
    if (el) {
      el.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }

  setPageSize(size: number): void {
    this.pageSize = size;
    this.currentPage = 1;
    this.updatePagination();
  }

  getEndIndex(): number {
    return Math.min(this.currentPage * this.pageSize, this.filteredVillas.length);
  }

  getPages(): (number | string)[] {
    const total = this.totalPages;
    const current = this.currentPage;
    if (total <= 7) {
      return Array.from({ length: total }, (_, i) => i + 1);
    }
    if (current <= 3) {
      return [1, 2, 3, 4, '...', total];
    }
    if (current >= total - 2) {
      return [1, '...', total - 3, total - 2, total - 1, total];
    }
    return [1, '...', current - 1, current, current + 1, '...', total];
  }

  getZoneCount(zoneName: string): number {
    if (zoneName === 'all') {
      return this.allVillas.length;
    }
    return this.allVillas.filter((v) =>
      (v.zone || '').toLowerCase().includes(zoneName.toLowerCase())
    ).length;
  }

  getVillaImageUrl(villa: Villa): string {
    if (villa.imageUrl && villa.imageUrl.trim().length > 0) {
      return villa.imageUrl;
    }
    if (villa.zone?.includes('Ngọc Trai')) {
      return '/assets/images/rooms/villa-beachfront.jpg';
    } else if (villa.zone?.includes('Sao Biển')) {
      return '/assets/images/rooms/grand-oceanfront.jpg';
    }
    return '/assets/images/rooms/royal-penthouse.jpg';
  }

  onImageError(event: Event): void {
    const target = event.target as HTMLImageElement;
    if (target) {
      target.src = '/assets/images/rooms/grand-oceanfront.jpg';
    }
  }

  formatPrice(price?: number): string {
    if (price === undefined || price === null || isNaN(price)) {
      return 'Liên hệ';
    }
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND',
      maximumFractionDigits: 0,
    }).format(price);
  }

  getZoneSlug(villa: Villa): string {
    if (!villa.zone) return 'villa-ngoc-trai';
    const found = this.zones.find(
      (z) => z.name.toLowerCase() === villa.zone?.toLowerCase()
    );
    if (found && found.slug) return found.slug;

    const lower = villa.zone.toLowerCase();
    if (lower.includes('ngọc trai')) return 'villa-ngoc-trai';
    if (lower.includes('sao biển')) return 'villa-sao-bien';
    if (lower.includes('san hô')) return 'villa-san-ho';
    return 'villa-ngoc-trai';
  }

  getZoneBadgeClass(zoneName?: string): string {
    if (!zoneName) return 'bg-slate-100 text-slate-800 border-slate-300';
    const lower = zoneName.toLowerCase();
    if (lower.includes('ngọc trai')) {
      return 'bg-emerald-50 text-emerald-800 border border-emerald-300';
    }
    if (lower.includes('sao biển')) {
      return 'bg-amber-50 text-amber-900 border border-amber-300';
    }
    if (lower.includes('san hô')) {
      return 'bg-sky-50 text-sky-800 border border-sky-300';
    }
    return 'bg-slate-50 text-slate-800 border border-slate-200';
  }

  getStatusBadge(status?: string): { label: string; class: string } {
    switch (status) {
      case 'AVAILABLE':
        return {
          label: 'Sẵn sàng đón khách',
          class: 'bg-emerald-50 text-emerald-700 border border-emerald-200',
        };
      case 'OCCUPIED':
        return {
          label: 'Đang có khách lưu trú',
          class: 'bg-amber-50 text-amber-700 border border-amber-200',
        };
      case 'CLEANING':
        return {
          label: 'Đang dọn phòng',
          class: 'bg-blue-50 text-blue-700 border border-blue-200',
        };
      case 'MAINTENANCE':
        return {
          label: 'Bảo trì',
          class: 'bg-rose-50 text-rose-700 border border-rose-200',
        };
      default:
        return {
          label: 'Biệt thự cao cấp',
          class: 'bg-slate-50 text-slate-700 border border-slate-200',
        };
    }
  }

  viewVillaDetail(villa: Villa): void {
    this.router.navigate(['/villas/detail', villa.id || villa.villaNumber]);
  }

  onBookVilla(villa: Villa): void {
    this.router.navigate(['/villas/detail', villa.id || villa.villaNumber]);
  }
}
