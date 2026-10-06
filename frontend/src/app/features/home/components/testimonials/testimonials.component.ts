import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TestimonialItem } from '../../models/home.models';
import { HomeMockService } from '../../services/home-mock.service';

@Component({
  selector: 'app-testimonials',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './testimonials.component.html',
  styleUrls: ['./testimonials.component.scss'],
})
export class TestimonialsComponent implements OnInit {
  testimonials: TestimonialItem[] = [];

  pressList = [
    'CONDÉ NAST TRAVELER',
    'FORBES TRAVEL GUIDE',
    'TRAVEL + LEISURE',
    'ROBB REPORT',
    'MICHELIN GUIDE HOTEL',
  ];

  constructor(private homeMockService: HomeMockService) {}

  ngOnInit(): void {
    this.testimonials = this.homeMockService.getTestimonials();
  }
}
