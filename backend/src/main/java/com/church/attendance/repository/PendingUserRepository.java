package com.church.attendance.repository;

import com.church.attendance.entity.PendingUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PendingUserRepository extends JpaRepository<PendingUser, Long> {
    
    /**
     * 전화번호로 사전 등록된 사용자 찾기
     */
    Optional<PendingUser> findByPhoneAndActiveTrue(String phone);
    
    /**
     * 이메일로 사전 등록된 사용자 찾기
     */
    Optional<PendingUser> findByEmailAndActiveTrue(String email);
    
    /**
     * 활성화된 모든 사전 등록 사용자 조회
     */
    List<PendingUser> findByActiveTrueOrderByCreatedAtDesc();
}

