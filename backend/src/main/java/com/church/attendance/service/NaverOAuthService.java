package com.church.attendance.service;

import com.church.attendance.dto.NaverUserInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * 네이버 OAuth2 서비스
 * 네이버 API를 통해 사용자 정보를 조회
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NaverOAuthService {
    
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;
    
    private static final String NAVER_USER_INFO_URL = "https://openapi.naver.com/v1/nid/me";
    
    /**
     * 네이버 액세스 토큰으로 사용자 정보 조회
     * 
     * @param accessToken 네이버 액세스 토큰
     * @return 네이버 사용자 정보
     */
    public NaverUserInfo getUserInfo(String accessToken) {
        try {
            WebClient webClient = webClientBuilder
                    .baseUrl(NAVER_USER_INFO_URL)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();
            
            Mono<String> responseMono = webClient.get()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(String.class);
            
            String responseBody = responseMono.block();
            
            if (responseBody == null) {
                throw new RuntimeException("네이버 API 응답이 없습니다");
            }
            
            NaverUserInfo userInfo = objectMapper.readValue(responseBody, NaverUserInfo.class);
            
            if (!"00".equals(userInfo.getResultCode())) {
                throw new RuntimeException("네이버 사용자 정보 조회 실패: " + userInfo.getMessage());
            }
            
            return userInfo;
        } catch (Exception e) {
            log.error("네이버 사용자 정보 조회 중 오류 발생", e);
            if (e instanceof RuntimeException) {
                throw e;
            }
            throw new RuntimeException("네이버 사용자 정보 조회 실패: " + e.getMessage());
        }
    }
}

