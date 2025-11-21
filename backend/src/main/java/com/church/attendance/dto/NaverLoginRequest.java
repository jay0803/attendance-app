package com.church.attendance.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NaverLoginRequest {
    
    @NotBlank(message = "네이버 액세스 토큰은 필수입니다")
    private String accessToken;
}

