import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { DashboardStatsResponse } from '../models/admin-dashboard.model';

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

@Injectable({
  providedIn: 'root',
})
export class AdminDashboardService {
  private readonly API_URL = 'http://localhost:8080/api/v1/admin/dashboard';

  constructor(private http: HttpClient) {}

  getDashboardStats(resortId = 'all', period = '7d'): Observable<DashboardStatsResponse> {
    const params = new HttpParams()
      .set('resortId', resortId)
      .set('period', period);

    return this.http.get<ApiResponse<DashboardStatsResponse>>(`${this.API_URL}/stats`, { params }).pipe(
      map((res) => res.data),
      catchError((error) => {
        console.warn('Backend API unavailable, returning empty executive stats:', error);
        return of(this.getMockDashboardStats());
      })
    );
  }

  private getMockDashboardStats(): any {
    return {
      kpis: {
        occupancyRate: 0,
        occupiedVillas: 0,
        totalVillas: 0,
        revenueWeekVnd: 0,
        revenueWeekFormatted: '0 đ',
        revenueVsLastWeekPct: 0,
        vipArrivalsToday: 0,
        vipBreakdown: 'Chưa có khách VIP',
        csatScore: 0,
        totalReviewsWeek: 0,
        csatSatisfactionPct: 0,
      },
      weeklyRevenueChart: [],
      fieldButlerDispatches: [],
      moduleStatuses: [
        {
          moduleCode: 'BOOKINGS',
          title: 'Quản Lý Đặt Phòng',
          description: 'Điều phối đặt phòng nghỉ dưỡng, theo dõi nhận phòng, trả phòng thời gian thực.',
          metricValue: '0 Đơn',
          metricLabel: 'Hệ thống sẵn sàng tiếp nhận',
          statusBadge: 'Trống',
          badgeClass: 'bg-slate-100 text-slate-700 border-slate-300',
          iconName: 'book_online',
          actionRoute: '/admin/bookings',
        },
        {
          moduleCode: 'VILLAS',
          title: 'Sơ Đồ Biệt Thự & Phòng',
          description: 'Bản đồ biệt thự biển, kiểm soát buồng phòng, dọn phòng và bảo trì tức thời.',
          metricValue: '0 Căn',
          metricLabel: 'Sẵn sàng khởi tạo biệt thự',
          statusBadge: 'Khởi tạo',
          badgeClass: 'bg-sky-50 text-sky-700 border-sky-300',
          iconName: 'holiday_village',
          actionRoute: '/admin/villas',
        },
        {
          moduleCode: 'VIP_CRM',
          title: 'Khách Hàng VIP & CRM',
          description: 'Hồ sơ khách hàng Diamond, sở thích ẩm thực, nhiệt độ phòng & lịch sử lưu trú.',
          metricValue: '0 Khách',
          metricLabel: 'Chưa có hồ sơ VIP',
          statusBadge: 'Chờ dữ liệu',
          badgeClass: 'bg-slate-100 text-slate-700 border-slate-300',
          iconName: 'diamond',
          actionRoute: '/admin/dashboard',
        },
        {
          moduleCode: 'BUTLERS',
          title: 'Quản Gia Riêng 24/7',
          description: 'Điều phối quản gia phục vụ từng yêu cầu thượng lưu.',
          metricValue: '0 Nhiệm vụ',
          metricLabel: 'Sẵn sàng điều phối',
          statusBadge: 'Sẵn sàng',
          badgeClass: 'bg-emerald-50 text-emerald-700 border-emerald-300',
          iconName: 'room_service',
          actionRoute: '/admin/staff',
        },
        {
          moduleCode: 'MICHELIN',
          title: 'Dịch Vụ Nghỉ Dưỡng',
          description: 'Quản lý các dịch vụ trải nghiệm ẩm thực, du thuyền, spa cao cấp.',
          metricValue: '0 Dịch vụ',
          metricLabel: 'Chưa kích hoạt',
          statusBadge: 'Sẵn sàng',
          badgeClass: 'bg-slate-100 text-slate-700 border-slate-300',
          iconName: 'restaurant',
          actionRoute: '/admin/services',
        },
        {
          moduleCode: 'ANALYTICS',
          title: 'Báo Cáo & Phân Tích AI',
          description: 'Mô hình dự báo nhu cầu đặt phòng, tối ưu biểu giá theo mùa và phân khúc khách.',
          metricValue: '0%',
          metricLabel: 'Đang theo dõi dữ liệu mới',
          statusBadge: 'AI Ready',
          badgeClass: 'bg-blue-50 text-blue-700 border-blue-300',
          iconName: 'monitoring',
          actionRoute: '/admin/dashboard',
        },
      ],
      vipArrivalDepartureLogs: [],
      showcaseResorts: [],
    };
  }
}
