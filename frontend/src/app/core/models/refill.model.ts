export type RefillTaskStatus = 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';

export interface VillaSupplyStandard {
  id: number;
  villaId: number;
  villaNumber: string;
  itemId: number;
  itemCode: string;
  itemName: string;
  itemUnit: string;
  itemCategory: string;
  itemUnitPrice?: number;
  standardQuantity: number;
  note?: string;
}

export interface VillaSupplyStandardPayload {
  villaId: number;
  itemId: number;
  standardQuantity: number;
  note?: string;
}

export interface VillaInventoryStock {
  id: number;
  villaId: number;
  villaNumber: string;
  itemId: number;
  itemCode: string;
  itemName: string;
  itemUnit: string;
  itemCategory: string;
  standardQuantity: number;
  currentQuantity: number;
  deficitQuantity: number;
  fillPercentage: number;
  lastCheckedAt?: string;
}

export interface RefillTaskItem {
  id: number;
  itemId: number;
  itemCode: string;
  itemName: string;
  itemUnit: string;
  itemCategory: string;
  unitPrice?: number;
  standardQuantity: number;
  actualQuantity: number;
  refillQuantity: number;
  consumedQuantity?: number;
  damagedQuantity?: number;
  missingQuantity?: number;
  isFulfilled: boolean;
  warehouseStock?: number;
}

export interface RefillTask {
  id: number;
  taskCode: string;
  villaId: number;
  villaNumber: string;
  housekeepingTaskId?: number;
  status: RefillTaskStatus;
  statusLabel: string;
  creator: string;
  assignedStaff?: string;
  completedAt?: string;
  note?: string;
  createdAt: string;
  totalItemsCount: number;
  totalRefillUnits: number;
  items: RefillTaskItem[];
}

export interface RefillItemCheckPayload {
  itemId: number;
  actualQuantity: number;
  consumedQuantity?: number;
  damagedQuantity?: number;
  missingQuantity?: number;
}

export interface CreateRefillTaskPayload {
  villaId: number;
  housekeepingTaskId?: number;
  assignedStaff?: string;
  note?: string;
  items: RefillItemCheckPayload[];
}
