# 출석 체크 Flutter 앱

교회 출석 체크 시스템의 모바일 클라이언트 애플리케이션

## 기능

- **인증**
  - 로그인/회원가입
  - JWT 토큰 기반 자동 로그인

- **위치 기반 출석 체크**
  - GPS 위치 권한 요청
  - 실시간 위치 정보 가져오기
  - 교회 위치와의 거리 계산
  - 출석/지각 자동 판정

- **출석 기록 조회**
  - 내 출석 기록 확인
  - 출석 통계 (총 출석, 정상, 지각)

- **예배 정보**
  - 활성화된 예배 목록
  - 예배 시간 30분 전부터 출석 버튼 활성화

## 설치

### 1. Flutter SDK 설치

Flutter SDK를 설치합니다 (https://flutter.dev/docs/get-started/install)

### 2. 의존성 설치

```bash
cd flutter_app
flutter pub get
```

### 3. API 서버 URL 설정

`lib/config/api_config.dart` 파일을 열고 백엔드 서버 주소를 설정합니다:

```dart
static const String baseUrl = 'http://YOUR_SERVER_IP:8080/api';
```

**중요**: 
- Android 에뮬레이터: `http://10.0.2.2:8080/api`
- iOS 시뮬레이터: `http://localhost:8080/api`
- 실제 기기: `http://YOUR_COMPUTER_IP:8080/api` (예: `http://192.168.0.100:8080/api`)

### 4. 실행

```bash
# Android
flutter run

# iOS (macOS만 가능)
flutter run -d ios

# 특정 디바이스 선택
flutter devices  # 사용 가능한 디바이스 확인
flutter run -d device_id
```

## 프로젝트 구조

```
lib/
├── config/
│   └── api_config.dart          # API 설정
├── models/
│   ├── user.dart                # 사용자 모델
│   ├── service.dart             # 예배 모델
│   └── attendance.dart          # 출석 모델
├── providers/
│   ├── auth_provider.dart       # 인증 상태 관리
│   └── attendance_provider.dart # 출석 상태 관리
├── screens/
│   ├── login_screen.dart        # 로그인 화면
│   ├── register_screen.dart     # 회원가입 화면
│   ├── home_screen.dart         # 홈 화면 (탭 네비게이션)
│   ├── attendance_check_screen.dart    # 출석 체크 화면
│   └── attendance_history_screen.dart  # 출석 기록 화면
├── services/
│   ├── api_service.dart         # API 통신 서비스
│   └── location_service.dart    # 위치 서비스
├── utils/
│   └── storage_helper.dart      # 로컬 저장소 헬퍼
└── main.dart                    # 앱 진입점
```

## 주요 패키지

- **provider**: 상태 관리
- **http**: HTTP 통신
- **geolocator**: GPS 위치 정보
- **permission_handler**: 권한 요청
- **shared_preferences**: 로컬 저장소
- **intl**: 날짜/시간 포맷팅

## 권한

### Android (AndroidManifest.xml)
```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.INTERNET" />
```

### iOS (Info.plist)
```xml
<key>NSLocationWhenInUseUsageDescription</key>
<string>출석 체크를 위해 현재 위치 정보가 필요합니다.</string>
```

## 빌드

### Android APK
```bash
flutter build apk --release
```

생성 위치: `build/app/outputs/flutter-apk/app-release.apk`

### iOS (macOS only)
```bash
flutter build ios --release
```

## 문제 해결

### 1. 위치 권한이 거부됨
- 설정 > 앱 > 출석체크 > 권한 > 위치에서 권한을 허용하세요

### 2. 서버 연결 실패
- `api_config.dart`의 서버 주소를 확인하세요
- 백엔드 서버가 실행 중인지 확인하세요
- 방화벽 설정을 확인하세요

### 3. HTTP 연결 오류 (Android)
Android는 기본적으로 HTTP를 차단합니다. 개발 환경에서만 사용하는 경우:

`android/app/src/main/AndroidManifest.xml`에 추가:
```xml
<application
    android:usesCleartextTraffic="true"
    ...>
```

## 라이센스

MIT License

