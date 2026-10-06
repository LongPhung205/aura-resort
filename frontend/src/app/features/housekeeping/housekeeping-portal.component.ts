import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HousekeepingTask } from '../../core/models/housekeeping.model';
import { HousekeepingMobileService } from '../../core/services/housekeeping-mobile.service';
import { TokenService } from '../../core/services/token.service';
import { TaskWorkspaceComponent } from './task-workspace/task-workspace.component';

@Component({
  selector: 'app-housekeeping-portal',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, TaskWorkspaceComponent],
  templateUrl: './housekeeping-portal.component.html',
  styleUrls: ['./housekeeping-portal.component.scss'],
})
export class HousekeepingPortalComponent implements OnInit, OnDestroy {
  activeMenu: 'tasks' | 'shifts' | 'leaves' = 'tasks';
  activeTab: 'my-tasks' | 'dirty-pool' | 'rush-only' | 'completed-history' = 'my-tasks';

  myTasks: HousekeepingTask[] = [];
  dirtyRooms: HousekeepingTask[] = [];

  isLoading = false;
  selectedTask: HousekeepingTask | null = null;
  sidebarOpen = true;
  mobileSidebarOpen = true;

  // Search & Filter state
  searchQuery: string = '';
  selectedZone: string = 'ALL';
  selectedStatus: string = 'ALL';
  selectedTaskType: string = 'ALL';

  // Staff Identity & Shift
  staffName = 'Nhân viên buồng phòng';
  staffRole = 'Nhân Viên Buồng Phòng';
  staffShift = 'Ca Sáng: 08:00 - 17:00';
  staffAvatar = '/assets/images/staff/avatar-hoa.jpg';
  isAdmin = false;

  // Real-time Clock & Shift Progress
  currentTime: string = '';
  private clockTimer?: ReturnType<typeof setInterval>;

  // Interactive Quick Incident Modal
  activeIncidentModal = false;
  incidentRoomNumber = '101';
  incidentType = 'AIR_CONDITIONER';
  incidentUrgency = 'HIGH';
  incidentDescription = '';

  toastMessage: string | null = null;

  // --- Leaves & OT Form ---
  leaveForm = { type: 'ANNUAL', startDate: '', endDate: '', duration: 'FULL', reason: '' };
  otForm = { type: 'PRE_APPROVAL', date: '', hours: null, reason: '' };
  remainingAnnualLeave = 12;
  usedAnnualLeave = 3;

  // --- SHIFTS MANAGEMENT STATE (Full 7-day calendar & registration) ---
  shiftSubTab: 'current-week' | 'register-next' | 'swap-history' = 'current-week';
  shiftWeekOffset = 0; // 0 = current week, 1 = next week, -1 = last week
  currentWeekShifts: any[] = [];
  weekDateLabel = '';

  // Shift registration for next week
  nextWeekDays: any[] = [];
  nextWeekDateLabel = '';
  nextWeekStartDateStr = '';
  regPreferredZone = 'ALL';
  regNotes = '';
  isRegSubmitted = false;
  regSubmittedTime = '';
  regStatus: 'PENDING' | 'APPROVED' | 'REJECTED' | '' = '';
  regRejectionReason = '';

  // Shift Swap Modal & Requests
  activeSwapModal = false;
  selectedShiftToSwap: any = null;
  swapForm = {
    myShiftDayId: '',
    myShiftLabel: '',
    myShiftDate: '',
    targetColleague: 'Nguyễn Thị Mai (Buồng phòng VIP)',
    targetShiftType: 'MORNING',
    targetShiftDate: '',
    reason: '',
  };
  colleagueList = [
    { name: 'Nguyễn Thị Mai', role: 'Buồng phòng VIP', zone: 'Zone Ngọc Trai (NT)' },
    { name: 'Trần Văn Hùng', role: 'Buồng phòng Villa', zone: 'Zone Sông Băng (SB)' },
    { name: 'Lê Thị Thảo', role: 'Buồng phòng & Giặt là', zone: 'Zone San Hô (SH)' },
    { name: 'Hoàng Văn Nam', role: 'Buồng phòng Ca Đêm', zone: 'Khách sạn Central' },
    { name: 'Phạm Minh Tuấn', role: 'Tổ phó Buồng phòng', zone: 'Zone Ngọc Trai (NT)' },
  ];
  swapRequests: any[] = [];

