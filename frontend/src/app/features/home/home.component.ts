import { Component, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeroSectionComponent } from './components/hero-section/hero-section.component';
import { BookingSearchWidgetComponent } from './components/booking-search-widget/booking-search-widget.component';
import { FeaturedResortsComponent } from './components/featured-resorts/featured-resorts.component';
import { CtaBannerComponent } from './components/cta-banner/cta-banner.component';
import { NewsletterComponent } from './components/newsletter/newsletter.component';
import { BookingSearchParams } from './models/home.models';
import { SpecialOffersComponent } from './components/special-offers/special-offers.component';
import { ExploreDestinationsComponent } from './components/explore-destinations/explore-destinations.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    HeroSectionComponent,
    BookingSearchWidgetComponent,
    FeaturedResortsComponent,
    CtaBannerComponent,
    NewsletterComponent,
    SpecialOffersComponent,
    ExploreDestinationsComponent
  ],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent {
  @ViewChild(FeaturedResortsComponent) featuredResorts?: FeaturedResortsComponent;

  searchFeedback = '';
  showToast = false;

  handleSearch(params: BookingSearchParams): void {
    const destName = params.destination === 'all' ? 'Tất cả các phân khu Biệt thự' : params.destination;
    this.searchFeedback = `Đang tìm biệt thự tại "${destName}" (${params.adults} người lớn, ${params.rooms} phòng)...`;
    this.showToast = true;

    // Filter featured villas by chosen zone
    if (this.featuredResorts) {
      this.featuredResorts.filterByZone(params.destination);
    }

    // Smooth scroll down to resorts list to show matching results
    const el = document.getElementById('resorts');
    if (el) {
      el.scrollIntoView({ behavior: 'smooth' });
    }

    setTimeout(() => {
      this.showToast = false;
    }, 4000);
  }
}
