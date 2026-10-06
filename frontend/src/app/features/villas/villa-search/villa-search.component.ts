import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { VillaService } from '../../../core/services/villa.service';
import { BannerService } from '../../../core/services/banner.service';
import { Villa } from '../../../core/models/villa.model';
import { BookingSearchWidgetComponent } from '../../home/components/booking-search-widget/booking-search-widget.component';

@Component({
  selector: 'app-villa-search',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, BookingSearchWidgetComponent],
  templateUrl: './villa-search.component.html',
  styleUrls: ['./villa-search.component.scss']
})
export class VillaSearchComponent implements OnInit {
  villas: Villa[] = [];
  filteredVillas: Villa[] = [];
  loading = true;
  hasError = false;
  
  bannerImageUrl = '/assets/images/rooms/grand-oceanfront.jpg';

  // Search Params (Route)
  zone = 'all';
  checkInDate = '';
  checkOutDate = '';
  adults = 2;

  // Search Widget Params (Local before submit)
  widgetZone = 'all';
  widgetCheckIn = '';
  widgetCheckOut = '';
  widgetAdults = 2;

  // Sorting
  sortBy = 'recommended';

  // Filters
  filterPriceRange = 'all'; // all, <2m, 2m-5m, >5m
  filterBedrooms: number[] = [];
  
  // Available filter options (Dynamic based on results)
  availableBedrooms: number[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private villaService: VillaService,
    private bannerService: BannerService
  ) {}

  ngOnInit(): void {
    this.fetchBanner();
    this.route.queryParams.subscribe((params: any) => {
      this.zone = params['zone'] || 'all';
      this.checkInDate = params['checkIn'] || this.getToday();
      this.checkOutDate = params['checkOut'] || this.getTomorrow();
      this.adults = params['adults'] ? parseInt(params['adults'], 10) : 2;

      // Sync widget with route params
      this.widgetZone = this.zone;
      this.widgetCheckIn = this.checkInDate;
      this.widgetCheckOut = this.checkOutDate;
      this.widgetAdults = this.adults;

      this.performSearch();
    });
  }
  
  fetchBanner(): void {
    this.bannerService.getActiveBanners('HOME').subscribe({
      next: (banners) => {
        if (banners && banners.length > 0) {
          this.bannerImageUrl = banners[0].imageUrl;
        }
      },
      error: () => { /* no-op */ }
    });
  }

  getToday(): string {
    return new Date().toISOString().split('T')[0];
  }

  getTomorrow(): string {
    const d = new Date();
    d.setDate(d.getDate() + 1);
    return d.toISOString().split('T')[0];
  }

  onSearchSubmit(): void {
    // Navigate with new query params
    this.router.navigate(['/villas/search'], {
      queryParams: {
        zone: this.widgetZone,
        checkIn: this.widgetCheckIn,
        checkOut: this.widgetCheckOut,
        adults: this.widgetAdults
      }
    });
  }

  handleWidgetSearch(params: any): void {
    this.router.navigate(['/villas/search'], {
      queryParams: {
        zone: params.destination,
        checkIn: params.checkInDate,
        checkOut: params.checkOutDate,
        adults: params.adults
      }
    });
  }

  performSearch(): void {
    this.loading = true;
    this.hasError = false;
    
    this.villaService.searchAvailableVillas(this.zone, this.checkInDate, this.checkOutDate, this.adults)
      .subscribe({
        next: (data: Villa[]) => {
          this.villas = data || [];
          this.extractFilterOptions();
          this.applyFiltersAndSort();
          this.loading = false;
          window.scrollTo({ top: 0, behavior: 'smooth' });
        },
        /* eslint-disable @typescript-eslint/no-explicit-any */ error: (err: any) => {
          this.hasError = true;
          this.loading = false;
        }
      });
  }

  extractFilterOptions(): void {
    const beds = new Set<number>();
    this.villas.forEach(v => {
      if (v.bedroomCount) beds.add(v.bedroomCount);
    });
    this.availableBedrooms = Array.from(beds).sort((a, b) => a - b);
  }

  toggleBedroomFilter(bedCount: number): void {
    const index = this.filterBedrooms.indexOf(bedCount);
    if (index > -1) {
      this.filterBedrooms.splice(index, 1);
    } else {
      this.filterBedrooms.push(bedCount);
    }
    this.applyFiltersAndSort();
  }

  onPriceRangeChange(range: string): void {
    this.filterPriceRange = range;
    this.applyFiltersAndSort();
  }

  onSortChange(event: any): void {
    this.sortBy = event.target.value;
    this.applyFiltersAndSort();
  }

  applyFiltersAndSort(): void {
    let result = [...this.villas];

    // Filter by Price
    if (this.filterPriceRange !== 'all') {
      result = result.filter(v => {
        const price = v.basePrice || 0;
        if (this.filterPriceRange === 'lt2m') return price < 2000000;
        if (this.filterPriceRange === '2m_to_5m') return price >= 2000000 && price <= 5000000;
        if (this.filterPriceRange === 'gt5m') return price > 5000000;
        return true;
      });
    }

    // Filter by Bedrooms
    if (this.filterBedrooms.length > 0) {
      result = result.filter(v => this.filterBedrooms.includes(v.bedroomCount || 1));
    }

    // Sorting
    if (this.sortBy === 'price_asc') {
      result.sort((a, b) => (a.basePrice || 0) - (b.basePrice || 0));
    } else if (this.sortBy === 'price_desc') {
      result.sort((a, b) => (b.basePrice || 0) - (a.basePrice || 0));
    } else if (this.sortBy === 'capacity') {
      result.sort((a, b) => (b.totalCapacity || 0) - (a.totalCapacity || 0));
    } else if (this.sortBy === 'recommended') {
      // Sắp xếp theo sức chứa phù hợp nhất với số khách, sau đó ưu tiên giá tốt
      result.sort((a, b) => {
        const diffA = Math.abs((a.totalCapacity || a.totalAdults || 4) - this.adults);
        const diffB = Math.abs((b.totalCapacity || b.totalAdults || 4) - this.adults);
        if (diffA !== diffB) {
          return diffA - diffB;
        }
        return (a.basePrice || 0) - (b.basePrice || 0);
      });
    }

    this.filteredVillas = result;
  }

  getVillaImageUrl(villa: Villa): string {
    if (villa.imageUrl && villa.imageUrl.trim().length > 0) {
      return villa.imageUrl;
    }
    return 'https://images.unsplash.com/photo-1512917774080-9991f1c4c750?auto=format&fit=crop&w=800&q=80';
  }

  formatPrice(price?: number): string {
    if (!price) return 'Liên hệ';
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND', maximumFractionDigits: 0 }).format(price);
  }
}


