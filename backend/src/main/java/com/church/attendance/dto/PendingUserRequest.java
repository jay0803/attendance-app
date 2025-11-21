package com.church.attendance.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PendingUserRequest {
    
    @NotBlank(message = "이름은 필수입니다")
    private String name;
    
    private String phone;
    
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;
    
    private String notes;  // 비고
}

