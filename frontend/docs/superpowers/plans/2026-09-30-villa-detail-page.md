# Villa Detail Page Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Create a luxurious, modern, and high-converting Villa Detail Page that showcases individual villa properties with rich imagery, clear amenities, and a floating booking widget.

**Architecture:** Create a new standalone Angular component `villa-detail` within the `src/app/features/villas` directory. The component will consume `VillaService` to fetch data by ID from the route parameters. The layout will follow a 2-column design on desktop (main content on left, sticky booking widget on right) and a stacked design on mobile.

**Tech Stack:** Angular 18 (Standalone Components), TailwindCSS, SCSS, RxJS.

## Global Constraints

- Design must strictly follow the `thietke.md` guidelines (Luxury resort aesthetic).
- Use `Montserrat` for general UI text and `Playfair Display` for serif headings, with `bg-[#FAFAFA]` for backgrounds.
- All styles must be implemented using Tailwind utility classes where possible.
- Avoid generic SaaS card looks. Emphasize "distinctive, intentional visual design" (bolder, more colorful gradients using Orange `#EA580C` and Ocean Blue `#0369A1`).
- Ensure full mobile responsiveness.

---

### Task 1: Component Scaffolding and Routing Setup

**Files:**
- Create: `src/app/features/villas/villa-detail/villa-detail.component.ts`
- Create: `src/app/features/villas/villa-detail/villa-detail.component.html`
- Create: `src/app/features/villas/villa-detail/villa-detail.component.scss`
- Modify: `src/app/app.routes.ts` (or the respective routing module for villas)

**Interfaces:**
- Consumes: `VillaService` and `ActivatedRoute` to fetch the villa ID.
- Produces: A routed page accessible at `/villas/detail/:id`.

- [ ] **Step 1: Scaffold the Component**
Generate the standalone component. Since we are manually creating files in the plan:

```typescript
// src/app/features/villas/villa-detail/villa-detail.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { VillaService } from '../../../../core/services/villa.service';
import { Villa } from '../../../../core/models/villa.model';

@Component({
  selector: 'app-villa-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './villa-detail.component.html',
  styleUrls: ['./villa-detail.component.scss']
})
export class VillaDetailComponent implements OnInit {
  villaId: string | null = null;
  villa: Villa | null = null;
  loading: boolean = true;
  hasError: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private villaService: VillaService
  ) {}

  ngOnInit(): void {
    this.villaId = this.route.snapshot.paramMap.get('id');
    if (this.villaId) {
      this.loadVilla(this.villaId);
    } else {
      this.hasError = true;
      this.loading = false;
    }
  }

  loadVilla(id: string): void {
    this.villaService.getVillas().subscribe({
      next: (villas) => {
        this.villa = villas.find(v => v.id === id) || null;
        if (!this.villa) this.hasError = true;
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.hasError = true;
        this.loading = false;
      }
    });
  }

  getVillaImageUrl(villa: Villa): string {
    if (villa.imageUrl) return villa.imageUrl;
    return '/assets/images/rooms/royal-penthouse.jpg';
  }
}
```

- [ ] **Step 2: Initialize HTML and SCSS**

```html
<!-- src/app/features/villas/villa-detail/villa-detail.component.html -->
<div *ngIf="loading" class="min-h-screen flex items-center justify-center bg-slate-50">
  <div class="animate-spin rounded-full h-12 w-12 border-4 border-[#0284C7] border-t-transparent"></div>
</div>

<div *ngIf="hasError" class="min-h-screen flex items-center justify-center bg-slate-50">
  <div class="text-center">
    <h2 class="text-2xl font-bold text-red-600">Không tìm thấy Biệt Thự</h2>
    <a routerLink="/" class="text-[#0369A1] hover:underline mt-4 inline-block">Quay lại trang chủ</a>
  </div>
</div>

<div *ngIf="!loading && !hasError && villa" class="bg-[#FAFAFA] min-h-screen pb-20">
  <p>Detail page for {{ villa.villaNumber }} loaded.</p>
</div>
```

```scss
// src/app/features/villas/villa-detail/villa-detail.component.scss
:host {
  display: block;
}
```

- [ ] **Step 3: Update Routing**
Modify the application routing (e.g., `app.routes.ts`) to include the new route. Ensure this correctly fits into your routing structure.

```typescript
// Add this route to your routes array
{
  path: 'villas/detail/:id',
  loadComponent: () => import('./features/villas/villa-detail/villa-detail.component').then(m => m.VillaDetailComponent)
}
```

### Task 2: Implement the Luxury Image Gallery & Header

**Files:**
- Modify: `src/app/features/villas/villa-detail/villa-detail.component.html`

- [ ] **Step 1: Replace the placeholder with the Image Gallery and Title**

