package com.church.attendance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NaverUserInfo {
    
    @JsonProperty("resultcode")
    private String resultCode;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("response")
    private Response response;
    
    @Data
    public static class Response {
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("email")
        private String email;
        
        @JsonProperty("name")
        private String name;
        
        @JsonProperty("mobile")
        private String mobile;
        
        @JsonProperty("mobile_e164")
        private String mobileE164;
    }
}

