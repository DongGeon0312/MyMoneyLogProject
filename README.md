# 머니로그 (MoneyLog)

개인 가계부 웹 서비스. 로그인한 사용자가 수입/지출을 기록하고, 카테고리별·월별 통계를 확인합니다.

핵심 원칙: **내 데이터는 나만 접근한다 (인가)** — 모든 조회·수정·삭제는 로그인한 사용자 기준으로만 동작합니다.

## 배포 URL

- **서비스**: http://16.176.26.134:8080/login.html
- **Swagger API 문서**: http://16.176.26.134:8080/swagger-ui/index.html

로그인 화면에서 회원가입 후 바로 이용하실 수 있습니다.

## 주요 기능

| 기능 | 설명 |
|---|---|
| 회원가입 / 로그인 | 이메일·비밀번호 가입, JWT 발급, BCrypt 비밀번호 저장 |
| 카테고리 | 가입 시 기본 카테고리 자동 시드(식비/교통/주거/문화, 급여/용돈) + 추가·수정·삭제 |
| 거래내역 CRUD | 수입/지출 등록·조회·수정·삭제 |
| 월별 목록 조회 | 이번 달 거래 목록(페이징) |
| 월별 통계 | 총수입·총지출·잔액, 카테고리별 지출 합계 |
| 인가 | 본인 데이터만 접근 가능 (남의 거래 조회 시 403, 없는 거래는 404) |
| 프론트엔드 | 로그인 / 거래내역 목록+등록 / 월별 통계 화면 3종 (Vanilla HTML/JS) |
| API 문서 | springdoc-openapi 기반 Swagger UI |
| 배포 | Docker + GitHub Actions CI/CD + AWS EC2 |

## 기술 스택

- **Backend**: Java 17, Spring Boot 3.3.2, Spring Data JPA, Spring Security, JWT(jjwt)
- **DB**: H2(로컬 개발) / MySQL 8(운영)
- **Frontend**: 정적 HTML + Vanilla JS (fetch)
- **DevOps**: Docker, docker-compose, GitHub Actions (CI: 빌드/테스트, CD: GHCR 이미지 빌드 후 EC2 SSH 배포), AWS EC2
- **문서화**: springdoc-openapi (Swagger UI)

## 프로젝트 구조

```
MyMoneyLogProject/
├── backend/                # Spring Boot 애플리케이션
│   ├── src/main/java/com/likelion/moneylog/
│   │   ├── domain/         # user, category, transaction, statistics (도메인형 패키지 구조)
│   │   └── global/         # config, security, entity, exception
│   ├── src/main/resources/
│   │   ├── application.yml # local(H2) / prod(MySQL) 프로파일 분리
│   │   └── static/         # login.html, transactions.html, statistics.html, api.js
│   └── Dockerfile
├── docker-compose.yml       # app + MySQL 로컬/배포 통합 실행
├── .github/workflows/       # ci.yml(빌드/테스트), deploy.yml(GHCR push + EC2 배포)
└── docs/
    ├── requirements.md      # 요구사항 정의서
    ├── erd.md               # ERD 및 테이블 설계
    ├── api-spec.md          # REST API 명세
    ├── progress-plan.md      # 1~5일차 진행 계획
    └── retrospective.md      # 회고
```

## 로컬 실행 방법

### 1) IDE / Gradle로 직접 실행 (H2, 가장 빠름)

```bash
cd backend
./gradlew bootRun
```

`http://localhost:8080/login.html` 접속 (기본적으로 H2 인메모리 DB 사용, 데이터는 재시작 시 초기화됩니다).

### 2) Docker Compose로 실행 (MySQL 포함, 배포 환경과 동일)

```bash
cp .env.example .env
# .env를 열어 DB_ROOT_PASSWORD, DB_PASSWORD, JWT_SECRET을 원하는 값으로 채운다
docker compose up -d --build
```

`http://localhost:8080/login.html` 접속.

## API 개요

전체 API 명세는 [docs/api-spec.md](docs/api-spec.md) 참고. 배포된 서비스의 상세 스펙은 Swagger UI에서 바로 확인 가능합니다.

| 영역 | 대표 엔드포인트 |
|---|---|
| 인증 | `POST /api/auth/signup`, `POST /api/auth/login` |
| 카테고리 | `GET/POST/PUT/DELETE /api/categories` |
| 거래내역 | `GET/POST /api/transactions`, `GET/PUT/DELETE /api/transactions/{id}` |
| 통계 | `GET /api/statistics/monthly?yearMonth=2026-07` |

모든 응답은 공통 형식(`{success, message, data, (meta)}` / 에러 `{success:false, code, message, data:null}`)으로 통일되어 있습니다.

## 설계 문서

- [요구사항 정의서](docs/requirements.md)
- [ERD / 도메인 설계](docs/erd.md)
- [REST API 명세](docs/api-spec.md)
- [진행 계획 (1~5일차)](docs/progress-plan.md)
- [회고](docs/retrospective.md)

## CI/CD

- `main` 브랜치에 push되면 GitHub Actions가 자동으로 빌드·테스트(CI)를 수행하고, 이어서 Docker 이미지를 빌드해 GHCR에 push한 뒤 SSH로 EC2에 접속해 재배포(CD)합니다.
- 워크플로: [`.github/workflows/ci.yml`](.github/workflows/ci.yml), [`.github/workflows/deploy.yml`](.github/workflows/deploy.yml)

## 알려진 한계

- accessToken은 `localStorage`에 저장합니다 (XSS에 취약할 수 있음 — 학습용 캡스톤 범위에서는 이 방식으로 진행했습니다).
- 목록 필터(타입/카테고리)·페이징 조합, 카테고리별 통계 집계 등 선택 범위는 기본 기능만 구현했습니다.
- 도전 과제(예산, 통계 시각화, 검색/CSV, 테스트, Jenkins/K8s 등)는 이번 범위에서 제외했습니다.
