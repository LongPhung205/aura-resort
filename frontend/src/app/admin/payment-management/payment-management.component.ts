import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AdminLedgerService } from '../../core/services/admin-ledger.service';
import {
  PaymentDashboardStatsResponse,
  DailyRevenue,
  MonthlyRevenue,
  PaymentMethodStat
} from '../../core/models/admin-ledger.model';

export interface ChartPoint {
  label: string;
  total: number;
  room: number;
  service: number;
  x: number;
  y: number;
}

export interface DonutSlice {
  label: string;
  amount: number;
  percentage: number;
  color: string;
  bgColorClass: string;
  textColorClass: string;
  dashArray: string;
  dashOffset: number;
}

@Component({
  selector: 'app-payment-management',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './payment-management.component.html',
  styleUrls: ['./payment-management.component.scss'],
})
export class PaymentManagementComponent implements OnInit {
  stats: PaymentDashboardStatsResponse | null = null;
  loading = true;

  // Time Range Filter: 30 days vs 12 months
  activeTimeRange: '30_DAYS' | '12_MONTHS' = '30_DAYS';

  // Area / Line Chart geometry (viewBox: 800 x 260)
  readonly svgWidth = 800;
  readonly svgHeight = 240;
  readonly paddingX = 40;
  readonly paddingY = 30;

  chartPoints: ChartPoint[] = [];
  svgLinePath = '';
  svgAreaPath = '';
  maxChartRevenue = 10000000;
  hoveredPoint: ChartPoint | null = null;
  yAxisTicks: { value: number; label: string; y: number }[] = [];

  // Donut chart geometry (radius 70, stroke 22)
  readonly donutRadius = 70;
  readonly donutCircumference = 2 * Math.PI * 70; // ~439.82
  donutSlices: DonutSlice[] = [];
  hoveredDonut: DonutSlice | null = null;

  constructor(private ledgerService: AdminLedgerService) {}

  ngOnInit(): void {
    this.fetchStats();
  }

  fetchStats(): void {
    this.loading = true;
    this.ledgerService.getDashboardStats().subscribe({
      next: (data) => {
        this.stats = data;
        this.renderAllCharts();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error fetching dashboard stats', err);
        this.loading = false;
      },
    });
  }

  setTimeRange(range: '30_DAYS' | '12_MONTHS'): void {
    if (this.activeTimeRange === range) return;
    this.activeTimeRange = range;
    this.hoveredPoint = null;
    this.renderLineChart();
  }

  renderAllCharts(): void {
    if (!this.stats) return;
    this.renderLineChart();
    this.renderDonutChart();
  }

  renderLineChart(): void {
    if (!this.stats) return;

    const rawData: { label: string; total: number; room: number; service: number }[] =
      this.activeTimeRange === '30_DAYS'
        ? (this.stats.lineChartData || []).map((d: DailyRevenue) => ({
            label: d.date,
            total: Number(d.revenue) || 0,
            room: Number(d.roomRevenue) || (Number(d.revenue) * 0.82),
            service: Number(d.serviceRevenue) || (Number(d.revenue) * 0.18),
          }))
        : (this.stats.monthlyChartData || []).map((m: MonthlyRevenue) => ({
            label: m.month,
            total: Number(m.revenue) || 0,
            room: Number(m.roomRevenue) || (Number(m.revenue) * 0.8),
            service: Number(m.serviceRevenue) || (Number(m.revenue) * 0.2),
          }));

    if (!rawData.length) {
      this.chartPoints = [];
      this.svgLinePath = '';
      this.svgAreaPath = '';
      return;
    }

    const maxVal = Math.max(...rawData.map((d) => d.total));
    this.maxChartRevenue = maxVal > 0 ? maxVal * 1.15 : 10000000;

    // Y Axis Ticks (4 ticks)
    this.yAxisTicks = [];
    const tickSteps = 4;
    for (let i = 0; i <= tickSteps; i++) {
      const val = (this.maxChartRevenue / tickSteps) * i;
      const y = this.svgHeight - this.paddingY - (val / this.maxChartRevenue) * (this.svgHeight - 2 * this.paddingY);
      this.yAxisTicks.push({
        value: val,
        label: this.formatShortVND(val),
        y: Math.round(y),
      });
    }

    // X Coordinates calculation
    const usableW = this.svgWidth - 2 * this.paddingX;
    const usableH = this.svgHeight - 2 * this.paddingY;
    const stepX = usableW / Math.max(1, rawData.length - 1);

    this.chartPoints = rawData.map((d, idx) => {
      const x = this.paddingX + idx * stepX;
      const y = this.svgHeight - this.paddingY - (d.total / this.maxChartRevenue) * usableH;
      return {
        label: d.label,
        total: d.total,
        room: d.room,
        service: d.service,
        x: Math.round(x * 10) / 10,
        y: Math.round(y * 10) / 10,
      };
    });

    // Build SVG Smooth Curve Line & Area
    this.svgLinePath = this.buildCurvedPath(this.chartPoints);
    const firstPt = this.chartPoints[0];
    const lastPt = this.chartPoints[this.chartPoints.length - 1];
    const bottomY = this.svgHeight - this.paddingY;
    this.svgAreaPath = `${this.svgLinePath} L ${lastPt.x} ${bottomY} L ${firstPt.x} ${bottomY} Z`;
  }

