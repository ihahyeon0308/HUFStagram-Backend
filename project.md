# HAIgram Project Handoff Note

## 1. Executive Summary

이 프로젝트는 **Instagram 웹 UI를 참고한 소셜 웹 애플리케이션 데모**입니다.  
초기 정적 HTML/CSS/JavaScript 프로토타입은 요구사항 대비 한계가 분명했습니다.

- UI가 실제 인스타그램 웹 레이아웃과 충분히 닮지 않았음
- 화면 상태와 데이터 구조가 분리되지 않아 유지보수가 어려웠음
- 브라우저 LocalStorage 중심 구조라 인증/기록/확장성 측면에서 제한이 컸음

이번 리워크에서는 구조를 다음과 같이 재정비했습니다.

- 프런트엔드: **React + TypeScript + Vite**
- 백엔드: **Spring Boot**
- 데이터 연동 방식: **JSON REST API**
- 데이터베이스 설정: **MySQL + Spring Data JPA**
- 로컬 개발 편의: **H2 로컬 프로필 제공**

현재 결과물은 “정적 목업”이 아니라, **프런트와 백엔드가 분리된 풀스택 데모 구조**입니다.

---

## 2. Repository Structure

```text
/
├─ frontend/               # React + TypeScript + Vite
├─ backend/                # Spring Boot API
├─ docker-compose.yml      # MySQL 실행용
├─ index.html              # 루트 안내용 랜딩 페이지
├─ styles.css
├─ script.js
└─ project.md              # 현재 문서
```

### frontend

- 인스타그램 웹과 유사한 좌측 사이드바 / 중앙 피드 / 우측 추천 레이아웃
- 로그인/회원가입 화면
- 홈, 메시지, 좋아요, 탐색, 릴스, 북마크, 프로필, 만들기 섹션
- 사진/영상 업로드
- 댓글 작성
- 좋아요, 북마크, 팔로우, 메시지 전송

### backend

- 인증 API
- 피드 및 부트스트랩 API
- 게시물 생성 API
- 좋아요/북마크/댓글/팔로우 API
- 대화 시작 및 메시지 전송 API
- CORS 설정
- MySQL / H2 프로필 분리

---

## 3. Why The Stack Changed

사용자 요구사항 기준으로 기존 구조는 더 이상 적합하지 않았습니다.

### React + TypeScript를 사용한 이유

- 화면이 홈/탐색/릴스/메시지/프로필처럼 명확히 분리되는 서비스 구조이기 때문
- 업로드, 댓글, 좋아요, 북마크, 인증 상태를 컴포넌트/상태 단위로 관리해야 하기 때문
- TypeScript로 API 응답 스키마와 UI 상태를 명확히 맞춰야 하기 때문

### Spring + JSON API를 사용한 이유

- 로그인/회원가입/좋아요/댓글/메시지와 같은 기능은 프런트 단독 구조보다 백엔드 API가 자연스럽기 때문
- 브라우저 저장소만으로는 인증과 데이터 무결성을 책임지기 어렵기 때문
- 기능 확장 시 REST API 계약이 있어야 팀 개발이 쉬워지기 때문

### MySQL을 넣은 이유

- 실제 서비스 전환 시 사용할 수 있는 운영형 DB 경로를 남기기 위해
- 로컬 검증은 H2로 빠르게, 운영/배포형 전환은 MySQL 프로필로 이어가기 위해

---

## 4. Implemented Feature Set

### 4.1 Authentication

- 로그인
- 회원가입
- 비밀번호 해시 저장
- 토큰 기반 세션 발급

### 4.2 Home

- 스토리 영역
- 피드 카드 렌더링
- 좋아요/북마크/팔로우 버튼
- 댓글 작성

### 4.3 Explore

- 게시물 탐색 그리드
- 검색어 기반 필터링

### 4.4 Reels

- 영상 중심 레이아웃
- 영상 재생 가능

### 4.5 Messages

- 대화 목록
- 대화 시작
- 메시지 전송

### 4.6 Likes / Activity

- 최근 활동 목록
- 내가 좋아요한 게시물 재조회

### 4.7 Bookmarks

- 저장한 게시물 재조회

### 4.8 Profile

- 프로필 헤더
- 게시물/좋아요/북마크/팔로잉 확인

### 4.9 Create

- 사진 업로드
- 영상 업로드
- 캡션 입력
- 위치 입력
- 업로드 전 미리보기

---

## 5. Frontend Design Notes

인스타그램 웹 레이아웃을 참고해 다음 원칙으로 재설계했습니다.