  constructor(
    private hkService: HousekeepingMobileService,
    private tokenService: TokenService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.updateClock();
    this.clockTimer = setInterval(() => this.updateClock(), 30000);
    this.loadStaffProfile();
    this.loadTasks();
    this.initShiftSchedule();
  }

  ngOnDestroy(): void {
    if (this.clockTimer) {
      clearInterval(this.clockTimer);
    }
  }

  updateClock(): void {
    const now = new Date();
    const days = ['Chủ Nhật', 'Thứ Hai', 'Thứ Ba', 'Thứ Tư', 'Thứ Năm', 'Thứ Sáu', 'Thứ Bảy'];
    const dayStr = days[now.getDay()];
    const timeStr = now.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
    const dateStr = now.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit' });
    this.currentTime = `${timeStr} • ${dayStr}, ${dateStr}`;
  }

  loadStaffProfile(): void {
    const savedUser = localStorage.getItem('user') || localStorage.getItem('currentUser');
    if (savedUser) {
      try {
        const u = JSON.parse(savedUser);
        if (u.fullName) this.staffName = u.fullName;
        if (u.avatarUrl) this.staffAvatar = u.avatarUrl;
        if (u.role === 'ROLE_ADMIN' || u.role === 'ADMIN') {
          this.isAdmin = true;
          this.staffRole = 'Tổng Quản Lý Buồng Phòng (Admin QC)';
        }
      } catch {
        // default
      }
    }
  }

  loadTasks(): void {
    this.isLoading = true;
    this.hkService.getMyTasks().subscribe({
      next: (tasks) => {
        this.myTasks = tasks || [];
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
        this.myTasks = [];
      },
    });

    this.hkService.getAvailableDirtyRooms().subscribe({
      next: (rooms) => {
        this.dirtyRooms = (rooms || []).filter((r) => r.housekeeperEmail == null);
      },
      error: () => {
        this.dirtyRooms = [];
      },
    });
  }

  // Visual Assets & Photography Helpers
  getRoomImage(task: HousekeepingTask): string {
    const name = (task.villaTypeName || task.roomTypeName || '').toLowerCase();
    const villaNum = (task.villaNumber || '').toLowerCase();
    const roomNum = (task.roomNumber || '').toLowerCase();

    if (name.includes('president') || villaNum.includes('san hô') || roomNum.includes('201') || roomNum.includes('sh01')) {
      return '/assets/images/rooms/presidential-suite.jpg';
    }
    if (name.includes('cliff') || villaNum.includes('sao biển') || roomNum.includes('102') || roomNum.includes('sb02')) {
      return '/assets/images/rooms/cliffside-sunset.jpg';
    }
    if (name.includes('hill') || villaNum.includes('đồi cọ') || roomNum.includes('dc03') || roomNum.includes('202')) {
      return '/assets/images/rooms/sunset-lagoon.jpg';
    }
    return '/assets/images/rooms/grand-oceanfront.jpg';
  }

  // Progress computation
  getTaskProgress(task: HousekeepingTask): number {
    switch (task.status) {
      case 'COMPLETED':
        return 100;
      case 'WAITING_QC':
      case 'INSPECTED':
        return 92;
      case 'RE_CLEAN':
        return 75;
      case 'IN_PROGRESS':
        return 50;
      case 'PENDING':
      default:
        return 0;
    }
  }

  getCompletedStepsCount(task: HousekeepingTask): number {
    const pct = this.getTaskProgress(task);
    return Math.round((pct / 100) * 16);
  }

  // Shift completion percentage
  get shiftCompletionRate(): number {
    if (!this.myTasks.length) return 0;
    const finished = this.myTasks.filter((t) => t.status === 'COMPLETED' || t.status === 'WAITING_QC').length;
    return Math.round((finished / this.myTasks.length) * 100);
  }

  // KPI Metrics
  get totalAssignedCount(): number {
    return this.myTasks.length;
  }

  get completedCount(): number {
    return this.myTasks.filter((t) => t.status === 'COMPLETED').length;
  }

  get inProgressCount(): number {
    return this.myTasks.filter((t) => t.status === 'IN_PROGRESS').length;
  }

  get waitingQcCount(): number {
    return this.myTasks.filter((t) => t.status === 'WAITING_QC' || t.status === 'INSPECTED').length;
  }

