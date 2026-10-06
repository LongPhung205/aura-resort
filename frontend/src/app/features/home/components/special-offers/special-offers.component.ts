import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ComboPackageService, ComboPackage } from '../../../../core/services/combo-package.service';

@Component({
  selector: 'app-special-offers',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './special-offers.component.html',
  styleUrls: ['./special-offers.component.scss'],
})
export class SpecialOffersComponent implements OnInit {
  featuredOffer: any = null;
  stackedOffers: any[] = [];
  loading = true;

  private readonly fallbackImages = [
    'https://images.unsplash.com/photo-1540555700478-4be289fbecef?auto=format&fit=crop&w=1200&q=80',
    'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=800&q=80',
    'https://images.unsplash.com/photo-1571896349842-33c89424de2d?auto=format&fit=crop&w=800&q=80',
    'https://images.unsplash.com/photo-1512918728675-ed5a9ecdebfd?auto=format&fit=crop&w=800&q=80',
    'https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80',
  ];

  private readonly DEFAULT_COMBOS: ComboPackage[] = [
    {
      name: 'Kỳ Nghỉ Lãng Mạn',
      price: 4500000, status: 'PUBLISH',
      imageUrl: 'https://images.unsplash.com/photo-1540555700478-4be289fbecef?auto=format&fit=crop&w=1200&q=80',
      description: 'Gói trăng mật lãng mạn kèm hoa tươi, rượu vang và bữa tối riêng tư tại villa.',
      extraServices: [{ name: 'Tiệc nướng BBQ tại Villa' }, { name: 'Dọn dẹp hằng ngày' }, { name: 'Đưa đón sân bay 2 chiều' }]
    },
    {
      name: 'Gia Đình Vui Vẻ',
      price: 6500000, status: 'PUBLISH',
      imageUrl: 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=800&q=80',
      description: 'Gói nghỉ dưỡng gia đình 4-6 người với tiệc BBQ tại sân vườn và đưa đón 2 chiều.',
      extraServices: [{ name: 'Tiệc nướng BBQ tại Villa' }, { name: 'Dọn dẹp hằng ngày' }]
    },
    {
      name: 'Nghỉ Dưỡng Cao Cấp',
      price: 8900000, status: 'PUBLISH',
      imageUrl: 'https://images.unsplash.com/photo-1571896349842-33c89424de2d?auto=format&fit=crop&w=800&q=80',
      description: 'Trải nghiệm xa hoa với đầy đủ dịch vụ 5 sao, spa riêng và butler cá nhân.',
      extraServices: [{ name: 'Dọn dẹp hằng ngày' }, { name: 'Đưa đón sân bay 2 chiều' }]
    }
  ];

  constructor(private comboService: ComboPackageService) {}

  ngOnInit(): void {
    this.comboService.getActive().subscribe({
      next: (data) => {
        this.loading = false;
        const combos = data && data.length > 0 ? data : this.DEFAULT_COMBOS;
        const mapped = combos.map((c, idx) => this.mapCombo(c, idx));
        this.featuredOffer = mapped[0] || null;
        this.stackedOffers = mapped.slice(1, 4);
      },
      error: () => {
        this.loading = false;
        const mapped = this.DEFAULT_COMBOS.map((c, idx) => this.mapCombo(c, idx));
        this.featuredOffer = mapped[0];
        this.stackedOffers = mapped.slice(1);
      }
    });
  }

  private mapCombo(c: ComboPackage, idx: number): any {
    const benefits: string[] = [];
    if (c.extraServices && c.extraServices.length > 0) {
      c.extraServices.forEach(s => benefits.push(s.name));
    }
    if (benefits.length === 0) benefits.push('Trải nghiệm nghỉ dưỡng cao cấp');
    if (c.price) benefits.push(`Trọn gói ${Number(c.price).toLocaleString('vi-VN')}₫`);

    const priceDisplay = c.price
      ? `Chỉ từ ${Number(c.price).toLocaleString('vi-VN')}₫`
      : 'Liên hệ báo giá';

    const imageUrl = c.imageUrl && c.imageUrl.length > 0
      ? c.imageUrl
      : this.fallbackImages[idx % this.fallbackImages.length];

    const badges = ['GÓI ĐỘC QUYỀN', 'DỊCH VỤ TRỌN GÓI', 'COMBO ƯU ĐÃI', 'GÓI CAO CẤP'];

    return {
      id: c.id,
      title: c.name,
      description: c.description || `Gói combo ${c.name} kèm đầy đủ tiện ích cao cấp.`,
      badge: badges[idx % badges.length],
      imageUrl,
      benefits,
      buttonText: 'Đặt Ngay',
      priceDisplay,
      extraServices: c.extraServices
    };
  }
}
