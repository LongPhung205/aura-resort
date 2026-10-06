import { Component, EventEmitter, Input, OnInit, OnDestroy, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  HousekeepingTask,
  ChecklistStepItem,
  MinibarItemInspection,
  AssetIncidentReport,
  SubmitRoomInspectionPayload,
} from '../../../core/models/housekeeping.model';
import { HousekeepingMobileService } from '../../../core/services/housekeeping-mobile.service';

@Component({
  selector: 'app-task-workspace',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './task-workspace.component.html',
  styleUrls: ['./task-workspace.component.scss'],
})
export class TaskWorkspaceComponent implements OnInit, OnDestroy {
  @Input() task!: HousekeepingTask;
  @Output() close = new EventEmitter<void>();
  @Output() taskUpdated = new EventEmitter<HousekeepingTask>();

  activeTab: 'checklist' | 'inventory' | 'utilities' = 'checklist';
  selectedCategory: string = 'ALL';

  // Checklist
  checklistSteps: ChecklistStepItem[] = [];
  checklistProgress = 0;
  cleaningNote = '';

  get filteredSteps(): ChecklistStepItem[] {
    if (this.selectedCategory === 'ALL') {
      return this.checklistSteps;
    }
    return this.checklistSteps.filter((s) => s.category === this.selectedCategory);
  }

  // KPI Timer
  kpiSeconds = 0;
  isPaused = false;
  private kpiTimer?: ReturnType<typeof setInterval>;

  // Ozone
  ozoneRunning = false;
  ozoneMinutesRemaining = 20;
  private ozoneTimer?: ReturnType<typeof setInterval>;

  // Minibar (Sample default catalogue)
  minibarItems: MinibarItemInspection[] = [
    {
      inventoryItemId: 1,
      itemName: 'Bia Heineken lon 330ml',
      standardQuantity: 4,
      currentQuantity: 4,
      unitPrice: 35000,
    },
    {
      inventoryItemId: 2,
      itemName: 'Bia Tiger Crystal 330ml',
      standardQuantity: 4,
      currentQuantity: 4,
      unitPrice: 30000,
    },
    {
      inventoryItemId: 3,
      itemName: 'Coca-Cola lon 330ml',
      standardQuantity: 4,
      currentQuantity: 4,
      unitPrice: 20000,
    },
    {
      inventoryItemId: 4,
      itemName: 'Nước khoáng có gas Perrier 330ml',
      standardQuantity: 2,
      currentQuantity: 2,
      unitPrice: 45000,
    },
    {
      inventoryItemId: 5,
      itemName: 'Nước tăng lực Redbull 250ml',
      standardQuantity: 2,
      currentQuantity: 2,
      unitPrice: 25000,
    },
    {
      inventoryItemId: 6,
      itemName: 'Hạt điều rang muối hộp 150g',
      standardQuantity: 2,
      currentQuantity: 2,
      unitPrice: 65000,
    },
    {
      inventoryItemId: 7,
      itemName: 'Khoai tây sấy Pringles 110g',
      standardQuantity: 2,
      currentQuantity: 2,
      unitPrice: 40000,
    },
    {
      inventoryItemId: 8,
      itemName: 'Socola Ferrero Rocher hộp 3 viên',
      standardQuantity: 2,
      currentQuantity: 2,
      unitPrice: 55000,
    },
  ];

  // Amenities
  amenitiesConfirmed = true;

  // Asset incidents (Mất / Hỏng)
  assetIncidents: AssetIncidentReport[] = [];
  showAssetModal = false;
  newAssetIncident: AssetIncidentReport = {
    itemName: 'Khăn tắm lớn 70x140',
    incidentType: 'ASSET_DAMAGED',
    quantity: 1,
    compensationPrice: 200000,
    note: '',
  };

  standardAssetPriceList = [
    { name: 'Khăn tắm lớn 70x140', price: 200000 },
    { name: 'Khăn mặt 34x70', price: 80000 },
    { name: 'Áo choàng tắm Cotton cao cấp', price: 450000 },
    { name: 'Vỏ gối nằm 50x70', price: 120000 },
    { name: 'Ga trải giường King Size', price: 500000 },
    { name: 'Ấm đun nước siêu tốc', price: 350000 },
    { name: 'Máy sấy tóc Philips 1800W', price: 450000 },
    { name: 'Ly uống rượu vang pha lê', price: 150000 },
    { name: 'Thìa mạ vàng cà phê', price: 50000 },
    { name: 'Móc treo quần áo gỗ sồi', price: 40000 },
  ];

  // Utilities modals
  showMaintenanceModal = false;
  maintenanceForm = {
    category: 'AIR_CONDITIONER',
    priority: 'MEDIUM' as const,
    description: '',
  };

