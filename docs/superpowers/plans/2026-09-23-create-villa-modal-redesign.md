# Nâng Cấp Form Khởi Tạo & Quản Lý Biệt Thự (Villa Management Redesign) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Nâng cấp toàn diện Modal "Khởi Tạo / Chỉnh Sửa Biệt Thự" trên giao diện Admin (`/admin/villas`) cho phép chọn nhiều loại giường trong 1 villa (theo cơ chế Hybrid Checklist & phân bổ phòng ngủ), tự động xác định Hạng Villa (`Villa N Phòng Ngủ`), đổi trường tầng thành "Số Tầng / Kết Cấu", chuyển Ozon thành "Dịch Vụ Đi Kèm & Tiện Ích Mở Rộng", đồng thời bảo toàn phân khu nghỉ dưỡng (Ngọc Trai...), trạng thái vận hành và tải ảnh máy tính/mẫu resort.

**Architecture:** 
- **Database & Backend:** Bổ sung các trường `structure_type`, `base_price`, `amenities`, `bedroom_count` vào entity `Villa`; mở rộng `VillaRequest` & `VillaResponse` hỗ trợ `bedSelections` và `childRooms`; trong `VillaServiceImpl` tự động ánh xạ hoặc tạo `VillaType` theo số phòng ngủ (`Villa N Phòng Ngủ`) và khởi tạo các bản ghi `Room` con liên kết với `room_types` (loại giường).
- **Frontend Angular:** Bổ sung state chọn giường đa năng (`bedSelections`), tính toán tức thời (Live Summary) tổng số phòng, số giường, sức chứa người lớn và trẻ nhỏ; thiết kế lại Modal 4 gồm 4 khối trực quan chuẩn luxury resort; cập nhật card hiển thị biệt thự ở Section 4.

**Tech Stack:** Java 17, Spring Boot 3, Hibernate JPA, MySQL 8, Angular 17, Tailwind CSS, TypeScript.

## Global Constraints
- Bảo toàn dữ liệu người dùng (8 tài khoản trong bảng `users`).
- Giữ nguyên bộ lọc phân khu nghỉ dưỡng (bao gồm cụm `Ngọc Trai`), trạng thái vận hành (`AVAILABLE`, `OCCUPIED`, `CLEANING`, `MAINTENANCE`), và tính năng upload/xem trước ảnh đại diện.
- Tự động phân loại tên Hạng Villa theo định dạng: `Villa N Phòng Ngủ` (với N = số phòng ngủ được cấu hình).
- Tuân thủ thiết kế Hybrid: Danh sách checklist chọn nhanh số lượng từng loại giường kèm vùng mở rộng phân bổ chi tiết tùy chọn.

---

### Task 1: Backend Data Model & DTOs

**Files:**
- Modify: `d:/booking_hotel/backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/entity/Villa.java`
- Create: `d:/booking_hotel/backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/request/VillaBedSelectionDto.java`
- Create: `d:/booking_hotel/backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/request/ChildRoomRequestDto.java`
- Modify: `d:/booking_hotel/backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/request/VillaRequest.java`
- Modify: `d:/booking_hotel/backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/response/VillaResponse.java`

**Interfaces:**
- Consumes: `VillaType`, `Room`, `RoomType`
- Produces: `VillaBedSelectionDto`, `ChildRoomRequestDto`, updated `VillaRequest`, updated `VillaResponse` with `structureType`, `amenities`, `bedroomCount`, `totalAdults`, `totalChildren`, `totalCapacity`, `totalBeds`.

- [ ] **Step 1: Write failing unit test for Villa DTO serialization and mapping**

