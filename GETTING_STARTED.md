# 시작하기 가이드

이 문서는 출석 체크 시스템을 처음 설정하고 실행하는 방법을 단계별로 설명합니다.

## 📋 사전 요구사항

### 백엔드
- ✅ Java 17 이상
- ✅ Maven 3.6+
- ✅ MySQL 8.0+

### Flutter 앱
- ✅ Flutter SDK 3.0+
- ✅ Android Studio (Android 개발) 또는 Xcode (iOS 개발)

### React 관리자 웹
- ✅ Node.js 16+
- ✅ npm 또는 yarn

## 🚀 설치 및 실행 순서

### 1단계: 데이터베이스 설정

```bash
# MySQL 접속
mysql -u root -p

# 데이터베이스 생성
CREATE DATABASE attendance_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 사용자 생성 (선택사항)
CREATE USER 'attendance_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON attendance_db.* TO 'attendance_user'@'localhost';
FLUSH PRIVILEGES;

# 종료
exit
```

### 2단계: 백엔드 실행

```bash
# backend 디렉토리로 이동
cd backend

# 환경변수 파일 생성
cp .env.example .env

# .env 파일 수정 (에디터로 열어서 수정)
# 필수 수정 항목:
# - DB_PASSWORD: MySQL 비밀번호
# - JWT_SECRET: 256비트 이상의 임의의 문자열
# - CHURCH_LATITUDE: 교회 위도
# - CHURCH_LONGITUDE: 교회 경도
# - CHURCH_RADIUS: 출석 허용 반경 (미터)

# 예시 .env 파일 내용:
# DB_PASSWORD=mypassword123
# JWT_SECRET=my-super-secret-key-must-be-at-least-256-bits-long-for-hs256
# CHURCH_LATITUDE=37.5665
# CHURCH_LONGITUDE=126.9780
# CHURCH_RADIUS=100

# 빌드 및 실행
mvn spring-boot:run

# 또는 jar 파일로 실행
mvn clean package
java -jar target/attendance-backend-1.0.0.jar
```

**확인**: 브라우저에서 `http://localhost:8080` 접속 시 Whitelabel Error Page가 보이면 정상입니다.

### 3단계: 테스트 데이터 생성

```bash
# MySQL에 다시 접속
mysql -u root -p attendance_db

# 관리자 계정 생성 (username: admin, password: admin123)
INSERT INTO users (username, password, name, phone, email, role, active, created_at, updated_at)
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 
        '관리자', '010-0000-0000', 'admin@church.com', 'ADMIN', true, NOW(), NOW());

# 테스트 사용자 생성 (username: user1, password: user123)
INSERT INTO users (username, password, name, phone, email, role, active, created_at, updated_at)
VALUES ('user1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 
        '홍길동', '010-1234-5678', 'user1@church.com', 'USER', true, NOW(), NOW());

# 예배 일정 생성 (다음 주일 오전 9시)
INSERT INTO services (name, service_time, type, active, created_at, updated_at)
VALUES ('주일 1부 예배', '2025-11-23 09:00:00', 'SUNDAY', true, NOW(), NOW());

INSERT INTO services (name, service_time, type, active, created_at, updated_at)
VALUES ('주일 2부 예배', '2025-11-23 11:00:00', 'SUNDAY', true, NOW(), NOW());

# 확인
SELECT * FROM users;
SELECT * FROM services;
exit
```

### 4단계: React 관리자 웹 실행

```bash
# 새 터미널 창에서
cd admin-web

# 의존성 설치
npm install

# 환경변수 파일 생성
cp .env.example .env

# .env 내용 (기본값이면 수정 불필요)
# REACT_APP_API_URL=http://localhost:8080/api

# 개발 서버 실행
npm start
```

**확인**: 브라우저가 자동으로 열리고 `http://localhost:3000`에서 로그인 페이지가 보입니다.

**로그인 테스트**:
- 사용자명: `admin`
- 비밀번호: `admin123`

