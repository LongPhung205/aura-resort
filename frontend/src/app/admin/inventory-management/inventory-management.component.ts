import { Component, OnInit } from '@angular/core';
import { forkJoin } from 'rxjs';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BodyPortalDirective } from '../../shared/directives/body-portal.directive';
import { AdminInventoryService, VillaOption } from '../../core/services/admin-inventory.service';
import { AdminRefillService } from '../../core/services/admin-refill.service';
import {
  InventoryCategory,
  InventoryItem,
  InventoryItemPayload,
  InventorySummary,
  InventoryTransaction,
  InventoryTransactionPayload,
  InventoryTransactionType,
  VillaAsset,
  VillaAssetPayload,
  VillaAssetStatus,
} from '../../core/models/inventory.model';
import {
  CreateRefillTaskPayload,
  RefillItemCheckPayload,
  RefillTask,
  RefillTaskStatus,
  VillaInventoryStock,
  VillaSupplyStandard,
  VillaSupplyStandardPayload,
} from '../../core/models/refill.model';

export interface RefillCheckRow {
  itemId: number;
  itemCode: string;
  itemName: string;
  itemUnit: string;
  itemCategory: string;
  standardQuantity: number;
  actualQuantity: number;
  refillQuantity: number;
  consumedQuantity: number;
  damagedQuantity: number;
  missingQuantity: number;
  warehouseStock: number;
}

@Component({
  selector: 'app-inventory-management',
  standalone: true,
  imports: [CommonModule, FormsModule, BodyPortalDirective],
  templateUrl: './inventory-management.component.html',
  styleUrls: ['./inventory-management.component.scss'],
})
export class InventoryManagementComponent implements OnInit {
  activeTab: 'STOCK' | 'REFILL' | 'STANDARDS' | 'VILLA_STOCK' | 'LOGS' | 'VILLA_ASSETS' = 'STOCK';
  categoryFilter = 'ALL';
  searchQuery = '';
  isLoading = false;

  // Data
  summary: InventorySummary = {
    totalItems: 0,
    lowStockCount: 0,
    totalInventoryValue: 0,
    totalTransactions: 0,
    totalVillaAssets: 0,
  };

  items: InventoryItem[] = [];
  logs: InventoryTransaction[] = [];
  villaAssets: VillaAsset[] = [];
  villasList: VillaOption[] = [];

  // Refill & Standards State
  refillTasks: RefillTask[] = [];
  activeRefillFilter: 'ALL' | RefillTaskStatus = 'ALL';
  isFulfillingTaskId: number | null = null;
  expandedTaskId: number | null = null;

  toggleTaskExpansion(taskId: number): void {
    if (this.expandedTaskId === taskId) {
      this.expandedTaskId = null;
    } else {
      this.expandedTaskId = taskId;
    }
  }


  // Matrix Mode
  isMatrixMode = true;
  allStandardsMatrix: { [itemId: number]: { [villaId: number]: VillaSupplyStandard | undefined } | undefined } = {};
  localStandardsMatrix: { [itemId: number]: { [villaId: number]: number } } = {};
  selectedStandardItems: Set<number> = new Set<number>();
  isSavingStandards = false;
  bulkStandardValue: number | null = null;

  // Modal 1: Thêm/Sửa vật tư
  showItemModal = false;
  isEditMode = false;
  selectedItem: Partial<InventoryItem> = {};
  isSavingItem = false;

  // Modal 2: Tạo phiếu Nhập/Xuất kho
  showTransactionModal = false;
  transactionForm: Partial<InventoryTransactionPayload> = {
    type: 'IMPORT',
    quantity: 1,
    reason: '',
    performer: '',
  };
  selectedTxItem: InventoryItem | null = null;
  isSavingTransaction = false;

  // Modal 3: Đăng ký / Sửa Tài sản Villa
  showAssetModal = false;
  isEditAssetMode = false;
  selectedAsset: Partial<VillaAsset> = {};
  isSavingAsset = false;

  // Modal 4: Tạo Phiếu Kiểm Kê & Bổ Sung Villa (Refill)
  showRefillModal = false;
  refillFormVillaId: number | null = null;
  refillFormStaff = 'Nhân viên buồng phòng';
  refillFormNote = 'Kiểm tra buồng phòng sau checkout';
  refillCheckItems: RefillCheckRow[] = [];
  isSavingRefill = false;

  // Toast
  toastMessage: string | null = null;
  toastType: 'success' | 'error' = 'success';

