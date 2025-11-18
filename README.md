# 위치 기반 출석 체크 시스템

Flutter(모바일 앱), Spring Boot(백엔드 API), React(관리자 웹)로 구성된 교회 출석 관리 시스템입니다.

## 📋 주요 기능

### 1. 위치 기반 출석 체크
- GPS 좌표를 이용한 실시간 위치 확인
- Haversine 공식으로 교회와의 거리 계산
- 지정된 반경 내에서만 출석 체크 가능

### 2. 자동 시간 관리
- 예배 시작 30분 전부터 출석 버튼 활성화
- 예배 시작 후 N분 경과 시 자동 지각 처리
- 실시간 예배 일정 조회

### 3. 관리 기능
- 관리자 대시보드 (통계, 최근 기록)
- 출석 기록 조회 및 필터링
- 사용자 및 예배 관리

## 🏗️ 프로젝트 구조

```
attendance-app/
├── backend/                    # Spring Boot 백엔드
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/church/attendance/
│   │   │   │   ├── entity/            # 엔티티 (User, Service, Attendance)
│   │   │   │   ├── repository/        # JPA Repository
│   │   │   │   ├── service/           # 비즈니스 로직
│   │   │   │   ├── controller/        # REST API 컨트롤러
│   │   │   │   ├── dto/              # 데이터 전송 객체
│   │   │   │   ├── security/         # JWT 인증
│   │   │   │   ├── config/           # 설정
│   │   │   │   └── util/             # 유틸리티 (거리 계산)
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       └── data.sql
│   │   └── pom.xml
│   ├── .env.example
│   └── README.md
│
├── flutter_app/                # Flutter 모바일 앱
│   ├── lib/
│   │   ├── config/            # API 설정
│   │   ├── models/            # 데이터 모델
│   │   ├── providers/         # 상태 관리 (Provider)
│   │   ├── screens/           # 화면 (로그인, 출석체크, 기록)
│   │   ├── services/          # API 및 위치 서비스
│   │   ├── utils/             # 유틸리티
│   │   └── main.dart
│   ├── android/               # Android 설정
│   ├── ios/                   # iOS 설정
│   ├── pubspec.yaml
│   └── README.md
│
├── admin-web/                  # React 관리자 웹
│   ├── public/
│   ├── src/
│   │   ├── pages/            # 페이지 컴포넌트
│   │   ├── services/         # API 서비스
│   │   ├── utils/            # 유틸리티
│   │   ├── App.js
│   │   └── index.js
│   ├── package.json
│   ├── .env.example
│   └── README.md
│
└── README.md                   # 이 파일
```

## 🚀 빠른 시작

### 1. 백엔드 (Spring Boot)

```bash
cd backend

# MySQL 데이터베이스 생성
mysql -u root -p
CREATE DATABASE attendance_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 환경변수 설정
cp .env.example .env
# .env 파일을 열어 필요한 값 수정

# 실행
mvn spring-boot:run
```

서버: `http://localhost:8080`

### 2. Flutter 앱

```bash
cd flutter_app

# 의존성 설치
flutter pub get

# API 서버 주소 설정
# lib/config/api_config.dart 파일 수정

# 실행
flutter run
```

### 3. React 관리자 웹

```bash
cd admin-web

# 의존성 설치
npm install

# 환경변수 설정
cp .env.example .env
# .env 파일을 열어 API URL 수정

# 실행
npm start
```

브라우저: `http://localhost:3000`

## 🔧 환경 설정

### 백엔드 환경변수 (.env)

```env
# 데이터베이스
DB_HOST=localhost
DB_PORT=3306
DB_NAME=attendance_db
DB_USERNAME=root
DB_PASSWORD=your_password

# JWT
JWT_SECRET=your-secret-key-min-256-bits
JWT_EXPIRATION=86400000

# 교회 위치 (위도, 경도)
CHURCH_LATITUDE=37.5665
CHURCH_LONGITUDE=126.9780
CHURCH_RADIUS=100

# 출석 체크 설정
ATTENDANCE_ACTIVATION_MINUTES=30
ATTENDANCE_LATE_GRACE_MINUTES=10
```

### Flutter 앱 설정

`flutter_app/lib/config/api_config.dart`:

```dart
static const String baseUrl = 'http://YOUR_IP:8080/api';
```

**중요**: 
- Android 에뮬레이터: `http://10.0.2.2:8080/api`
- iOS 시뮬레이터: `http://localhost:8080/api`
- 실제 기기: 컴퓨터의 실제 IP 사용

### React 관리자 웹 설정

`admin-web/.env`:

```env
REACT_APP_API_URL=http://localhost:8080/api
```

## 📊 데이터베이스 스키마

### users 테이블
- 사용자 정보 (아이디, 이름, 권한 등)

### services 테이블
- 예배 정보 (이름, 시간, 유형 등)

### attendance 테이블
- 출석 기록 (사용자, 예배, 상태, 위치, 시간 등)

## 🔐 API 명세

### 인증 API
- `POST /api/auth/register` - 회원가입
- `POST /api/auth/login` - 로그인

### 예배 API
- `GET /api/services` - 활성화된 예배 목록
- `GET /api/services/next` - 다음 예배 정보

### 출석 API
- `POST /api/attendance/check` - 출석 체크
- `GET /api/attendance/my` - 내 출석 기록
- `GET /api/attendance/all` - 전체 출석 기록 (관리자)
- `GET /api/attendance/service/{id}` - 예배별 출석 기록 (관리자)

자세한 API 문서는 각 프로젝트의 README를 참조하세요.

## 🧪 테스트 계정

백엔드 실행 후 다음과 같이 테스트 계정을 생성할 수 있습니다:

```sql
-- 관리자 계정 (username: admin, password: admin123)
INSERT INTO users (username, password, name, phone, email, role, active, created_at, updated_at)
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 
        '관리자', '010-1234-5678', 'admin@church.com', 'ADMIN', true, NOW(), NOW());

-- 일반 사용자 (username: user1, password: user123)
INSERT INTO users (username, password, name, phone, email, role, active, created_at, updated_at)
VALUES ('user1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 
        '홍길동', '010-1111-2222', 'user1@church.com', 'USER', true, NOW(), NOW());
```

## 🛠️ 기술 스택

### 백엔드
- Java 17
- Spring Boot 3.2.0
- Spring Security + JWT
- Spring Data JPA
- MySQL
- Maven

### 모바일 앱
- Flutter 3.x
- Provider (상태 관리)
- Geolocator (위치)
- HTTP/Dio (네트워크)

### 관리자 웹
- React 18
- React Router v6
- Axios
- CSS3

## 📱 기능 스크린샷 설명

### 모바일 앱
1. **로그인/회원가입**: JWT 기반 인증
2. **출석 체크**: 예배 목록 + GPS 위치 기반 출석
3. **출석 기록**: 내 출석 통계 및 히스토리

### 관리자 웹
1. **대시보드**: 전체 통계, 예배 목록, 최근 기록
2. **출석 관리**: 전체 기록 조회, 필터링, 검색

## 🤝 기여

이슈 및 Pull Request를 환영합니다!

## 📄 라이센스

MIT License

## 📧 문의

프로젝트에 대한 문의사항이 있으시면 이슈를 등록해주세요.

---

## 🎯 향후 개선 계획

- [ ] 알림 기능 (FCM)
- [ ] QR 코드 출석
- [ ] 출석 통계 리포트
- [ ] 예배 일정 반복 설정
- [ ] 멤버십/소그룹 관리
- [ ] Excel 내보내기

