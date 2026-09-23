package com.phungvanlong.booking_hotel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "villas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Villa extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "villa_number", nullable = false, unique = true, length = 30)
    private String villaNumber; // Ví dụ: "Villa #801", "Pine-01", "V001"

    private Integer floor;

    @Column(name = "structure_type", length = 50)
    private String structureType; // Ví dụ: "1 Tầng", "2 Tầng", "Duplex Thông Tầng", "Penthouse"

    @Column(name = "base_price", precision = 12, scale = 2)
    private BigDecimal basePrice;

    @Column(length = 50)
    private String zone; // Ví dụ: "Khu A - Biển Đông", "Khu B - Đầm Hoàng Hôn"

    @Column(name = "ozone_status", length = 30)
    @Builder.Default
    private String ozoneStatus = "EXPIRED"; // STERILIZED, RUNNING, EXPIRED

    @Column(columnDefinition = "TEXT")
    private String amenities; // Danh sách tiện ích phân tách dấu phẩy hoặc JSON

    @Column(name = "bedroom_count")
    private Integer bedroomCount;

    @Column(name = "last_cleaned_at")
    private LocalDateTime lastCleanedAt;

    @Column(name = "current_guest_name", length = 100)
    private String currentGuestName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private VillaStatus status = VillaStatus.AVAILABLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "villa_type_id", nullable = false)
    private VillaType villaType;

    // 1 Villa có nhiều phòng ngủ con (Master Bedroom, Bedroom 2...)
    @OneToMany(mappedBy = "villa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Room> rooms = new ArrayList<>();

    // Bộ sưu tập ảnh của Villa
    @OneToMany(mappedBy = "villa", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<VillaImage> images = new ArrayList<>();
}
