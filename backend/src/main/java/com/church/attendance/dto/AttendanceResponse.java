package com.church.attendance.dto;

import com.church.attendance.entity.Attendance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponse {
    private Long id;
    private Long userId;
    private String userName;
    private Long serviceId;
    private String serviceName;
    private String status;
    private Double latitude;
    private Double longitude;
    private Double distance;
    private LocalDateTime checkedAt;
    private String notes;
    
    public static AttendanceResponse from(Attendance attendance) {
        return AttendanceResponse.builder()
                .id(attendance.getId())
                .userId(attendance.getUser().getId())
                .userName(attendance.getUser().getName())
                .serviceId(attendance.getService().getId())
                .serviceName(attendance.getService().getName())
                .status(attendance.getStatus().name())
                .latitude(attendance.getLatitude())
                .longitude(attendance.getLongitude())
                .distance(attendance.getDistance())
                .checkedAt(attendance.getCheckedAt())
                .notes(attendance.getNotes())
                .build();
    }
}


