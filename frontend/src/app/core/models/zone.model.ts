export interface Zone {
  id: number;
  name: string;
  matchKey?: string;
  tag?: string;
  icon?: string;
  badgeClass?: string;
  description?: string;
  slug: string;
  bannerUrl?: string;
  highlights?: string;
  displayOrder?: number;
  isActive?: boolean;
  villaCount?: number;
  createdAt?: string;
}

export interface ZonePayload {
  name: string;
  tag?: string;
  icon?: string;
  badgeClass?: string;
  description?: string;
  slug?: string;
  bannerUrl?: string;
  highlights?: string;
  displayOrder?: number;
  isActive?: boolean;
}

export interface ZoneDetailVilla {
  id: number;
  villaNumber: string;
  floor?: number;
  structureType?: string;
  basePrice?: number;
  status: string;
  villaTypeId?: number;
  villaTypeName?: string;
  zoneId?: number;
  zone?: string;
  zoneTag?: string;
  zoneIcon?: string;
  zoneBadgeClass?: string;
  ozoneStatus?: string;
  amenities?: string[];
  lastCleanedAt?: string;
  currentGuestName?: string;
  bedroomCount?: number;
  totalBeds?: number;
  totalAdults?: number;
  totalChildren?: number;
  totalCapacity?: number;
  imageUrl?: string;
  images?: string[];
}

export interface ZoneDetail {
  zone: Zone;
  villas: ZoneDetailVilla[];
}
