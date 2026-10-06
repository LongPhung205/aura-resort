export interface RoomTypeYieldItem {
  roomTypeId: number;
  roomTypeName: string;
  basePrice: number;
  dynamicPrice: number;
  occupancyRate: number;
  suggestedAdjustmentPercent: number;
  demandLevel: 'PEAK' | 'HIGH' | 'NORMAL' | 'LOW';
}

export interface YieldMatrixResponse {
  currentOccupancy: number;
  predictedWeekendOccupancy: number;
  isAiDynamicPricingActive: boolean;
  yieldStrategy: string;
  roomTypes: RoomTypeYieldItem[];
}

export interface YieldRule {
  id: number;
  ruleName: string;
  conditionType: string;
  thresholdValue?: number;
  priceMultiplier: number;
  targetRoomTypes?: string;
  isActive: boolean;
  description?: string;
}