- 좌측 고정형 내비게이션
- 중앙 주 콘텐츠
- 우측 추천/요약 패널
- 로그인 화면 좌측에 비주얼 쇼케이스 배치
- 카드 경계, 여백, 라운드, 밝은 배경 톤을 실제 인스타그램 웹 흐름에 가깝게 조정

실제 픽셀 단위 복제는 아니지만, 이전 버전 대비 “인스타그램처럼 보이지 않는다”는 문제를 해소하는 방향으로 구조를 교정했습니다.

---

## 6. Backend Design Notes

현재 백엔드는 다음 전략으로 구성되어 있습니다.

- Spring Boot REST API
- JSON 요청/응답
- Spring Data JPA 기반 영속 계층
- H2 로컬 프로필
- MySQL 운영 프로필

즉, **인증/피드/좋아요/북마크/댓글/팔로우/메시지 데이터가 DB 테이블에 저장되는 구조**이며, 로컬 개발과 MySQL 운영 프로필을 모두 지원합니다.

### 주요 엔드포인트

- `POST /api/auth/login`
- `POST /api/auth/signup`
- `GET /api/app/bootstrap`
- `POST /api/posts`
- `POST /api/posts/{postId}/likes`
- `POST /api/posts/{postId}/bookmarks`
- `POST /api/posts/{postId}/comments`
- `POST /api/users/{userId}/follow`
- `POST /api/conversations`
- `POST /api/conversations/{conversationId}/messages`

---

## 7. Security Review

### 적용한 개선

- 비밀번호 평문 저장 제거
- `BCryptPasswordEncoder` 기반 해시 적용
- Authorization Bearer 토큰 기반 세션 처리
- 업로드 파일 타입 제한
- 업로드 개수 제한
- 데이터 URL 길이 제한
- 입력 길이 검증
- 전역 예외 처리 및 일관된 JSON 에러 응답
- CORS 명시 설정

### 남은 한계

- 세션 토큰은 DB 테이블에 저장되지만 JWT 기반 인증은 아직 아님
- 파일 업로드는 현재 Data URL 저장 방식이라 대용량 운영 환경에는 적합하지 않음
- JWT, Refresh Token, RBAC 같은 운영형 인증 체계는 아직 없음
- 파일 저장소(S3, Cloudinary 등) 연동은 아직 없음

---

## 8. QA Checklist

이번 턴에서 확인한 항목입니다.

- React 프런트엔드 빌드 성공
- ESLint 통과
- Spring Boot 테스트 성공
- 로그인/회원가입 API 경로 구성 확인
- 홈/메시지/좋아요/탐색/릴스/북마크/프로필/만들기 섹션 연결 확인
- 사진/영상 업로드 입력 경로 확인
- 댓글, 좋아요, 북마크, 팔로우, 메시지 액션 API 연결 확인

---

## 9. Local Run Guide

### 9.1 Backend

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

기본 실행은 `local` 프로필이며 H2를 사용합니다. JPA 엔티티와 리포지토리 기반으로 실제 테이블이 생성됩니다.

### 9.2 Frontend

```powershell
cd frontend
npm install
npm run dev
```

Vite dev server는 `/api` 요청을 `http://localhost:8080` 으로 프록시합니다.

### 9.3 MySQL Profile

```powershell
docker compose up -d
```

이후 환경 변수를 지정해 실행합니다.

```powershell
$env:APP_PROFILE="mysql"
$env:MYSQL_HOST="localhost"
$env:MYSQL_PORT="3306"
$env:MYSQL_DATABASE="haigram"
$env:MYSQL_USERNAME="root"
$env:MYSQL_PASSWORD="password"
cd backend
.\mvnw.cmd spring-boot:run
```

---

## 10. Recommended Next Steps

우선순위 기준으로는 아래가 적절합니다.

1. 세션 토큰을 JWT 또는 서버 저장형 세션으로 고도화
2. 업로드 파일을 외부 스토리지로 분리
3. 게시물 상세 모달, 알림 세분화, 검색 자동완성 추가
4. Query 최적화와 fetch 전략 튜닝
5. E2E 테스트 도입

---

## 11. Handoff Summary

이제 이 프로젝트는 더 이상 단순 정적 목업이 아닙니다.

- UI는 인스타그램 웹 구조를 참고한 방향으로 재설계되었고
- 프런트는 React + TypeScript로 분리되었으며
- 백엔드는 Spring Boot + Spring Data JPA + JSON API 기반으로 구성되었고
- MySQL 영속 구조까지 구현되어 있습니다

신입 개발자는 `frontend/src`, `backend/src/main/java`, `backend/src/main/resources` 세 영역만 따라가도 현재 구조를 빠르게 이해할 수 있습니다.
