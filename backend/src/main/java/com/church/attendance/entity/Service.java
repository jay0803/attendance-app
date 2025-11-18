package com.church.attendance.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Service {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;  // 예: "주일 1부 예배", "수요 예배"
    
    @Column(nullable = false)
    private LocalDateTime serviceTime;  // 예배 시작 시간
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ServiceType type = ServiceType.SUNDAY;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    public enum ServiceType {
        SUNDAY,      // 주일 예배
        WEDNESDAY,   // 수요 예배
        FRIDAY,      // 금요 예배
        SPECIAL      // 특별 예배
    }
}


