package com.church.attendance.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AttendanceCheckRequest {
    @NotNull(message = "예배 ID는 필수입니다")
    private Long serviceId;
    
    @NotNull(message = "위도는 필수입니다")
    private Double latitude;
    
    @NotNull(message = "경도는 필수입니다")
    private Double longitude;
}


