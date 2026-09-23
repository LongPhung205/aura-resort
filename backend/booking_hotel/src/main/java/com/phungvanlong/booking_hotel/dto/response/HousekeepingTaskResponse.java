package com.phungvanlong.booking_hotel.dto.response;

import com.phungvanlong.booking_hotel.entity.HousekeepingTask;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HousekeepingTaskResponse {
    private Long id;
    
    // Villa info
    private Long villaId;
    private String villaNumber;
    private String villaTypeName;
    private String villaZone;

    // Room info (phòng ngủ con nếu task gán riêng cho phòng con)
    private Long roomId;
    private String roomNumber;
    private String roomName;
    private String roomTypeName;

    private Long housekeeperId;
    private String housekeeperEmail;
    private String housekeeperPhone;
    private String housekeeperName;
    private String supervisorName;
    private String taskType;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime ozoneStartedAt;
    private LocalDateTime ozoneEndedAt;
    private String checklistJson;
    private String evidencePhotoUrl;
    private LocalDateTime completedAt;
    private String cleaningNote;
    private String supervisorNote;

    public static HousekeepingTaskResponse fromEntity(HousekeepingTask task) {
        Long vId = null;
        String vNum = null;
        String vType = null;
        String vZone = null;

        if (task.getVilla() != null) {
            vId = task.getVilla().getId();
            vNum = task.getVilla().getVillaNumber();
            vType = task.getVilla().getVillaType() != null ? task.getVilla().getVillaType().getName() : null;
            vZone = task.getVilla().getZone();
        } else if (task.getRoom() != null && task.getRoom().getVilla() != null) {
            vId = task.getRoom().getVilla().getId();
            vNum = task.getRoom().getVilla().getVillaNumber();
            vType = task.getRoom().getVilla().getVillaType() != null ? task.getRoom().getVilla().getVillaType().getName() : null;
            vZone = task.getRoom().getVilla().getZone();
        }

        Long rId = task.getRoom() != null ? task.getRoom().getId() : null;
        String rNum = task.getRoom() != null ? task.getRoom().getRoomNumber() : vNum;
        String rName = task.getRoom() != null ? task.getRoom().getName() : null;
        String rType = task.getRoom() != null && task.getRoom().getRoomType() != null ? task.getRoom().getRoomType().getName() : vType;

        return HousekeepingTaskResponse.builder()
                .id(task.getId())
                .villaId(vId)
                .villaNumber(vNum)
                .villaTypeName(vType)
                .villaZone(vZone)
                .roomId(rId)
                .roomNumber(rNum)
                .roomName(rName)
                .roomTypeName(rType)
                .housekeeperId(task.getHousekeeper() != null ? task.getHousekeeper().getId() : null)
                .housekeeperEmail(task.getHousekeeper() != null ? task.getHousekeeper().getEmail() : null)
                .housekeeperPhone(task.getHousekeeper() != null ? task.getHousekeeper().getPhone() : null)
                .housekeeperName(task.getHousekeeper() != null ? task.getHousekeeper().getFullName() : null)
                .supervisorName(task.getSupervisor() != null ? task.getSupervisor().getFullName() : null)
                .taskType(task.getTaskType())
                .status(task.getStatus())
                .startedAt(task.getStartedAt() != null ? task.getStartedAt() : task.getOzoneStartedAt())
                .ozoneStartedAt(task.getOzoneStartedAt())
                .ozoneEndedAt(task.getOzoneEndedAt())
                .checklistJson(task.getChecklistJson())
                .evidencePhotoUrl(task.getEvidencePhotoUrl())
                .completedAt(task.getCompletedAt())
                .cleaningNote(task.getCleaningNote())
                .supervisorNote(task.getSupervisorNote())
                .build();
    }
}