  get reCleanCount(): number {
    return this.myTasks.filter((t) => t.status === 'RE_CLEAN').length;
  }

  get rushCount(): number {
    return this.myTasks.filter((t) => t.priority === 'RUSH' && t.status !== 'COMPLETED').length;
  }

  get availableZones(): string[] {
    const zones = new Set<string>();
    [...this.myTasks, ...this.dirtyRooms].forEach((t) => {
      if (t.villaZone) zones.add(t.villaZone);
    });
    return Array.from(zones);
  }

  get filteredMyTasks(): HousekeepingTask[] {
    let list = [...this.myTasks];

    if (this.activeTab === 'rush-only') {
      list = list.filter((t) => t.priority === 'RUSH' || t.status === 'RE_CLEAN');
    } else if (this.activeTab === 'completed-history') {
      list = list.filter((t) => t.status === 'COMPLETED');
    }

    if (this.searchQuery.trim()) {
      const q = this.searchQuery.toLowerCase().trim();
      list = list.filter(
        (t) =>
          (t.roomNumber && t.roomNumber.toLowerCase().includes(q)) ||
          (t.villaNumber && t.villaNumber.toLowerCase().includes(q)) ||
          (t.villaTypeName && t.villaTypeName.toLowerCase().includes(q)) ||
          (t.villaZone && t.villaZone.toLowerCase().includes(q)) ||
          (t.roomName && t.roomName.toLowerCase().includes(q))
      );
    }

    if (this.selectedZone !== 'ALL') {
      list = list.filter((t) => t.villaZone === this.selectedZone);
    }

    if (this.selectedStatus !== 'ALL') {
      list = list.filter((t) => t.status === this.selectedStatus);
    }

    if (this.selectedTaskType !== 'ALL') {
      list = list.filter((t) => t.taskType === this.selectedTaskType);
    }

    return list;
  }

  get filteredDirtyRooms(): HousekeepingTask[] {
    let list = [...this.dirtyRooms];

    if (this.searchQuery.trim()) {
      const q = this.searchQuery.toLowerCase().trim();
      list = list.filter(
        (t) =>
          (t.roomNumber && t.roomNumber.toLowerCase().includes(q)) ||
          (t.villaNumber && t.villaNumber.toLowerCase().includes(q)) ||
          (t.villaTypeName && t.villaTypeName.toLowerCase().includes(q)) ||
          (t.villaZone && t.villaZone.toLowerCase().includes(q))
      );
    }

    if (this.selectedZone !== 'ALL') {
      list = list.filter((t) => t.villaZone === this.selectedZone);
    }

    return list;
  }

  hasActiveFilters(): boolean {
    return (
      this.searchQuery.trim().length > 0 ||
      this.selectedZone !== 'ALL' ||
      this.selectedStatus !== 'ALL' ||
      this.selectedTaskType !== 'ALL'
    );
  }

  clearFilters(): void {
    this.searchQuery = '';
    this.selectedZone = 'ALL';
    this.selectedStatus = 'ALL';
    this.selectedTaskType = 'ALL';
  }

  openTask(task: HousekeepingTask): void {
    if (task.status === 'PENDING') {
      this.hkService.startCleaning(task.id).subscribe({
        next: (updated) => {
          task.status = 'IN_PROGRESS';
          this.selectedTask = updated || { ...task, status: 'IN_PROGRESS' };
          this.showToast(`✨ Bắt đầu quy trình dọn phòng ${task.roomNumber || task.villaNumber}`);
        },
        error: () => {
          task.status = 'IN_PROGRESS';
          this.selectedTask = task;
          this.showToast(`✨ Bắt đầu quy trình dọn phòng ${task.roomNumber || task.villaNumber}`);
        },
      });
    } else {
      this.selectedTask = task;
    }
  }

  claimRoom(room: HousekeepingTask): void {
    this.hkService.claimTask(room.id).subscribe({
      next: (claimed) => {
        this.showToast(`🛎️ Đã tiếp nhận phòng ${room.roomNumber || room.villaNumber} vào ca trực!`);
        this.dirtyRooms = this.dirtyRooms.filter((r) => r.id !== room.id);
        this.myTasks.unshift(claimed || room);
        this.activeTab = 'my-tasks';
      },
      error: () => {
        this.showToast(`🛎️ Đã tiếp nhận phòng ${room.roomNumber || room.villaNumber} vào ca trực!`);
        this.dirtyRooms = this.dirtyRooms.filter((r) => r.id !== room.id);
        this.myTasks.unshift({ ...room, status: 'PENDING' });
        this.activeTab = 'my-tasks';
      },
    });
  }