### 5단계: Flutter 앱 실행

```bash
# 새 터미널 창에서
cd flutter_app

# 의존성 설치
flutter pub get

# API 서버 주소 설정
# lib/config/api_config.dart 파일을 에디터로 열기

# 실제 기기에서 테스트하는 경우:
# 1. 컴퓨터의 로컬 IP 확인
#    Windows: ipconfig
#    Mac/Linux: ifconfig 또는 ip addr

# 2. api_config.dart 파일 수정
#    static const String baseUrl = 'http://YOUR_IP:8080/api';
#    예: static const String baseUrl = 'http://192.168.0.100:8080/api';

# Android 에뮬레이터 사용 시:
#    static const String baseUrl = 'http://10.0.2.2:8080/api';

# 사용 가능한 디바이스 확인
flutter devices

# 앱 실행 (디바이스 자동 선택)
flutter run

# 또는 특정 디바이스 지정
flutter run -d <device_id>
```

**로그인 테스트**:
- 사용자명: `user1`
- 비밀번호: `user123`

## ✅ 동작 확인

### 1. 관리자 웹 확인
1. `http://localhost:3000`에서 관리자로 로그인
2. 대시보드에서 통계 확인
3. 출석 기록 메뉴에서 필터링 테스트

### 2. 모바일 앱 확인
1. 앱에서 user1로 로그인
2. 예배 목록이 표시되는지 확인
3. 위치 권한 허용
4. (출석 시간이 되면) 출석 체크 버튼 활성화 확인

### 3. 출석 체크 테스트

**실제 위치에서 테스트하려면**:
1. 교회 좌표를 정확히 설정 (backend/.env)
2. 교회 근처에서 앱 실행
3. 출석 체크 버튼 클릭

**테스트 환경에서**:
1. backend/.env에서 CHURCH_RADIUS를 크게 설정 (예: 50000m)
2. 또는 현재 위치의 좌표를 CHURCH_LATITUDE, CHURCH_LONGITUDE에 설정

## 🔧 문제 해결

### 백엔드가 시작되지 않음
```bash
# MySQL 연결 확인
mysql -u root -p
USE attendance_db;

# 포트 충돌 확인 (8080 포트가 사용 중인지)
# Windows: netstat -ano | findstr :8080
# Mac/Linux: lsof -i :8080
```

### React 웹이 API에 연결되지 않음
- 브라우저 콘솔(F12)에서 에러 확인
- CORS 문제인 경우: backend/src/main/resources/application.yml의 cors.allowed-origins 확인

### Flutter 앱이 서버에 연결되지 않음
- API URL이 올바른지 확인
- 실제 기기에서는 localhost가 아닌 컴퓨터의 실제 IP 사용
- 방화벽 확인
- Android에서 HTTP 연결이 차단되는 경우: AndroidManifest.xml에 `android:usesCleartextTraffic="true"` 추가

### 위치 권한 오류
- 설정 > 앱 > 출석체크 > 권한에서 위치 권한 허용
- Android: 위치 서비스 활성화 확인
- iOS: Info.plist의 위치 권한 설명 확인

## 📚 다음 단계

1. **프로덕션 배포**: 각 서비스의 배포 가이드 참조
2. **커스터마이징**: 교회에 맞게 설정 변경
3. **데이터 백업**: MySQL 정기 백업 설정

## 💡 유용한 명령어

```bash
# 백엔드 로그 확인
tail -f backend/logs/application.log

# Flutter 앱 재시작
flutter run --hot-reload

# React 캐시 삭제 후 재시작
cd admin-web
rm -rf node_modules package-lock.json
npm install
npm start
```

## 🆘 추가 도움말

- 백엔드 API 문서: `backend/README.md`
- Flutter 앱 가이드: `flutter_app/README.md`
- React 관리자 가이드: `admin-web/README.md`
- 이슈 등록: GitHub Issues

---

**축하합니다! 🎉** 출석 체크 시스템이 정상적으로 실행되고 있습니다.

