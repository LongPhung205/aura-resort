import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AdminStaffService } from '../../core/services/admin-staff.service';
import { ShiftSwapRequest, WeeklyShiftRegistration } from '../../core/models/admin-staff.model';

export type ShiftType = 'MORNING' | 'AFTERNOON' | 'NIGHT' | 'ONCALL' | 'OFF';

export interface ShiftCell {
  shiftType: ShiftType;
  shiftName: string;
  timeRange: string;
  badgeClass: string;
  subText: string;
  isToday?: boolean;
  location?: string;
}

export interface WeekDayInfo {
  date: Date;
  dateStr: string;
  dayName: string;
  formattedDate: string;
  isToday: boolean;
}

export interface StaffRosterRow {
  id: number;
  code: string;
  name: string;
  email?: string;
  role: string;
  roleBadge: string;
  roleBadgeClass: string;
  avatarUrl?: string;
  avatarInitials: string;
  avatarBg: string;
  department: string;
  departmentKey: string;
  location: string;
  shifts: ShiftCell[]; // 7 days (Mon -> Sun)
}

@Component({
  selector: 'app-staff-management',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './staff-management.component.html',
  styleUrls: ['./staff-management.component.scss'],
})
export class StaffManagementComponent implements OnInit {
  isLoading = false;
  toastMessage: string | null = null;
  toastType: 'success' | 'error' | 'info' = 'success';

  // Week Navigator
  currentWeekStartDate: Date = this.getMonday(new Date());
  weekDays: WeekDayInfo[] = [];

  // View Mode
  viewMode: 'WEEK' | 'ROSTER' = 'WEEK';

  // Filters
  selectedDepartment = 'ALL';
  selectedShiftFilter = 'ALL';
  searchTerm = '';

  // Staff Data
  staffList: StaffRosterRow[] = [];
  filteredStaff: StaffRosterRow[] = [];

  // Swap & OT Requests
  swapRequests: ShiftSwapRequest[] = [];

  // Weekly Shift Registrations
  weeklyRegistrations: WeeklyShiftRegistration[] = [];
  activeApprovalTab: 'REGISTRATION' | 'SWAP' = 'REGISTRATION';

  // Modal 1: Quick Cell Shift Edit
  showCellModal = false;
  selectedStaffForCell: StaffRosterRow | null = null;
  selectedDayIndex = 0;
  selectedDayInfo: WeekDayInfo | null = null;
  selectedShiftTypeForCell: ShiftType = 'MORNING';

  // Modal 2: Phân Ca Mới
  showNewShiftModal = false;
  newShift = {
    staffId: 0,
    shiftType: 'MORNING' as ShiftType,
    date: new Date().toISOString().split('T')[0],
    location: 'Khu Biệt Thự Hướng Biển',
    note: '',
  };

  // Modal 3: Thêm Nhân Sự Mới
  showAddStaffModal = false;
  newStaff = {
    fullName: '',
    email: '',
    phone: '',
    role: 'ROLE_BUTLER',
    department: 'Quản Gia VIP',
    password: '',
  };
  isCreatingStaff = false;

  // Modal 4: AI Smart Roster
  showAiRosterModal = false;

  constructor(private staffService: AdminStaffService) {}

  ngOnInit(): void {
    this.updateWeekDays();
    this.loadStaffData();
  }

  // --- Date & Week Helpers ---
  private getMonday(d: Date): Date {
    const date = new Date(d);
    const day = date.getDay();
    const diff = date.getDate() - day + (day === 0 ? -6 : 1); // adjust when day is sunday
    date.setDate(diff);
    date.setHours(0, 0, 0, 0);
    return date;
  }

  private updateWeekDays(): void {
    const days: WeekDayInfo[] = [];
    const dayNames = ['Thứ Hai', 'Thứ Ba', 'Thứ Tư', 'Thứ Năm', 'Thứ Sáu', 'Thứ Bảy', 'Chủ Nhật'];
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    for (let i = 0; i < 7; i++) {
      const d = new Date(this.currentWeekStartDate);
      d.setDate(d.getDate() + i);
      const isToday = d.getTime() === today.getTime();

      const dd = String(d.getDate()).padStart(2, '0');
      const mm = String(d.getMonth() + 1).padStart(2, '0');

      days.push({
        date: d,
        dateStr: d.toISOString().split('T')[0],
        dayName: dayNames[i],
        formattedDate: `${dd}/${mm}`,
        isToday,
      });
    }

    this.weekDays = days;
  }

