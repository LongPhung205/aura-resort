import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CollectionService } from '../../../../core/services/collection.service';

@Component({
  selector: 'app-browse-by-style',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './browse-by-style.component.html',
  styleUrls: ['./browse-by-style.component.scss']
})
export class BrowseByStyleComponent implements OnInit {
  styles: { id?: string | number; name: string; count: number; image: string }[] = [];
  loading = true;

  constructor(private collectionService: CollectionService) {}

  ngOnInit(): void {
    this.collectionService.getActiveCollections().subscribe({
      next: (data) => {
        if (data && data.length > 0) {
          this.styles = data.map(col => ({
            id: col.slug || col.id || '',
            name: col.name,
            count: Math.floor(Math.random() * 50) + 10, // Mock count until we link to actual villas
            image: col.imageUrl || 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=400&q=80'
          }));
        }
        this.loading = false;
      },
      error: (err) => {
        console.error('Error fetching collections', err);
        this.loading = false;
      }
    });
  }
}