Create file `d:/booking_hotel/backend/booking_hotel/src/test/java/com/phungvanlong/booking_hotel/dto/VillaDtoTest.java`:
```java
package com.phungvanlong.booking_hotel.dto;

import com.phungvanlong.booking_hotel.dto.request.ChildRoomRequestDto;
import com.phungvanlong.booking_hotel.dto.request.VillaBedSelectionDto;
import com.phungvanlong.booking_hotel.dto.request.VillaRequest;
import com.phungvanlong.booking_hotel.dto.response.VillaResponse;
import com.phungvanlong.booking_hotel.entity.Room;
import com.phungvanlong.booking_hotel.entity.RoomType;
import com.phungvanlong.booking_hotel.entity.Villa;
import com.phungvanlong.booking_hotel.entity.VillaStatus;
import com.phungvanlong.booking_hotel.entity.VillaType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class VillaDtoTest {

    @Test
    void testVillaRequestAndResponseMapping() {
        VillaBedSelectionDto bed1 = VillaBedSelectionDto.builder()
                .roomTypeId(1L)
                .quantity(1)
                .build();

        VillaRequest request = VillaRequest.builder()
                .villaNumber("Villa #101")
                .floor(2)
                .structureType("2 Tầng")
                .basePrice(BigDecimal.valueOf(25000000))
                .zone("Ngọc Trai")
                .status(VillaStatus.AVAILABLE)
                .amenities(List.of("Khử trùng Ozon định kỳ", "Quản gia Lead Butler 24/7"))
                .bedroomCount(3)
                .bedSelections(List.of(bed1))
                .build();

        assertEquals("Villa #101", request.getVillaNumber());
        assertEquals("2 Tầng", request.getStructureType());
        assertEquals(2, request.getAmenities().size());

        RoomType kingRoomType = RoomType.builder()
                .id(1L)
                .name("King Size Bed")
                .bedType("Rộng 1.8m × Dài 2.0m")
                .adults(2)
                .children(1)
                .capacity(3)
                .build();

        Room room1 = Room.builder()
                .id(10L)
                .roomNumber("Villa #101-P1")
                .name("Phòng Ngủ Master")
                .floor(1)
                .roomType(kingRoomType)
                .build();

        VillaType villaType = VillaType.builder()
                .id(5L)
                .name("Villa 3 Phòng Ngủ")
                .build();

        Villa villa = Villa.builder()
                .id(100L)
                .villaNumber("Villa #101")
                .floor(2)
                .structureType("2 Tầng")
                .basePrice(BigDecimal.valueOf(25000000))
                .zone("Ngọc Trai")
                .status(VillaStatus.AVAILABLE)
                .ozoneStatus("STERILIZED")
                .amenities("Khử trùng Ozon định kỳ, Quản gia Lead Butler 24/7")
                .bedroomCount(3)
                .villaType(villaType)
                .rooms(List.of(room1))
                .build();

        VillaResponse response = VillaResponse.fromEntity(villa);
        assertNotNull(response);
        assertEquals("Villa #101", response.getVillaNumber());
        assertEquals("2 Tầng", response.getStructureType());
        assertEquals(BigDecimal.valueOf(25000000), response.getBasePrice());
        assertEquals(3, response.getBedroomCount());
        assertEquals(1, response.getRooms().size());
        assertEquals("King Size Bed", response.getRooms().get(0).getRoomTypeName());
        assertEquals(2, response.getTotalAdults());
        assertEquals(1, response.getTotalChildren());
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `.\mvnw.cmd test -Dtest=VillaDtoTest`
Expected: Compilation failure because fields `structureType`, `basePrice`, `amenities`, `bedroomCount`, `bedSelections` do not exist yet.

- [ ] **Step 3: Update `Villa.java`**

In `d:/booking_hotel/backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/entity/Villa.java`, add the new fields:
```java
    @Column(name = "structure_type", length = 50)
    private String structureType; // Ví dụ: "1 Tầng", "2 Tầng", "Duplex Thông Tầng", "Penthouse"

    @Column(name = "base_price", precision = 12, scale = 2)
    private java.math.BigDecimal basePrice;

    @Column(columnDefinition = "TEXT")
    private String amenities; // Danh sách tiện ích phân tách dấu phẩy hoặc JSON

    @Column(name = "bedroom_count")
    private Integer bedroomCount;
```

- [ ] **Step 4: Create DTOs `VillaBedSelectionDto.java` and `ChildRoomRequestDto.java`**

Create `d:/booking_hotel/backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/request/VillaBedSelectionDto.java`:
```java
package com.phungvanlong.booking_hotel.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VillaBedSelectionDto implements Serializable {
    private Long roomTypeId;
    private Integer quantity;
}
```

Create `d:/booking_hotel/backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/request/ChildRoomRequestDto.java`:
```java
package com.phungvanlong.booking_hotel.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChildRoomRequestDto implements Serializable {
    private String roomNumber;
    private String name;
    private Integer floor;
    private Long roomTypeId;
    private String description;
}
```

- [ ] **Step 5: Update `VillaRequest.java`**

In `d:/booking_hotel/backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/request/VillaRequest.java`:
```java
package com.phungvanlong.booking_hotel.dto.request;