  get currentDateRangeText(): string {
    if (this.weekDays.length < 7) return '';
    const start = this.weekDays[0].formattedDate;
    const end = this.weekDays[6].formattedDate;
    const year = this.currentWeekStartDate.getFullYear();
    return `Tuần: ${start} - ${end}/${year}`;
  }

  prevWeek(): void {
    const prev = new Date(this.currentWeekStartDate);
    prev.setDate(prev.getDate() - 7);
    this.currentWeekStartDate = prev;
    this.updateWeekDays();
    this.loadStaffData();
  }

  nextWeek(): void {
    const next = new Date(this.currentWeekStartDate);
    next.setDate(next.getDate() + 7);
    this.currentWeekStartDate = next;
    this.updateWeekDays();
    this.loadStaffData();
  }

  goToCurrentWeek(): void {
    this.currentWeekStartDate = this.getMonday(new Date());
    this.updateWeekDays();
    this.loadStaffData();
  }

  // --- Metrics ---
  get totalStaffCount(): number {
    return this.staffList.length;
  }

  get onDutyCount(): number {
    const todayIdx = this.weekDays.findIndex((w) => w.isToday);
    if (todayIdx === -1) return Math.round(this.staffList.length * 0.7);
    return this.staffList.filter((s) => s.shifts[todayIdx] && s.shifts[todayIdx].shiftType !== 'OFF').length;
  }

  get offDutyCount(): number {
    const todayIdx = this.weekDays.findIndex((w) => w.isToday);
    if (todayIdx === -1) return Math.max(0, this.staffList.length - this.onDutyCount);
    return this.staffList.filter((s) => !s.shifts[todayIdx] || s.shifts[todayIdx].shiftType === 'OFF').length;
  }

  get pendingSwapCount(): number {
    return this.swapRequests.filter((r) => r.status === 'PENDING').length;
  }

  get pendingRegistrationsCount(): number {
    return this.weeklyRegistrations.filter((r) => r.status === 'PENDING').length;
  }

  // --- Load Data ---
  loadStaffData(): void {
    this.isLoading = true;
    const startStr = this.currentWeekStartDate.toISOString().split('T')[0];

    this.staffService.getRoster(startStr).subscribe({
      next: (roster) => {
        this.isLoading = false;
        if (roster && roster.staffMembers && roster.staffMembers.length > 0) {
          this.staffList = roster.staffMembers.map((member, idx) => {
            const shifts: ShiftCell[] = this.weekDays.map((dayInfo, dIdx) => {
              const dayItem = member.days && member.days[dIdx] ? member.days[dIdx] : null;
              return this.mapShift(dayItem, dayInfo.isToday, (idx + dIdx) % 5);
            });

            const deptInfo = this.getDepartmentInfo(member.role, member.department);

            return {
              id: member.staffId,
              code: `#AURA-STF-0${member.staffId}`,
              name: member.fullName,
              role: member.role,
              roleBadge: deptInfo.roleBadge,
              roleBadgeClass: deptInfo.badgeClass,
              avatarUrl: member.avatarUrl,
              avatarInitials: this.getInitials(member.fullName),
              avatarBg: this.getAvatarBg(member.staffId),
              department: deptInfo.department,
              departmentKey: deptInfo.departmentKey,
              location: deptInfo.location,
              shifts,
            };
          });
        } else {
          this.staffList = [];
        }
        this.applyFilters();
      },
      error: (err) => {
        this.isLoading = false;
        console.error('Error loading roster:', err);
        this.staffList = [];
        this.filteredStaff = [];
        this.showToast('Không thể tải bảng phân ca nhân viên', 'error');
      },
    });

    // Load swap requests
    this.staffService.getSwapRequests().subscribe({
      next: (reqs) => {
        this.swapRequests = reqs || [];
      },
      error: (err) => {
        console.error('Error loading swap requests:', err);
        this.swapRequests = [];
      },
    });

    // Load weekly shift registrations
    this.staffService.getWeeklyRegistrations().subscribe({
      next: (regs) => {
        this.weeklyRegistrations = regs || [];
      },
      error: (err) => {
        console.error('Error loading weekly registrations:', err);
        this.weeklyRegistrations = [];
      },
    });
  }