```html
<!-- src/app/features/villas/villa-detail/villa-detail.component.html -->
<!-- (Keep loading/error blocks above) -->

<div *ngIf="!loading && !hasError && villa" class="bg-[#FAFAFA] min-h-screen pb-20">
  
  <!-- Hero Gallery -->
  <div class="w-full h-[50vh] md:h-[65vh] relative bg-slate-900">
    <img [src]="getVillaImageUrl(villa)" alt="Villa Image" class="w-full h-full object-cover opacity-90" />
    <div class="absolute inset-0 bg-gradient-to-t from-black/80 via-transparent to-transparent"></div>
    
    <!-- Title overlay -->
    <div class="absolute bottom-0 left-0 w-full p-6 md:p-12 max-w-7xl mx-auto">
      <span class="inline-block px-3 py-1 bg-[#EA580C] text-white text-[11px] font-black uppercase tracking-widest rounded-sm mb-4">
        Phân Khu {{ villa.zone || 'Aura' }}
      </span>
      <h1 class="text-4xl md:text-6xl font-black text-white mb-2 tracking-wide leading-tight drop-shadow-lg">
        Villa {{ villa.villaNumber }}
      </h1>
      <p class="text-lg text-slate-200 font-bold tracking-wider uppercase drop-shadow-md">
        {{ villa.villaTypeName || 'Biệt Thự Nghỉ Dưỡng Căn Góc' }}
      </p>
    </div>
  </div>

  <!-- Breadcrumbs -->
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
    <div class="flex items-center space-x-2 text-[12px] font-black tracking-widest text-slate-500 uppercase">
      <a routerLink="/" class="hover:text-[#0369A1] transition-colors">Trang Chủ</a>
      <span class="material-symbols-outlined text-[14px]">chevron_right</span>
      <a routerLink="/villas" class="hover:text-[#0369A1] transition-colors">Bộ Sưu Tập</a>
      <span class="material-symbols-outlined text-[14px]">chevron_right</span>
      <span class="text-[#0369A1]">Villa {{ villa.villaNumber }}</span>
    </div>
  </div>
```

### Task 3: Implement Content and Sticky Booking Widget

**Files:**
- Modify: `src/app/features/villas/villa-detail/villa-detail.component.html`

- [ ] **Step 1: Build the 2-Column Layout**

Append to the `villa-detail.component.html`:

