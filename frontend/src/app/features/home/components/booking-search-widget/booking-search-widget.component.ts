import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BookingSearchParams } from '../../models/home.models';
import { ZoneService } from '../../../../core/services/zone.service';

import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-booking-search-widget',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './booking-search-widget.component.html',
  styleUrls: ['./booking-search-widget.component.scss'],
})
export class BookingSearchWidgetComponent implements OnInit {
  @Output() search = new EventEmitter<BookingSearchParams>();

  activeTab: 'resorts' | 'villas' | 'packages' = 'villas';
  isBusinessTrip = false;

  destination = 'all';
  checkInDate: string = '';
  checkOutDate: string = '';
  adults = 2;
  children = 0;
  rooms = 1;
  promoCode = '';

  isGuestDropdownOpen = false;
  isDestinationDropdownOpen = false;

  destinations = [
    { id: 'all', name: 'Tất cả các phân khu Biệt thự Aura', sub: 'Hệ thống Biệt thự nghỉ dưỡng cao cấp' },
    { id: 'Ngọc Trai', name: 'Phân khu Ngọc Trai', sub: 'Ven biển & Gần Club House' },
    { id: 'Sao Biển', name: 'Phân khu Sao Biển', sub: 'Liền kề bãi tắm & Sân Golf' },
    { id: 'San Hô', name: 'Phân khu San Hô', sub: 'Không gian xanh riêng tư & Hồ bơi' },
  ];

  constructor(private zoneService: ZoneService, private router: Router, private route: ActivatedRoute) {
    const today = new Date();
    const next2Days = new Date();
    next2Days.setDate(today.getDate() + 2);
    this.checkInDate = today.toISOString().split('T')[0];
    this.checkOutDate = next2Days.toISOString().split('T')[0];
  }

  ngOnInit(): void {
    this.loadZones();
    // Subscribe to query params to always stay in sync
    this.route.queryParams.subscribe(params => {
      if (params['zone']) this.destination = params['zone'];
      if (params['checkIn']) this.checkInDate = params['checkIn'];
      if (params['checkOut']) this.checkOutDate = params['checkOut'];
      if (params['adults']) this.adults = parseInt(params['adults'], 10);
    });
  }

  loadZones(): void {
    this.zoneService.getActiveZones().subscribe({
      next: (zones) => {
        if (zones && zones.length > 0) {
          this.destinations = [
            { id: 'all', name: 'Tất cả các phân khu Biệt thự Aura', sub: 'Hệ thống Biệt thự nghỉ dưỡng cao cấp' },
            ...zones.map((z) => ({
              id: z.name,
              name: `Phân khu ${z.name}`,
              sub: z.description || 'Biệt thự nghỉ dưỡng cao cấp',
            })),
          ];
        }
      },
      error: (err) => {
        console.warn('Could not load dynamic zones for search widget, using default Aura zones.', err);
      },
    });
  }

  get nightsCount(): number {
    if (!this.checkInDate || !this.checkOutDate) return 1;
    const start = new Date(this.checkInDate).getTime();
    const end = new Date(this.checkOutDate).getTime();
    const diff = Math.ceil((end - start) / (1000 * 60 * 60 * 24));
    return diff > 0 ? diff : 1;
  }

  get selectedDestinationName(): string {
    const found = this.destinations.find((d) => d.id === this.destination);
    return found ? found.name : 'Tất cả các khu nghỉ dưỡng Aura';
  }

  selectDestination(id: string): void {
    this.destination = id;
    this.isDestinationDropdownOpen = false;
  }

  toggleGuestDropdown(): void {
    this.isGuestDropdownOpen = !this.isGuestDropdownOpen;
    this.isDestinationDropdownOpen = false;
  }

  toggleDestinationDropdown(): void {
    this.isDestinationDropdownOpen = !this.isDestinationDropdownOpen;
    this.isGuestDropdownOpen = false;
  }

  updateCount(field: 'adults' | 'children' | 'rooms', delta: number): void {
    if (field === 'adults') {
      this.adults = Math.max(1, Math.min(10, this.adults + delta));
    } else if (field === 'children') {
      this.children = Math.max(0, Math.min(8, this.children + delta));
    } else if (field === 'rooms') {
      this.rooms = Math.max(1, Math.min(5, this.rooms + delta));
    }
  }

  onSearch(): void {
    // Notify parent component if needed (for fallback/logging)
    const searchParams: BookingSearchParams = {
      destination: this.destination,
      checkInDate: this.checkInDate,
      checkOutDate: this.checkOutDate,
      adults: this.adults,
      children: this.children,
      rooms: this.rooms,
      promoCode: this.promoCode,
      isBusinessTrip: this.isBusinessTrip,
      activeTab: this.activeTab,
    };
    this.search.emit(searchParams);

    // Dẫn hướng sang trang Search kèm tham số
    this.router.navigate(['/villas/search'], {
      queryParams: {
        zone: this.destination,
        checkIn: this.checkInDate,
        checkOut: this.checkOutDate,
        adults: this.adults
      }
    });
  }
}
