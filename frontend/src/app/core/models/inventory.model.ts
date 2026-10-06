export type InventoryCategory =
  'AMENITY' | 'LINEN' | 'MINIBAR' | 'CLEANING' | 'EQUIPMENT' | 'OTHER';

export type InventoryTransactionType = 'IMPORT' | 'EXPORT' | 'ADJUSTMENT';

export type VillaAssetStatus = 'GOOD' | 'MAINTENANCE' | 'BROKEN';

export interface InventoryItem {
  id: number;
  code: string;
  name: string;
  category: InventoryCategory;
  categoryLabel?: string;
  unit: string;
  inStock: number;
  minThreshold: number;
  unitPrice: number;
  totalValue?: number;
  supplier?: string;
  location: string;
  description?: string;
  isLowStock?: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface InventoryItemPayload {
  code?: string;
  name: string;
  category: InventoryCategory;
  unit: string;
  inStock?: number;
  minThreshold?: number;
  unitPrice?: number;
  supplier?: string;
  location?: string;
  description?: string;
}

export interface InventoryTransaction {
  id: number;
  itemId: number;
  itemCode: string;
  itemName: string;
  type: InventoryTransactionType;
  quantity: number;
  unitPrice: number;
  totalAmount: number;
  reason?: string;
  performer: string;
  destinationVillaId?: number;
  destinationVillaNumber?: string;
  createdAt: string;
}

export interface InventoryTransactionPayload {
  itemId: number;
  type: InventoryTransactionType;
  quantity: number;
  unitPrice?: number;
  reason?: string;
  performer?: string;
  destinationVillaId?: number;
}

export interface VillaAsset {
  id: number;
  villaId?: number;
  villaNumber: string;
  assetName: string;
  serialNumber?: string;
  category: string;
  status: VillaAssetStatus;
  statusLabel?: string;
  installDate?: string;
  warrantyExpiry?: string;
  note?: string;
  createdAt?: string;
}

export interface VillaAssetPayload {
  villaId?: number;
  villaNumber?: string;
  assetName: string;
  serialNumber?: string;
  category?: string;
  status?: VillaAssetStatus;
  installDate?: string;
  warrantyExpiry?: string;
  note?: string;
}

export interface InventorySummary {
  totalItems: number;
  lowStockCount: number;
  totalInventoryValue: number;
  totalTransactions: number;
  totalVillaAssets: number;
}
