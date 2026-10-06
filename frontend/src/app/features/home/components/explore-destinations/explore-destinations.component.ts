import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ZoneService } from '../../../../core/services/zone.service';

@Component({
  selector: 'app-explore-destinations',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './explore-destinations.component.html',
  styleUrls: ['./explore-destinations.component.scss']
})
export class ExploreDestinationsComponent implements OnInit {
  /* eslint-disable @typescript-eslint/no-explicit-any */
  zones: any[] = [];
  loading = true;

  constructor(private zoneService: ZoneService) {}

  ngOnInit(): void {
    this.zoneService.getActiveZones().subscribe({
      next: (data) => {
        this.zones = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading zones', err);
        this.loading = false;
      }
    });
  }

  getZoneImage(zone: any): string {
    // Nếu admin đã cấu hình URL ảnh thật qua field 'icon'
    if (zone.icon && (zone.icon.startsWith('http') || zone.icon.startsWith('/assets'))) {
      return zone.icon;
    }
    
    // Dynamic mapping for mock images based on zone as fallback
    const name = (zone.name || '').toLowerCase();
    if (name.includes('ngọc trai')) {
      return 'https://images.unsplash.com/photo-1512917774080-9991f1c4c750?auto=format&fit=crop&w=800&q=80';
    } else if (name.includes('sao biển')) {
      return 'https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?auto=format&fit=crop&w=800&q=80';
    } else if (name.includes('san hô')) {
      return 'https://images.unsplash.com/photo-1582268611958-ebfd161ef9cf?auto=format&fit=crop&w=800&q=80';
    }
    return 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=800&q=80';
  }
}