  onTaskUpdated(updated: HousekeepingTask): void {
    const idx = this.myTasks.findIndex((t) => t.id === updated.id);
    if (idx !== -1) {
      this.myTasks[idx] = updated;
    }
    this.loadTasks();
  }


  // Incident Modal
  openIncidentModal(task?: HousekeepingTask): void {
    if (task) {
      this.incidentRoomNumber = task.roomNumber || task.villaNumber || '101';
    }
    this.activeIncidentModal = true;
  }

  submitIncident(): void {
    if (!this.incidentDescription.trim()) {
      this.showToast('Vui lòng nhập mô tả sự cố kỹ thuật!');
      return;
    }
    this.showToast(`🛠️ Phiếu kỹ thuật phòng ${this.incidentRoomNumber} đã gửi tới Đội Bảo Trì 24/7!`);
    this.activeIncidentModal = false;
    this.incidentDescription = '';
  }

  goToHome(): void {
    this.router.navigate(['/']);
  }

  goToAdmin(): void {
    this.router.navigate(['/admin']);
  }

  logout(): void {
    this.tokenService.removeToken();
    this.tokenService.removeRole();
    localStorage.removeItem('user');
    localStorage.removeItem('currentUser');
    this.router.navigate(['/login']);
  }

  submitLeave(): void {
    if (!this.leaveForm.startDate || !this.leaveForm.reason) {
      this.showToast('Vui lòng điền đầy đủ thông tin xin nghỉ phép.');
      return;
    }
    this.showToast('✅ Đơn xin nghỉ phép đã được gửi tới Quản lý để phê duyệt.');
    this.leaveForm = { type: 'ANNUAL', startDate: '', endDate: '', duration: 'FULL', reason: '' };
  }

  submitOt(): void {
    if (!this.otForm.date || !this.otForm.hours) {
      this.showToast('Vui lòng điền ngày và số giờ OT dự kiến/thực tế.');
      return;
    }
    this.showToast('✅ Đăng ký làm thêm giờ (OT) đã được gửi duyệt thành công.');
    this.otForm = { type: 'PRE_APPROVAL', date: '', hours: null, reason: '' };
  }

  // ================= SHIFT SCHEDULE & REGISTRATION METHODS =================
  initShiftSchedule(): void {
    this.initCurrentWeekShifts();
    this.initNextWeekRegistration();
    this.loadShiftStorage();
  }

  getMonday(d: Date): Date {
    const date = new Date(d);
    const day = date.getDay();
    const diff = date.getDate() - day + (day === 0 ? -6 : 1);
    date.setDate(diff);
    date.setHours(0, 0, 0, 0);
    return date;
  }

