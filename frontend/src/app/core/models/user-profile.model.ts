export interface UserProfile {
  id: number;
  email: string;
  fullName: string;
  phone?: string;
  avatar?: string;
  role: string;
  provider: string;
  createdAt: string;
  totalBookings: number;
  totalSpent: number;
}

