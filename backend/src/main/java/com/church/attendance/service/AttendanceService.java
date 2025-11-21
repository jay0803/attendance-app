package com.church.attendance.service;

import com.church.attendance.dto.AttendanceCheckRequest;
import com.church.attendance.dto.AttendanceResponse;
import com.church.attendance.entity.Attendance;
import com.church.attendance.entity.Service;
import com.church.attendance.entity.User;
import com.church.attendance.repository.AttendanceRepository;
import com.church.attendance.repository.ServiceRepository;
import com.church.attendance.repository.UserRepository;
import com.church.attendance.util.LocationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class AttendanceService {
    
    private final AttendanceRepository attendanceRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    
    @Value("${church.location.latitude}")
    private double churchLatitude;
    
    @Value("${church.location.longitude}")
    private double churchLongitude;
    
    @Value("${church.location.radius}")
    private double churchRadius;
    
    @Value("${attendance.activation-minutes-before}")
    private int activationMinutesBefore;
    
    @Value("${attendance.late-grace-minutes}")
    private int lateGraceMinutes;
    
    @Transactional
    public AttendanceResponse checkAttendance(AttendanceCheckRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        
        Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new RuntimeException("예배를 찾을 수 없습니다"));
        
        // 이미 출석 체크했는지 확인
        if (attendanceRepository.existsByUserAndService(user, service)) {
            throw new RuntimeException("이미 출석 체크를 완료했습니다");
        }
        
        // 출석 체크 가능 시간인지 확인
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime activationTime = service.getServiceTime().minusMinutes(activationMinutesBefore);
        
        if (now.isBefore(activationTime)) {
            throw new RuntimeException("아직 출석 체크 시간이 아닙니다");
        }
        
        // 거리 계산
        double distance = LocationUtil.calculateDistance(
            churchLatitude, churchLongitude,
            request.getLatitude(), request.getLongitude()
        );
        
        // 반경 체크
        if (distance > churchRadius) {
            throw new RuntimeException(String.format("교회 반경(%.0fm) 밖입니다. 현재 거리: %.0fm", churchRadius, distance));
        }
        
        // 출석 상태 결정 (정상/지각)
        Attendance.AttendanceStatus status;
        LocalDateTime lateThreshold = service.getServiceTime().plusMinutes(lateGraceMinutes);
        
        if (now.isAfter(lateThreshold)) {
            status = Attendance.AttendanceStatus.LATE;
        } else {
            status = Attendance.AttendanceStatus.PRESENT;
        }
        
        // 출석 기록 저장
        Attendance attendance = Attendance.builder()
                .user(user)
                .service(service)
                .status(status)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .distance(distance)
                .build();
        
        attendance = attendanceRepository.save(attendance);
        
        return AttendanceResponse.from(attendance);
    }
    
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getMyAttendances() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        
        return attendanceRepository.findByUser(user).stream()
                .map(AttendanceResponse::from)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getAttendancesByService(Long serviceId) {
        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("예배를 찾을 수 없습니다"));
        
        return attendanceRepository.findByServiceOrderByCheckedAtDesc(service).stream()
                .map(AttendanceResponse::from)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getAllAttendances() {
        return attendanceRepository.findAll().stream()
                .map(AttendanceResponse::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 예배 시작 후 10분 경과 시 미출석 사용자 자동 지각 처리
     * 
     * @param service 처리할 예배
     * @return 자동 지각 처리된 사용자 수
     */
    @Transactional
    public int processAutoLateAttendance(Service service) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lateThreshold = service.getServiceTime().plusMinutes(lateGraceMinutes);
        
        // 예배 시작 후 10분이 지났는지 확인
        if (now.isBefore(lateThreshold)) {
            return 0; // 아직 10분이 지나지 않음
        }
        
        // 예배 시작 후 11분이 지났다면 이미 처리된 것으로 간주 (중복 처리 방지)
        LocalDateTime maxThreshold = service.getServiceTime().plusMinutes(lateGraceMinutes + 1);
        if (now.isAfter(maxThreshold)) {
            return 0; // 이미 처리 시간이 지남
        }
        
        // 활성화된 일반 사용자 조회 (관리자 제외)
        List<User> activeUsers = userRepository.findByActiveTrueAndRole(User.Role.USER);
        
        int processedCount = 0;
        
        for (User user : activeUsers) {
            // 이미 출석 체크했는지 확인
            if (attendanceRepository.existsByUserAndService(user, service)) {
                continue; // 이미 출석 체크함
            }
            
            // 자동 지각 처리: LATE 상태로 출석 기록 생성
            // GPS 좌표는 교회 좌표 사용, 거리는 0으로 설정
            Attendance attendance = Attendance.builder()
                    .user(user)
                    .service(service)
                    .status(Attendance.AttendanceStatus.LATE)
                    .latitude(churchLatitude)
                    .longitude(churchLongitude)
                    .distance(0.0)
                    .notes("자동 지각 처리 (예배 시작 후 10분 경과)")
                    .build();
            
            attendanceRepository.save(attendance);
            processedCount++;
        }
        
        return processedCount;
    }
}