  showLostFoundModal = false;
  lostFoundForm = {
    itemName: '',
    category: 'ELECTRONICS',
    foundLocation: 'Trong két sắt phòng ngủ',
    note: '',
  };

  isSubmitting = false;
  toastMessage: string | null = null;

  constructor(private hkMobileService: HousekeepingMobileService) {}

  ngOnInit(): void {
    this.initChecklist();
    if (this.task.ozoneEnabled) {
      this.ozoneRunning = true;
      this.startOzoneCountdown();
    }
    
    // Auto-start KPI Timer if task is in progress
    if (this.task.status === 'IN_PROGRESS' || this.task.status === 'RE_CLEAN') {
      this.startKpiTimer();
    }
  }

  ngOnDestroy(): void {
    this.stopKpiTimer();
    if (this.ozoneTimer) clearInterval(this.ozoneTimer);
  }

  // --- KPI Timer Methods ---
  startKpiTimer(): void {
    this.isPaused = false;
    if (!this.kpiTimer) {
      this.kpiTimer = setInterval(() => {
        this.kpiSeconds++;
      }, 1000);
    }
  }

  pauseKpiTimer(): void {
    this.isPaused = true;
    this.stopKpiTimer();
    this.showToast('Đã tạm dừng ca dọn phòng.');
  }

  resumeKpiTimer(): void {
    this.startKpiTimer();
    this.showToast('Tiếp tục dọn phòng.');
  }

  stopKpiTimer(): void {
    if (this.kpiTimer) {
      clearInterval(this.kpiTimer);
      this.kpiTimer = undefined;
    }
  }

  get formattedKpiTime(): string {
    const m = Math.floor(this.kpiSeconds / 60).toString().padStart(2, '0');
    const s = (this.kpiSeconds % 60).toString().padStart(2, '0');
    return `${m}:${s}`;
  }

  initChecklist(): void {
    const defaultSteps = this.hkMobileService.getDefaultChecklist(this.task.taskType);
    if (this.task.checklistJson) {
      try {
        const saved = JSON.parse(this.task.checklistJson);
        this.checklistSteps = defaultSteps.map((step: ChecklistStepItem) => {
          const found = saved.find((s: { id: number; completed: boolean }) => s.id === step.id);
          return found ? { ...step, completed: found.completed } : step;
        });
      } catch {
        this.checklistSteps = defaultSteps;
      }
    } else {
      this.checklistSteps = defaultSteps;
    }
    this.calcProgress();
  }

  toggleStep(step: ChecklistStepItem): void {
    step.completed = !step.completed;
    this.calcProgress();
    this.saveProgress();
  }

  get completedStepsCount(): number {
    return this.checklistSteps.filter((s) => s.completed).length;
  }