  constructor(
    private inventoryService: AdminInventoryService,
    private refillService: AdminRefillService
  ) {}

  ngOnInit(): void {
    this.refreshAll();
    this.loadVillas();
  }

  refreshAll(): void {
    this.loadSummary();
    this.loadItems();
    this.loadTransactions();
    this.loadVillaAssets();
    this.loadRefillTasks();
    if (this.isMatrixMode) {
      this.loadAllStandards();
    }
  }

  loadSummary(): void {
    this.inventoryService.getSummary().subscribe({
      next: (data) => {
        if (data) this.summary = data;
      },
      error: (err) => console.error('Lỗi tải tổng quan kho:', err),
    });
  }

  loadItems(): void {
    this.isLoading = true;
    this.inventoryService.getItems(this.categoryFilter, this.searchQuery).subscribe({
      next: (data) => {
        this.items = data || [];
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Lỗi tải danh sách vật tư:', err);
        this.isLoading = false;
      },
    });
  }

  loadTransactions(): void {
    this.inventoryService.getTransactions().subscribe({
      next: (data) => {
        this.logs = data || [];
      },
      error: (err) => console.error('Lỗi tải nhật ký kho:', err),
    });
  }

  loadVillaAssets(): void {
    this.inventoryService.getVillaAssets().subscribe({
      next: (data) => {
        this.villaAssets = data || [];
      },
      error: (err) => console.error('Lỗi tải danh mục tài sản:', err),
    });
  }

  loadVillas(): void {
    this.inventoryService.getVillasList().subscribe({
      next: (data) => {
        this.villasList = data || [];
      },
      error: (err) => console.error('Lỗi tải danh sách villa:', err),
    });
  }

  // --- REFILL WORKFLOW ---
  loadRefillTasks(): void {
    const status = this.activeRefillFilter === 'ALL' ? undefined : this.activeRefillFilter;
    this.refillService.getTasks(status).subscribe({
      next: (data) => {
        this.refillTasks = data || [];
      },
      error: (err) => console.error('Lỗi tải danh sách refill:', err),
    });
  }

  setRefillFilter(filter: 'ALL' | RefillTaskStatus): void {
    this.activeRefillFilter = filter;
    this.loadRefillTasks();
  }

  fulfillRefillTask(task: RefillTask): void {
    this.isFulfillingTaskId = task.id;
    this.refillService.fulfillRefillTask(task.id).subscribe({
      next: (updated) => {
        this.isFulfillingTaskId = null;
        this.showToast(
          `Đã xuất kho và hoàn tất bổ sung vật tư cho ${updated.villaNumber}!`,
          'success'
        );
        this.refreshAll();
      },
      error: (err) => {
        this.isFulfillingTaskId = null;
        this.showToast(err?.error?.message || 'Lỗi khi xuất kho bổ sung vật tư!', 'error');
      },
    });
  }

  openCreateRefillModal(): void {
    if (this.villasList.length === 0) {
      this.showToast('Chưa có Villa nào trong hệ thống!', 'error');
      return;
    }
    this.refillFormVillaId = this.villasList[0].id;
    const savedUser = localStorage.getItem('user') || localStorage.getItem('currentUser');
    if (savedUser) {
      try {
        const u = JSON.parse(savedUser);
        this.refillFormStaff = u.fullName || 'Nhân viên buồng phòng';
      } catch {
        this.refillFormStaff = 'Nhân viên buồng phòng';
      }
    } else {
      this.refillFormStaff = 'Nhân viên buồng phòng';
    }
    this.refillFormNote = 'Kiểm tra buồng phòng sau checkout';
    this.onRefillVillaSelected(this.refillFormVillaId);
    this.showRefillModal = true;
  }

  onRefillVillaSelected(villaId: number): void {
    this.refillFormVillaId = villaId;
    this.refillService.getStandards(villaId).subscribe({
      next: (standards) => {
        if (!standards || standards.length === 0) {
          // Tự động khởi tạo định mức mẫu nếu villa chưa có định mức
          this.refillService.applyDefaultStandards(villaId).subscribe({
            next: () => this.onRefillVillaSelected(villaId),
            error: () => (this.refillCheckItems = []),
          });
          return;
        }

        this.refillCheckItems = standards.map((std) => {
          const item = this.items.find((i) => i.id === std.itemId);
          const inStock = item?.inStock || 0;
          const stdQty = std.standardQuantity;
          // Mặc định sau checkout: giả lập hao hụt khoảng 30-50%
          const actualQty = Math.max(0, Math.floor(stdQty * 0.4));
          const refillQty = Math.max(0, stdQty - actualQty);

          return {
            itemId: std.itemId,
            itemCode: std.itemCode,
            itemName: std.itemName,
            itemUnit: std.itemUnit,
            itemCategory: std.itemCategory,
            standardQuantity: stdQty,
            actualQuantity: actualQty,
            refillQuantity: refillQty,
            consumedQuantity: refillQty,
            damagedQuantity: 0,
            missingQuantity: 0,
            warehouseStock: inStock,
          };
        });
      },
      error: () => {
        this.refillCheckItems = [];
      },
    });
  }

