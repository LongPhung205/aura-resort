export interface ExtraServiceItem {
  id: number;
  name: string;
  description?: string;
  price: number;
  type: string;
  unit: string;
  icon?: string;
  imageUrl?: string;
  isActive: boolean;
  villaCount?: number;
}

export interface ExtraServicePayload {
  name: string;
  description?: string;
  price: number;
  type: string;
  unit: string;
  icon?: string;
  imageUrl?: string;
  isActive?: boolean;
}

export interface VillaServiceItem {
  id: number;
  villaId: number;
  villaNumber: string;
  serviceId: number;
  serviceName: string;
  serviceType: string;
  serviceIcon?: string;
  serviceUnit?: string;
  defaultPrice: number;
  priceOverride?: number;
  effectivePrice: number;
  isAvailable: boolean;
  note?: string;
}

export interface VillaServicePayload {
  villaId: number;
  serviceId: number;
  priceOverride?: number;
  isAvailable?: boolean;
  note?: string;
}