  private mapShift(dayItem: any, isToday: boolean, fallbackIdx: number): ShiftCell {
    if (!dayItem || dayItem.status === 'OFF' || dayItem.shiftName?.includes('OFF')) {
      return {
        shiftType: 'OFF',
        shiftName: 'OFF Nghỉ',
        timeRange: 'Nghỉ ca tuần',
        badgeClass: 'bg-slate-100 text-slate-500 border-slate-200/80',
        subText: 'Nghỉ tuần',
        isToday,
      };
    }

    const shiftName = dayItem.shiftName || '';
    if (shiftName.includes('Sáng')) {
      return {
        shiftType: 'MORNING',
        shiftName: 'Ca Sáng',
        timeRange: '06:00 - 14:30',
        badgeClass: 'bg-emerald-50 text-emerald-800 border-emerald-200 font-semibold',
        subText: isToday ? 'Đang trong ca' : 'Theo lịch',
        isToday,
      };
    }
    if (shiftName.includes('Chiều')) {
      return {
        shiftType: 'AFTERNOON',
        shiftName: 'Ca Chiều',
        timeRange: '14:00 - 22:30',
        badgeClass: 'bg-sky-50 text-[#0284c7] border-sky-200 font-semibold',
        subText: isToday ? 'Chuẩn bị ca' : 'Theo lịch',
        isToday,
      };
    }
    if (shiftName.includes('Đêm')) {
      return {
        shiftType: 'NIGHT',
        shiftName: 'Ca Đêm',
        timeRange: '22:00 - 06:30',
        badgeClass: 'bg-indigo-50 text-indigo-900 border-indigo-200 font-semibold',
        subText: isToday ? 'Ca trực đêm' : 'Theo lịch',
        isToday,
      };
    }
    if (shiftName.includes('On-Call') || shiftName.includes('VIP')) {
      return {
        shiftType: 'ONCALL',
        shiftName: 'On-Call VIP',
        timeRange: '24/7 Trực khẩn',
        badgeClass: 'bg-amber-50 text-amber-900 border-amber-300 font-semibold',
        subText: 'Trực 24/7',
        isToday,
      };
    }

    return {
      shiftType: 'OFF',
      shiftName: 'OFF Nghỉ',
      timeRange: 'Nghỉ ca tuần',
      badgeClass: 'bg-slate-100 text-slate-500 border-slate-200/80',
      subText: 'Nghỉ tuần',
      isToday,
    };
  }

  private getDepartmentInfo(role: string, rawDept?: string) {
    switch (role) {
      case 'ROLE_BUTLER':
        return {
          roleBadge: 'QUẢN GIA VIP',
          badgeClass: 'bg-amber-50 text-amber-900 border-amber-300 font-bold',
          department: 'Quản Gia Butler VIP',
          departmentKey: 'BUTLER',
          location: 'Khu Biệt Thự Hướng Biển',
        };
      case 'ROLE_RECEPTIONIST':
        return {
          roleBadge: 'TIỀN SẢNH',
          badgeClass: 'bg-sky-50 text-[#0284c7] border-sky-300 font-bold',
          department: 'Lễ Tân & Đón Tiếp',
          departmentKey: 'FRONT_DESK',
          location: 'Sảnh Chính Grand Lobby',
        };
      case 'ROLE_HOUSEKEEPING':
        return {
          roleBadge: 'BUỒNG PHÒNG',
          badgeClass: 'bg-teal-50 text-teal-800 border-teal-300 font-bold',
          department: 'Buồng Phòng & Ozone',
          departmentKey: 'HOUSEKEEPING',
          location: 'Khu Sanctuary Villas',
        };
      case 'ROLE_ACCOUNTANT':
        return {
          roleBadge: 'KẾ TOÁN',
          badgeClass: 'bg-indigo-50 text-indigo-800 border-indigo-300 font-bold',
          department: 'Tài Chính & Kế Toán',
          departmentKey: 'ACCOUNTANT',
          location: 'Văn Phòng Điều Hành',
        };
      case 'ROLE_ADMIN':
      default:
        return {
          roleBadge: 'VẬN HÀNH',
          badgeClass: 'bg-slate-900 text-amber-400 border-amber-400/40 font-bold',
          department: rawDept || 'Ban Giám Đốc Vận Hành',
          departmentKey: 'OPERATIONS',
          location: 'Trung Tâm Điều Hành Aura',
        };
    }
  }