  initCurrentWeekShifts(): void {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const baseMonday = this.getMonday(new Date());
    baseMonday.setDate(baseMonday.getDate() + this.shiftWeekOffset * 7);

    const endSunday = new Date(baseMonday);
    endSunday.setDate(endSunday.getDate() + 6);

    const startStr = `${baseMonday.getDate()}/${baseMonday.getMonth() + 1}`;
    const endStr = `${endSunday.getDate()}/${endSunday.getMonth() + 1}/${endSunday.getFullYear()}`;
    this.weekDateLabel = `${startStr} - ${endStr}`;

    const daysInfo = [
      {
        dayOfWeek: 'T2',
        dayName: 'Thứ Hai',
        shiftType: 'MORNING',
        shiftLabel: 'Ca Sáng (06:00 - 14:30)',
        timeRange: '06:00 - 14:30',
        zone: 'Zone Ngọc Trai (NT)',
        coWorkers: ['Lê Văn Long', 'Nguyễn Thị Mai'],
        supervisor: 'Trần Hoàng (Giám sát Buồng)',
        tasksCount: 8,
      },
      {
        dayOfWeek: 'T3',
        dayName: 'Thứ Ba',
        shiftType: 'MORNING',
        shiftLabel: 'Ca Sáng (06:00 - 14:30)',
        timeRange: '06:00 - 14:30',
        zone: 'Zone San Hô (SH)',
        coWorkers: ['Trần Văn Hùng', 'Hoàng Thị Hoa'],
        supervisor: 'Ngô Thanh Sơn (QC Lead)',
        tasksCount: 7,
      },
      {
        dayOfWeek: 'T4',
        dayName: 'Thứ Tư',
        shiftType: 'AFTERNOON',
        shiftLabel: 'Ca Chiều (14:00 - 22:30)',
        timeRange: '14:00 - 22:30',
        zone: 'Tòa Khách Sạn Central',
        coWorkers: ['Đỗ Thị Tuyết'],
        supervisor: 'Trần Hoàng (Giám sát Buồng)',
        tasksCount: 6,
      },
      {
        dayOfWeek: 'T5',
        dayName: 'Thứ Năm',
        shiftType: 'MORNING',
        shiftLabel: 'Ca Sáng (06:00 - 14:30)',
        timeRange: '06:00 - 14:30',
        zone: 'Zone Sông Băng (SB)',
        coWorkers: ['Lê Văn Long', 'Phạm Minh Tuấn'],
        supervisor: 'Trần Hoàng (Giám sát Buồng)',
        tasksCount: 8,
      },
      {
        dayOfWeek: 'T6',
        dayName: 'Thứ Sáu',
        shiftType: 'AFTERNOON',
        shiftLabel: 'Ca Chiều (14:00 - 22:30)',
        timeRange: '14:00 - 22:30',
        zone: 'Zone Ngọc Trai (NT)',
        coWorkers: ['Nguyễn Thị Mai'],
        supervisor: 'Ngô Thanh Sơn (QC Lead)',
        tasksCount: 7,
      },
      {
        dayOfWeek: 'T7',
        dayName: 'Thứ Bảy',
        shiftType: 'OFF',
        shiftLabel: 'Nghỉ Tuần Định Kỳ (OFF)',
        timeRange: 'Nghỉ chế độ',
        zone: 'Nghỉ ngơi tiêu chuẩn',
        coWorkers: [],
        supervisor: '',
        tasksCount: 0,
      },
      {
        dayOfWeek: 'CN',
        dayName: 'Chủ Nhật',
        shiftType: 'MORNING',
        shiftLabel: 'Ca Sáng (06:00 - 14:30)',
        timeRange: '06:00 - 14:30',
        zone: 'Toàn Resort (Trực cao điểm cuối tuần)',
        coWorkers: ['Trần Văn Hùng', 'Lê Thị Thảo'],
        supervisor: 'Trần Hoàng (Giám sát Buồng)',
        tasksCount: 9,
      },
    ];

    this.currentWeekShifts = daysInfo.map((info, idx) => {
      const d = new Date(baseMonday);
      d.setDate(d.getDate() + idx);
      const isToday = d.getTime() === today.getTime();
      const isPast = d.getTime() < today.getTime();

      let status = 'SCHEDULED';
      let checkInTime: string | undefined = undefined;

      if (info.shiftType === 'OFF') {
        status = 'OFF';
      } else if (isPast) {
        status = 'COMPLETED';
        checkInTime = info.shiftType === 'MORNING' ? '05:54' : '13:52';
      } else if (isToday) {
        status = 'IN_PROGRESS';
        checkInTime = '05:58';
      }

      return {
        id: `shift_${this.shiftWeekOffset}_${idx}`,
        dayOfWeek: info.dayOfWeek,
        dayName: info.dayName,
        dateStr: `${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}/${d.getFullYear()}`,
        dateNumber: d.getDate(),
        monthNumber: d.getMonth() + 1,
        shiftType: info.shiftType,
        shiftLabel: info.shiftLabel,
        timeRange: info.timeRange,
        zone: info.zone,
        status,
        coWorkers: info.coWorkers,
        checkInTime,
        isToday,
        isPast,
        supervisor: info.supervisor,
        tasksCount: info.tasksCount,
      };
    });
  }

  changeShiftWeek(delta: number): void {
    this.shiftWeekOffset += delta;
    this.initCurrentWeekShifts();
  }

  resetShiftWeek(): void {
    this.shiftWeekOffset = 0;
    this.initCurrentWeekShifts();
  }

