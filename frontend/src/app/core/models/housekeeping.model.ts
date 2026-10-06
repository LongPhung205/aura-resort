export type TaskType = 'CHECKOUT_DEEP' | 'DAILY' | 'TURNDOWN';
export type TaskStatus =
  | 'PENDING'
  | 'IN_PROGRESS'
  | 'OZONE_RUNNING'
  | 'WAITING_QC'
  | 'INSPECTED'
  | 'RE_CLEAN'
  | 'COMPLETED';

export type TaskPriority = 'NORMAL' | 'RUSH';

export interface HousekeepingTask {
  id: number;
  villaId?: number;
  villaNumber?: string;
  villaTypeName?: string;
  villaZone?: string;
  roomId?: number;
  roomNumber?: string;
  roomName?: string;
  roomTypeName?: string;
  housekeeperId?: number;
  housekeeperEmail?: string;
  housekeeperPhone?: string;
  housekeeperName?: string;
  supervisorName?: string;
  taskType: TaskType;
  status: TaskStatus;
  startedAt?: string;
  ozoneStartedAt?: string;
  ozoneEndedAt?: string;
  checklistJson?: string;
  evidencePhotoUrl?: string;
  completedAt?: string;
  priority?: TaskPriority;
  ozoneEnabled?: boolean;
  reCleanReason?: string;
  bookingId?: number;
  bookingCode?: string;
  cleaningNote?: string;
  supervisorNote?: string;
}

export type ChecklistCategory =
  | 'CLEANING'
  | 'BEDDING'
  | 'BATHROOM'
  | 'AMENITIES'
  | 'FLOOR'
  | 'DISINFECTION'
  | 'MAINTENANCE'
  | 'OUTDOOR';

export interface ChecklistStepItem {
  id: number;
  title: string;
  category: ChecklistCategory;
  completed: boolean;
  required?: boolean;
}

export interface MinibarItemInspection {
  itemId?: number;
  inventoryItemId?: number;
  itemName: string;
  standardQuantity: number;
  currentQuantity: number;
  consumedQuantity?: number;
  unitPrice: number;
}

export interface AssetIncidentReport {
  inventoryItemId?: number;
  itemName: string;
  incidentType: 'ASSET_DAMAGED' | 'ASSET_LOST';
  quantity: number;
  compensationPrice: number;
  evidencePhotoUrl?: string;
  note?: string;
}

export interface SubmitRoomInspectionPayload {
  minibarItems: {
    inventoryItemId?: number;
    itemName: string;
    standardQuantity: number;
    currentQuantity: number;
    unitPrice: number;
  }[];
  amenitiesComplimentaryConfirmed: boolean;
  damagedOrLostAssets: {
    inventoryItemId?: number;
    itemName: string;
    incidentType: 'ASSET_DAMAGED' | 'ASSET_LOST';
    quantity: number;
    compensationPrice: number;
    evidencePhotoUrl?: string;
    note?: string;
  }[];
  note?: string;
}

export type RoomConsumptionItemType = 'MINIBAR_CONSUMED' | 'ASSET_DAMAGED' | 'ASSET_LOST';
export type RoomConsumptionStatus = 'PENDING_RECEPTION_APPROVAL' | 'APPROVED_CHARGED' | 'WAIVED';

export interface RoomConsumptionRecord {
  id: number;
  bookingId?: number;
  bookingCode?: string;
  housekeepingTaskId: number;
  villaId: number;
  villaName?: string;
  roomId?: number;
  roomNumber?: string;
  itemType: RoomConsumptionItemType;
  itemName: string;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
  status: RoomConsumptionStatus;
  evidencePhotoUrl?: string;
  note?: string;
  recordedBy?: string;
  approvedBy?: string;
  createdAt?: string;
}

export type LostAndFoundStatus = 'STORED' | 'GUEST_NOTIFIED' | 'RETURNED' | 'DISPOSED';

export interface LostAndFoundItem {
  id: number;
  itemCode: string;
  villaId: number;
  villaName?: string;
  roomId?: number;
  roomNumber?: string;
  bookingId?: number;
  itemName: string;
  category: string;
  foundLocation: string;
  photoUrl?: string;
  finderName: string;
  guestName?: string;
  guestPhone?: string;
  status: LostAndFoundStatus;
  storageLocation?: string;
  returnedAt?: string;
  note?: string;
  createdAt?: string;
}

export type MaintenancePriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'EMERGENCY';
export type MaintenanceStatus = 'REPORTED' | 'IN_PROGRESS' | 'RESOLVED' | 'CANCELLED';

export interface MaintenanceTicket {
  id: number;
  ticketCode: string;
  villaId: number;
  villaName?: string;
  roomId?: number;
  roomNumber?: string;
  category: string;
  priority: MaintenancePriority;
  description: string;
  photoUrl?: string;
  status: MaintenanceStatus;
  reportedBy: string;
  technicianName?: string;
  resolvedAt?: string;
  technicianNote?: string;
  createdAt?: string;
}
