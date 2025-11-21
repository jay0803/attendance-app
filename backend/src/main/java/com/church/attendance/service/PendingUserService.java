package com.church.attendance.service;

import com.church.attendance.dto.PendingUserRequest;
import com.church.attendance.dto.PendingUserResponse;
import com.church.attendance.entity.PendingUser;
import com.church.attendance.repository.PendingUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PendingUserService {
    
    private final PendingUserRepository pendingUserRepository;
    
    /**
     * 사전 등록 추가
     */
    @Transactional
    public PendingUserResponse createPendingUser(PendingUserRequest request) {
        // 전화번호 또는 이메일 중복 확인
        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            pendingUserRepository.findByPhoneAndActiveTrue(request.getPhone())
                    .ifPresent(pendingUser -> {
                        throw new RuntimeException("이미 등록된 전화번호입니다");
                    });
        }
        
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            pendingUserRepository.findByEmailAndActiveTrue(request.getEmail())
                    .ifPresent(pendingUser -> {
                        throw new RuntimeException("이미 등록된 이메일입니다");
                    });
        }
        
        PendingUser pendingUser = PendingUser.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .notes(request.getNotes())
                .active(true)
                .build();
        
        pendingUser = pendingUserRepository.save(pendingUser);
        
        return PendingUserResponse.from(pendingUser);
    }
    
    /**
     * 사전 등록 목록 조회
     */
    @Transactional(readOnly = true)
    public List<PendingUserResponse> getAllPendingUsers() {
        return pendingUserRepository.findByActiveTrueOrderByCreatedAtDesc().stream()
                .map(PendingUserResponse::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 사전 등록 삭제 (비활성화)
     */
    @Transactional
    public void deletePendingUser(Long id) {
        PendingUser pendingUser = pendingUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사전 등록된 사용자를 찾을 수 없습니다"));
        
        pendingUser.setActive(false);
        pendingUserRepository.save(pendingUser);
    }
    
    /**
     * 전화번호로 사전 등록 확인
     */
    @Transactional(readOnly = true)
    public boolean existsByPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        return pendingUserRepository.findByPhoneAndActiveTrue(phone).isPresent();
    }
    
    /**
     * 이메일로 사전 등록 확인
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return pendingUserRepository.findByEmailAndActiveTrue(email).isPresent();
    }
    
    /**
     * 전화번호 또는 이메일로 사전 등록 확인 및 조회
     */
    @Transactional
    public PendingUser validateAndGetPendingUser(String phone, String email) {
        PendingUser pendingUser = null;
        
        if (phone != null && !phone.trim().isEmpty()) {
            pendingUser = pendingUserRepository.findByPhoneAndActiveTrue(phone.trim())
                    .orElse(null);
        }
        
        if (pendingUser == null && email != null && !email.trim().isEmpty()) {
            pendingUser = pendingUserRepository.findByEmailAndActiveTrue(email.trim())
                    .orElse(null);
        }
        
        if (pendingUser == null) {
            String identifier = "";
            if (phone != null && !phone.trim().isEmpty()) {
                identifier = "전화번호: " + phone;
            } else if (email != null && !email.trim().isEmpty()) {
                identifier = "이메일: " + email;
            }
            throw new RuntimeException("사전 등록되지 않은 사용자입니다. 관리자에게 문의하세요. (" + identifier + ")");
        }
        
        return pendingUser;
    }
    
    /**
     * 사전 등록 사용자 비활성화 (회원가입 완료 시 호출)
     */
    @Transactional
    public void deactivatePendingUser(PendingUser pendingUser) {
        pendingUser.setActive(false);
        pendingUserRepository.save(pendingUser);
    }
}

