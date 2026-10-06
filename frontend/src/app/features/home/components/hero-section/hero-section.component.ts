import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { BannerService } from '../../../../core/services/banner.service';
import { HomeBanner } from '../../../../core/models/banner.model';

@Component({
  selector: 'app-hero-section',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './hero-section.component.html',
  styleUrls: ['./hero-section.component.scss'],
})
export class HeroSectionComponent implements OnInit, OnDestroy {
  banners: HomeBanner[] = [];
  currentIndex: number = 0;
  private intervalId: any = null;
  readonly slideDuration = 6000; // 6 seconds per slide

  // Fallback banners in case backend or network is offline
  private readonly fallbackBanners: HomeBanner[] = [
    {
      id: 1,
      title: 'Nâng Tầm Kỳ Nghỉ Đỉnh Cao Của Bạn',
      subtitle: 'Hệ Thống 12 Điểm Đến Thượng Lưu AURA',
      description: 'Hòa quyện kiến trúc thanh lịch cùng dịch vụ 5 sao chuẩn quốc tế. Tận hưởng kỳ nghỉ dưỡng xa hoa, riêng tư tuyệt đối tại các vịnh biển đẹp nhất Việt Nam.',
      imageUrl: '/assets/images/rooms/grand-oceanfront.jpg',
      ctaText: 'Khám Phá Biệt Thự',
      ctaLink: '/villas',
      displayOrder: 1,
      isActive: true
    },
    {
      id: 2,
      title: 'Trải Nghiệm Biệt Thự Biển Sầm Sơn',
      subtitle: 'FLC Sầm Sơn Luxury Resort & Villas',
      description: 'Tận hưởng làn gió biển mát lành và cảnh hoàng hôn tuyệt mỹ ngay tại phân khu Biệt thự Ngọc Trai và San Hô với hồ bơi vô cực riêng tư.',
      imageUrl: '/assets/images/rooms/villa-beachfront.jpg',
      ctaText: 'Xem Phân Khu Ngọc Trai',
      ctaLink: '/villas/zone/villa-ngoc-trai',
      displayOrder: 2,
      isActive: true
    },
    {
      id: 3,
      title: 'Aura Elite Club VIP',
      subtitle: 'Đặc Quyền Nghỉ Dưỡng Sang Trọng',
      description: 'Ưu đãi độc quyền lên tới 25% cho kỳ nghỉ cuối tuần dành riêng cho hội viên VIP khi đặt phòng sớm qua hệ thống website.',
      imageUrl: '/assets/images/rooms/royal-penthouse.jpg',
      ctaText: 'Xem Ưu Đãi VIP',
      ctaLink: '/promotions',
      displayOrder: 3,
      isActive: true
    }
  ];

  constructor(private bannerService: BannerService) {}

  ngOnInit(): void {
    this.loadBanners();
  }

  ngOnDestroy(): void {
    this.stopAutoplay();
  }

  loadBanners(): void {
    this.bannerService.getActiveBanners('HOME').subscribe({
      next: (data) => {
        if (data && data.length > 0) {
          this.banners = data;
        } else {
          this.banners = this.fallbackBanners;
        }
        this.startAutoplay();
      },
      error: () => {
        this.banners = this.fallbackBanners;
        this.startAutoplay();
      }
    });
  }

  get currentBanner(): HomeBanner {
    if (this.banners.length === 0) {
      return this.fallbackBanners[0];
    }
    return this.banners[this.currentIndex] || this.banners[0];
  }

  startAutoplay(): void {
    this.stopAutoplay();
    if (this.banners.length > 1) {
      this.intervalId = setInterval(() => {
        this.nextSlide();
      }, this.slideDuration);
    }
  }

  stopAutoplay(): void {
    if (this.intervalId) {
      clearInterval(this.intervalId);
      this.intervalId = null;
    }
  }

  nextSlide(): void {
    if (this.banners.length <= 1) return;
    this.currentIndex = (this.currentIndex + 1) % this.banners.length;
  }

  prevSlide(): void {
    if (this.banners.length <= 1) return;
    this.currentIndex = (this.currentIndex - 1 + this.banners.length) % this.banners.length;
  }

  goToSlide(index: number): void {
    this.currentIndex = index;
    this.startAutoplay(); // Reset timer on manual action
  }
}
