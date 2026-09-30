# Housekeeping Ecosystem Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [x]`) syntax for tracking.

**Goal:** Build a complete two-tier Housekeeping Ecosystem for Aura Resort consisting of a dedicated Mobile PWA Portal (`/housekeeping`) for field staff, a Web Admin Supervisor Hub (`/admin/housekeeping`) for dispatching and QC approval, and a two-step verification workflow connecting Housekeeping, Front Desk (Checkout billing), and Warehouse (Refill).

**Architecture:** 
- Backend: Spring Boot 3 with JPA/Hibernate, expanding `HousekeepingTask` and adding `RoomConsumptionRecord` (for minibar & damaged asset billing), `LostAndFoundItem`, and `MaintenanceTicket`.
- Frontend: Angular 17+ standalone components, featuring a mobile-first responsive PWA layout for field workers (`/housekeeping`) with 3-group inventory checking (+/- buttons), 16-step checklist, optional ozone toggle, and camera evidence upload, alongside a Supervisor dashboard in Web Admin (`/admin/housekeeping`).
- Integration: Two-step verification where Housekeeping logs consumption $\rightarrow$ Front Desk reviews and posts to Booking Folio upon Checkout $\rightarrow$ Warehouse fulfills automated Refill tasks.

**Tech Stack:** Spring Boot 3.3.x, Java 17, Spring Data JPA, Hibernate, MySQL, Angular 17, TailwindCSS, SCSS, RxJS, Lucide/Material Symbols.

## Global Constraints
- Target Spec: `docs/superpowers/specs/2026-09-30-housekeeping-system-design.md`.
- Room status cycle: `DIRTY` $\rightarrow$ `CLEANING` $\rightarrow$ (`OZONE_OPTIONAL`) $\rightarrow$ `WAITING_QC` $\rightarrow$ `CLEAN_READY` or `RE_CLEAN`.
- Three cleaning modes: `CHECKOUT_DEEP`, `DAILY`, `TURNDOWN`.
- Three inventory groups:
  1. `Minibar có tính phí`: standard vs current $\rightarrow$ consumed billable item.
  2. `Amenities miễn phí`: 1-touch 100% complimentary refill, never charged to guest.
  3. `Đồ vải & Tài sản`: Normal laundry is standard operational cost; only Lost/Damaged triggers asset compensation with mandatory evidence photo.
- Role matrix: `ROLE_HOUSEKEEPING` for mobile field portal; `ROLE_ADMIN` / `ROLE_SUPERVISOR` for Web Admin.

---

### Task 1: Backend Data Model Expansion & Entities

**Files:**
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/entity/RoomConsumptionRecord.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/entity/RoomConsumptionItemType.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/entity/RoomConsumptionStatus.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/entity/LostAndFoundItem.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/entity/MaintenanceTicket.java`
- Modify: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/entity/HousekeepingTask.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/repository/RoomConsumptionRecordRepository.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/repository/LostAndFoundItemRepository.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/repository/MaintenanceTicketRepository.java`

**Interfaces:**
- Consumes: `HousekeepingTask`, `Booking`, `Villa`, `Room`, `User`.
- Produces: `RoomConsumptionRecord`, `LostAndFoundItem`, `MaintenanceTicket` entities and Spring Data repositories.

- [x] **Step 1: Create Enums for Consumption and Task Priorities**
Create `RoomConsumptionItemType.java` (`MINIBAR_CONSUMED`, `ASSET_DAMAGED`, `ASSET_LOST`) and `RoomConsumptionStatus.java` (`PENDING_RECEPTION_APPROVAL`, `APPROVED_CHARGED`, `WAIVED`).

- [x] **Step 2: Update HousekeepingTask entity**
In `HousekeepingTask.java`, add fields:
  - `private String priority; // NORMAL, RUSH`
  - `private Boolean ozoneEnabled;`
  - `private String reCleanReason;`
  - `@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "booking_id") private Booking booking;`

