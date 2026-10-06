export type { HousekeepingTask, TaskType, TaskStatus, TaskPriority } from './housekeeping.model';

export interface HousekeeperSummary {
  id: number;
  fullName: string;
  email: string;
  phone?: string;
  activeTasksCount: number;
  activeTasks?: number;
  completedTasks?: number;
}

export interface AssignHousekeepingTaskRequest {
  taskId?: number;
  roomId?: number;
  villaId?: number;
  housekeeperId: number;
  taskType?: 'CHECKOUT_DEEP' | 'DAILY' | 'TURNDOWN';
  notes?: string;
}

export interface UpdateCleaningProgressRequest {
  taskId: number;
  status?: string;
  checklistJson?: string;
  cleaningNote?: string;
  evidencePhotoUrl?: string;
}

export interface HousekeepingChecklistRequest {
  taskId: number;
  checklistJson: string;
  evidencePhotoUrl?: string;
  notes?: string;
}
