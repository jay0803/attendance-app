package com.church.attendance.controller;

import com.church.attendance.dto.ServiceResponse;
import com.church.attendance.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {
    
    private final ServiceService serviceService;
    
    /**
     * 활성화된 예배 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<ServiceResponse>> getActiveServices() {
        List<ServiceResponse> services = serviceService.getActiveServices();
        return ResponseEntity.ok(services);
    }
    
    /**
     * 다가오는 다음 예배 조회
     */
    @GetMapping("/next")
    public ResponseEntity<ServiceResponse> getNextService() {
        ServiceResponse service = serviceService.getNextService();
        return ResponseEntity.ok(service);
    }
    
    /**
     * 모든 예배 조회 (관리자)
     */
    @GetMapping("/all")
    public ResponseEntity<List<ServiceResponse>> getAllServices() {
        List<ServiceResponse> services = serviceService.getAllServices();
        return ResponseEntity.ok(services);
    }
}