import com.phungvanlong.booking_hotel.entity.VillaStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VillaRequest {

    @NotBlank(message = "Mã số căn Villa không được để trống")
    private String villaNumber;

    private Integer floor;
    private String structureType;
    private BigDecimal basePrice;
    private Long villaTypeId;

    private String zone;
    private VillaStatus status;
    private String ozoneStatus;
    private List<String> amenities;
    private Integer bedroomCount;
    private List<VillaBedSelectionDto> bedSelections;
    private List<ChildRoomRequestDto> childRooms;

    private String imageUrl;
    private List<String> images;

    // Backward-compatibility getters/setters for legacy clients
    public String getRoomNumber() {
        return villaNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.villaNumber = roomNumber;
    }

    public Long getRoomTypeId() {
        return villaTypeId;
    }

    public void setRoomTypeId(Long roomTypeId) {
        this.villaTypeId = roomTypeId;
    }
}
```

- [ ] **Step 6: Update `VillaResponse.java`**

In `d:/booking_hotel/backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/response/VillaResponse.java`, update `ChildRoomDto` and `fromEntity` mapping:
```java
package com.phungvanlong.booking_hotel.dto.response;

import com.phungvanlong.booking_hotel.entity.Villa;
import com.phungvanlong.booking_hotel.entity.VillaImage;
import com.phungvanlong.booking_hotel.entity.VillaStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VillaResponse implements Serializable {
    private Long id;
    private String villaNumber;
    private Integer floor;
    private String structureType;
    private BigDecimal basePrice;
    private VillaStatus status;
    private Long villaTypeId;
    private String villaTypeName;
    private String zone;
    private String ozoneStatus;
    private List<String> amenities;
    private LocalDateTime lastCleanedAt;
    private String currentGuestName;
    private Integer bedroomCount;
    private Integer totalBeds;
    private Integer totalAdults;
    private Integer totalChildren;
    private Integer totalCapacity;
    private String imageUrl;
    private List<String> images;
    private List<ChildRoomDto> rooms;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChildRoomDto implements Serializable {
        private Long id;
        private String roomNumber;
        private String name;
        private String description;
        private Integer floor;
        private String status;
        private Long roomTypeId;
        private String roomTypeName;
        private String bedType;
        private Integer adults;
        private Integer children;
        private Integer capacity;
    }

    public static VillaResponse fromEntity(Villa entity) {
        if (entity == null) return null;

        List<ChildRoomDto> childRooms = new ArrayList<>();
        int calculatedAdults = 0;
        int calculatedChildren = 0;
        int calculatedCapacity = 0;

        if (entity.getRooms() != null) {
            childRooms = entity.getRooms().stream()
                    .map(r -> {
                        Long rtId = r.getRoomType() != null ? r.getRoomType().getId() : null;
                        String rtName = r.getRoomType() != null ? r.getRoomType().getName() : null;
                        String bType = r.getRoomType() != null ? r.getRoomType().getBedType() : null;
                        Integer adults = r.getRoomType() != null ? r.getRoomType().getAdults() : 2;
                        Integer children = r.getRoomType() != null ? r.getRoomType().getChildren() : 0;
                        Integer capacity = r.getRoomType() != null ? r.getRoomType().getCapacity() : 2;

                        return ChildRoomDto.builder()
                                .id(r.getId())
                                .roomNumber(r.getRoomNumber())
                                .name(r.getName())
                                .description(r.getDescription())
                                .floor(r.getFloor())
                                .status(r.getStatus() != null ? r.getStatus().name() : "AVAILABLE")
                                .roomTypeId(rtId)
                                .roomTypeName(rtName)
                                .bedType(bType)
                                .adults(adults)
                                .children(children)
                                .capacity(capacity)
                                .build();
                    })
                    .collect(Collectors.toList());

            for (ChildRoomDto cr : childRooms) {
                calculatedAdults += cr.getAdults() != null ? cr.getAdults() : 0;
                calculatedChildren += cr.getChildren() != null ? cr.getChildren() : 0;
                calculatedCapacity += cr.getCapacity() != null ? cr.getCapacity() : 0;
            }
        }

        List<String> imageUrls = new ArrayList<>();
        if (entity.getImages() != null) {
            imageUrls = entity.getImages().stream()
                    .map(VillaImage::getImageUrl)
                    .collect(Collectors.toList());
        }

        String primaryImg = null;
        if (!imageUrls.isEmpty()) {
            primaryImg = imageUrls.get(0);
        } else if (entity.getVillaType() != null) {
            primaryImg = entity.getVillaType().getImageUrl();
        }

        List<String> parsedAmenities = new ArrayList<>();
        if (entity.getAmenities() != null && !entity.getAmenities().trim().isEmpty()) {
            parsedAmenities = Arrays.stream(entity.getAmenities().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }

        int bedroomCount = entity.getBedroomCount() != null ? entity.getBedroomCount() : childRooms.size();

        return VillaResponse.builder()
                .id(entity.getId())
                .villaNumber(entity.getVillaNumber())
                .floor(entity.getFloor())
                .structureType(entity.getStructureType())
                .basePrice(entity.getBasePrice() != null ? entity.getBasePrice() : (entity.getVillaType() != null ? entity.getVillaType().getBasePrice() : BigDecimal.ZERO))
                .status(entity.getStatus())
                .villaTypeId(entity.getVillaType() != null ? entity.getVillaType().getId() : null)
                .villaTypeName(entity.getVillaType() != null ? entity.getVillaType().getName() : null)
                .zone(entity.getZone())
                .ozoneStatus(entity.getOzoneStatus())
                .amenities(parsedAmenities)
                .lastCleanedAt(entity.getLastCleanedAt())
                .currentGuestName(entity.getCurrentGuestName())
                .bedroomCount(bedroomCount)
                .totalBeds(childRooms.size())
                .totalAdults(calculatedAdults)
                .totalChildren(calculatedChildren)
                .totalCapacity(calculatedCapacity > 0 ? calculatedCapacity : (calculatedAdults + calculatedChildren))
                .imageUrl(primaryImg)
                .images(imageUrls)
                .rooms(childRooms)
                .build();
    }

    // Backward compatibility aliases for legacy Angular models
    public String getRoomNumber() {
        return villaNumber;
    }

    public Long getRoomTypeId() {
        return villaTypeId;
    }

    public String getRoomTypeName() {
        return villaTypeName;
    }
}
```

- [ ] **Step 7: Run test to verify it passes**

Run: `.\mvnw.cmd test -Dtest=VillaDtoTest`
Expected: `BUILD SUCCESS` (1 test passed).

- [ ] **Step 8: Execute SQL migration on MySQL to add missing columns to `villas`**

Run:
```powershell
& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p10092005Long -e "
  ALTER TABLE hotel_booking_db.villas 
    ADD COLUMN IF NOT EXISTS structure_type VARCHAR(50) NULL AFTER floor,
    ADD COLUMN IF NOT EXISTS base_price DECIMAL(12,2) NULL AFTER structure_type,
    ADD COLUMN IF NOT EXISTS amenities TEXT NULL AFTER ozone_status,
    ADD COLUMN IF NOT EXISTS bedroom_count INT NULL AFTER amenities;
"
```

- [ ] **Step 9: Commit**

```bash
git add backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/entity/Villa.java backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/dto/ backend/booking_hotel/src/test/java/com/phungvanlong/booking_hotel/dto/VillaDtoTest.java
git commit -m "feat: add structureType, basePrice, amenities, and bedroomCount to Villa and DTOs"
```

---

### Task 2: Backend VillaService & VillaType Auto-Classification

**Files:**
- Modify: `d:/booking_hotel/backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/service/impl/VillaServiceImpl.java`
- Modify: `d:/booking_hotel/backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/repository/VillaTypeRepository.java`
- Test: `d:/booking_hotel/backend/booking_hotel/src/test/java/com/phungvanlong/booking_hotel/service/VillaServiceRedesignTest.java`

**Interfaces:**
- Consumes: `VillaRequest`, `RoomTypeRepository`, `VillaTypeRepository`
- Produces: `VillaServiceImpl.createVilla`, `VillaServiceImpl.updateVilla` handling auto-naming and child room persistence.

- [ ] **Step 1: Write integration test for VillaService auto-naming and multi-bed creation**

Create `d:/booking_hotel/backend/booking_hotel/src/test/java/com/phungvanlong/booking_hotel/service/VillaServiceRedesignTest.java`:
```java
package com.phungvanlong.booking_hotel.service;

import com.phungvanlong.booking_hotel.dto.request.VillaBedSelectionDto;
import com.phungvanlong.booking_hotel.dto.request.VillaRequest;
import com.phungvanlong.booking_hotel.dto.response.VillaResponse;
import com.phungvanlong.booking_hotel.entity.VillaStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
public class VillaServiceRedesignTest {

    @Autowired
    private VillaService villaService;

    @Test
    void testCreateVillaWithBedSelectionsAutoNaming() {
        VillaRequest request = VillaRequest.builder()
                .villaNumber("TEST-VILLA-999")
                .floor(2)
                .structureType("2 Tầng")
                .basePrice(BigDecimal.valueOf(28000000))
                .zone("Ngọc Trai")
                .status(VillaStatus.AVAILABLE)
                .amenities(List.of("Khử trùng Ozon định kỳ", "Quản gia riêng Lead Butler 24/7", "Hồ bơi vô cực"))
                .bedroomCount(3)
                .bedSelections(List.of(
                        VillaBedSelectionDto.builder().roomTypeId(1L).quantity(1).build(), // Single Bed
                        VillaBedSelectionDto.builder().roomTypeId(3L).quantity(2).build()  // King Size Bed x2
                ))
                .build();

        VillaResponse response = villaService.createVilla(request);
        assertNotNull(response);
        assertEquals("TEST-VILLA-999", response.getVillaNumber());
        assertEquals("2 Tầng", response.getStructureType());
        assertEquals("Ngọc Trai", response.getZone());
        assertEquals("Villa 3 Phòng Ngủ", response.getVillaTypeName());
        assertEquals(3, response.getBedroomCount());
        assertEquals(3, response.getRooms().size());
        assertEquals(3, response.getAmenities().size());
        assertTrue(response.getTotalAdults() >= 4);
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `.\mvnw.cmd test -Dtest=VillaServiceRedesignTest`
Expected: FAIL (Service does not yet handle `bedSelections`, auto-classification, or `structureType`).

- [ ] **Step 3: Update `VillaTypeRepository.java` to support lookup by name**

In `d:/booking_hotel/backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/repository/VillaTypeRepository.java`, add method:
```java
    java.util.Optional<VillaType> findByName(String name);
```

- [ ] **Step 4: Update `VillaServiceImpl.java`**

Inject `RoomTypeRepository` into `VillaServiceImpl`.
In `createVilla`:
1. If `request.getBedroomCount()` > 0, determine category name: `"Villa " + request.getBedroomCount() + " Phòng Ngủ"`.
2. Find existing `VillaType` with this name via `villaTypeRepository.findByName(categoryName)`.
   If not found, auto-create a new `VillaType`:
   ```java
   VillaType autoType = VillaType.builder()
       .name(categoryName)
       .description("Biệt thự nghỉ dưỡng cao cấp " + request.getBedroomCount() + " phòng ngủ")
       .basePrice(request.getBasePrice() != null ? request.getBasePrice() : BigDecimal.valueOf(15000000))
       .capacity(request.getBedroomCount() * 2)
       .adults(request.getBedroomCount() * 2)
       .children(1)
       .bedType(request.getBedroomCount() + " Phòng Ngủ Riêng Biệt")
       .build();
   villaType = villaTypeRepository.save(autoType);
   ```
3. Set `structureType`, `basePrice`, `amenities` (joined with `, `), `bedroomCount`.
4. Create child `Room` entities:
   - If `childRooms` provided, iterate through each `ChildRoomRequestDto`, lookup `RoomType`, build `Room` and attach to `villa.getRooms()`.
   - If `bedSelections` provided:
     For each `selection`:
       Lookup `RoomType` by `selection.getRoomTypeId()`.
       For i = 1 to quantity:
         Build child `Room`:
         - `roomNumber = villa.getVillaNumber() + "-P" + roomIndex`
         - `name = "Phòng Ngủ " + roomIndex + " (" + roomType.getName() + ")"`
         - `floor = Math.min(roomIndex, villa.getFloor() != null ? villa.getFloor() : 1)`
         - `roomType = roomType`
         - `status = RoomStatus.AVAILABLE`
         - `villa = villa`
         Add to `villa.getRooms()`.
5. Save `villa` and return `VillaResponse.fromEntity(saved)`.
6. Implement matching logic in `updateVilla` to update child rooms, `structureType`, `basePrice`, `amenities`, and `bedroomCount`.

- [ ] **Step 5: Run test to verify it passes**

Run: `.\mvnw.cmd test -Dtest=VillaServiceRedesignTest`
Expected: `BUILD SUCCESS` (1 test passed).

- [ ] **Step 6: Commit**

```bash
git add backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/repository/VillaTypeRepository.java backend/booking_hotel/src/main/java/com/phungvanlong/booking_hotel/service/impl/VillaServiceImpl.java backend/booking_hotel/src/test/java/com/phungvanlong/booking_hotel/service/VillaServiceRedesignTest.java
git commit -m "feat: implement villa auto-classification and child rooms generation in VillaServiceImpl"
```

---

### Task 3: Frontend Model & Service Updates

**Files:**
- Modify: `d:/booking_hotel/frontend/src/app/core/services/admin-housekeeping.service.ts`

**Interfaces:**
- Consumes: Backend `VillaResponse` and `VillaRequest` API contract.
- Produces: `AdminRoomItem`, `CreateRoomPayload`, `ChildRoomItem` with fields `structureType`, `amenities`, `bedroomCount`, `bedSelections`, `childRooms`, `totalAdults`, `totalChildren`, `totalCapacity`, `totalBeds`.

- [ ] **Step 1: Update `AdminRoomItem` and `CreateRoomPayload` interfaces**

In `d:/booking_hotel/frontend/src/app/core/services/admin-housekeeping.service.ts`:
```typescript
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
}

export interface CreateRoomPayload {
  roomNumber: string;
  villaNumber?: string;
  floor: number;
  structureType?: string;
  roomTypeId?: number;
  villaTypeId?: number;
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
}
```

- [ ] **Step 2: Run frontend typecheck**

Run: `npx tsc --noEmit -p tsconfig.app.json`
Expected: 0 errors.

- [ ] **Step 3: Commit**

```bash
git add frontend/src/app/core/services/admin-housekeeping.service.ts
git commit -m "feat: enrich AdminRoomItem and CreateRoomPayload for multi-bed villa management"
```

---

### Task 4: Frontend Component Logic (`room-management.component.ts`)

**Files:**
- Modify: `d:/booking_hotel/frontend/src/app/admin/room-management/room-management.component.ts`

**Interfaces:**
- Consumes: `admin-housekeeping.service.ts`
- Produces: Reactive state and methods for multi-bed selector, live summary computations, auto-category naming, amenities checklist, structure selector, and updated `openCreateRoomModal`, `openEditRoomModal`, `saveRoom`.

- [ ] **Step 1: Add new interfaces and reactive state for hybrid bed selector**

In `room-management.component.ts`:
Define interface:
```typescript
export interface VillaBedSelectionItem {
  roomTypeId: number;
  name: string;
  bedDimensions: string;
  adults: number;
  children: number;
  capacity: number;
  quantity: number;
  isSelected: boolean;
}

export interface DetailedChildRoom {
  roomIndex: number;
  roomNumber: string;
  name: string;
  floor: number;
  roomTypeId: number;
}
```

Add component properties:
```typescript
  structureOptions: string[] = [
    '1 Tầng (Ground Floor)',
    '2 Tầng (2 Floors)',
    '3 Tầng (3 Floors)',
    'Duplex Thông Tầng',
    'Triplex Cao Cấp',
    'Penthouse Hoàng Gia',
  ];

  amenityOptions: string[] = [
    'Khử trùng Ozon định kỳ (Chuẩn 5*)',
    'Quản gia riêng Lead Butler 24/7',
    'Hồ bơi vô cực riêng tư',
    'Miễn phí xe đạp nội khu',
    'Bếp nấu cao cấp & minibar',
    'Đón tiễn sân bay bằng xe riêng',
    'Tiệc nướng BBQ & trà chiều sân vườn',
  ];

  selectedAmenities: string[] = [
    'Khử trùng Ozon định kỳ (Chuẩn 5*)',
    'Quản gia riêng Lead Butler 24/7',
  ];

  bedSelections: VillaBedSelectionItem[] = [];
  showAdvancedRoomAllocation = false;
  childRoomsAllocation: DetailedChildRoom[] = [];
```

Update `roomForm` object:
```typescript
  roomForm = {
    roomNumber: '',
    floor: 1,
    structureType: '1 Tầng (Ground Floor)',
    roomTypeId: null as number | null,
    zone: '',
    status: 'AVAILABLE',
    ozoneStatus: 'STERILIZED',
    basePrice: 25000000,
    imageUrl: '',
    imageName: '',
    isUploadingImage: false,
  };
```

- [ ] **Step 2: Add getters for Live Summary banner & Auto-Category Name**

```typescript
  get totalSelectedBeds(): number {
    return this.bedSelections
      .filter((b) => b.isSelected && b.quantity > 0)
      .reduce((sum, b) => sum + b.quantity, 0);
  }

  get totalSelectedBedrooms(): number {
    // If advanced child rooms configured, use its count; otherwise each bed is in its room (or min 1)
    if (this.childRoomsAllocation.length > 0) {
      return this.childRoomsAllocation.length;
    }
    return Math.max(1, this.totalSelectedBeds);
  }

  get totalAdultsCapacity(): number {
    return this.bedSelections
      .filter((b) => b.isSelected && b.quantity > 0)
      .reduce((sum, b) => sum + b.adults * b.quantity, 0);
  }

  get totalChildrenCapacity(): number {
    return this.bedSelections
      .filter((b) => b.isSelected && b.quantity > 0)
      .reduce((sum, b) => sum + b.children * b.quantity, 0);
  }

  get totalMaxGuests(): number {
    return this.totalAdultsCapacity + this.totalChildrenCapacity;
  }

  get autoVillaCategoryName(): string {
    const rooms = this.totalSelectedBedrooms;
    return `Villa ${rooms} Phòng Ngủ`;
  }
```

- [ ] **Step 3: Implement bed selector methods and allocation sync**

```typescript
  initBedSelections(): void {
    this.bedSelections = this.roomTypesList.map((rt) => ({
      roomTypeId: rt.id,
      name: rt.name,
      bedDimensions: rt.bedType || 'Rộng 1.8m × Dài 2.0m',
      adults: rt.adults !== undefined ? rt.adults : 2,
      children: rt.children !== undefined ? rt.children : 0,
      capacity: rt.capacity || 2,
      quantity: 0,
      isSelected: false,
    }));

    // Default: select the first bed type with quantity 1
    if (this.bedSelections.length > 0) {
      this.bedSelections[0].isSelected = true;
      this.bedSelections[0].quantity = 1;
    }
    this.syncChildRoomsFromBedSelections();
  }

  toggleBedSelection(item: VillaBedSelectionItem): void {
    item.isSelected = !item.isSelected;
    if (item.isSelected && item.quantity === 0) {
      item.quantity = 1;
    } else if (!item.isSelected) {
      item.quantity = 0;
    }
    this.syncChildRoomsFromBedSelections();
  }

  changeBedQuantity(item: VillaBedSelectionItem, delta: number, event?: Event): void {
    if (event) event.stopPropagation();
    const newQty = Math.max(0, item.quantity + delta);
    item.quantity = newQty;
    item.isSelected = newQty > 0;
    this.syncChildRoomsFromBedSelections();
  }

  syncChildRoomsFromBedSelections(): void {
    const list: DetailedChildRoom[] = [];
    let idx = 1;
    for (const bed of this.bedSelections) {
      if (bed.isSelected && bed.quantity > 0) {
        for (let i = 0; i < bed.quantity; i++) {
          list.push({
            roomIndex: idx,
            roomNumber: `${this.roomForm.roomNumber ? this.roomForm.roomNumber.trim() : 'Villa'}-P${idx}`,
            name: idx === 1 ? 'Phòng Ngủ Master' : `Phòng Ngủ Phụ ${idx - 1} (${bed.name})`,
            floor: Math.min(idx, this.roomForm.floor || 1),
            roomTypeId: bed.roomTypeId,
          });
          idx++;
        }
      }
    }
    this.childRoomsAllocation = list;
  }

  toggleAmenity(amenity: string): void {
    const idx = this.selectedAmenities.indexOf(amenity);
    if (idx >= 0) {
      this.selectedAmenities.splice(idx, 1);
    } else {
      this.selectedAmenities.push(amenity);
    }
  }

  isAmenitySelected(amenity: string): boolean {
    return this.selectedAmenities.includes(amenity);
  }
```

- [ ] **Step 4: Update `openCreateRoomModal`, `openEditRoomModal`, and `saveRoom`**

Update `openCreateRoomModal`:
- Reset `structureType` to `'1 Tầng (Ground Floor)'`
- Reset `selectedAmenities` to default 2 items
- Call `initBedSelections()`
- Set `showAdvancedRoomAllocation = false`

Update `openEditRoomModal(room: RoomCard)`:
- Populate `structureType` from `room.structureType`
- Populate `selectedAmenities` from `room.amenities`
- Parse `room.childRooms` to setup `bedSelections` and `childRoomsAllocation`

Update `saveRoom()`:
- Validate: `totalSelectedBeds > 0`
- Build payload with `structureType`, `amenities: this.selectedAmenities`, `bedroomCount: this.totalSelectedBedrooms`, `bedSelections`, and `childRooms: this.childRoomsAllocation`.
- Call `housekeepingService.createRoom` or `updateRoom`.

- [ ] **Step 5: Run frontend typecheck**

Run: `npx tsc --noEmit -p tsconfig.app.json`
Expected: 0 errors.

- [ ] **Step 6: Commit**

```bash
git add frontend/src/app/admin/room-management/room-management.component.ts
git commit -m "feat: add multi-bed selector and live calculation state to room-management component"
```

---

### Task 5: Frontend HTML Redesign (`room-management.component.html`)

**Files:**
- Modify: `d:/booking_hotel/frontend/src/app/admin/room-management/room-management.component.html`

**Interfaces:**
- Consumes: Reactive properties from Task 4
- Produces: Updated Modal 4 structure matching Wireframe in Design Spec 2.1 and enhanced Villa cards in Section 4.

- [ ] **Step 1: Redesign Modal 4 (Khởi Tạo / Chỉnh Sửa Biệt Thự)**

Replace lines 2060-2360 in `room-management.component.html` with:
- **Khối 1: Thông Tin Định Danh & Kiến Trúc**:
  - Mã Số / Tên Villa (*)
  - Phân Khu Nghỉ Dưỡng (*) (preserves `customZones` dropdown including `Ngọc Trai`)
  - Số Tầng / Kết Cấu (*) (dropdown `structureOptions`)
  - Đơn Giá Thuê Villa / Đêm (VNĐ) (*)
  - Trạng Thái Vận Hành (dropdown `AVAILABLE`, `OCCUPIED`, `CLEANING`, `MAINTENANCE`)
  - Hạng Biệt Thự (Tự Động) (sparkle icon ✨, `autoVillaCategoryName` disabled luxury badge)
- **Khối 2: Cấu Hình Loại Giường & Phòng Ngủ (Hybrid Multi-Bed Selector)**:
  - Live Summary Header Banner (`totalSelectedBedrooms`, `totalSelectedBeds`, `totalAdultsCapacity`, `totalChildrenCapacity`, `totalMaxGuests`)
  - Checklist table of beds with checkbox, icon, name, dimensions, adults & children info, and `[-] [ qty ] [+]` stepper
  - Collapsible button: `[⌄ Phân bổ chi tiết vào từng phòng ngủ con (Tùy chọn nâng cao)]`
  - Child room assignment cards list when expanded
- **Khối 3: Dịch Vụ Đi Kèm & Tiện Ích Mở Rộng**:
  - Grid 2 columns of interactive amenity pill-checkboxes (`amenityOptions`, `toggleAmenity(amenity)`)
- **Khối 4: Hình Ảnh Biệt Thự / Homestay**:
  - Keep folder upload button, preview thumbnail, error-safe preview, remove button
  - Keep resort presets: Biển Đông, Hoàng Hôn, Vách Đá, Dinh Thự VIP
- Footer: Hủy Bỏ, Xác Nhận Lưu

- [ ] **Step 2: Update Villa Card in Section 4 to show bedroom count, structure, and amenities**

In Section 4 (Room Grid and Table), enrich the villa cards:
- Show category badge: `{{ room.category }}`
- Show structure badge: `{{ room.structureType || (room.floor ? room.floor + ' Tầng' : '1 Tầng') }}`
- Show bedroom & bed count: `{{ room.bedroomCount || room.childRooms?.length || 1 }} PN • {{ room.totalAdults || 2 }} NL`
- Show amenity tags: Ozon, Butler, Private Pool (if in `room.amenities`)

- [ ] **Step 3: Format and check template syntax**

Run:
`npx prettier --write src/app/admin/room-management/room-management.component.html`
`npm run build`
Expected: Successful build with 0 template errors.

- [ ] **Step 4: Commit**

```bash
git add frontend/src/app/admin/room-management/room-management.component.html
git commit -m "feat: redesign villa modal with hybrid bed selector, structure options, and amenities checklist"
```

---

### Task 6: Verification & End-to-End Testing

**Files:**
- Test via browser & API

- [ ] **Step 1: Run complete backend test suite**

Run: `.\mvnw.cmd test`
Expected: `BUILD SUCCESS`.

- [ ] **Step 2: Run frontend build**

Run: `npm run build`
Expected: Build successfully created without warnings or errors.

- [ ] **Step 3: Perform End-to-End creation test**

1. Create a Villa via UI or API:
   - Name: `Villa #101`
   - Phân khu: `Ngọc Trai`
   - Số Tầng / Kết Cấu: `2 Tầng (2 Floors)`
   - Đơn Giá: `25,000,000`
   - Giường: `1x King Size Bed` + `1x Queen Size Bed` + `2x Single Bed`
   - Tiện ích: `Khử trùng Ozon`, `Quản gia Butler 24/7`, `Hồ bơi vô cực`
2. Verify:
   - Live summary shows 3 bedrooms, 4 beds, 5 adults, 1 child, 6 guests.
   - Hạng Villa shows `Villa 3 Phòng Ngủ`.
   - Card displays accurately in Section 4.
   - Data persists cleanly in MySQL `villas` and `rooms` tables.

- [ ] **Step 4: Update walkthrough artifact**

Document completed changes and screenshots in `walkthrough.md`.
