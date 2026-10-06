import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule, Router } from '@angular/router';

@Component({
  selector: 'app-momo-result',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="min-h-screen bg-gradient-to-br from-slate-50 to-sky-50 flex items-center justify-center p-6">
      <div class="bg-white rounded-3xl shadow-2xl max-w-md w-full p-8 text-center">

        <!-- Success -->
        <ng-container *ngIf="isSuccess">
          <div class="w-20 h-20 bg-emerald-100 rounded-full flex items-center justify-center mx-auto mb-6 animate-bounce">
            <span class="material-symbols-outlined text-5xl text-emerald-600">check_circle</span>
          </div>
          <h1 class="text-2xl font-black text-slate-900 mb-2">Thanh toán thành công!</h1>
          <p class="text-slate-500 text-sm mb-2">Đơn đặt phòng của bạn đã được xác nhận.</p>
          <p class="text-[12px] text-slate-400 font-mono mb-6">{{ orderId }}</p>
          <div class="bg-emerald-50 rounded-xl p-4 mb-6 text-left">
            <p class="text-[13px] text-emerald-800 font-semibold">✓ Xác nhận đặt phòng đã gửi về email</p>
            <p class="text-[13px] text-emerald-800 font-semibold mt-1">✓ Nhân viên sẽ liên hệ trước ngày nhận phòng</p>
          </div>
        </ng-container>

        <!-- Fail / Cancel -->
        <ng-container *ngIf="!isSuccess">
          <div class="w-20 h-20 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-6">
            <span class="material-symbols-outlined text-5xl text-red-500">cancel</span>
          </div>
          <h1 class="text-2xl font-black text-slate-900 mb-2">Thanh toán không thành công</h1>
          <p class="text-slate-500 text-sm mb-6">{{ message || 'Giao dịch bị hủy hoặc xảy ra lỗi trong quá trình thanh toán.' }}</p>
          <div class="bg-rose-50 border border-rose-200 rounded-xl p-4 mb-6 text-left">
            <p class="text-[13px] text-rose-800 font-semibold">✕ Đơn đặt phòng chưa hoàn tất thanh toán và không được lên lịch.</p>
            <p class="text-[12px] text-rose-600 mt-1">Phòng đã được giải phóng trên hệ thống. Quý khách vui lòng đặt lại phòng nếu có nhu cầu.</p>
          </div>
        </ng-container>

        <!-- Actions -->
        <div class="flex flex-col gap-3">
          <a routerLink="/bookings"
             class="w-full bg-[#0369A1] hover:bg-[#0284c7] text-white py-3 rounded-xl font-bold text-sm transition-colors">
            Xem đơn đặt phòng của tôi
          </a>
          <a routerLink="/"
             class="w-full border border-slate-200 hover:bg-slate-50 text-slate-700 py-3 rounded-xl font-bold text-sm transition-colors">
            Về trang chủ
          </a>
        </div>

        <!-- MoMo branding -->
        <div class="mt-6 flex items-center justify-center gap-2 text-slate-400 text-[11px]">
          <img src="https://upload.wikimedia.org/wikipedia/vi/f/fe/MoMo_Logo.png" alt="MoMo" class="h-5 object-contain">
          <span>Thanh toán qua Ví MoMo</span>
        </div>
      </div>
    </div>
  `
})
export class MomoResultComponent implements OnInit {
  isSuccess = false;
  resultCode = '-1';
  orderId = '';
  message = '';

  constructor(private route: ActivatedRoute, private router: Router) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.resultCode = params['resultCode'] || '-1';
      this.orderId = params['orderId'] || '';
      this.message = params['message'] || '';
      this.isSuccess = this.resultCode === '0';
    });
  }
}