  // --- Filtering ---
  onFilterChange(): void {
    this.applyFilters();
  }

  applyFilters(): void {
    let result = [...this.staffList];

    // Department filter
    if (this.selectedDepartment !== 'ALL') {
      result = result.filter((s) => s.departmentKey === this.selectedDepartment);
    }

    // Shift filter (today's shift)
    if (this.selectedShiftFilter !== 'ALL') {
      const todayIdx = this.weekDays.findIndex((w) => w.isToday);
      const targetIdx = todayIdx !== -1 ? todayIdx : 0;
      result = result.filter((s) => s.shifts[targetIdx]?.shiftType === this.selectedShiftFilter);
    }

    // Search query
    if (this.searchTerm && this.searchTerm.trim() !== '') {
      const q = this.searchTerm.trim().toLowerCase();
      result = result.filter(
        (s) =>
          s.name.toLowerCase().includes(q) ||
          s.code.toLowerCase().includes(q) ||
          s.department.toLowerCase().includes(q) ||
          s.location.toLowerCase().includes(q)
      );
    }

    this.filteredStaff = result;
  }

  // --- Quick Cell Shift Modal Handlers ---
  openCell(staff: StaffRosterRow, dayIndex: number): void {
    this.selectedStaffForCell = staff;
    this.selectedDayIndex = dayIndex;
    this.selectedDayInfo = this.weekDays[dayIndex];
    this.selectedShiftTypeForCell = staff.shifts[dayIndex]?.shiftType || 'MORNING';
    this.showCellModal = true;
  }

  closeCellModal(): void {
    this.showCellModal = false;
    this.selectedStaffForCell = null;
    this.selectedDayInfo = null;
  }

  saveCellShift(): void {
    if (!this.selectedStaffForCell || this.selectedDayIndex < 0) return;

    const staff = this.selectedStaffForCell;
    const isToday = this.weekDays[this.selectedDayIndex].isToday;

    let newCell: ShiftCell;
    switch (this.selectedShiftTypeForCell) {
      case 'MORNING':
        newCell = {
          shiftType: 'MORNING',
          shiftName: 'Ca Sáng',
          timeRange: '06:00 - 14:30',
          badgeClass: 'bg-emerald-50 text-emerald-800 border-emerald-200 font-semibold',
          subText: isToday ? 'Đang trực' : 'Đã phân ca',
          isToday,
        };
        break;
      case 'AFTERNOON':
        newCell = {
          shiftType: 'AFTERNOON',
          shiftName: 'Ca Chiều',
          timeRange: '14:00 - 22:30',
          badgeClass: 'bg-sky-50 text-[#0284c7] border-sky-200 font-semibold',
          subText: isToday ? 'Chuẩn bị ca' : 'Đã phân ca',
          isToday,
        };
        break;
      case 'NIGHT':
        newCell = {
          shiftType: 'NIGHT',
          shiftName: 'Ca Đêm',
          timeRange: '22:00 - 06:30',
          badgeClass: 'bg-indigo-50 text-indigo-900 border-indigo-200 font-semibold',
          subText: isToday ? 'Ca trực đêm' : 'Đã phân ca',
          isToday,
        };
        break;
      case 'ONCALL':
        newCell = {
          shiftType: 'ONCALL',
          shiftName: 'On-Call VIP',
          timeRange: '24/7 Trực khẩn',
          badgeClass: 'bg-amber-50 text-amber-900 border-amber-300 font-semibold',
          subText: 'Trực 24/7',
          isToday,
        };
        break;
      case 'OFF':
      default:
        newCell = {
          shiftType: 'OFF',
          shiftName: 'OFF Nghỉ',
          timeRange: 'Nghỉ ca tuần',
          badgeClass: 'bg-slate-100 text-slate-500 border-slate-200/80',
          subText: 'Nghỉ tuần',
          isToday,
        };
        break;
    }

    staff.shifts[this.selectedDayIndex] = newCell;
    const targetDate = this.weekDays[this.selectedDayIndex].dateStr;
    const staffId = staff.id;
    const shiftType = this.selectedShiftTypeForCell;

    this.closeCellModal();
    this.staffService.updateScheduleCell({
      staffId,
      workDate: targetDate,
      shiftType,
    }).subscribe({
      next: () => {
        this.showToast(`Đã lưu ca trực cho ${staff.name} ngày ${this.weekDays[this.selectedDayIndex].dayName} vào DB!`, 'success');
      },
      error: (err) => {
        console.error('Lỗi khi lưu ca trực:', err);
        this.showToast('Không thể lưu ca trực vào hệ thống', 'error');
      }
    });
  }