  onActualQtyChanged(row: RefillCheckRow): void {
    const act = Number(row.actualQuantity) || 0;
    row.actualQuantity = act;
    row.refillQuantity = Math.max(0, row.standardQuantity - act);
    row.consumedQuantity = row.refillQuantity;
  }

  saveRefillTask(): void {
    if (!this.refillFormVillaId) {
      this.showToast('Vui lòng chọn Villa kiểm kê!', 'error');
      return;
    }
    if (this.refillCheckItems.length === 0) {
      this.showToast('Villa chưa có danh mục định mức vật tư!', 'error');
      return;
    }

    this.isSavingRefill = true;
    const itemsPayload: RefillItemCheckPayload[] = this.refillCheckItems.map((r) => ({
      itemId: r.itemId,
      actualQuantity: r.actualQuantity,
      consumedQuantity: r.consumedQuantity,
      damagedQuantity: r.damagedQuantity,
      missingQuantity: r.missingQuantity,
    }));

    const payload: CreateRefillTaskPayload = {
      villaId: this.refillFormVillaId,
      assignedStaff: this.refillFormStaff.trim(),
      note: this.refillFormNote.trim(),
      items: itemsPayload,
    };

    this.refillService.createRefillTask(payload).subscribe({
      next: (res) => {
        this.isSavingRefill = false;
        this.showRefillModal = false;
        this.showToast(
          `Đã tạo nhiệm vụ Refill ${res.taskCode} thành công! Cần cấp bù ${res.totalRefillUnits} món.`,
          'success'
        );
        this.activeTab = 'REFILL';
        this.loadRefillTasks();
        this.refreshAll();
      },
      error: (err) => {
        this.isSavingRefill = false;
        this.showToast(err?.error?.message || 'Lỗi khi tạo nhiệm vụ Refill!', 'error');
      },
    });
  }

  // --- STANDARDS WORKFLOW ---
  loadAllStandards(): void {
    this.refillService.getStandards().subscribe({
      next: (data) => {
        this.allStandardsMatrix = {};
        if (data) {
          for (const std of data) {
            if (!this.allStandardsMatrix[std.itemId]) {
              this.allStandardsMatrix[std.itemId] = {};
            }
            this.allStandardsMatrix[std.itemId]![std.villaId] = std;
          }
        }
      },
      error: (err) => console.error('Lỗi tải toàn bộ định mức:', err),
    });
  }

  getStandardQty(itemId: number, villaId: number): number {
    if (this.localStandardsMatrix[itemId] && this.localStandardsMatrix[itemId][villaId] !== undefined) {
      return this.localStandardsMatrix[itemId][villaId];
    }
    return this.allStandardsMatrix[itemId]?.[villaId]?.standardQuantity || 0;
  }

  updateStandardQty(itemId: number, villaId: number, qtyStr: string): void {
    const qty = parseInt(qtyStr, 10);
    if (isNaN(qty) || qty < 0) return;
    if (!this.localStandardsMatrix[itemId]) {
      this.localStandardsMatrix[itemId] = {};
    }
    this.localStandardsMatrix[itemId][villaId] = qty;
  }

  toggleStandardItemSelection(itemId: number): void {
    if (this.selectedStandardItems.has(itemId)) {
      this.selectedStandardItems.delete(itemId);
    } else {
      this.selectedStandardItems.add(itemId);
    }
  }

  toggleAllStandardItems(event: Event): void {
    const isChecked = (event.target as HTMLInputElement).checked;
    if (isChecked) {
      this.items.forEach(item => this.selectedStandardItems.add(item.id));
    } else {
      this.selectedStandardItems.clear();
    }
  }

  isAllStandardItemsSelected(): boolean {
    return this.items.length > 0 && this.selectedStandardItems.size === this.items.length;
  }