- [x] **Step 3: Create RoomConsumptionRecord Entity**
Create `RoomConsumptionRecord.java` with `@Entity`, fields: `id`, `booking`, `housekeepingTask`, `villa`, `room`, `itemType`, `itemName`, `quantity`, `unitPrice`, `totalPrice`, `status`, `evidencePhotoUrl`, `note`, `recordedBy`, `approvedBy`.

- [x] **Step 4: Create LostAndFoundItem & MaintenanceTicket Entities**
Create `LostAndFoundItem.java` (itemCode, villa, room, itemName, category, foundLocation, photoUrl, finderName, guestName, guestPhone, status) and `MaintenanceTicket.java` (ticketCode, villa, room, category, priority, description, photoUrl, status, reportedBy, technicianName).

- [x] **Step 5: Create Repositories**
Implement `RoomConsumptionRecordRepository`, `LostAndFoundItemRepository`, and `MaintenanceTicketRepository` with query methods for booking, villa, and status filters.

- [x] **Step 6: Verify compilation**
Run backend build check: `./gradlew compileJava` or `mvn compile` (or check IDE linter).

- [x] **Step 7: Commit Task 1**
```bash
git add backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/entity/
git add backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/repository/
git commit -m "feat(housekeeping): add entities and repositories for consumption, lost-found, and maintenance"
```

---

### Task 2: Backend Inspection DTOs & Business Services (Chốt Kiểm Soát Kép)

**Files:**
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/request/SubmitRoomInspectionRequest.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/request/RecordConsumptionItemRequest.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/request/LostAndFoundRequest.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/request/MaintenanceTicketRequest.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/response/RoomConsumptionResponse.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/response/LostAndFoundResponse.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/response/MaintenanceTicketResponse.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/service/HousekeepingInspectionService.java`
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/service/impl/HousekeepingInspectionServiceImpl.java`
- Modify: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/service/AdminHousekeepingService.java`
- Modify: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/service/impl/AdminHousekeepingServiceImpl.java`

**Interfaces:**
- Consumes: Repositories from Task 1, `AdminRefillService`, `BookingRepository`.
- Produces: `HousekeepingInspectionService` handling inspection submissions, automatic `RefillTask` generation for missing items, and Front Desk folio integration.

- [x] **Step 1: Write Unit Test for Inspection Calculation**
Create `backend/booking_hotel/src/test/java/com/phungvanlong/booking_hotel/service/HousekeepingInspectionServiceTest.java`.
Assert that:
  - Standard quantity 4, current quantity 2 $\rightarrow$ consumed quantity = 2, total amount = 2 * price.
  - Zero consumed produces no minibar consumption record.
  - Missing quantities automatically append to `CreateRefillTaskRequest`.

- [x] **Step 2: Implement Request/Response DTOs**
Create `SubmitRoomInspectionRequest.java`, `RecordConsumptionItemRequest.java`, `LostAndFoundRequest.java`, `MaintenanceTicketRequest.java` and matching responses with builder and mapping methods.

- [x] **Step 3: Implement HousekeepingInspectionServiceImpl**
Implement methods:
  - `submitInspection(Long taskId, SubmitRoomInspectionRequest request, String staffEmail)`:
    + Validates task.
    + Processes Minibar items: creates `RoomConsumptionRecord` with `MINIBAR_CONSUMED`.
    + Processes Damaged/Lost assets: creates `RoomConsumptionRecord` with `ASSET_DAMAGED`/`ASSET_LOST` and photo evidence.
    + Automatically triggers `adminRefillService.createRefillTask` for all items where `neededQuantity > 0`.
  - `approveConsumption(Long consumptionId, String receptionistEmail)`:
    + Marks record `APPROVED_CHARGED`, updates `booking.folioBalance` and persists.
  - `waiveConsumption(Long consumptionId, String reason, String receptionistEmail)`:
    + Marks record `WAIVED` with note.