```html
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <div class="flex flex-col lg:flex-row gap-12">
      
      <!-- Left Column: Details -->
      <div class="lg:w-2/3 space-y-10">
        
        <!-- Quick Specs -->
        <div class="bg-white p-6 rounded-2xl shadow-sm border border-slate-100 flex flex-wrap gap-6 items-center">
          <div class="flex items-center space-x-3">
            <span class="material-symbols-outlined text-[28px] text-[#EA580C]">bed</span>
            <div>
              <div class="text-[11px] text-slate-400 font-bold uppercase tracking-widest">Phòng ngủ</div>
              <div class="text-[15px] font-black text-[#0369A1]">{{ villa.bedroomCount || villa.rooms?.length || 1 }} Phòng</div>
            </div>
          </div>
          <div class="w-px h-10 bg-slate-100 hidden sm:block"></div>
          <div class="flex items-center space-x-3">
            <span class="material-symbols-outlined text-[28px] text-[#EA580C]">group</span>
            <div>
              <div class="text-[11px] text-slate-400 font-bold uppercase tracking-widest">Sức chứa</div>
              <div class="text-[15px] font-black text-[#0369A1]">Tối đa {{ villa.totalCapacity || villa.capacity || 4 }} Khách</div>
            </div>
          </div>
          <div class="w-px h-10 bg-slate-100 hidden sm:block"></div>
          <div class="flex items-center space-x-3">
            <span class="material-symbols-outlined text-[28px] text-[#EA580C]">pool</span>
            <div>
              <div class="text-[11px] text-slate-400 font-bold uppercase tracking-widest">Hồ bơi</div>
              <div class="text-[15px] font-black text-[#0369A1]">Riêng tư</div>
            </div>
          </div>
        </div>

        <!-- Description -->
        <div>
          <h2 class="text-2xl font-black text-[#0369A1] uppercase tracking-wider mb-4 border-l-4 border-[#EA580C] pl-4">Tổng Quan</h2>
          <p class="text-slate-600 font-medium leading-relaxed text-[15px]">
            Tận hưởng kỳ nghỉ dưỡng xa hoa và trọn vẹn tại Villa {{ villa.villaNumber }}, kiệt tác kiến trúc ven biển thuộc quần thể Aura Resort FLC Sầm Sơn. Với thiết kế {{ villa.structureType || 'hiện đại' }} cùng không gian mở ôm trọn luồng gió biển, đây là lựa chọn hoàn hảo cho các gia đình và nhóm bạn muốn tìm kiếm sự riêng tư tuyệt đối nhưng vẫn kết nối với các tiện ích 5 sao.
          </p>
        </div>

        <!-- Amenities -->
        <div>
          <h2 class="text-2xl font-black text-[#0369A1] uppercase tracking-wider mb-4 border-l-4 border-[#EA580C] pl-4">Tiện ích Đặc Quyền</h2>
          <div class="grid grid-cols-2 md:grid-cols-3 gap-y-4 gap-x-2 mt-6">
            <ng-container *ngIf="villa.amenities && villa.amenities.length > 0; else defaultAmenities">
              <div *ngFor="let am of villa.amenities" class="flex items-center space-x-2 text-[14px] text-slate-700 font-bold">
                <span class="material-symbols-outlined text-[#EA580C]">done</span>
                <span>{{ am }}</span>
              </div>
            </ng-container>
            <ng-template #defaultAmenities>
              <div class="flex items-center space-x-2 text-[14px] text-slate-700 font-bold"><span class="material-symbols-outlined text-[#EA580C]">done</span><span>Bể bơi riêng</span></div>
              <div class="flex items-center space-x-2 text-[14px] text-slate-700 font-bold"><span class="material-symbols-outlined text-[#EA580C]">done</span><span>Phòng Karaoke VIP</span></div>
              <div class="flex items-center space-x-2 text-[14px] text-slate-700 font-bold"><span class="material-symbols-outlined text-[#EA580C]">done</span><span>Bếp nướng BBQ ngoài trời</span></div>
              <div class="flex items-center space-x-2 text-[14px] text-slate-700 font-bold"><span class="material-symbols-outlined text-[#EA580C]">done</span><span>Dọn phòng hàng ngày</span></div>
              <div class="flex items-center space-x-2 text-[14px] text-slate-700 font-bold"><span class="material-symbols-outlined text-[#EA580C]">done</span><span>Xe điện đưa đón</span></div>
              <div class="flex items-center space-x-2 text-[14px] text-slate-700 font-bold"><span class="material-symbols-outlined text-[#EA580C]">done</span><span>Wifi tốc độ cao</span></div>
            </ng-template>
          </div>
        </div>
      </div>

      <!-- Right Column: Sticky Booking Widget -->
      <div class="lg:w-1/3">
        <div class="sticky top-28 bg-white rounded-2xl shadow-2xl border-t-4 border-[#0369A1] p-6 lg:p-8">
          <div class="text-[12px] text-slate-400 font-black uppercase tracking-widest mb-1">Giá thuê nguyên căn từ</div>
          <div class="flex items-end space-x-1 mb-6">
            <span class="text-3xl font-black text-[#EA580C] leading-none">{{ villa.basePrice ? (villa.basePrice | number) : 'Liên hệ' }}đ</span>
            <span class="text-[13px] text-slate-500 font-bold mb-1">/ đêm</span>
          </div>

          <form class="space-y-4">
            <div>
              <label class="block text-[11px] font-black text-[#0369A1] uppercase tracking-widest mb-2">Ngày Nhận / Trả Phòng</label>
              <div class="grid grid-cols-2 gap-2">
                <input type="date" class="w-full bg-slate-50 border border-slate-200 rounded-lg px-3 py-2.5 text-sm font-bold text-slate-700 focus:outline-none focus:ring-2 focus:ring-[#0369A1]">
                <input type="date" class="w-full bg-slate-50 border border-slate-200 rounded-lg px-3 py-2.5 text-sm font-bold text-slate-700 focus:outline-none focus:ring-2 focus:ring-[#0369A1]">
              </div>
            </div>

            <div>
              <label class="block text-[11px] font-black text-[#0369A1] uppercase tracking-widest mb-2">Số lượng khách</label>
              <select class="w-full bg-slate-50 border border-slate-200 rounded-lg px-3 py-3 text-sm font-bold text-slate-700 focus:outline-none focus:ring-2 focus:ring-[#0369A1]">
                <option>Tiêu chuẩn ({{ villa.capacity || 4 }} người)</option>
                <option>Tối đa (Cần phụ thu)</option>
              </select>
            </div>

            <button type="button" class="w-full mt-6 bg-gradient-to-r from-[#EA580C] to-[#f97316] hover:from-[#f97316] hover:to-[#fb923c] text-white py-4 rounded-full text-[13px] font-black uppercase tracking-widest transition-all shadow-lg shadow-orange-500/30">
              YÊU CẦU ĐẶT PHÒNG
            </button>
            
            <p class="text-[11px] text-center text-slate-400 font-medium mt-4">
              Bạn sẽ không bị trừ tiền ngay. Chúng tôi sẽ liên hệ để xác nhận tình trạng phòng.
            </p>
          </form>
        </div>
      </div>
      
    </div>
  </div>
</div>
```
