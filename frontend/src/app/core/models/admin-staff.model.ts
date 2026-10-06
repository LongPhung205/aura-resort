export interface DayScheduleItem {
  date: string;
  shiftName: string;
  shiftColor: string;
  status: 'SCHEDULED' | 'PRESENT' | 'LATE' | 'OFF';
}

export interface StaffWeeklyScheduleItem {
  staffId: number;
  fullName: string;
  role: string;
  department: string;
  avatarUrl?: string;
  days: DayScheduleItem[];
}

export interface StaffRosterResponse {
  weekStartDate: string;
  weekEndDate: string;
  totalStaff: number;
  onDutyToday: number;
  lateToday: number;
  onLeaveToday: number;
  staffMembers: StaffWeeklyScheduleItem[];
}

export interface AttendanceLog {
  id: number;
  staffId?: number;
  staffName: string;
  department?: string;
  checkTime: string;
  checkType: string;
  method: string;
  locationName: string;
  matchAccuracy?: number;
  photoUrl?: string;
  status: string;
}

export interface ShiftSwapRequest {
  id: number;
  requesterId?: number;
  requesterName: string;
  requesterRole?: string;
  targetStaffId?: number;
  targetStaffName?: string;
  targetDate: string;
  currentShiftName?: string;
  desiredShiftName?: string;
  requestType: 'SWAP' | 'OVERTIME' | 'LEAVE';
  reason?: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  approverName?: string;
  actionAt?: string;
  createdAt?: string;
}

export interface WeeklyRegistrationDayItem {
  id?: number;
  workDate: string;
  dayOfWeek: string;
  shiftType: string;
  note?: string;
}

export interface WeeklyShiftRegistration {
  id: number;
  staffId: number;
  staffName: string;
  staffRole?: string;
  department?: string;
  avatarUrl?: string;
  weekStartDate: string;
  weekEndDate: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  preferredZone?: string;
  notes?: string;
  approverName?: string;
  approvedAt?: string;
  rejectionReason?: string;
  createdAt?: string;
  days: WeeklyRegistrationDayItem[];
}
