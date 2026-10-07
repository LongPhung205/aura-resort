import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError, tap } from 'rxjs/operators';
import {
  HousekeepingTask,
  RoomConsumptionRecord,
  SubmitRoomInspectionPayload,
  LostAndFoundItem,
  MaintenanceTicket,
  ChecklistStepItem,
} from '../models/housekeeping.model';

import { environment } from '../../../environments/environment';

export interface ApiResponse<T> {
  status: string;
  message: string;
  data: T;
}

@Injectable({
  providedIn: 'root',
})
export class HousekeepingMobileService {
  private readonly API_URL = `${environment.apiUrl}/housekeeping`;

  constructor(private http: HttpClient) {}

  getMyTasks(): Observable<HousekeepingTask[]> {
    return this.http.get<ApiResponse<HousekeepingTask[]>>(`${this.API_URL}/my-tasks`).pipe(
      map((res) => res.data || []),
      catchError(() => of([]))
    );
  }

  getAvailableDirtyRooms(): Observable<HousekeepingTask[]> {
    return this.http
      .get<ApiResponse<HousekeepingTask[]>>(`${this.API_URL}/available-dirty-rooms`)
      .pipe(
        map((res) => res.data || []),
        catchError(() => of([]))
      );
  }

  claimTask(taskId: number): Observable<HousekeepingTask> {
    return this.http
      .post<ApiResponse<HousekeepingTask>>(`${this.API_URL}/tasks/${taskId}/claim`, {})
      .pipe(map((res) => res.data));
  }

  startCleaning(taskId: number): Observable<HousekeepingTask> {
    return this.http
      .post<ApiResponse<HousekeepingTask>>(`${this.API_URL}/tasks/${taskId}/start`, {})
      .pipe(map((res) => res.data));
  }

  updateProgress(
    taskId: number,
    checklistJson: string,
    note?: string
  ): Observable<HousekeepingTask> {
    // Lưu tạm LocalStorage để chống mất dữ liệu khi mất mạng
    localStorage.setItem(`hk_progress_${taskId}`, checklistJson);

    return this.http
      .post<ApiResponse<HousekeepingTask>>(`${this.API_URL}/tasks/progress`, {
        taskId,
        checklistJson,
        cleaningNote: note,
      })
      .pipe(map((res) => res.data));
  }

  toggleOzone(taskId: number, enabled: boolean): Observable<HousekeepingTask> {
    return this.http
      .post<ApiResponse<HousekeepingTask>>(
        `${this.API_URL}/tasks/${taskId}/toggle-ozone?enabled=${enabled}`,
        {}
      )
      .pipe(map((res) => res.data));
  }

  submitInspection(
    taskId: number,
    payload: SubmitRoomInspectionPayload
  ): Observable<RoomConsumptionRecord[]> {
    return this.http
      .post<ApiResponse<RoomConsumptionRecord[]>>(
        `${this.API_URL}/tasks/${taskId}/submit-inspection`,
        payload
      )
      .pipe(map((res) => res.data));
  }

  submitQc(taskId: number, note?: string): Observable<HousekeepingTask> {
    const url = note
      ? `${this.API_URL}/tasks/${taskId}/submit-qc?note=${encodeURIComponent(note)}`
      : `${this.API_URL}/tasks/${taskId}/submit-qc`;
    return this.http.post<ApiResponse<HousekeepingTask>>(url, {}).pipe(
      tap(() => localStorage.removeItem(`hk_progress_${taskId}`)),
      map((res) => res.data)
    );
  }

  submitLostFound(payload: Partial<LostAndFoundItem>): Observable<LostAndFoundItem> {
    return this.http
      .post<ApiResponse<LostAndFoundItem>>(`${this.API_URL}/lost-found`, payload)
      .pipe(map((res) => res.data));
  }

  submitMaintenance(payload: Partial<MaintenanceTicket>): Observable<MaintenanceTicket> {
    return this.http
      .post<ApiResponse<MaintenanceTicket>>(`${this.API_URL}/maintenance-tickets`, payload)
      .pipe(map((res) => res.data));
  }

