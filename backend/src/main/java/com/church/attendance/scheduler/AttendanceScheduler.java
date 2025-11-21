package com.church.attendance.scheduler;

import com.church.attendance.entity.Service;
import com.church.attendance.repository.ServiceRepository;
import com.church.attendance.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 출석 체크 자동화 스케줄러
 * 예배 시작 후 10분 경과 시 미출석 사용자를 자동으로 지각 처리
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AttendanceScheduler {
    
    private final ServiceRepository serviceRepository;
    private final AttendanceService attendanceService;
    
    /**
     * 매 1분마다 실행
     * 예배 시작 후 10분 경과한 예배를 찾아 미출석 사용자를 자동 지각 처리
     */
    @Scheduled(fixedRate = 60000) // 1분 = 60000ms
    public void processAutoLateAttendance() {
        try {
            LocalDateTime now = LocalDateTime.now();
            
            // 예배 시작 후 10분이 지난 예배 찾기
            // 예배 시작 시간 + 10분 <= 현재 시간
            // 예배 시작 시간 + 11분 > 현재 시간
            // (1분 간격으로 체크하므로 중복 처리 방지)
            LocalDateTime beforeTime = now.minusMinutes(10); // 예배 시작 시간이 이 시간 이하여야 함
            LocalDateTime afterTime = now.minusMinutes(11);  // 예배 시작 시간이 이 시간 이후여야 함
            
            List<Service> servicesToProcess = serviceRepository.findServicesForAutoLateProcessing(
                    beforeTime, 
                    afterTime
            );
            
            if (servicesToProcess.isEmpty()) {
                return; // 처리할 예배가 없음
            }
            
            for (Service service : servicesToProcess) {
                int processedCount = attendanceService.processAutoLateAttendance(service);
                
                if (processedCount > 0) {
                    log.info("예배 [{}] (시작: {}) 자동 지각 처리 완료: {}명", 
                            service.getName(), 
                            service.getServiceTime(),
                            processedCount);
                }
            }
        } catch (Exception e) {
            log.error("자동 지각 처리 중 오류 발생", e);
        }
    }
}

