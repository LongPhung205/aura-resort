import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-momo-result',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="min-h-screen bg-gradient-to-br from-slate-50 to-sky-50 flex items-center justify-center p-6">
      <div class="bg-white rounded-3xl shadow-2xl max-w-md w-full p-8 text-center border border-slate-100">

        <!-- Success -->
        <ng-container *ngIf="isSuccess">
          <div class="w-20 h-20 bg-emerald-100 rounded-full flex items-center justify-center mx-auto mb-6 animate-bounce shadow-sm">
            <span class="material-symbols-outlined text-5xl text-emerald-600">check_circle</span>
          </div>
          <h1 class="text-2xl font-black text-slate-900 mb-2">Thanh toán thành công!</h1>
          <p class="text-slate-500 text-sm mb-2">Đơn đặt phòng của bạn đã được xác nhận.</p>
          <p class="text-[12px] text-slate-400 font-mono mb-6">{{ orderId }}</p>
          <div class="bg-emerald-50 border border-emerald-200/60 rounded-xl p-4 mb-6 text-left">
            <p class="text-[13px] text-emerald-800 font-semibold flex items-center gap-2">
              <span class="material-symbols-outlined text-[18px] text-emerald-600">mail</span>
              <span>Xác nhận đặt phòng đã gửi về email</span>
            </p>
            <p class="text-[13px] text-emerald-800 font-semibold mt-2 flex items-center gap-2">
              <span class="material-symbols-outlined text-[18px] text-emerald-600">support_agent</span>
              <span>Nhân viên sẽ liên hệ trước ngày nhận phòng</span>
            </p>
          </div>
        </ng-container>

        <!-- Fail / Cancel -->
        <ng-container *ngIf="!isSuccess">
          <div class="w-20 h-20 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-6 shadow-sm">
            <span class="material-symbols-outlined text-5xl text-red-500">cancel</span>
          </div>
          <h1 class="text-2xl font-black text-slate-900 mb-2">Thanh toán chưa hoàn tất</h1>
          <p class="text-slate-500 text-sm mb-4">{{ message || 'Giao dịch bị hủy hoặc chưa hoàn tất qua cổng thanh toán MoMo.' }}</p>

          <!-- Sandbox Simulator Helper for Developers & Testers -->
          <div *ngIf="bookingId" class="mb-6 p-4 bg-pink-50/80 border border-pink-200/80 rounded-2xl text-left shadow-2xs">
            <div class="flex items-center gap-2 text-pink-900 font-bold text-xs mb-1.5">
              <span class="material-symbols-outlined text-[18px] text-pink-600">science</span>
              <span>Chế độ thử nghiệm MoMo Sandbox</span>
            </div>
            <p class="text-[12px] text-pink-800 leading-relaxed mb-3">
              Nếu bạn không sử dụng ứng dụng MoMo Test để quét mã QR, bạn có thể nhấn nút dưới đây để giả lập thanh toán thành công và kiểm tra toàn bộ luồng hệ thống:
            </p>
            <button
              type="button"
              (click)="simulateSandboxPayment()"
              [disabled]="isSimulating"
              class="w-full py-2.5 px-4 bg-gradient-to-r from-pink-600 to-rose-600 hover:from-pink-700 hover:to-rose-700 text-white rounded-xl font-bold text-xs shadow-md transition-all flex items-center justify-center gap-2 cursor-pointer disabled:opacity-50">
              <span *ngIf="isSimulating" class="w-3.5 h-3.5 border-2 border-white border-t-transparent rounded-full animate-spin"></span>
              <span class="material-symbols-outlined text-[16px]">verified</span>
              <span>{{ isSimulating ? 'Đang xử lý...' : 'Xác nhận thanh toán thành công (Sandbox)' }}</span>
            </button>
            <p *ngIf="simulateError" class="text-[11px] text-red-600 font-semibold mt-2 text-center">
              {{ simulateError }}
            </p>
          </div>

          <div *ngIf="!bookingId" class="bg-rose-50 border border-rose-200 rounded-xl p-4 mb-6 text-left">
            <p class="text-[13px] text-rose-800 font-semibold">✕ Đơn đặt phòng chưa hoàn tất thanh toán.</p>
            <p class="text-[12px] text-rose-600 mt-1">Quý khách vui lòng thử lại hoặc chọn hình thức thanh toán khác.</p>
          </div>
        </ng-container>

        <!-- Actions -->
        <div class="flex flex-col gap-3">
          <a routerLink="/bookings"
             class="w-full bg-[#0369A1] hover:bg-[#0284c7] text-white py-3 rounded-xl font-bold text-sm transition-colors text-center shadow-sm">
            Xem đơn đặt phòng của tôi
          </a>
          <a routerLink="/"
             class="w-full border border-slate-200 hover:bg-slate-50 text-slate-700 py-3 rounded-xl font-bold text-sm transition-colors text-center">
            Về trang chủ
          </a>
        </div>

        <!-- MoMo branding -->
        <div class="mt-6 flex items-center justify-center gap-2 text-slate-400 text-[11px]">
          <img src="https://upload.wikimedia.org/wikipedia/vi/f/fe/MoMo_Logo.png" alt="MoMo" class="h-5 object-contain">
          <span>Thanh toán qua Cổng MoMo (Sandbox)</span>
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
  bookingId: number | null = null;
  isSimulating = false;
  simulateError = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.resultCode = params['resultCode'] || '-1';
      this.orderId = params['orderId'] || '';
      this.message = params['message'] || '';
      this.isSuccess = this.resultCode === '0';

      if (this.orderId) {
        const rawId = this.orderId.split('_')[0];
        if (rawId && !isNaN(Number(rawId))) {
          this.bookingId = Number(rawId);
        }
      }
    });
  }

  simulateSandboxPayment(): void {
    if (!this.bookingId || this.isSimulating) return;
    this.isSimulating = true;
    this.simulateError = '';

    /* eslint-disable @typescript-eslint/no-explicit-any */
    this.http.post<any>(`${environment.apiUrl}/payments/sandbox-simulate/${this.bookingId}`, {}).subscribe({
      next: () => {
        this.isSimulating = false;
        this.isSuccess = true;
        this.resultCode = '0';
      },
      error: (err) => {
        this.isSimulating = false;
        this.simulateError = err?.error?.message || 'Không thể xác nhận thanh toán giả lập. Vui lòng kiểm tra lại backend!';
      }
    });
  }
}
