import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { ZoneService } from '../../../core/services/zone.service';
import { Zone, ZoneDetail, ZoneDetailVilla } from '../../../core/models/zone.model';

@Component({
  selector: 'app-zone-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './zone-detail.component.html',
  styleUrls: ['./zone-detail.component.scss']
})
export class ZoneDetailComponent implements OnInit, OnDestroy {
  slug: string = '';
  zoneDetail: ZoneDetail | null = null;
  allZones: Zone[] = [];
  loading: boolean = true;
  errorMessage: string | null = null;

  // Filter & Sort State
  selectedBedrooms: string = 'ALL';
  selectedSort: string = 'DEFAULT';
  searchQuery: string = '';

  private routeSub: Subscription | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private zoneService: ZoneService
  ) {}

  ngOnInit(): void {
    // Load all zones for bottom switcher
    this.zoneService.getActiveZones().subscribe({
      next: (zones) => {
        this.allZones = zones || [];
      },
      error: (err) => console.error('Error fetching all zones', err)
    });

    // Listen to route param changes
    this.routeSub = this.route.paramMap.subscribe((params) => {
      const slugParam = params.get('slug');
      if (slugParam) {
        this.slug = slugParam;
        this.loadZoneDetail(this.slug);
      }
    });
  }

  ngOnDestroy(): void {
    if (this.routeSub) {
      this.routeSub.unsubscribe();
    }
  }

  loadZoneDetail(slug: string): void {
    this.loading = true;
    this.errorMessage = null;

    this.zoneService.getZoneBySlug(slug).subscribe({
      next: (data) => {
        this.zoneDetail = data;
        this.loading = false;
        // Scroll to top smoothly
        window.scrollTo({ top: 0, behavior: 'smooth' });
      },
      error: (err) => {
        console.error('Error fetching zone detail', err);
        this.errorMessage = 'Không tìm thấy thông tin phân khu biệt thự này hoặc đường dẫn không đúng.';
        this.loading = false;
      }
    });
  }

  get otherZones(): Zone[] {
    if (!this.zoneDetail?.zone) return this.allZones;
    return this.allZones.filter(z => z.slug !== this.zoneDetail?.zone.slug);
  }

  get filteredVillas(): ZoneDetailVilla[] {
    if (!this.zoneDetail?.villas) return [];
    let list = [...this.zoneDetail.villas];

    // Filter by bedroom
    if (this.selectedBedrooms !== 'ALL') {
      const beds = parseInt(this.selectedBedrooms, 10);
      list = list.filter(v => (v.bedroomCount || 1) >= beds);
    }

    // Filter by search query
    if (this.searchQuery.trim()) {
      const q = this.searchQuery.toLowerCase().trim();
      list = list.filter(v =>
        v.villaNumber?.toLowerCase().includes(q) ||
        v.villaTypeName?.toLowerCase().includes(q)
      );
    }

    // Sort
    if (this.selectedSort === 'PRICE_ASC') {
      list.sort((a, b) => (a.basePrice || 0) - (b.basePrice || 0));
    } else if (this.selectedSort === 'PRICE_DESC') {
      list.sort((a, b) => (b.basePrice || 0) - (a.basePrice || 0));
    }

    return list;
  }

  parseHighlights(highlights?: string): string[] {
    if (!highlights) return [];
    return highlights.split(',').map(s => s.trim()).filter(s => s.length > 0);
  }

  goToBooking(villa: ZoneDetailVilla): void {
    this.router.navigate(['/booking'], { queryParams: { villaId: villa.id, villaNumber: villa.villaNumber } });
  }
}