- [x] **Step 4: Update AdminHousekeepingServiceImpl for QC and Claim**
Add methods to `AdminHousekeepingServiceImpl`:
  - `claimTask(Long taskId, String staffEmail)`: Housekeeper self-assigns a dirty room.
  - `rejectTask(Long taskId, String reason, String supervisorEmail)`: Marks task `RE_CLEAN` with `supervisorNote`.
  - `toggleOzone(Long taskId, boolean enabled)`: Toggles optional ozone mode.
  - `getAvailableDirtyRooms(String staffEmail)`: Lists unassigned dirty rooms in staff's zone.

- [x] **Step 5: Run Unit Tests**
Run `./gradlew test --tests com.phungvanlong.booking_hotel.service.HousekeepingInspectionServiceTest` and ensure PASS.

- [x] **Step 6: Commit Task 2**
```bash
git add backend/booking_hotel/src/
git commit -m "feat(housekeeping): implement inspection service, QC actions and two-step consumption approval"
```

---

### Task 3: Backend REST Controllers (Field Mobile & Supervisor APIs)

**Files:**
- Create: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/controller/HousekeepingMobileController.java`
- Modify: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/controller/AdminHousekeepingController.java`
- Modify: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/controller/AdminBookingController.java` (or create `AdminBillingConsumptionController.java`)

**Interfaces:**
- Consumes: `HousekeepingInspectionService`, `AdminHousekeepingService`.
- Produces: REST endpoints under `/api/v1/housekeeping/**` and `/api/v1/admin/housekeeping/**`.

- [x] **Step 1: Create HousekeepingMobileController**
Under `@RequestMapping("/housekeeping")` (and `/api/v1/housekeeping`):
  - `GET /my-tasks`: Returns tasks assigned to logged-in housekeeper.
  - `GET /available-dirty-rooms`: Returns unassigned dirty rooms.
  - `POST /tasks/{id}/claim`: Claim a room.
  - `POST /tasks/{id}/start`: Start cleaning.
  - `POST /tasks/{id}/progress`: Update checklist progress JSON.
  - `POST /tasks/{id}/toggle-ozone`: Toggle ozone mode.
  - `POST /tasks/{id}/submit-inspection`: Submit 3-group inspection.
  - `POST /tasks/{id}/submit-qc`: Finish cleaning and request supervisor approval (`WAITING_QC`).
  - `POST /lost-found`: Submit lost & found item.
  - `POST /maintenance-tickets`: Submit maintenance ticket.

- [x] **Step 2: Extend AdminHousekeepingController**
Add:
  - `POST /tasks/{id}/reject`: Supervisor rejects with reason.
  - `GET /matrix`: Returns all resort rooms grouped by Zone with status and `RUSH` flags.
  - `GET /lost-found`: Get list of lost & found items.
  - `PUT /lost-found/{id}/status`: Update item status (`STORED`, `GUEST_NOTIFIED`, `RETURNED`).
  - `GET /maintenance-tickets`: List open room maintenance tickets.

- [x] **Step 3: Create Front Desk Billing Consumption Controller**
Under `@RequestMapping("/admin/billing/consumptions")`:
  - `GET /pending?bookingId={id}`: List consumption items awaiting Front Desk checkout review.
  - `POST /{id}/approve`: Front Desk approves charging to guest bill.
  - `POST /{id}/waive`: Front Desk waives charge.

- [x] **Step 4: Verify Controller Integration**
Perform sanity request testing or MockMvc integration test.

- [x] **Step 5: Commit Task 3**
```bash
git add backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/controller/
git commit -m "feat(housekeeping): add mobile field controller, supervisor QC endpoints and billing folio review"
```

---

### Task 4: Frontend Models & Housekeeping Services

**Files:**
- Create: `frontend/src/app/core/models/housekeeping.model.ts`
- Create: `frontend/src/app/core/services/housekeeping-mobile.service.ts`
- Modify: `frontend/src/app/core/services/admin-housekeeping.service.ts`

**Interfaces:**
- Consumes: Backend REST APIs.
- Produces: TypeScript models and Angular Injectable services with RxJS observables.

- [x] **Step 1: Define TypeScript Models in `housekeeping.model.ts`**
Define interfaces:
  - `HousekeepingTask` with `priority`, `ozoneEnabled`, `reCleanReason`, `taskType`, `status`.
  - `ChecklistStepItem` (id, title, category, completed).
  - `MinibarItemInspection` (itemId, itemName, standardQuantity, currentQuantity, consumedQuantity, unitPrice).
  - `AssetIncidentReport` (itemId, itemName, incidentType: 'DAMAGED' | 'LOST', quantity, compensationPrice, photoUrl, note).
  - `LostAndFoundItem` and `MaintenanceTicket`.
  - `RoomConsumptionRecord`.

- [x] **Step 2: Implement `HousekeepingMobileService`**
Methods:
  - `getMyTasks()`, `getAvailableDirtyRooms()`, `claimTask(id)`.
  - `startCleaning(id)`, `updateProgress(payload)`, `toggleOzone(id, enabled)`.
  - `submitInspection(taskId, payload)`, `submitQc(taskId, note)`.
  - `createLostFound(payload)`, `createMaintenanceTicket(payload)`.
  - Add LocalStorage cache fallback for offline resilience.

- [x] **Step 3: Update `AdminHousekeepingService`**
Add supervisor methods:
  - `getRoomMatrix()`, `rejectTask(taskId, reason)`.
  - `getLostAndFoundList()`, `updateLostFoundStatus(id, status)`.
  - `getMaintenanceTickets()`.
  - `getPendingConsumptions(bookingId)`, `approveConsumption(id)`, `waiveConsumption(id, reason)`.

- [x] **Step 4: Commit Task 4**
```bash
git add frontend/src/app/core/models/housekeeping.model.ts
git add frontend/src/app/core/services/housekeeping-mobile.service.ts
git add frontend/src/app/core/services/admin-housekeeping.service.ts
git commit -m "feat(frontend): create housekeeping models and mobile field service with supervisor APIs"
```

---

### Task 5: Frontend Mobile Field Portal (`/housekeeping`)

**Files:**
- Create: `frontend/src/app/features/housekeeping/housekeeping-portal.component.ts|html|scss`
- Create: `frontend/src/app/features/housekeeping/task-workspace/task-workspace.component.ts|html|scss`
- Modify: `frontend/src/app/app.routes.ts`

**Interfaces:**
- Consumes: `HousekeepingMobileService`, `TokenService`.
- Produces: Dedicated mobile viewport (`/housekeeping`) for `ROLE_HOUSEKEEPING`.

- [x] **Step 1: Create Main Portal Component (`housekeeping-portal`)**
Design high-end mobile UI:
  - Header: Staff avatar, name, shift status, summary chips (Assigned, Done, Pending QC).
  - Tab 1: "Phòng Của Tôi" (cards with Room Number, Villa Name, Task Type, Status Badge, `RUSH` indicator, big CTA [Bắt đầu / Tiếp tục dọn]).
  - Tab 2: "Phòng Trống Cần Dọn" (list of dirty rooms with 1-click [Nhận dọn phòng này]).
  - Quick action floating bar: [Báo Hỏng Kỹ Thuật] & [Nhặt Được Đồ Thất Lạc].

- [x] **Step 2: Create Task Workspace Component (`task-workspace`)**
Full-screen interactive mobile workspace:
  - Top progress bar (0% - 100%).
  - Room info header with quick back button.
  - **Tab 1: Quy trình dọn**: 16-step checklist with category badges (Vệ sinh, Make bed, Thay khăn, Lau kính). Optional Ozone card with toggle switch and 20-minute countdown indicator.
  - **Tab 2: Kiểm kê & Bù đồ (3 Khối rõ rệt)**:
    + *Khối 1: Minibar có tính phí*: Item row with standard tag, `[-]` `[+]` counter, auto-calculated consumed and estimated bill amount.
    + *Khối 2: Amenities miễn phí*: Big toggle [✔ Đã setup đầy đủ mới 100% đón khách].
    + *Khối 3: Đồ vải & Tài sản*: Default clean laundry note + Red action button [⚠ Báo Mất / Hỏng Đồ] opening modal with item selector, incident type, camera image capture preview, and compensation price lookup.
  - **Tab 3: Tiện ích**: Quick buttons for Maintenance Report and Lost & Found.
  - Bottom sticky bar: **[HOÀN TẤT & GỬI NGHIỆM THU]** with confirmation modal.

- [x] **Step 3: Register Route in `app.routes.ts`**
Add route `{ path: 'housekeeping', loadComponent: () => import('./features/housekeeping/housekeeping-portal.component').then(m => m.HousekeepingPortalComponent) }`.
Ensure role guard allows `ROLE_HOUSEKEEPING` and `ROLE_ADMIN`.

- [x] **Step 4: Test in Mobile Viewport**
Verify responsive layout at 375px (iPhone SE) and 430px (iPhone 15 Pro Max) width in Chrome DevTools. Check button tap targets and counter interactions.

- [x] **Step 5: Commit Task 5**
```bash
git add frontend/src/app/features/housekeeping/
git add frontend/src/app/app.routes.ts
git commit -m "feat(frontend): implement mobile field housekeeping portal and interactive workspace"
```

---

### Task 6: Frontend Web Admin Supervisor Hub (`/admin/housekeeping`)

**Files:**
- Create: `frontend/src/app/admin/housekeeping-management/housekeeping-management.component.ts|html|scss`
- Modify: `frontend/src/app/admin/layout/admin-layout.component.html` (sidebar link)
- Modify: `frontend/src/app/app.routes.ts`

**Interfaces:**
- Consumes: `AdminHousekeepingService`, `AdminRefillService`.
- Produces: Web Admin module under `/admin/housekeeping` for supervisors.

- [x] **Step 1: Create HousekeepingManagementComponent**
Build a dashboard with:
  - Metric counters: Tổng phòng, Cần dọn (`DIRTY`), Đang dọn (`CLEANING`), Chờ nghiệm thu (`WAITING_QC`), Sẵn sàng đón khách (`CLEAN_READY`), Bảo trì (`MAINTENANCE`).
  - **Tab 1: Sơ đồ Ma Trận Phòng (Live Room Matrix)**: Filter by Zone (Phân khu) and status. Room cards displaying room code, housekeeper avatar, elapsed cleaning time, and flashing `RUSH` badge for priority check-in rooms.
  - **Tab 2: Điều phối & Phân công**: List of housekeepers on duty + 1-click room assignment dropdown.
  - **Tab 3: Sổ Đồ Thất Lạc (Lost & Found)**: Grid/Table with photo, room, finder, found location, guest info, and status update button (`STORED` $\rightarrow$ `RETURNED`).
  - **Tab 4: Theo dõi Phiếu Refill Kho**: Pipeline tracker for villa supply refills.

- [x] **Step 2: Build QC Inspection Modal**
When clicking any room in `WAITING_QC`:
  - Show inspector modal: Room number, housekeeper name, duration, completed checklist steps.
  - Summary of Minibar items consumed and Damaged/Lost asset reports with evidence photo zoom.
  - Action buttons:
    + **[✔ DUYỆT ĐẠT (Pass & Ready)]**: Calls `approveTask`, changes room to `CLEAN_READY`.
    + **[✖ YÊU CẦU DỌN LẠI (Re-clean)]**: Opens reason picker + note input, calls `rejectTask`, sets room to `RE_CLEAN`.

- [x] **Step 3: Add to Admin Navigation & Routes**
In `admin-layout.component.html`, under "VẬN HÀNH & DỊCH VỤ", add navigation item:
  - Link: `/admin/housekeeping`
  - Icon: `cleaning_services`
  - Label: `Quản Lý Buồng Phòng & QC`
In `app.routes.ts`, register child route `housekeeping` under `admin`.

- [x] **Step 4: Test Supervisor Workflow**
Verify room state changes from `WAITING_QC` to `CLEAN_READY` or `RE_CLEAN` and verify modal triggers.

- [x] **Step 5: Commit Task 6**
```bash
git add frontend/src/app/admin/housekeeping-management/
git add frontend/src/app/admin/layout/admin-layout.component.html
git add frontend/src/app/app.routes.ts
git commit -m "feat(frontend): implement admin housekeeping supervisor hub with live matrix and QC modal"
```

---

### Task 7: Front Desk Billing Integration (Chốt Kiểm Soát Kép)

**Files:**
- Modify: `frontend/src/app/admin/booking-management/booking-management.component.ts`
- Modify: `frontend/src/app/admin/booking-management/booking-management.component.html`

**Interfaces:**
- Consumes: `AdminHousekeepingService.getPendingConsumptions`, `approveConsumption`, `waiveConsumption`.
- Produces: Checkout review panel showing minibar and asset damage charges before finalizing guest bill.

- [x] **Step 1: Fetch Pending Consumptions in Checkout Modal**
When Front Desk opens the Checkout / Payment modal for a booking, call `adminHousekeepingService.getPendingConsumptions(booking.id)`.

- [x] **Step 2: Render Consumption Review Panel in Checkout UI**
Add a dedicated card in the Checkout modal:
  - List of items reported by Housekeeping:
    + Item name, type badge (`MINIBAR`, `MẤT ĐỒ`, `HỎNG ĐỒ`), quantity, unit price, total price.
    + Small thumbnail with photo modal preview for damaged/lost items.
  - Action buttons for each item:
    + `[✔ Tính vào hóa đơn]`: Calls `approveConsumption`, updates total checkout amount.
    + `[✖ Miễn phí / Bỏ qua]`: Prompts for note, calls `waiveConsumption`.

- [x] **Step 3: Test Two-Step Verification Flow**
Housekeeper logs 2 beers on Mobile $\rightarrow$ Front Desk opens Checkout modal $\rightarrow$ Verifies 2 beers displayed with pending status $\rightarrow$ Clicks approve $\rightarrow$ Bill balance increases by 2 * price.

- [x] **Step 4: Commit Task 7**
```bash
git add frontend/src/app/admin/booking-management/
git commit -m "feat(billing): integrate housekeeping consumption review into front desk checkout modal"
```

---

### Task 8: Seed Data, End-to-End Verification & Polish

**Files:**
- Modify: `backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/config/DataInitializer.java`

- [x] **Step 1: Seed Realistic Demo Data**
In `DataInitializer.java`:
  - Ensure sample villa rooms have defined standards (Minibar: 4 Heineken, 4 Coca, 4 Perrier; Amenities: 4 toothbrush sets, 4 shampoo bottles; Linen: 4 large towels, 2 bathrobes).
  - Seed 1 task in `DIRTY` status with `RUSH` priority.
  - Seed 1 task in `WAITING_QC` status with 2 consumed beers and 1 lost towel with sample photo.
  - Seed 1 sample Lost & Found item (Apple Watch) and 1 Maintenance ticket (Leaking AC).

- [x] **Step 2: Backend & Frontend Build Verification**
Run backend compilation and test:
```bash
cd backend/booking_hotel
./mvnw clean compile # or ./gradlew compileJava
```
Run frontend build check:
```bash
cd frontend
npm run build -- --configuration development
```

- [x] **Step 3: Full End-to-End Walkthrough**
1. Log in as `hoa.housekeeping@auraholdings.vn` $\rightarrow$ navigate to `/housekeeping`.
2. Inspect room cards $\rightarrow$ click room $\rightarrow$ complete checklist $\rightarrow$ adjust minibar $(-) \rightarrow$ click finish.
3. Log in as `admin@auroresort.com` $\rightarrow$ navigate to `/admin/housekeeping` $\rightarrow$ see room in `WAITING_QC` $\rightarrow$ open QC modal $\rightarrow$ click [Duyệt Đạt].
4. Check room state becomes `CLEAN_READY`.
5. Open `/admin/bookings` $\rightarrow$ verify minibar items appear in pending consumption review.

- [x] **Step 4: Commit Task 8**
```bash
git add backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/config/DataInitializer.java
git commit -m "chore(housekeeping): seed realistic operational data and verify build stability"
```
