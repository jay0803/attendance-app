# 출석 체크 시스템 관리자 웹

React 기반의 출석 체크 시스템 관리자 페이지

## 기능

- **인증**
  - 관리자 로그인 (ADMIN 권한 필요)
  - JWT 토큰 기반 자동 로그인

- **대시보드**
  - 전체 출석 통계
  - 예배 목록 조회
  - 최근 출석 기록 조회

- **출석 기록 관리**
  - 전체 출석 기록 조회
  - 예배별 필터링
  - 이름으로 검색
  - 상세 정보 (위치, 거리, 시간 등)

## 설치 및 실행

### 1. 의존성 설치

```bash
cd admin-web
npm install
```

### 2. 환경변수 설정

`.env.example` 파일을 `.env`로 복사하고 API 서버 주소를 설정합니다:

```bash
cp .env.example .env
```

`.env` 파일 수정:
```
REACT_APP_API_URL=http://localhost:8080/api
```

### 3. 개발 서버 실행

```bash
npm start
```

브라우저에서 `http://localhost:3000`으로 접속합니다.

### 4. 프로덕션 빌드

```bash
npm run build
```

빌드된 파일은 `build/` 디렉토리에 생성됩니다.

## 프로젝트 구조

```
src/
├── pages/
│   ├── Login.js              # 로그인 페이지
│   ├── Login.css
│   ├── Dashboard.js          # 대시보드
│   └── AttendanceList.js     # 출석 기록 페이지
├── services/
│   └── api.js                # API 통신 서비스
├── utils/
│   └── auth.js               # 인증 유틸리티
├── App.js                    # 메인 앱 컴포넌트
├── App.css
├── index.js                  # 진입점
├── index.css
└── config.js                 # 설정
```

## 주요 기술

- **React 18**: UI 프레임워크
- **React Router v6**: 라우팅
- **Axios**: HTTP 클라이언트
- **LocalStorage**: 토큰 저장

## API 엔드포인트

### 인증
- `POST /api/auth/login` - 로그인

### 출석
- `GET /api/attendance/all` - 전체 출석 기록 조회 (관리자)
- `GET /api/attendance/service/{serviceId}` - 예배별 출석 기록 조회 (관리자)

### 예배
- `GET /api/services/all` - 전체 예배 조회
- `GET /api/services` - 활성화된 예배 조회

## 스크린샷 설명

### 로그인 페이지
- 관리자 사용자명과 비밀번호 입력
- ADMIN 권한이 있는 계정만 로그인 가능

### 대시보드
- 전체 출석 통계 (전체, 정상, 지각, 결석)
- 등록된 예배 목록
- 최근 출석 기록 10개

### 출석 기록 페이지
- 전체 출석 기록 테이블
- 예배별 필터링
- 이름 검색
- 상세 정보 (ID, 이름, 예배, 상태, 거리, 위치, 시간)

## 배포

### Nginx 설정 예시

```nginx
server {
    listen 80;
    server_name admin.your-domain.com;

    root /var/www/attendance-admin/build;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://localhost:8080/api;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### Docker로 배포

```dockerfile
FROM node:18-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

## 문제 해결

### CORS 오류
백엔드 서버의 CORS 설정에 관리자 페이지 주소를 추가해야 합니다.

`backend/src/main/resources/application.yml`:
```yaml
cors:
  allowed-origins: http://localhost:3000
```

### 로그인 실패
- 관리자 권한(ADMIN)이 있는 계정인지 확인
- 백엔드 서버가 실행 중인지 확인
- API URL이 올바른지 확인

## 라이센스

MIT License

