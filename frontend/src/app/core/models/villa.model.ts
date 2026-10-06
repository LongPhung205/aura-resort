import { VillaType } from './villa-type.model';

export interface ChildRoom {
  id?: number;
  roomNumber: string;
  name?: string;
  description?: string;
  floor?: number;
  status?: string;
  zone?: string;
  roomTypeId?: number;
  roomTypeName?: string;
  bedType?: string;
  adults?: number;
  children?: number;
  capacity?: number;
}

export interface Villa {
  id: number;
  villaNumber: string;
  floor?: number;
  structureType?: string;
  zone?: string;
  zoneId?: number;
  zoneTag?: string;
  zoneIcon?: string;
  zoneBadgeClass?: string;
  ozoneStatus?: string;
  amenities?: string[];
  bedroomCount?: number;
  totalBeds?: number;
  totalAdults?: number;
  totalChildren?: number;
  totalCapacity?: number;
  lastCleanedAt?: string;
  currentGuestName?: string;
  status: 'AVAILABLE' | 'OCCUPIED' | 'CLEANING' | 'MAINTENANCE' | string;
  villaTypeId?: number;
  villaTypeName?: string;
  basePrice?: number;
  dynamicPrice?: number;
  capacity?: number;
  imageUrl?: string;
  images?: string[];
  childRooms?: ChildRoom[];
  rooms?: ChildRoom[];
  // Legacy backward compatibility fields
  roomNumber?: string;
  roomTypeId?: number;
  roomTypeName?: string;
  
  // New detail fields
  area?: number;
  viewDirection?: string;
  poolSize?: number;
  overviewDescription?: string;
}

export interface VillaRequest {
  villaNumber: string;
  floor?: number;
  zone?: string;
  status?: string;
  ozoneStatus?: string;
  villaTypeId: number;
  // Legacy aliases
  roomNumber?: string;
  roomTypeId?: number;
}

// Aliases for backward compatibility
export type Room = Villa;
export type RoomRequest = VillaRequest;
