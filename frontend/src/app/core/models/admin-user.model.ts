export type UserRole = 
  | 'ROLE_CUSTOMER' 
  | 'ROLE_STAFF' 
  | 'ROLE_RECEPTIONIST' 
  | 'ROLE_BUTLER' 
  | 'ROLE_HOUSEKEEPING' 
  | 'ROLE_ACCOUNTANT' 
  | 'ROLE_ADMIN';

export type AuthProvider = 'LOCAL' | 'GOOGLE' | 'FACEBOOK';

export interface AdminUser {
  id: number;
  email: string;
  fullName: string;
  phone?: string;
  role: UserRole;
  isActive: boolean;
  provider: AuthProvider;
  createdAt: string;
  totalBookings?: number;
  totalSpent?: number;
  lastBookingDate?: string;
}

export interface UserSummaryStats {
  totalUsers: number;
  totalCustomers: number;
  totalStaff: number;
  totalLocked: number;
}

export interface CreateUserRequest {
  email: string;
  password?: string;
  fullName: string;
  phone?: string;
  role: UserRole;
}

export interface UpdateUserRequest {
  fullName: string;
  phone?: string;
  role: UserRole;
}

export interface ResetPasswordRequest {
  newPassword: string;
}

export interface UserFilter {
  tab: 'CUSTOMER' | 'STAFF' | 'ALL';
  search?: string;
  role?: UserRole | '';
  isActive?: boolean | '';
  page: number;
  size: number;
}

export interface PageResponse<T> {
  content: T[];
  pageNo: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface ApiResponse<T> {
  status: string;
  message: string;
  data: T;
}