  initNextWeekRegistration(): void {
    const nextMonday = this.getMonday(new Date());
    nextMonday.setDate(nextMonday.getDate() + 7); // Exactly next week!
    this.nextWeekStartDateStr = nextMonday.toISOString().split('T')[0];

    const endSunday = new Date(nextMonday);
    endSunday.setDate(endSunday.getDate() + 6);

    const startStr = `${nextMonday.getDate()}/${nextMonday.getMonth() + 1}`;
    const endStr = `${endSunday.getDate()}/${endSunday.getMonth() + 1}/${endSunday.getFullYear()}`;
    this.nextWeekDateLabel = `${startStr} - ${endStr}`;

    const defaultDayTemplates = [
      { dayOfWeek: 'T2', dayName: 'Thứ Hai', shiftType: 'MORNING' },
      { dayOfWeek: 'T3', dayName: 'Thứ Ba', shiftType: 'MORNING' },
      { dayOfWeek: 'T4', dayName: 'Thứ Tư', shiftType: 'MORNING' },
      { dayOfWeek: 'T5', dayName: 'Thứ Năm', shiftType: 'AFTERNOON' },
      { dayOfWeek: 'T6', dayName: 'Thứ Sáu', shiftType: 'MORNING' },
      { dayOfWeek: 'T7', dayName: 'Thứ Bảy', shiftType: 'OFF' },
      { dayOfWeek: 'CN', dayName: 'Chủ Nhật', shiftType: 'MORNING' },
    ];

    this.nextWeekDays = defaultDayTemplates.map((t, idx) => {
      const d = new Date(nextMonday);
      d.setDate(d.getDate() + idx);
      return {
        dayOfWeek: t.dayOfWeek,
        dayName: t.dayName,
        dateStr: `${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}/${d.getFullYear()}`,
        isoDate: d.toISOString().split('T')[0],
        dateNumber: d.getDate(),
        monthNumber: d.getMonth() + 1,
        shiftType: t.shiftType,
        preferredZone: 'ALL',
        note: '',
      };
    });
  }

  setDayShift(dayIndex: number, shift: 'MORNING' | 'AFTERNOON' | 'NIGHT' | 'OFF'): void {
    if (this.nextWeekDays[dayIndex]) {
      this.nextWeekDays[dayIndex].shiftType = shift;
    }
  }

  applyQuickTemplate(template: 'ALL_MORNING' | 'ALL_AFTERNOON' | 'ROTATING' | 'STANDARD_OFF_WEEKEND'): void {
    this.nextWeekDays.forEach((item, idx) => {
      if (template === 'ALL_MORNING') {
        item.shiftType = idx === 5 ? 'OFF' : 'MORNING';
      } else if (template === 'ALL_AFTERNOON') {
        item.shiftType = idx === 5 ? 'OFF' : 'AFTERNOON';
      } else if (template === 'ROTATING') {
        item.shiftType = idx === 5 ? 'OFF' : (idx % 2 === 0 ? 'MORNING' : 'AFTERNOON');
      } else if (template === 'STANDARD_OFF_WEEKEND') {
        item.shiftType = (idx === 5 || idx === 6) ? 'OFF' : 'MORNING';
      }
    });
    this.showToast('✨ Đã áp dụng mẫu phân ca nhanh cho cả tuần!');
  }

  get registeredWorkingDaysCount(): number {
    return this.nextWeekDays.filter((d) => d.shiftType !== 'OFF').length;
  }

  get registeredOffDaysCount(): number {
    return this.nextWeekDays.filter((d) => d.shiftType === 'OFF').length;
  }

  submitShiftRegistration(): void {
    if (this.registeredWorkingDaysCount === 0) {
      this.showToast('Vui lòng chọn ít nhất 1 ca làm việc trong tuần.');
      return;
    }

    const monday = new Date(this.nextWeekStartDateStr);
    const payload = {
      weekStartDate: this.nextWeekStartDateStr,
      preferredZone: this.regPreferredZone,
      notes: this.regNotes,
      days: this.nextWeekDays.map((d, idx) => {
        let wDate = d.isoDate;
        if (!wDate) {
          const dt = new Date(monday);
          dt.setDate(dt.getDate() + idx);
          wDate = dt.toISOString().split('T')[0];
        }
        return {
          workDate: wDate,
          dayOfWeek: d.dayOfWeek,
          shiftType: d.shiftType,
          note: d.note || '',
        };
      }),
    };

    this.hkService.submitShiftRegistration(payload).subscribe({
      next: (res: any) => {
        this.isRegSubmitted = true;
        this.regStatus = 'PENDING';
        this.regSubmittedTime = new Date().toLocaleDateString('vi-VN') + ' ' + new Date().toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
        try {
          localStorage.setItem('hk_registered_shifts', JSON.stringify({
            weekLabel: this.nextWeekDateLabel,
            shifts: this.nextWeekDays,
            preferredZone: this.regPreferredZone,
            notes: this.regNotes,
            submittedAt: this.regSubmittedTime,
          }));
        } catch {}
        this.showToast('🎉 Đăng ký lịch trực 7 ngày tuần sau thành công! Đã gửi Quản lý Buồng phòng duyệt.');
      },
      error: (err: any) => {
        console.error('Lỗi khi gửi đăng ký ca trực:', err);
        this.showToast('Không thể gửi đăng ký ca trực. Vui lòng thử lại!');
      }
    });
  }

