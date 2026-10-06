import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminHousekeepingService } from '../../core/services/admin-housekeeping.service';
import { AdminRefillService } from '../../core/services/admin-refill.service';
import {
  HousekeeperSummary,
  AssignHousekeepingTaskRequest,
} from '../../core/models/admin-housekeeping.model';
import {
  HousekeepingTask,
  LostAndFoundItem,
  LostAndFoundStatus,
  MaintenanceTicket,
  RoomConsumptionRecord,
} from '../../core/models/housekeeping.model';

@Component({
  selector: 'app-housekeeping-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './housekeeping-management.component.html',
  styleUrls: ['./housekeeping-management.component.scss'],
})
export class HousekeepingManagementComponent implements OnInit {
  activeTab: 'matrix' | 'staff' | 'lost-found' | 'maintenance' = 'matrix';

  // Task list
  tasks: HousekeepingTask[] = [];
  housekeepers: HousekeeperSummary[] = [];
  isLoading = false;

  // Filters
  selectedZone = 'ALL';
  selectedStatus = 'ALL';
  searchQuery = '';

  // QC Inspection Modal
  showQcModal = false;
  inspectingTask: HousekeepingTask | null = null;
  inspectingConsumptions: RoomConsumptionRecord[] = [];
  reCleanReason = '';
  isProcessingQc = false;

  // Assign Modal
  showAssignModal = false;
  taskToAssign: HousekeepingTask | null = null;
  selectedHousekeeperId: number | null = null;
  assignPriority: 'NORMAL' | 'RUSH' = 'NORMAL';

  // Lost & Found
  lostFoundList: LostAndFoundItem[] = [];
  lostFoundStatusFilter = 'ALL';

  // Maintenance
  maintenanceTickets: MaintenanceTicket[] = [];

  // Toast
  toastMessage: string | null = null;
  toastType: 'success' | 'error' = 'success';

  constructor(
    private hkService: AdminHousekeepingService,
    private refillService: AdminRefillService
  ) {}

  ngOnInit(): void {
    this.loadTasks();
    this.loadHousekeepers();
    this.loadLostAndFound();
    this.loadMaintenance();
  }

