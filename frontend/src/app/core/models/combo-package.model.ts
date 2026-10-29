/* eslint-disable @typescript-eslint/no-explicit-any */
import { ExtraServiceItem } from './extra-service.model';

export interface ComboPackage {
  id?: number;
  name: string;
  description?: string;
  imageUrl?: string;
  price?: number;
  status: string;
  extraServiceIds?: number[];
  extraServices?: (ExtraServiceItem | any)[];
  createdAt?: string;
  updatedAt?: string;
}
