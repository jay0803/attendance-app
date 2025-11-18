package com.church.attendance.service;

import com.church.attendance.dto.ServiceResponse;
import com.church.attendance.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceService {
    
    private final ServiceRepository serviceRepository;
    
    @Value("${attendance.activation-minutes-before}")
    private int activationMinutesBefore;
    
    @Transactional(readOnly = true)
    public List<ServiceResponse> getActiveServices() {
        LocalDateTime now = LocalDateTime.now();
        
        return serviceRepository.findByActiveTrue().stream()
                .map(service -> {
                    LocalDateTime activationTime = service.getServiceTime().minusMinutes(activationMinutesBefore);
                    boolean canCheck = now.isAfter(activationTime) || now.isEqual(activationTime);
                    return ServiceResponse.from(service, canCheck);
                })
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public ServiceResponse getNextService() {
        LocalDateTime now = LocalDateTime.now();
        com.church.attendance.entity.Service service = serviceRepository.findNextUpcomingService(now)
                .orElseThrow(() -> new RuntimeException("다가오는 예배가 없습니다"));
        
        LocalDateTime activationTime = service.getServiceTime().minusMinutes(activationMinutesBefore);
        boolean canCheck = now.isAfter(activationTime) || now.isEqual(activationTime);
        
        return ServiceResponse.from(service, canCheck);
    }
    
    @Transactional(readOnly = true)
    public List<ServiceResponse> getAllServices() {
        LocalDateTime now = LocalDateTime.now();
        
        return serviceRepository.findAll().stream()
                .map(service -> {
                    LocalDateTime activationTime = service.getServiceTime().minusMinutes(activationMinutesBefore);
                    boolean canCheck = now.isAfter(activationTime) || now.isEqual(activationTime);
                    return ServiceResponse.from(service, canCheck);
                })
                .collect(Collectors.toList());
    }
}