  getDefaultChecklist(taskType = 'CHECKOUT_DEEP'): ChecklistStepItem[] {
    if (taskType === 'DAILY') {
      return [
        {
          id: 1,
          title: 'Thu gom rác và gạt tàn trong phòng & ban công',
          category: 'CLEANING',
          completed: false,
          required: true,
        },
        {
          id: 2,
          title: 'Làm lại giường ngủ (make bed) phẳng phiu, thay vỏ gối nếu dơ',
          category: 'BEDDING',
          completed: false,
          required: true,
        },
        {
          id: 3,
          title: 'Thu gom khăn ướt trong nhà tắm và treo khăn sạch thay thế',
          category: 'BATHROOM',
          completed: false,
          required: true,
        },
        {
          id: 4,
          title: 'Vệ sinh bồn rửa mặt, lau khô mặt đá lavabo',
          category: 'BATHROOM',
          completed: false,
          required: true,
        },
        {
          id: 5,
          title: 'Bổ sung nước suối chai complimentary và trà/cà phê',
          category: 'AMENITIES',
          completed: false,
          required: true,
        },
        {
          id: 6,
          title: 'Kiểm tra minibar và ghi nhận đồ uống khách đã dùng',
          category: 'AMENITIES',
          completed: false,
          required: true,
        },
        {
          id: 7,
          title: 'Hút bụi sàn và lau sàn phòng ngủ, nhà tắm',
          category: 'FLOOR',
          completed: false,
          required: true,
        },
        {
          id: 8,
          title: 'Xịt thơm phòng và kiểm tra điều hòa để ở 25°C',
          category: 'DISINFECTION',
          completed: false,
          required: false,
        },
      ];
    }

    if (taskType === 'TURNDOWN') {
      return [
        {
          id: 1,
          title: 'Dọn rác nhẹ và chỉnh trang cốc chén trên bàn',
          category: 'CLEANING',
          completed: false,
          required: true,
        },
        {
          id: 2,
          title: 'Kéo rèm cửa ban công và rèm phòng ngủ kín đáo',
          category: 'BEDDING',
          completed: false,
          required: true,
        },
        {
          id: 3,
          title: 'Gấp góc chăn 45 độ, đặt dép đi trong phòng cạnh giường',
          category: 'BEDDING',
          completed: false,
          required: true,
        },
        {
          id: 4,
          title: 'Đặt thiệp chúc ngủ ngon & nước ấm cạnh bàn đầu giường',
          category: 'AMENITIES',
          completed: false,
          required: true,
        },
        {
          id: 5,
          title: 'Bật đèn ngủ ánh sáng ấm dịu nhẹ, tắt đèn trần chính',
          category: 'DISINFECTION',
          completed: false,
          required: true,
        },
      ];
    }

    // Default: CHECKOUT_DEEP (16 bước chuẩn sao đầy đủ)
    return [
      {
        id: 1,
        title: 'Kiểm tra toàn phòng: Đồ khách bỏ quên (Lost & Found) & Sự cố hư hỏng',
        category: 'CLEANING',
        completed: false,
        required: true,
      },
      {
        id: 2,
        title: 'Thu gom rác, rửa gạt tàn & phân loại rác tái chế trong phòng & ban công',
        category: 'CLEANING',
        completed: false,
        required: true,
      },
      {
        id: 3,
        title: 'Tháo bỏ toàn bộ ga giường, vỏ gối, vỏ chăn, khăn tắm vào túi đồ dơ',
        category: 'BEDDING',
        completed: false,
        required: true,
      },
      {
        id: 4,
        title: 'Cọ rửa, tẩy trùng bồn cầu, bồn tắm nằm, vách kính phòng tắm',
        category: 'BATHROOM',
        completed: false,
        required: true,
      },
      {
        id: 5,
        title: 'Lau khô và đánh bóng vòi sen, gương soi kính, mặt bàn lavabo đá hoa cương',
        category: 'BATHROOM',
        completed: false,
        required: true,
      },
      {
        id: 6,
        title: 'Trải ga giường mới, lồng ruột gối ruột chăn sạch, make bed góc vuông chuẩn sao',
        category: 'BEDDING',
        completed: false,
        required: true,
      },
      {
        id: 7,
        title: 'Setup bộ đồ vải mới: 4 Khăn tắm, 4 Khăn mặt, 2 Áo choàng tắm sạch thơm',
        category: 'BEDDING',
        completed: false,
        required: true,
      },
      {
        id: 8,
        title: 'Bổ sung Amenities mới 100%: Bàn chải, xà phòng, dầu gội, sữa tắm, chụp tóc',
        category: 'AMENITIES',
        completed: false,
        required: true,
      },
      {
        id: 9,
        title: 'Setup quầy Mini Bar: Nước suối miễn phí, gói trà, cà phê, kiểm kê tủ mát',
        category: 'AMENITIES',
        completed: false,
        required: true,
      },
      {
        id: 10,
        title: 'Lau bụi bàn làm việc, kệ tivi, tủ quần áo, kiểm tra két sắt mở sẵn',
        category: 'CLEANING',
        completed: false,
        required: true,
      },
      {
        id: 11,
        title: 'Hút bụi thảm, quét và lau sạch bóng sàn gạch/gỗ phòng ngủ & phòng khách',
        category: 'FLOOR',
        completed: false,
        required: true,
      },
      {
        id: 12,
        title: 'Lau kính cửa sổ, cửa trượt ban công, tay nắm cửa và công tắc đèn',
        category: 'CLEANING',
        completed: false,
        required: true,
      },
      {
        id: 13,
        title: 'Kiểm tra hoạt động máy lạnh điều hòa (set 25°C), quạt trần và rèm cửa',
        category: 'CLEANING',
        completed: false,
        required: true,
      },
      {
        id: 14,
        title: 'Kiểm tra khu vực ban công, bàn ghế ngoài trời và khu vực bể bơi riêng',
        category: 'CLEANING',
        completed: false,
        required: true,
      },
      {
        id: 15,
        title: 'Bật máy khử trùng và khử mùi Ozone 20 phút đảm bảo không khí tinh khiết',
        category: 'DISINFECTION',
        completed: false,
        required: true,
      },
      {
        id: 16,
        title: 'Xịt tinh dầu thơm nhẹ tự nhiên, kiểm tra lần cuối trước khi bàn giao QC',
        category: 'DISINFECTION',
        completed: false,
        required: true,
      },
    ];
  }

  submitShiftRegistration(payload: {
    weekStartDate: string;
    preferredZone?: string;
    notes?: string;
    days: Array<{
      workDate: string;
      dayOfWeek: string;
      shiftType: string;
      note?: string;
    }>;
  }): Observable<any> {
    return this.http.post<ApiResponse<any>>(`${this.API_URL}/shift-registration`, payload).pipe(
      map((res) => res.data),
      catchError((err) => {
        console.error('Error submitting shift registration:', err);
        return of(null);
      })
    );
  }

  getMyShiftRegistration(weekStartDate: string): Observable<any> {
    return this.http.get<ApiResponse<any>>(`${this.API_URL}/shift-registration/my-status`, {
      params: { weekStartDate }
    }).pipe(
      map((res) => res.data),
      catchError((err) => {
        console.error('Error fetching shift registration status:', err);
        return of(null);
      })
    );
  }
}
