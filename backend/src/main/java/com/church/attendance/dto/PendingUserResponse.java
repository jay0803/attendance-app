package com.church.attendance.dto;

import com.church.attendance.entity.PendingUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingUserResponse {
    
    private Long id;
    private String name;
    private String phone;
    private String email;
    private String notes;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static PendingUserResponse from(PendingUser pendingUser) {
        return PendingUserResponse.builder()
                .id(pendingUser.getId())
                .name(pendingUser.getName())
                .phone(pendingUser.getPhone())
                .email(pendingUser.getEmail())
                .notes(pendingUser.getNotes())
                .active(pendingUser.getActive())
                .createdAt(pendingUser.getCreatedAt())
                .updatedAt(pendingUser.getUpdatedAt())
                .build();
    }
}

