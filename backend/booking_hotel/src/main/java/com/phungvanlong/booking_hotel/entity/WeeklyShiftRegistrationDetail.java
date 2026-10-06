package com.phungvanlong.booking_hotel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "weekly_shift_registration_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyShiftRegistrationDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registration_id", nullable = false)
    @JsonIgnore
    private WeeklyShiftRegistration registration;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Column(name = "day_of_week", length = 20)
    private String dayOfWeek; // T2, T3, T4, T5, T6, T7, CN

    @Column(name = "shift_type", nullable = false, length = 30)
    private String shiftType; // MORNING, AFTERNOON, NIGHT, OFF

    @Column(length = 255)
    private String note;
}
