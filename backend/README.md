# 출석 체크 시스템 백엔드

Spring Boot 기반의 위치 기반 출석 체크 시스템 백엔드 API

## 기술 스택

- Java 17
- Spring Boot 3.2.0
- Spring Security + JWT
- Spring Data JPA
- MySQL
- Maven

## 주요 기능

1. **인증/인가**
   - JWT 기반 인증
   - 사용자 회원가입/로그인
   - 역할 기반 권한 관리 (USER, ADMIN)

2. **출석 체크**
   - GPS 위치 기반 출석 체크
   - Haversine 공식으로 거리 계산
   - 예배 시작 30분 전부터 출석 버튼 활성화
   - 자동 지각 처리

3. **예배 관리**
   - 예배 일정 조회
   - 다음 예배 정보 제공

4. **관리자 기능**
   - 전체 출석 기록 조회
   - 예배별 출석 현황 조회

## 설치 및 실행

### 1. 데이터베이스 설정

MySQL에 데이터베이스 생성:

```sql
CREATE DATABASE attendance_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 환경변수 설정

`.env.example` 파일을 `.env`로 복사하고 실제 값으로 수정:

```bash
cp .env.example .env
```

### 3. 애플리케이션 실행

```bash
# Maven으로 빌드 및 실행
mvn spring-boot:run

# 또는 jar 파일로 실행
mvn clean package
java -jar target/attendance-backend-1.0.0.jar
```

서버는 기본적으로 `http://localhost:8080`에서 실행됩니다.

## API 명세

### 인증 API

#### 회원가입
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "user1",
  "password": "password123",
  "name": "홍길동",
  "phone": "010-1234-5678",
  "email": "user1@example.com"
}
```

#### 로그인
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "user1",
  "password": "password123"
}
```

### 예배 API

#### 활성화된 예배 목록 조회
```http
GET /api/services
Authorization: Bearer {token}
```

#### 다음 예배 조회
```http
GET /api/services/next
Authorization: Bearer {token}
```

### 출석 API

#### 출석 체크
```http
POST /api/attendance/check
Authorization: Bearer {token}
Content-Type: application/json

{
  "serviceId": 1,
  "latitude": 37.5665,
  "longitude": 126.9780
}
```

#### 내 출석 기록 조회
```http
GET /api/attendance/my
Authorization: Bearer {token}
```

#### 전체 출석 기록 조회 (관리자)
```http
GET /api/attendance/all
Authorization: Bearer {token}
```

#### 예배별 출석 기록 조회 (관리자)
```http
GET /api/attendance/service/{serviceId}
Authorization: Bearer {token}
```

## 환경변수 설명

| 변수명 | 설명 | 기본값 |
|--------|------|--------|
| DB_HOST | MySQL 호스트 | localhost |
| DB_PORT | MySQL 포트 | 3306 |
| DB_NAME | 데이터베이스 이름 | attendance_db |
| DB_USERNAME | 데이터베이스 사용자명 | root |
| DB_PASSWORD | 데이터베이스 비밀번호 | password |
| JWT_SECRET | JWT 시크릿 키 (최소 256비트) | - |
| JWT_EXPIRATION | JWT 만료 시간 (밀리초) | 86400000 (24시간) |
| CHURCH_LATITUDE | 교회 위도 | 37.5665 |
| CHURCH_LONGITUDE | 교회 경도 | 126.9780 |
| CHURCH_RADIUS | 출석 체크 허용 반경 (미터) | 100 |
| ATTENDANCE_ACTIVATION_MINUTES | 출석 버튼 활성화 시간 (예배 전 분) | 30 |
| ATTENDANCE_LATE_GRACE_MINUTES | 지각 처리 유예 시간 (예배 후 분) | 10 |

## 데이터베이스 스키마

### users 테이블
- id: 사용자 ID (PK)
- username: 사용자명 (unique)
- password: 암호화된 비밀번호
- name: 이름
- phone: 전화번호
- email: 이메일
- role: 역할 (USER, ADMIN)
- active: 활성화 여부
- created_at: 생성일시
- updated_at: 수정일시

### services 테이블
- id: 예배 ID (PK)
- name: 예배 이름
- service_time: 예배 시작 시간
- type: 예배 유형 (SUNDAY, WEDNESDAY, FRIDAY, SPECIAL)
- active: 활성화 여부
- created_at: 생성일시
- updated_at: 수정일시

### attendance 테이블
- id: 출석 ID (PK)
- user_id: 사용자 ID (FK)
- service_id: 예배 ID (FK)
- status: 출석 상태 (PRESENT, LATE, ABSENT)
- latitude: 체크 시 위도
- longitude: 체크 시 경도
- distance: 교회와의 거리
- checked_at: 출석 체크 시간
- notes: 비고

## 라이센스

MIT License

