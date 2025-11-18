package com.church.attendance.dto;

import com.church.attendance.entity.Service;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponse {
    private Long id;
    private String name;
    private LocalDateTime serviceTime;
    private String type;
    private Boolean active;
    private Boolean canCheckAttendance;
    
    public static ServiceResponse from(Service service, Boolean canCheckAttendance) {
        return ServiceResponse.builder()
                .id(service.getId())
                .name(service.getName())
                .serviceTime(service.getServiceTime())
                .type(service.getType().name())
                .active(service.getActive())
                .canCheckAttendance(canCheckAttendance)
                .build();
    }
}