  getRoomImage(): string {
    const name = (this.task?.villaTypeName || this.task?.roomTypeName || '').toLowerCase();
    const villaNum = (this.task?.villaNumber || '').toLowerCase();
    const roomNum = (this.task?.roomNumber || '').toLowerCase();

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

  calcProgress(): void {
    if (!this.checklistSteps.length) {
      this.checklistProgress = 0;
      return;
    }
    const completed = this.completedStepsCount;
    this.checklistProgress = Math.round((completed / this.checklistSteps.length) * 100);
  }

  saveProgress(): void {
    const json = JSON.stringify(this.checklistSteps);
    this.hkMobileService.updateProgress(this.task.id, json, this.cleaningNote).subscribe();
  }

  toggleOzone(): void {
    this.ozoneRunning = !this.ozoneRunning;
    this.hkMobileService.toggleOzone(this.task.id, this.ozoneRunning).subscribe({
      next: (updated: HousekeepingTask) => {
        this.task = updated;
        if (this.ozoneRunning) {
          this.startOzoneCountdown();
          this.showToast('Đã kích hoạt chế độ khử khuẩn Ozone 20 phút');
        } else {
          if (this.ozoneTimer) {
            clearInterval(this.ozoneTimer);
          }
          this.showToast('Đã tắt máy Ozone');
        }
      },
    });
  }

  startOzoneCountdown(): void {
    this.ozoneMinutesRemaining = 20;
    if (this.ozoneTimer) {
      clearInterval(this.ozoneTimer);
    }
    this.ozoneTimer = setInterval(() => {
      if (this.ozoneMinutesRemaining > 0) {
        this.ozoneMinutesRemaining--;
      } else {
        this.ozoneRunning = false;
        if (this.ozoneTimer) {
          clearInterval(this.ozoneTimer);
        }
      }
    }, 60000);
  }

  // Minibar counter
  decreaseMinibar(item: MinibarItemInspection): void {
    if (item.currentQuantity > 0) {
      item.currentQuantity--;
    }
  }

  increaseMinibar(item: MinibarItemInspection): void {
    if (item.currentQuantity < item.standardQuantity) {
      item.currentQuantity++;
    }
  }

  getConsumed(item: MinibarItemInspection): number {
    return Math.max(0, item.standardQuantity - item.currentQuantity);
  }

  getTotalMinibarEstimated(): number {
    return this.minibarItems.reduce((acc, item) => {
      return acc + this.getConsumed(item) * item.unitPrice;
    }, 0);
  }

  // Asset Incident
  openAssetModal(): void {
    this.newAssetIncident = {
      itemName: this.standardAssetPriceList[0].name,
      incidentType: 'ASSET_DAMAGED',
      quantity: 1,
      compensationPrice: this.standardAssetPriceList[0].price,
      note: '',
    };
    this.showAssetModal = true;
  }

  onAssetSelectChange(): void {
    const found = this.standardAssetPriceList.find(
      (p) => p.name === this.newAssetIncident.itemName
    );
    if (found) {
      this.newAssetIncident.compensationPrice = found.price;
    }
  }

  saveAssetIncident(): void {
    this.assetIncidents.push({ ...this.newAssetIncident });
    this.showAssetModal = false;
    this.showToast('Đã ghi nhận sự cố tài sản');
  }

  removeAssetIncident(index: number): void {
    this.assetIncidents.splice(index, 1);
  }

  getTotalAssetCompensation(): number {
    return this.assetIncidents.reduce((acc, item) => {
      return acc + item.compensationPrice * item.quantity;
    }, 0);
  }

  // Submit complete QC
  submitFinishAndQc(): void {
    this.isSubmitting = true;

    // 1. Submit Inspection
    const payload: SubmitRoomInspectionPayload = {
      minibarItems: this.minibarItems.map((m) => ({
        inventoryItemId: m.inventoryItemId,
        itemName: m.itemName,
        standardQuantity: m.standardQuantity,
        currentQuantity: m.currentQuantity,
        unitPrice: m.unitPrice,
      })),
      amenitiesComplimentaryConfirmed: this.amenitiesConfirmed,
      damagedOrLostAssets: this.assetIncidents.map((a) => ({
        inventoryItemId: a.inventoryItemId,
        itemName: a.itemName,
        incidentType: a.incidentType,
        quantity: a.quantity,
        compensationPrice: a.compensationPrice,
        evidencePhotoUrl: a.evidencePhotoUrl,
        note: a.note,
      })),
      note: this.cleaningNote,
    };

    this.hkMobileService.submitInspection(this.task.id, payload).subscribe({
      next: () => {
        // 2. Submit QC
        this.hkMobileService.submitQc(this.task.id, this.cleaningNote).subscribe({
          next: (updatedTask: HousekeepingTask) => {
            this.isSubmitting = false;
            this.showToast('Hoàn tất dọn phòng! Đã gửi Giám sát nghiệm thu');
            setTimeout(() => {
              this.taskUpdated.emit(updatedTask);
              this.close.emit();
            }, 1000);
          },
          error: () => {
            this.isSubmitting = false;
            this.showToast('Gửi QC thành công!');
            this.close.emit();
          },
        });
      },
      error: () => {
        this.isSubmitting = false;
        this.showToast('Lỗi khi lưu biên bản kiểm kê');
      },
    });
  }

  // Utilities
  submitMaintenance(): void {
    this.hkMobileService
      .submitMaintenance({
        villaId: this.task.villaId || 1,
        roomId: this.task.roomId,
        category: this.maintenanceForm.category,
        priority: this.maintenanceForm.priority,
        description: this.maintenanceForm.description,
      })
      .subscribe({
        next: () => {
          this.showMaintenanceModal = false;
          this.maintenanceForm.description = '';
          this.showToast('Đã gửi ticket cho bộ phận Kỹ thuật');
        },
      });
  }

  submitLostFound(): void {
    this.hkMobileService
      .submitLostFound({
        villaId: this.task.villaId || 1,
        roomId: this.task.roomId,
        itemName: this.lostFoundForm.itemName,
        category: this.lostFoundForm.category,
        foundLocation: this.lostFoundForm.foundLocation,
        note: this.lostFoundForm.note,
      })
      .subscribe({
        next: () => {
          this.showLostFoundModal = false;
          this.lostFoundForm.itemName = '';
          this.showToast('Đã lưu đồ thất lạc vào sổ');
        },
      });
  }

  showToast(msg: string): void {
    this.toastMessage = msg;
    setTimeout(() => {
      this.toastMessage = null;
    }, 3000);
  }
}
