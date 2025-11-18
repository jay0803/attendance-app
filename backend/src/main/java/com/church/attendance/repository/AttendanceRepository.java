package com.church.attendance.repository;

import com.church.attendance.entity.Attendance;
import com.church.attendance.entity.User;
import com.church.attendance.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    
    Optional<Attendance> findByUserAndService(User user, Service service);
    
    boolean existsByUserAndService(User user, Service service);
    
    List<Attendance> findByUser(User user);
    
    List<Attendance> findByService(Service service);
    
    @Query("SELECT a FROM Attendance a WHERE a.user = :user AND a.checkedAt BETWEEN :start AND :end")
    List<Attendance> findByUserAndDateRange(User user, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT a FROM Attendance a WHERE a.service = :service ORDER BY a.checkedAt DESC")
    List<Attendance> findByServiceOrderByCheckedAtDesc(Service service);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.user = :user AND a.status = 'PRESENT'")
    long countPresentByUser(User user);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.user = :user AND a.status = 'LATE'")
    long countLateByUser(User user);
}