  applyCapacityToSelected(): void {
    if (this.selectedStandardItems.size === 0) {
      this.showToast('Vui lòng chọn ít nhất 1 vật tư để áp dụng!', 'error');
      return;
    }
    for (const itemId of this.selectedStandardItems) {
      if (!this.localStandardsMatrix[itemId]) {
        this.localStandardsMatrix[itemId] = {};
      }
      for (const villa of this.villasList) {
        this.localStandardsMatrix[itemId][villa.id] = villa.capacity || 4;
      }
    }
    this.showToast('Đã áp dụng định mức theo sức chứa (Chưa lưu). Vui lòng nhấn Lưu để xác nhận.');
  }

  applyBulkValueToSelected(): void {
    if (this.selectedStandardItems.size === 0) {
      this.showToast('Vui lòng chọn ít nhất 1 vật tư để áp dụng!', 'error');
      return;
    }
    if (this.bulkStandardValue === null || this.bulkStandardValue < 0) {
      this.showToast('Vui lòng nhập số lượng hợp lệ để áp dụng hàng loạt!', 'error');
      return;
    }

    const qty = this.bulkStandardValue;
    for (const itemId of this.selectedStandardItems) {
      if (!this.localStandardsMatrix[itemId]) {
        this.localStandardsMatrix[itemId] = {};
      }
      for (const villa of this.villasList) {
        this.localStandardsMatrix[itemId][villa.id] = qty;
      }
    }
    this.showToast(`Đã áp dụng định mức ${qty} cho các vật tư được chọn (Chưa lưu).`);
  }

  saveAllStandards(): void {
    const payloads: VillaSupplyStandardPayload[] = [];
    
    // Thu thập các thay đổi từ localStandardsMatrix
    for (const itemIdStr of Object.keys(this.localStandardsMatrix)) {
      const itemId = Number(itemIdStr);
      for (const villaIdStr of Object.keys(this.localStandardsMatrix[itemId])) {
        const villaId = Number(villaIdStr);
        const localQty = this.localStandardsMatrix[itemId][villaId];
        const existingQty = this.allStandardsMatrix[itemId]?.[villaId]?.standardQuantity || 0;
        
        if (localQty !== existingQty) {
          payloads.push({
            villaId,
            itemId,
            standardQuantity: localQty,
            note: this.allStandardsMatrix[itemId]?.[villaId]?.note || 'Cập nhật hàng loạt',
          });
        }
      }
    }

    if (payloads.length === 0) {
      this.showToast('Không có thay đổi nào để lưu!');
      return;
    }

    this.isSavingStandards = true;
    const saveRequests = payloads.map(p => this.refillService.saveStandard(p));
    
    forkJoin(saveRequests).subscribe({
      next: () => {
        this.showToast(`Đã lưu thành công ${payloads.length} thay đổi!`);
        this.localStandardsMatrix = {}; // clear local edits
        this.selectedStandardItems.clear(); // clear selection
        this.isSavingStandards = false;
        this.loadAllStandards(); // reload data
      },
      error: (err) => {
        console.error(err);
        this.showToast('Lỗi khi lưu định mức!', 'error');
        this.isSavingStandards = false;
      }
    });
  }


  onFilterChange(): void {
    this.loadItems();
  }

  get filteredItems(): InventoryItem[] {
    return this.items;
  }

  // --- MODAL 1: THÊM / SỬA VẬT TƯ ---
  openAddItemModal(): void {
    this.isEditMode = false;
    this.selectedItem = {
      code: '',
      name: '',
      category: 'AMENITY',
      unit: 'Bộ',
      inStock: 0,
      minThreshold: 10,
      unitPrice: 0,
      supplier: '',
      location: 'Kho Tổng A1',
      description: '',
    };
    this.showItemModal = true;
  }

  openEditItemModal(item: InventoryItem): void {
    this.isEditMode = true;
    this.selectedItem = { ...item };
    this.showItemModal = true;
  }

  openTransactionFromItemModal(type: InventoryTransactionType): void {
    const item =
      this.items.find((i) => i.id === this.selectedItem.id) ||
      ({ ...this.selectedItem } as InventoryItem);
    this.showItemModal = false;
    this.openCreateTransactionModal(type, item);
  }

