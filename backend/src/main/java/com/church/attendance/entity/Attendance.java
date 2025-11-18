package com.church.attendance.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "attendance", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "service_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Attendance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttendanceStatus status;
    
    @Column(nullable = false)
    private Double latitude;  // 출석 체크 시 사용자 위도
    
    @Column(nullable = false)
    private Double longitude;  // 출석 체크 시 사용자 경도
    
    @Column(nullable = false)
    private Double distance;  // 교회와의 거리(미터)
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime checkedAt;  // 출석 체크 시간
    
    @Column(length = 500)
    private String notes;  // 비고
    
    public enum AttendanceStatus {
        PRESENT,  // 정상 출석
        LATE,     // 지각
        ABSENT    // 결석
    }
}


