package com.church.attendance.repository;

import com.church.attendance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    
    /**
     * 활성화된 일반 사용자 조회 (관리자 제외)
     */
    List<User> findByActiveTrueAndRole(User.Role role);
    
    /**
     * 네이버 ID로 사용자 찾기
     */
    Optional<User> findByNaverId(String naverId);
}


