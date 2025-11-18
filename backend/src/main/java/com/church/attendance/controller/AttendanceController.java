package com.church.attendance.controller;

import com.church.attendance.dto.AttendanceCheckRequest;
import com.church.attendance.dto.AttendanceResponse;
import com.church.attendance.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    
    private final AttendanceService attendanceService;
    
    /**
     * 출석 체크
     */
    @PostMapping("/check")
    public ResponseEntity<AttendanceResponse> checkAttendance(@Valid @RequestBody AttendanceCheckRequest request) {
        AttendanceResponse response = attendanceService.checkAttendance(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 내 출석 기록 조회
     */
    @GetMapping("/my")
    public ResponseEntity<List<AttendanceResponse>> getMyAttendances() {
        List<AttendanceResponse> attendances = attendanceService.getMyAttendances();
        return ResponseEntity.ok(attendances);
    }
    
    /**
     * 특정 예배의 출석 기록 조회 (관리자)
     */
    @GetMapping("/service/{serviceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AttendanceResponse>> getAttendancesByService(@PathVariable Long serviceId) {
        List<AttendanceResponse> attendances = attendanceService.getAttendancesByService(serviceId);
        return ResponseEntity.ok(attendances);
    }
    
    /**
     * 모든 출석 기록 조회 (관리자)
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AttendanceResponse>> getAllAttendances() {
        List<AttendanceResponse> attendances = attendanceService.getAllAttendances();
        return ResponseEntity.ok(attendances);
    }
}

