-- 출석 체크 시스템 데이터베이스 스키마
-- MySQL 8.0+

-- 데이터베이스 생성
CREATE DATABASE IF NOT EXISTS attendance_db 
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE attendance_db;

-- Users 테이블
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    naver_id VARCHAR(100),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_username (username),
    INDEX idx_role (role),
    INDEX idx_naver_id (naver_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Services 테이블
CREATE TABLE IF NOT EXISTS services (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    service_time DATETIME NOT NULL,
    type VARCHAR(20) NOT NULL DEFAULT 'SUNDAY',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_service_time (service_time),
    INDEX idx_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Pending Users 테이블 (사전 등록된 사용자)
CREATE TABLE IF NOT EXISTS pending_users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    notes VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_phone (phone),
    INDEX idx_email (email),
    INDEX idx_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Attendance 테이블
CREATE TABLE IF NOT EXISTS attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    distance DOUBLE NOT NULL,
    checked_at DATETIME NOT NULL,
    notes VARCHAR(500),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES services(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_service (user_id, service_id),
    INDEX idx_user_id (user_id),
    INDEX idx_service_id (service_id),
    INDEX idx_status (status),
    INDEX idx_checked_at (checked_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 샘플 데이터 삽입

-- 관리자 계정 (username: admin, password: admin123)
INSERT INTO users (username, password, name, phone, email, role, active, created_at, updated_at)
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 
        '관리자', '010-0000-0000', 'admin@church.com', 'ADMIN', true, NOW(), NOW());

-- 테스트 사용자들 (password: user123)
INSERT INTO users (username, password, name, phone, email, role, active, created_at, updated_at)
VALUES 
    ('user1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 
     '김준경', '010-1111-1111', 'hong@example.com', 'USER', true, NOW(), NOW()),
    ('user2', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 
     '양주은', '010-2222-2222', 'kim@example.com', 'USER', true, NOW(), NOW()),
    ('user3', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 
     '김미송', '010-3333-3333', 'lee@example.com', 'USER', true, NOW(), NOW());

-- 예배 일정 (날짜는 실제 사용 시 수정 필요)
INSERT INTO services (name, service_time, type, active, created_at, updated_at)
VALUES 
    ('주일 청년부 예배', '2025-11-23 14:00:00', 'SUNDAY', true, NOW(), NOW());

-- 샘플 출석 기록 (테스트용)
-- INSERT INTO attendance (user_id, service_id, status, latitude, longitude, distance, checked_at)
-- VALUES 
--     (2, 1, 'PRESENT', 37.5665, 126.9780, 50.5, '2025-11-16 08:45:00'),
--     (3, 1, 'LATE', 37.5665, 126.9780, 75.2, '2025-11-16 09:15:00'),
--     (4, 1, 'PRESENT', 37.5665, 126.9780, 30.8, '2025-11-16 08:50:00');

-- 데이터 확인
SELECT 'Users:' as Info;
SELECT * FROM users;

SELECT 'Services:' as Info;
SELECT * FROM services;

SELECT 'Attendance:' as Info;
SELECT * FROM attendance;

SELECT 'Pending Users:' as Info;
SELECT * FROM pending_users;