  private buildCurvedPath(points: ChartPoint[]): string {
    if (points.length === 0) return '';
    if (points.length === 1) return `M ${points[0].x} ${points[0].y}`;

    let path = `M ${points[0].x} ${points[0].y}`;
    for (let i = 0; i < points.length - 1; i++) {
      const p0 = i > 0 ? points[i - 1] : points[i];
      const p1 = points[i];
      const p2 = points[i + 1];
      const p3 = i < points.length - 2 ? points[i + 2] : p2;

      const cp1x = p1.x + (p2.x - p0.x) / 6;
      const cp1y = p1.y + (p2.y - p0.y) / 6;
      const cp2x = p2.x - (p3.x - p1.x) / 6;
      const cp2y = p2.y - (p3.y - p1.y) / 6;

      path += ` C ${cp1x.toFixed(1)} ${cp1y.toFixed(1)}, ${cp2x.toFixed(1)} ${cp2y.toFixed(1)}, ${p2.x.toFixed(1)} ${p2.y.toFixed(1)}`;
    }
    return path;
  }

  renderDonutChart(): void {
    if (!this.stats || !this.stats.methodStats) return;

    const colors = [
      { color: '#0284c7', bgColorClass: 'bg-sky-500', textColorClass: 'text-sky-600' },    // PayOS (QR)
      { color: '#ec4899', bgColorClass: 'bg-pink-500', textColorClass: 'text-pink-600' },  // Ví MoMo
      { color: '#f59e0b', bgColorClass: 'bg-amber-500', textColorClass: 'text-amber-600' }, // Tiền mặt COD
    ];

    const total = this.stats.totalRevenue > 0
      ? this.stats.totalRevenue
      : this.stats.methodStats.reduce((sum, m) => sum + Number(m.actualRevenue || 0), 0);

    let accumulatedOffset = 0;
    this.donutSlices = this.stats.methodStats.map((item: PaymentMethodStat, idx: number) => {
      const amt = Number(item.actualRevenue) || 0;
      const pct = total > 0 ? (amt / total) : (1 / this.stats!.methodStats.length);
      const dashLength = pct * this.donutCircumference;
      const remainingLength = this.donutCircumference - dashLength;

      const slice: DonutSlice = {
        label: item.method,
        amount: amt,
        percentage: Math.round(pct * 1000) / 10,
        color: colors[idx % colors.length].color,
        bgColorClass: colors[idx % colors.length].bgColorClass,
        textColorClass: colors[idx % colors.length].textColorClass,
        dashArray: `${dashLength.toFixed(2)} ${remainingLength.toFixed(2)}`,
        dashOffset: -accumulatedOffset,
      };

      accumulatedOffset += dashLength;
      return slice;
    });
  }

  onPointHover(pt: ChartPoint | null): void {
    this.hoveredPoint = pt;
  }

  onDonutHover(slice: DonutSlice | null): void {
    this.hoveredDonut = slice;
  }

  formatShortVND(val: number): string {
    if (val >= 1000000000) return (val / 1000000000).toFixed(1) + ' Tỷ';
    if (val >= 1000000) return (val / 1000000).toFixed(1) + ' Tr';
    if (val >= 1000) return (val / 1000).toFixed(0) + ' K';
    return val ? val.toLocaleString('vi-VN') : '0';
  }

  formatFullVND(val: number): string {
    if (!val) return '0 ₫';
    return Number(val).toLocaleString('vi-VN') + ' ₫';
  }

  exportCsv(): void {
    if (!this.stats) return;
    const headers = ['Phân Loại', 'Chỉ Số', 'Giá Trị (VNĐ / Đơn vị)'];
    const rows: string[][] = [
      ['Tổng Doanh Thu', 'Gross Revenue', this.stats.totalRevenue.toString()],
      ['Doanh Thu Villa', 'Room Revenue', this.stats.roomRevenue.toString()],
      ['Doanh Thu Dịch Vụ', 'Service Revenue', this.stats.serviceRevenue.toString()],
      ['Dòng Tiền Thực Thu', 'Collected Cash', this.stats.collectedAmount.toString()],
      ['Dòng Tiền Chờ Thu', 'Pending Cash', this.stats.pendingAmount.toString()],
      ['Tiền Đã Hoàn Trả', 'Refunded Cash', this.stats.refundedAmount.toString()],
      ['Tỷ Lệ Thành Công', 'Success Rate', `${this.stats.successRate}%`],
      ['Giá Trị Đơn Trung Bình', 'AOV', this.stats.aov.toString()],
      ['Tăng Trưởng Kỳ Trước', 'Growth Rate', `+${this.stats.growthRate}%`],
      ['Tổng Số Giao Dịch', 'Total Transactions', this.stats.totalTransactions.toString()],
    ];

    if (this.stats.methodStats) {
      this.stats.methodStats.forEach((m) => {
        rows.push([`Cổng: ${m.method}`, `Thực thu: ${m.actualRevenue}`, `Tỷ trọng: ${m.percentage}%`]);
      });
    }

    const csvContent =
      'data:text/csv;charset=utf-8,\uFEFF' +
      [headers.join(','), ...rows.map((e) => e.map((val) => `"${val}"`).join(','))].join('\n');

    const encodedUri = encodeURI(csvContent);
    const link = document.createElement('a');
    link.setAttribute('href', encodedUri);
    link.setAttribute('download', `AURA_Financial_Report_${new Date().toISOString().slice(0, 10)}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }
}