  loadTasks(): void {
    this.isLoading = true;
    this.hkService.getTasks().subscribe({
      next: (tasks) => {
        this.tasks = tasks;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  loadHousekeepers(): void {
    this.hkService.getHousekeepers().subscribe({
      next: (staff) => (this.housekeepers = staff),
    });
  }

  loadLostAndFound(): void {
    this.hkService.getLostAndFoundList().subscribe({
      next: (list) => (this.lostFoundList = list),
    });
  }

  loadMaintenance(): void {
    this.hkService.getMaintenanceTickets().subscribe({
      next: (list) => (this.maintenanceTickets = list),
    });
  }

  // Filtered tasks for Room Matrix
  get filteredTasks(): HousekeepingTask[] {
    return this.tasks.filter((t) => {
      // Zone filter
      if (this.selectedZone !== 'ALL' && t.villaZone !== this.selectedZone) {
        return false;
      }
      // Status filter
      if (this.selectedStatus !== 'ALL') {
        if (this.selectedStatus === 'DIRTY' && t.status !== 'PENDING') return false;
        if (
          this.selectedStatus === 'CLEANING' &&
          t.status !== 'IN_PROGRESS' &&
          t.status !== 'OZONE_RUNNING'
        )
          return false;
        if (
          this.selectedStatus === 'WAITING_QC' &&
          t.status !== 'WAITING_QC' &&
          t.status !== 'INSPECTED'
        )
          return false;
        if (this.selectedStatus === 'CLEAN_READY' && t.status !== 'COMPLETED') return false;
        if (this.selectedStatus === 'RE_CLEAN' && t.status !== 'RE_CLEAN') return false;
      }
      // Search
      if (this.searchQuery.trim()) {
        const q = this.searchQuery.toLowerCase();
        const rNum = (t.roomNumber || '').toLowerCase();
        const vNum = (t.villaNumber || '').toLowerCase();
        const staff = (t.housekeeperName || '').toLowerCase();
        if (!rNum.includes(q) && !vNum.includes(q) && !staff.includes(q)) {
          return false;
        }
      }
      return true;
    });
  }

  // Stat counters
  get dirtyCount(): number {
    return this.tasks.filter((t) => t.status === 'PENDING').length;
  }

  get cleaningCount(): number {
    return this.tasks.filter((t) => t.status === 'IN_PROGRESS' || t.status === 'OZONE_RUNNING')
      .length;
  }

  get waitingQcCount(): number {
    return this.tasks.filter((t) => t.status === 'WAITING_QC' || t.status === 'INSPECTED').length;
  }

  get cleanReadyCount(): number {
    return this.tasks.filter((t) => t.status === 'COMPLETED').length;
  }

  get reCleanCount(): number {
    return this.tasks.filter((t) => t.status === 'RE_CLEAN').length;
  }

  // Open QC Modal
  openQcModal(task: HousekeepingTask): void {
    this.inspectingTask = task;
    this.reCleanReason = '';
    this.showQcModal = true;
    this.inspectingConsumptions = [];

    // Load consumptions for this task
    this.hkService.getPendingConsumptions(task.bookingId || 0).subscribe({
      next: (consumptions) => {
        this.inspectingConsumptions = consumptions.filter((c) => c.housekeepingTaskId === task.id);
      },
    });
  }

  approveQc(): void {
    if (!this.inspectingTask) return;
    this.isProcessingQc = true;

    this.hkService.approveTask(this.inspectingTask.id).subscribe({
      next: () => {
        this.isProcessingQc = false;
        this.showQcModal = false;
        this.showToast(
          `Nghiệm thu thành công phòng ${this.inspectingTask?.roomNumber || this.inspectingTask?.villaNumber}! Phòng đã sẵn sàng đón khách.`,
          'success'
        );
        this.loadTasks();
      },
      error: () => {
        this.isProcessingQc = false;
        this.showToast('Lỗi khi duyệt nghiệm thu phòng', 'error');
      },
    });
  }

  rejectQc(): void {
    if (!this.inspectingTask) return;
    if (!this.reCleanReason.trim()) {
      this.showToast('Vui lòng nhập lý do yêu cầu nhân viên dọn lại!', 'error');
      return;
    }

    this.isProcessingQc = true;
    this.hkService.rejectTask(this.inspectingTask.id, this.reCleanReason.trim()).subscribe({
      next: () => {
        this.isProcessingQc = false;
        this.showQcModal = false;
        this.showToast(
          `Đã yêu cầu dọn lại phòng ${this.inspectingTask?.roomNumber || this.inspectingTask?.villaNumber}.`,
          'success'
        );
        this.loadTasks();
      },
      error: () => {
        this.isProcessingQc = false;
        this.showToast('Lỗi khi từ chối nghiệm thu', 'error');
      },
    });
  }

  // Assign Task
  openAssignModal(task: HousekeepingTask): void {
    this.taskToAssign = task;
    this.selectedHousekeeperId = task.housekeeperId || null;
    this.assignPriority = (task.priority as 'NORMAL' | 'RUSH') || 'NORMAL';
    this.showAssignModal = true;
  }

  submitAssign(): void {
    if (!this.taskToAssign || !this.selectedHousekeeperId) {
      this.showToast('Vui lòng chọn nhân viên dọn phòng!', 'error');
      return;
    }

    const payload: AssignHousekeepingTaskRequest = {
      taskId: this.taskToAssign.id,
      roomId: this.taskToAssign.roomId,
      villaId: this.taskToAssign.villaId,
      housekeeperId: this.selectedHousekeeperId,
      taskType: this.taskToAssign.taskType,
      notes: this.assignPriority === 'RUSH' ? 'ƯU TIÊN DỌN GẤP - KHÁCH SẮP ĐẾN' : undefined,
    };

    this.hkService.assignTask(payload).subscribe({
      next: () => {
        this.showAssignModal = false;
        this.showToast('Phân công dọn phòng thành công!');
        this.loadTasks();
      },
      error: () => {
        this.showToast('Lỗi khi phân công phòng', 'error');
      },
    });
  }

  // Lost & Found
  updateLostFoundStatus(item: LostAndFoundItem, newStatus: LostAndFoundStatus): void {
    this.hkService.updateLostAndFoundStatus(item.id, newStatus).subscribe({
      next: (updated) => {
        item.status = updated.status;
        this.showToast(`Đã cập nhật trạng thái đồ thất lạc thành ${newStatus}!`);
      },
    });
  }

  // Maintenance
  resolveTicket(ticket: MaintenanceTicket): void {
    this.hkService
      .updateMaintenanceStatus(ticket.id, 'RESOLVED', 'Đã sửa chữa xong', 'Kỹ thuật viên ca trực')
      .subscribe({
        next: (updated) => {
          ticket.status = updated.status;
          this.showToast('Đã đánh dấu sửa chữa hoàn tất!');
        },
      });
  }

  showToast(msg: string, type: 'success' | 'error' = 'success'): void {
    this.toastMessage = msg;
    this.toastType = type;
    setTimeout(() => {
      this.toastMessage = null;
    }, 3000);
  }
}
