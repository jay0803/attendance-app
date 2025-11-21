package com.church.attendance.service;

import com.church.attendance.dto.AuthResponse;
import com.church.attendance.dto.LoginRequest;
import com.church.attendance.dto.NaverLoginRequest;
import com.church.attendance.dto.NaverUserInfo;
import com.church.attendance.dto.RegisterRequest;
import com.church.attendance.entity.PendingUser;
import com.church.attendance.entity.User;
import com.church.attendance.repository.UserRepository;
import com.church.attendance.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final PendingUserService pendingUserService;
    private final NaverOAuthService naverOAuthService;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("사용자명이 이미 존재합니다");
        }
        
        // 전화번호 또는 이메일 중 하나는 필수
        if ((request.getPhone() == null || request.getPhone().trim().isEmpty()) &&
            (request.getEmail() == null || request.getEmail().trim().isEmpty())) {
            throw new RuntimeException("전화번호 또는 이메일 중 하나는 필수입니다");
        }
        
        // 사전 등록 확인
        PendingUser pendingUser = pendingUserService.validateAndGetPendingUser(
                request.getPhone(), 
                request.getEmail()
        );
        
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .role(User.Role.USER)
                .active(true)
                .build();
        
        user = userRepository.save(user);
        
        // 사전 등록 사용자 비활성화
        pendingUserService.deactivatePendingUser(pendingUser);
        
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        
        String token = tokenProvider.generateToken(authentication);
        
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .role(user.getRole().name())
                .build();
    }
    
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String token = tokenProvider.generateToken(authentication);
        
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .role(user.getRole().name())
                .build();
    }
    
    /**
     * 네이버 로그인
     */
    @Transactional
    public AuthResponse naverLogin(NaverLoginRequest request) {
        // 네이버 사용자 정보 조회
        NaverUserInfo naverUserInfo = naverOAuthService.getUserInfo(request.getAccessToken());
        NaverUserInfo.Response naverUser = naverUserInfo.getResponse();
        
        if (naverUser == null || naverUser.getId() == null) {
            throw new RuntimeException("네이버 사용자 정보를 가져올 수 없습니다");
        }
        
        // 네이버 ID로 기존 사용자 찾기
        User user = userRepository.findByNaverId(naverUser.getId())
                .orElse(null);
        
        if (user != null) {
            // 기존 사용자 로그인
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user.getUsername(), 
                    null,
                    java.util.Collections.emptyList()
            );
            
            String token = tokenProvider.generateToken(authentication);
            
            return AuthResponse.builder()
                    .token(token)
                    .userId(user.getId())
                    .username(user.getUsername())
                    .name(user.getName())
                    .role(user.getRole().name())
                    .build();
        }
        
        // 신규 사용자: 사전 등록 확인
        // 전화번호 형식 정리 (하이픈 제거)
        String phone = null;
        if (naverUser.getMobile() != null && !naverUser.getMobile().isEmpty()) {
            phone = naverUser.getMobile().replaceAll("[^0-9]", "");
        } else if (naverUser.getMobileE164() != null && !naverUser.getMobileE164().isEmpty()) {
            phone = naverUser.getMobileE164().replaceAll("[^0-9]", "");
        }
        String email = naverUser.getEmail();
        
        if ((phone == null || phone.isEmpty()) && (email == null || email.isEmpty())) {
            throw new RuntimeException("네이버 계정에 전화번호 또는 이메일이 등록되어 있지 않습니다");
        }
        
        // 사전 등록 확인
        PendingUser pendingUser = pendingUserService.validateAndGetPendingUser(phone, email);
        
        // 신규 사용자 생성
        String username = "naver_" + naverUser.getId();
        
        // username 중복 체크 및 처리
        int suffix = 1;
        String originalUsername = username;
        while (userRepository.existsByUsername(username)) {
            username = originalUsername + "_" + suffix;
            suffix++;
        }
        
        user = User.builder()
                .username(username)
                .password(passwordEncoder.encode("naver_" + naverUser.getId() + System.currentTimeMillis())) // 랜덤 비밀번호
                .name(naverUser.getName() != null ? naverUser.getName() : "사용자")
                .phone(phone)
                .email(email)
                .naverId(naverUser.getId())
                .role(User.Role.USER)
                .active(true)
                .build();
        
        user = userRepository.save(user);
        
        // 사전 등록 사용자 비활성화
        pendingUserService.deactivatePendingUser(pendingUser);
        
        // 인증 토큰 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                null,
                java.util.Collections.emptyList()
        );
        
        String token = tokenProvider.generateToken(authentication);
        
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .role(user.getRole().name())
                .build();
    }
}


