import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CustomerCategoryItem } from '../../models/home.models';
import { HomeMockService } from '../../services/home-mock.service';

@Component({
  selector: 'app-stats-bar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './stats-bar.component.html',
  styleUrls: ['./stats-bar.component.scss'],
})
export class StatsBarComponent implements OnInit {
  customerCategories: CustomerCategoryItem[] = [];

  constructor(private homeMockService: HomeMockService) {}

  ngOnInit(): void {
    this.customerCategories = this.homeMockService.getCustomerCategories();
  }

  onSelectCategory(): void {
    const el = document.getElementById('resorts');
    if (el) {
      el.scrollIntoView({ behavior: 'smooth' });
    }
  }
}