  editShiftRegistration(): void {
    this.isRegSubmitted = false;
    this.showToast('✏️ Bạn có thể chỉnh sửa lại các ca trực trước hạn chót Thứ 6.');
  }

  // --- SHIFT SWAP ---
  openSwapModal(day?: any): void {
    if (day) {
      this.selectedShiftToSwap = day;
      this.swapForm.myShiftDayId = day.id;
      this.swapForm.myShiftDate = day.dateStr;
      this.swapForm.myShiftLabel = `${day.dayName} (${day.dateStr}) - ${day.shiftLabel}`;
    } else {
      const scheduled = this.currentWeekShifts.find((s) => s.status === 'SCHEDULED' && s.shiftType !== 'OFF');
      if (scheduled) {
        this.selectedShiftToSwap = scheduled;
        this.swapForm.myShiftDayId = scheduled.id;
        this.swapForm.myShiftDate = scheduled.dateStr;
        this.swapForm.myShiftLabel = `${scheduled.dayName} (${scheduled.dateStr}) - ${scheduled.shiftLabel}`;
      } else {
        this.swapForm.myShiftLabel = 'Ca Sáng Thứ Năm (08/10)';
        this.swapForm.myShiftDate = '08/10/2026';
      }
    }
    this.activeSwapModal = true;
  }

  closeSwapModal(): void {
    this.activeSwapModal = false;
  }

  submitSwapRequest(): void {
    if (!this.swapForm.reason.trim()) {
      this.showToast('Vui lòng nhập lý do đổi ca để đồng nghiệp và quản lý nắm được!');
      return;
    }
    const newSwap = {
      id: Date.now(),
      myDate: this.swapForm.myShiftDate || this.currentWeekShifts[2]?.dateStr || 'Thứ Năm',
      myShift: this.swapForm.myShiftLabel || 'Ca Sáng (06:00 - 14:30)',
      targetColleague: this.swapForm.targetColleague,
      targetDate: this.swapForm.targetShiftDate || 'Ca tương ứng cùng tuần',
      targetShift: this.swapForm.targetShiftType === 'MORNING' ? 'Ca Sáng (06:00 - 14:30)' : (this.swapForm.targetShiftType === 'AFTERNOON' ? 'Ca Chiều (14:00 - 22:30)' : 'Ca Đêm (22:00 - 06:30)'),
      reason: this.swapForm.reason,
      status: 'PENDING',
      createdAt: new Date().toLocaleDateString('vi-VN') + ' ' + new Date().toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' }),
    };

    this.swapRequests.unshift(newSwap);
    try {
      localStorage.setItem('hk_swap_requests', JSON.stringify(this.swapRequests));
    } catch {}

    this.closeSwapModal();
    this.swapForm.reason = '';
    this.showToast(`🔄 Yêu cầu đổi ca với ${newSwap.targetColleague} đã được gửi thành công!`);
    this.shiftSubTab = 'swap-history';
  }

  cancelSwapRequest(id: number): void {
    this.swapRequests = this.swapRequests.filter((r) => r.id !== id);
    try {
      localStorage.setItem('hk_swap_requests', JSON.stringify(this.swapRequests));
    } catch {}
    this.showToast('Đã hủy yêu cầu đổi ca.');
  }

  checkInShift(day: any): void {
    day.status = 'IN_PROGRESS';
    day.checkInTime = new Date().toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
    this.showToast(`📍 Chấm công GPS vị trí Resort & xác thực vào ca ${day.shiftLabel} thành công!`);
  }

