package com.church.attendance.repository;

import com.church.attendance.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    List<Service> findByActiveTrue();
    
    @Query("SELECT s FROM Service s WHERE s.active = true AND s.serviceTime BETWEEN :start AND :end")
    List<Service> findActiveServicesBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT s FROM Service s WHERE s.active = true AND s.serviceTime > :now ORDER BY s.serviceTime ASC")
    Optional<Service> findNextUpcomingService(LocalDateTime now);
    
    /**
     * 예배 시작 후 특정 시간이 경과한 예배 조회
     * 예배 시작 시간 + 10분 <= 현재 시간
     * 예배 시작 시간 + 11분 > 현재 시간
     * (1분 간격으로 체크하므로 중복 처리 방지)
     */
    @Query("SELECT s FROM Service s WHERE s.active = true " +
           "AND s.serviceTime <= :beforeTime " +
           "AND s.serviceTime > :afterTime")
    List<Service> findServicesForAutoLateProcessing(LocalDateTime beforeTime, LocalDateTime afterTime);
}


