package com.church.attendance.scheduler;

import com.church.attendance.entity.Service;
import com.church.attendance.service.ServiceService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 예배 자동 생성 스케줄러
 * 매주 일요일 오후 2시에 예배를 자동으로 생성
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ServiceScheduler {
    
    private final ServiceService serviceService;
    
    private static final int SERVICE_HOUR = 14; // 오후 2시
    private static final int SERVICE_MINUTE = 0;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * 애플리케이션 시작 시 실행
     * 다음 일요일 오후 2시 예배가 없으면 미리 생성
     */
    @PostConstruct
    public void createNextSundayServiceOnStartup() {
        try {
            createNextSundayService();
        } catch (Exception e) {
            log.error("애플리케이션 시작 시 다음 주일 예배 생성 중 오류 발생", e);
        }
    }
    
    /**
     * 매일 오전 0시에 실행
     * 오늘이 일요일이고 오후 2시 예배가 없으면 생성
     */
    @Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행
    public void createWeeklyService() {
        try {
            LocalDateTime now = LocalDateTime.now();
            
            // 오늘이 일요일인지 확인
            if (now.getDayOfWeek() != DayOfWeek.SUNDAY) {
                return; // 일요일이 아니면 종료
            }
            
            // 오늘 오후 2시 시간 계산
            LocalDateTime serviceTime = now.toLocalDate()
                    .atTime(SERVICE_HOUR, SERVICE_MINUTE);
            
            // 이미 오늘 예배가 있는지 확인
            if (serviceService.existsServiceOnDate(serviceTime)) {
                log.debug("오늘({}) 예배가 이미 존재합니다. 건너뜁니다.", 
                        serviceTime.format(DATE_FORMATTER));
                return; // 이미 예배가 있으면 생성하지 않음
            }
            
            // 예배 생성
            String serviceName = String.format("주일 예배 (%s)", 
                    serviceTime.format(DATE_FORMATTER));
            
            Service service = serviceService.createService(
                    serviceName,
                    serviceTime,
                    Service.ServiceType.SUNDAY
            );
            
            log.info("주일 예배 자동 생성 완료: {} (시작 시간: {})", 
                    service.getName(), service.getServiceTime());
                    
        } catch (Exception e) {
            log.error("주일 예배 자동 생성 중 오류 발생", e);
        }
    }
    
    /**
     * 다음 일요일 오후 2시 예배가 없으면 미리 생성
     */
    private void createNextSundayService() {
        LocalDateTime now = LocalDateTime.now();
        
        // 다음 일요일 찾기
        int daysUntilSunday = DayOfWeek.SUNDAY.getValue() - now.getDayOfWeek().getValue();
        
        if (daysUntilSunday <= 0) {
            // 오늘이 일요일이거나 지났으면 다음 주 일요일
            daysUntilSunday += 7;
        }
        
        LocalDateTime nextSunday = now.plusDays(daysUntilSunday);
        
        // 다음 일요일 오후 2시 시간 계산
        LocalDateTime serviceTime = nextSunday.toLocalDate()
                .atTime(SERVICE_HOUR, SERVICE_MINUTE);
        
        // 이미 해당 날짜에 예배가 있는지 확인
        if (serviceService.existsServiceOnDate(serviceTime)) {
            log.debug("다음 일요일({}) 예배가 이미 존재합니다. 건너뜁니다.", 
                    serviceTime.format(DATE_FORMATTER));
            return;
        }
        
        // 예배 생성
        String serviceName = String.format("주일 예배 (%s)", 
                serviceTime.format(DATE_FORMATTER));
        
        Service service = serviceService.createService(
                serviceName,
                serviceTime,
                Service.ServiceType.SUNDAY
        );
        
        log.info("다음 주일 예배 미리 생성 완료: {} (시작 시간: {})", 
                service.getName(), service.getServiceTime());
    }
}