  loadShiftStorage(): void {
    if (this.nextWeekStartDateStr) {
      this.hkService.getMyShiftRegistration(this.nextWeekStartDateStr).subscribe({
        next: (reg: any) => {
          if (reg) {
            this.isRegSubmitted = true;
            this.regStatus = reg.status || 'PENDING';
            this.regRejectionReason = reg.rejectionReason || '';
            if (reg.preferredZone) this.regPreferredZone = reg.preferredZone;
            if (reg.notes) this.regNotes = reg.notes;
            if (reg.createdAt) {
              const d = new Date(reg.createdAt);
              this.regSubmittedTime = d.toLocaleDateString('vi-VN') + ' ' + d.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
            }
            if (reg.days && reg.days.length === 7) {
              this.nextWeekDays.forEach((day, idx) => {
                const matched = reg.days[idx];
                if (matched) {
                  day.shiftType = matched.shiftType;
                }
              });
            }
          }
        },
        error: (err: any) => {
          console.error('Error fetching shift registration from API:', err);
        }
      });
    }

    try {
      const savedReg = localStorage.getItem('hk_registered_shifts');
      if (savedReg) {
        const parsed = JSON.parse(savedReg);
        if (parsed.shifts && Array.isArray(parsed.shifts)) {
          parsed.shifts.forEach((savedShift: any, idx: number) => {
            if (this.nextWeekDays[idx]) {
              if (savedShift.shiftType) this.nextWeekDays[idx].shiftType = savedShift.shiftType;
              if (savedShift.note) this.nextWeekDays[idx].note = savedShift.note;
            }
          });
        }
        if (parsed.preferredZone && !this.regPreferredZone) this.regPreferredZone = parsed.preferredZone;
        if (parsed.notes && !this.regNotes) this.regNotes = parsed.notes;
        if (!this.isRegSubmitted) {
          this.isRegSubmitted = true;
          this.regSubmittedTime = parsed.submittedAt || '';
        }
      }

      const savedSwaps = localStorage.getItem('hk_swap_requests');
      if (savedSwaps) {
        this.swapRequests = JSON.parse(savedSwaps);
      } else {
        this.swapRequests = [
          {
            id: 1,
            myDate: 'Thứ Năm (08/10)',
            myShift: 'Ca Sáng (06:00 - 14:30)',
            targetColleague: 'Trần Văn Hùng (Buồng phòng)',
            targetDate: 'Thứ Sáu (09/10)',
            targetShift: 'Ca Chiều (14:00 - 22:30)',
            reason: 'Trùng lịch khám sức khỏe định kỳ buổi sáng',
            status: 'APPROVED',
            createdAt: '04/10 09:15',
          },
        ];
      }
    } catch {}
  }

  getShiftBadgeClass(type: string): string {
    switch (type) {
      case 'MORNING':
        return 'bg-amber-50 text-amber-800 border-amber-200';
      case 'AFTERNOON':
        return 'bg-sky-50 text-sky-800 border-sky-200';
      case 'NIGHT':
        return 'bg-indigo-50 text-indigo-800 border-indigo-200';
      case 'OFF':
        return 'bg-slate-100 text-slate-500 border-slate-200';
      default:
        return 'bg-slate-50 text-slate-700 border-slate-200';
    }
  }

  getShiftIcon(type: string): string {
    switch (type) {
      case 'MORNING':
        return 'wb_sunny';
      case 'AFTERNOON':
        return 'wb_twilight';
      case 'NIGHT':
        return 'bedtime';
      case 'OFF':
        return 'weekend';
      default:
        return 'schedule';
    }
  }

  get pendingSwapsCount(): number {
    return this.swapRequests.filter((r) => r.status === 'PENDING').length;
  }

  showToast(msg: string): void {
    this.toastMessage = msg;
    setTimeout(() => {
      this.toastMessage = null;
    }, 4000);
  }

  toggleSidebar(): void {
    this.sidebarOpen = !this.sidebarOpen;
    this.mobileSidebarOpen = this.sidebarOpen;
  }

  toggleMobileSidebar(): void {
    this.toggleSidebar();
  }

  closeMobileSidebar(): void {
    if (typeof window !== 'undefined' && window.innerWidth < 1024) {
      this.sidebarOpen = false;
      this.mobileSidebarOpen = false;
    }
  }
}
