export interface ChildRoomItem {
  id?: number;
  roomNumber: string;
  name?: string;
  description?: string;
  floor?: number;
  status?: string;
  roomTypeId?: number;
  roomTypeName?: string;
  bedType?: string;
  adults?: number;
  children?: number;
  capacity?: number;
  zone?: string;
}

export interface AdminRoomItem {
  id: number;
  roomNumber: string;
  floor: number;
  structureType?: string;
  basePrice?: number;
  status: string;
  roomTypeId?: number;
  roomTypeName?: string;
  zone?: string;
  ozoneStatus?: string;
  amenities?: string[];
  lastCleanedAt?: string;
  currentGuestName?: string;
  villaNumber?: string;
  villaTypeName?: string;
  villaTypeId?: number;
  bedroomCount?: number;
  totalBeds?: number;
  totalAdults?: number;
  totalChildren?: number;
  totalCapacity?: number;
  childRooms?: ChildRoomItem[];
  rooms?: ChildRoomItem[];
  imageUrl?: string;
  images?: string[];
  area?: number;
  viewDirection?: string;
  poolSize?: number;
  overviewDescription?: string;
}

export interface AdminRoomTypeItem {
  id: number;
  name: string;
  description?: string;
  basePrice?: number;
  dynamicPrice?: number;
  capacity?: number;
  adults?: number;
  children?: number;
  bedType?: string;
  imageUrl?: string;
}

export interface UpdateRoomTypePayload {
  name: string;
  description?: string;
  basePrice: number;
  capacity: number;
  adults?: number;
  children?: number;
  bedType?: string;
  imageUrl?: string;
}

export interface ZoneItem {
  id?: number;
  name: string;
  matchKey?: string;
  tag?: string;
  icon?: string;
  badgeClass?: string;
  description?: string;
  isActive?: boolean;
  villaCount?: number;
  createdAt?: string;
}

export interface CreateRoomPayload {
  roomNumber: string;
  villaNumber?: string;
  floor: number;
  structureType?: string;
  roomTypeId?: number;
  villaTypeId?: number;
  zoneId?: number;
  zone?: string;
  status?: string;
  ozoneStatus?: string;
  amenities?: string[];
  basePrice?: number;
  price?: number;
  bedroomCount?: number;
  bedSelections?: { roomTypeId: number; quantity: number }[];
  childRooms?: {
    roomNumber: string;
    name?: string;
    floor?: number;
    roomTypeId: number;
  }[];
  imageUrl?: string;
  images?: string[];
  area?: number;
  viewDirection?: string;
  poolSize?: number;
  overviewDescription?: string;
}

export type UpdateRoomPayload = CreateRoomPayload;

