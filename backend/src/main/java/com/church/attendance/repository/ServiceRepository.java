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
}