  saveItem(): void {
    if (!this.selectedItem.name || !this.selectedItem.name.trim()) {
      this.showToast('Vui lòng nhập tên vật tư!', 'error');
      return;
    }
    if (!this.selectedItem.category) {
      this.showToast('Vui lòng chọn phân loại vật tư!', 'error');
      return;
    }

    this.isSavingItem = true;
    const payload: InventoryItemPayload = {
      code: this.selectedItem.code ? this.selectedItem.code.trim() : undefined,
      name: this.selectedItem.name.trim(),
      category: this.selectedItem.category as InventoryCategory,
      unit: this.selectedItem.unit || 'Chiếc',
      inStock: this.isEditMode ? undefined : (Number(this.selectedItem.inStock) || 0),
      minThreshold: Number(this.selectedItem.minThreshold) || 10,
      unitPrice: Number(this.selectedItem.unitPrice) || 0,
      supplier: this.selectedItem.supplier?.trim(),
      location: this.selectedItem.location?.trim() || 'Kho Tổng A1',
      description: this.selectedItem.description?.trim(),
    };

    if (this.isEditMode && this.selectedItem.id) {
      this.inventoryService.updateItem(this.selectedItem.id, payload).subscribe({
        next: () => {
          this.isSavingItem = false;
          this.showItemModal = false;
          this.showToast('Đã cập nhật thông tin vật tư thành công!');
          this.loadItems();
          this.loadSummary();
        },
        error: (err) => {
          this.isSavingItem = false;
          this.showToast(err?.error?.message || 'Lỗi khi cập nhật vật tư!', 'error');
        },
      });
    } else {
      this.inventoryService.createItem(payload).subscribe({
        next: () => {
          this.isSavingItem = false;
          this.showItemModal = false;
          this.showToast('Đã khởi tạo vật tư mới vào hệ thống kho!');
          this.loadItems();
          this.loadSummary();
        },
        error: (err) => {
          this.isSavingItem = false;
          this.showToast(err?.error?.message || 'Lỗi khi tạo vật tư mới!', 'error');
        },
      });
    }
  }

  deleteItem(item: InventoryItem, event: Event): void {
    event.stopPropagation();
    if (
      !confirm(`Bạn có chắc chắn muốn xóa vật tư "${item.name}" (${item.code}) khỏi hệ thống kho?`)
    ) {
      return;
    }

    this.inventoryService.deleteItem(item.id).subscribe({
      next: () => {
        this.showToast(`Đã xóa vật tư ${item.code} thành công!`);
        this.loadItems();
        this.loadSummary();
      },
      error: (err) => {
        this.showToast(err?.error?.message || 'Không thể xóa vật tư này!', 'error');
      },
    });
  }

  // --- MODAL 2: TẠO PHIẾU NHẬP / XUẤT KHO ---
  openCreateTransactionModal(
    type: InventoryTransactionType = 'IMPORT',
    defaultItem?: InventoryItem
  ): void {
    this.selectedTxItem = defaultItem || (this.items.length > 0 ? this.items[0] : null);
    this.transactionForm = {
      type: type,
      itemId: this.selectedTxItem ? this.selectedTxItem.id : undefined,
      quantity: 1,
      unitPrice: this.selectedTxItem ? this.selectedTxItem.unitPrice : 0,
      reason: type === 'IMPORT' ? 'Nhập bổ sung kho định kỳ' : 'Cấp phát phục vụ Villa',
      performer: 'Ban Quản Lý Kho',
      destinationVillaId: undefined,
    };
    this.showTransactionModal = true;
  }

  onTxItemSelect(itemIdStr: string): void {
    const id = Number(itemIdStr);
    const found = this.items.find((i) => i.id === id);
    if (found) {
      this.selectedTxItem = found;
      this.transactionForm.itemId = found.id;
      this.transactionForm.unitPrice = found.unitPrice;
    }
  }

