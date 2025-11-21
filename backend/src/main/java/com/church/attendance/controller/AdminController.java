package com.church.attendance.controller;

import com.church.attendance.dto.PendingUserRequest;
import com.church.attendance.dto.PendingUserResponse;
import com.church.attendance.service.PendingUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    private final PendingUserService pendingUserService;
    
    /**
     * 사전 등록 추가
     */
    @PostMapping("/pending-users")
    public ResponseEntity<PendingUserResponse> createPendingUser(
            @Valid @RequestBody PendingUserRequest request) {
        PendingUserResponse response = pendingUserService.createPendingUser(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 사전 등록 목록 조회
     */
    @GetMapping("/pending-users")
    public ResponseEntity<List<PendingUserResponse>> getAllPendingUsers() {
        List<PendingUserResponse> pendingUsers = pendingUserService.getAllPendingUsers();
        return ResponseEntity.ok(pendingUsers);
    }
    
    /**
     * 사전 등록 삭제
     */
    @DeleteMapping("/pending-users/{id}")
    public ResponseEntity<Void> deletePendingUser(@PathVariable Long id) {
        pendingUserService.deletePendingUser(id);
        return ResponseEntity.noContent().build();
    }
}

