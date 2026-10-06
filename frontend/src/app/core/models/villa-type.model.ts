export interface VillaType {
  id: number;
  name: string;
  description?: string;
  basePrice: number;
  dynamicPrice?: number;
  isDynamicPricingEnabled?: boolean;
  capacity: number;
  adults?: number;
  children?: number;
  bedType?: string;
  imageUrl?: string;
  totalVillas?: number;
}

export interface VillaTypeRequest {
  name: string;
  description?: string;
  basePrice: number;
  dynamicPrice?: number;
  isDynamicPricingEnabled?: boolean;
  capacity: number;
  adults?: number;
  children?: number;
  bedType?: string;
  imageUrl?: string;
}

// Alias for backward compatibility
export type RoomType = VillaType;
export type RoomTypeRequest = VillaTypeRequest;