  saveTransaction(): void {
    if (!this.transactionForm.itemId) {
      this.showToast('Vui lòng chọn vật tư giao dịch!', 'error');
      return;
    }
    const qty = Number(this.transactionForm.quantity);
    if (!qty || qty <= 0) {
      this.showToast('Số lượng giao dịch phải lớn hơn 0!', 'error');
      return;
    }

    if (this.transactionForm.type === 'EXPORT' && this.selectedTxItem) {
      if (qty > this.selectedTxItem.inStock) {
        this.showToast(
          `Số lượng xuất (${qty}) vượt quá tồn kho hiện có (${this.selectedTxItem.inStock})!`,
          'error'
        );
        return;
      }
    }

    this.isSavingTransaction = true;
    const payload: InventoryTransactionPayload = {
      itemId: this.transactionForm.itemId,
      type: this.transactionForm.type as InventoryTransactionType,
      quantity: qty,
      unitPrice: Number(this.transactionForm.unitPrice) || 0,
      reason: this.transactionForm.reason?.trim() || '',
      performer: this.transactionForm.performer?.trim() || 'Ban Quản Lý Kho',
      destinationVillaId: this.transactionForm.destinationVillaId
        ? Number(this.transactionForm.destinationVillaId)
        : undefined,
    };

    this.inventoryService.recordTransaction(payload).subscribe({
      next: () => {
        this.isSavingTransaction = false;
        this.showTransactionModal = false;
        const actionText =
          payload.type === 'IMPORT'
            ? 'nhập kho'
            : payload.type === 'EXPORT'
              ? 'xuất kho'
              : 'điều chỉnh';
        this.showToast(`Đã tạo phiếu ${actionText} thành công!`);
        this.refreshAll();
      },
      error: (err) => {
        this.isSavingTransaction = false;
        this.showToast(err?.error?.message || 'Lỗi khi xử lý giao dịch kho!', 'error');
      },
    });
  }

  // --- MODAL 3: ĐĂNG KÝ / SỬA TÀI SẢN VILLA ---
  openAddAssetModal(): void {
    this.isEditAssetMode = false;
    this.selectedAsset = {
      assetName: '',
      serialNumber: '',
      category: 'Điện tử',
      status: 'GOOD',
      installDate: new Date().toISOString().split('T')[0],
      note: '',
    };
    this.showAssetModal = true;
  }

  openEditAssetModal(asset: VillaAsset): void {
    this.isEditAssetMode = true;
    this.selectedAsset = { ...asset };
    this.showAssetModal = true;
  }



  saveAsset(): void {
    if (!this.selectedAsset.assetName || !this.selectedAsset.assetName.trim()) {
      this.showToast('Vui lòng nhập tên tài sản/thiết bị!', 'error');
      return;
    }

    this.isSavingAsset = true;
    const payload: VillaAssetPayload = {
      assetName: this.selectedAsset.assetName.trim(),
      serialNumber: this.selectedAsset.serialNumber?.trim(),
      category: this.selectedAsset.category || 'Trang thiết bị',
      status: (this.selectedAsset.status as VillaAssetStatus) || 'GOOD',
      installDate: this.selectedAsset.installDate,
      warrantyExpiry: this.selectedAsset.warrantyExpiry,
      note: this.selectedAsset.note?.trim(),
    };

    if (this.isEditAssetMode && this.selectedAsset.id) {
      this.inventoryService.updateVillaAsset(this.selectedAsset.id, payload).subscribe({
        next: () => {
          this.isSavingAsset = false;
          this.showAssetModal = false;
          this.showToast('Đã cập nhật tình trạng tài sản thành công!');
          this.loadVillaAssets();
          this.loadSummary();
        },
        error: (err) => {
          this.isSavingAsset = false;
          this.showToast(err?.error?.message || 'Lỗi khi cập nhật tài sản!', 'error');
        },
      });
    } else {
      this.inventoryService.createVillaAsset(payload).subscribe({
        next: () => {
          this.isSavingAsset = false;
          this.showAssetModal = false;
          this.showToast('Đã đăng ký tài sản mới cho villa!');
          this.loadVillaAssets();
          this.loadSummary();
        },
        error: (err) => {
          this.isSavingAsset = false;
          this.showToast(err?.error?.message || 'Lỗi khi tạo tài sản mới!', 'error');
        },
      });
    }
  }

  deleteAsset(asset: VillaAsset, event: Event): void {
    event.stopPropagation();
    if (
      !confirm(`Bạn có chắc chắn muốn xóa tài sản "${asset.assetName}" khỏi danh mục theo dõi?`)
    ) {
      return;
    }

    this.inventoryService.deleteVillaAsset(asset.id).subscribe({
      next: () => {
        this.showToast(`Đã xóa tài sản ${asset.assetName} thành công!`);
        this.loadVillaAssets();
        this.loadSummary();
      },
      error: (err) => {
        this.showToast(err?.error?.message || 'Không thể xóa tài sản này!', 'error');
      },
    });
  }

  showToast(msg: string, type: 'success' | 'error' = 'success'): void {
    this.toastMessage = msg;
    this.toastType = type;
    setTimeout(() => {
      this.toastMessage = null;
    }, 3500);
  }
}