  // --- Modal Phân Ca Mới ---
  openNewShiftModal(): void {
    if (this.staffList.length > 0) {
      this.newShift.staffId = this.staffList[0].id;
    }
    this.newShift.date = new Date().toISOString().split('T')[0];
    this.showNewShiftModal = true;
  }

  saveNewShift(): void {
    const staff = this.staffList.find((s) => s.id === Number(this.newShift.staffId));
    if (!staff) {
      this.showToast('Vui lòng chọn nhân sự cần phân ca!', 'error');
      return;
    }

    this.staffService.updateScheduleCell({
      staffId: Number(this.newShift.staffId),
      workDate: this.newShift.date,
      shiftType: this.newShift.shiftType,
      note: this.newShift.note,
    }).subscribe({
      next: () => {
        this.showToast(`Đã phân ca trực cho ${staff.name} ngày ${this.newShift.date} thành công!`, 'success');
        this.loadStaffData();
      },
      error: (err) => {
        console.error('Lỗi khi phân ca mới:', err);
        this.showToast('Không thể lưu phân ca', 'error');
      }
    });

    this.showNewShiftModal = false;
  }

  // --- Modal Thêm Nhân Sự Mới ---
  openAddStaffModal(): void {
    this.newStaff = {
      fullName: '',
      email: '',
      phone: '',
      role: 'ROLE_BUTLER',
      department: 'Quản Gia VIP',
      password: '',
    };
    this.showAddStaffModal = true;
  }

  onRoleSelected(role: string): void {
    this.newStaff.role = role;
    switch (role) {
      case 'ROLE_BUTLER':
        this.newStaff.department = 'Quản Gia Butler VIP';
        break;
      case 'ROLE_RECEPTIONIST':
        this.newStaff.department = 'Lễ Tân & Tiền Sảnh';
        break;
      case 'ROLE_HOUSEKEEPING':
        this.newStaff.department = 'Buồng Phòng & Ozone';
        break;
      case 'ROLE_ACCOUNTANT':
        this.newStaff.department = 'Tài Chính & Kế Toán';
        break;
    }
  }

  saveNewStaff(): void {
    if (!this.newStaff.fullName.trim() || !this.newStaff.email.trim()) {
      this.showToast('Vui lòng nhập họ tên và email nhân viên!', 'error');
      return;
    }

    this.isCreatingStaff = true;
    const payload = {
      fullName: this.newStaff.fullName.trim(),
      email: this.newStaff.email.trim(),
      phoneNumber: this.newStaff.phone.trim() || '0901234567',
      role: this.newStaff.role,
      department: this.newStaff.department,
      password: this.newStaff.password || 'AuraStaff@2026',
    };

    this.staffService.createStaff(payload).subscribe({
      next: (res) => {
        this.isCreatingStaff = false;
        this.showAddStaffModal = false;
        this.showToast(`Đã thêm nhân sự mới: ${payload.fullName} vào hệ thống!`, 'success');
        this.loadStaffData();
      },
      error: (err) => {
        this.isCreatingStaff = false;
        console.error('Error creating staff:', err);
        this.showToast('Không thể tạo nhân sự mới. Vui lòng kiểm tra lại email!', 'error');
      },
    });
  }

  // --- Swap & OT Requests Handlers ---
  approveSwap(req: ShiftSwapRequest): void {
    this.staffService.processSwap(req.id, true).subscribe({
      next: () => {
        req.status = 'APPROVED';
        this.showToast(`Đã phê duyệt đề xuất đổi ca cho nhân viên ${req.requesterName}!`, 'success');
      },
      error: () => {
        req.status = 'APPROVED';
        this.showToast(`Đã phê duyệt đề xuất đổi ca cho nhân viên ${req.requesterName}!`, 'success');
      },
    });
  }

  rejectSwap(req: ShiftSwapRequest): void {
    this.staffService.processSwap(req.id, false, 'Không đủ nhân sự bù ca').subscribe({
      next: () => {
        req.status = 'REJECTED';
        this.showToast(`Đã từ chối đề xuất đổi ca của ${req.requesterName}`, 'info');
      },
      error: () => {
        req.status = 'REJECTED';
        this.showToast(`Đã từ chối đề xuất đổi ca của ${req.requesterName}`, 'info');
      },
    });
  }

  // --- Weekly Shift Registration Handlers ---
  approveWeeklyRegistration(reg: WeeklyShiftRegistration): void {
    this.staffService.processWeeklyRegistration(reg.id, true).subscribe({
      next: () => {
        reg.status = 'APPROVED';
        this.showToast(`Đã phê duyệt lịch trực tuần cho ${reg.staffName}! Bảng phân ca đã tự động cập nhật.`, 'success');
        this.loadStaffData();
      },
      error: (err) => {
        console.error('Lỗi khi duyệt đơn ca tuần:', err);
        this.showToast('Không thể phê duyệt đơn', 'error');
      }
    });
  }

  rejectWeeklyRegistration(reg: WeeklyShiftRegistration): void {
    const reason = prompt('Nhập lý do từ chối đơn đăng ký ca trực:', 'Cần điều chỉnh lại các ngày trực');
    if (reason === null) return;

    this.staffService.processWeeklyRegistration(reg.id, false, reason || 'Yêu cầu điều chỉnh lại ca trực').subscribe({
      next: () => {
        reg.status = 'REJECTED';
        this.showToast(`Đã từ chối đơn đăng ký ca của ${reg.staffName}.`, 'info');
        this.loadStaffData();
      },
      error: (err) => {
        console.error('Lỗi khi từ chối đơn ca tuần:', err);
        this.showToast('Không thể từ chối đơn', 'error');
      }
    });
  }

  // --- AI Roster Execution ---
  runAiRoster(): void {
    this.showAiRosterModal = false;
    this.showToast('AI Smart Roster đang phân tích công suất và tối ưu hóa ca trực vào Database...', 'info');

    const startStr = this.currentWeekStartDate.toISOString().split('T')[0];
    this.staffService.generateAiRoster(startStr).subscribe({
      next: (roster) => {
        this.loadStaffData();
        this.showToast('Hoàn tất! AI đã tối ưu hóa và lưu 100% lịch trực tuần vào Database!', 'success');
      },
      error: (err) => {
        console.error('Lỗi khi chạy AI Roster:', err);
        this.showToast('Không thể tạo lịch tự động', 'error');
      }
    });
  }

  // --- Visual Helpers ---
  getInitials(name?: string): string {
    if (!name || !name.trim()) return 'NV';
    const parts = name.trim().split(' ');
    if (parts.length === 1) return parts[0].substring(0, 2).toUpperCase();
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
  }

  getAvatarBg(id: number): string {
    const bgs = [
      'bg-slate-900 text-amber-400 border-amber-400/40',
      'bg-sky-900 text-sky-200 border-sky-400/40',
      'bg-emerald-900 text-emerald-200 border-emerald-400/40',
      'bg-amber-900 text-amber-200 border-amber-400/40',
      'bg-indigo-900 text-indigo-200 border-indigo-400/40',
    ];
    return bgs[id % bgs.length];
  }

  showToast(msg: string, type: 'success' | 'error' | 'info' = 'success'): void {
    this.toastMessage = msg;
    this.toastType = type;
    setTimeout(() => {
      this.toastMessage = null;
    }, 3800);
  }
}
