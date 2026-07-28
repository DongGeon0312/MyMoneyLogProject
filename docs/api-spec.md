# 머니로그(MoneyLog) REST API 명세서

## 1. 설계 원칙

- URL이 자원(명사)을 가리킨다.
- 행위는 HTTP 메서드로 표현한다.
- 상태코드: 200(성공) / 201(생성 성공) / 400(잘못된 요청) / 401(인증 안 됨) / 403(인가 실패) / 404(자원 없음) / 409(충돌)

## 2. API 명세서

### 인증 (Auth)

| 메서드 | 경로 | 설명 | 요청 바디 | 응답 | 인증 |
|---|---|---|---|---|---|
| POST | /api/auth/signup | 회원가입 | {email, password, nickname} | 201 · 생성된 사용자 요약 | ❌ |
| POST | /api/auth/login | 로그인(JWT 발급) | {email, password} | 200 · {accessToken} | ❌ |

### 카테고리 (Category)

| 메서드 | 경로 | 설명 | 요청 바디 | 응답 | 인증 |
|---|---|---|---|---|---|
| GET | /api/categories | 내 카테고리 목록 | — | 200 · [{id, name, type}] | ✅ |
| POST | /api/categories | 카테고리 추가 | {name, type} | 201 · 생성된 카테고리 | ✅ |
| PUT | /api/categories/{id} | 카테고리 수정 | {name, type} | 200 · 수정된 카테고리 | ✅ |
| DELETE | /api/categories/{id} | 카테고리 삭제 | — | 204 | ✅ |

### 거래내역 (Transaction)

| 메서드 | 경로 | 설명 | 요청 바디 | 응답 | 인증 |
|---|---|---|---|---|---|
| GET | /api/transactions?yearMonth=&type=&categoryId=&page=&size= | 목록(월별 필수, 타입/카테고리 필터·페이징은 여유 시) | — | 200 · 페이지 응답 | ✅ |
| POST | /api/transactions | 거래 등록 | {type, amount, categoryId, description, transactionDate} | 201 · 생성된 거래 | ✅ |
| GET | /api/transactions/{id} | 거래 상세 | — | 200 · 거래 1건 | ✅ |
| PUT | /api/transactions/{id} | 거래 수정 | {type, amount, categoryId, description, transactionDate} | 200 · 수정된 거래 | ✅ |
| DELETE | /api/transactions/{id} | 거래 삭제 | — | 204 | ✅ |

### 통계 (Statistics)

| 메서드 | 경로 | 설명 | 요청 바디 | 응답 | 인증 |
|---|---|---|---|---|---|
| GET | /api/statistics/monthly?yearMonth= | 월별 통계(총수입/총지출/잔액, 카테고리별 집계는 여유 시) | — | 200 · {income, expense, balance, byCategory} | ✅ |

## 3. 요청/응답 예시

### 로그인 — POST /api/auth/login

요청:
```json
{ "email": "hong@moneylog.com", "password": "pass1234!" }
```

응답 (200):
```json
{
  "success": true,
  "message": "로그인에 성공했습니다.",
  "data": { "accessToken": "eyJhbGciOiJIUzI1NiJ9..." }
}
```

### 거래 등록 — POST /api/transactions

요청 (헤더에 `Authorization: Bearer ...` 포함):
```json
{
  "type": "EXPENSE",
  "amount": 12000,
  "categoryId": 3,
  "description": "점심 - 김치찌개",
  "transactionDate": "2026-07-08"
}
```

응답 (201):
```json
{
  "success": true,
  "message": "거래내역이 등록되었습니다.",
  "data": {
    "id": 42,
    "type": "EXPENSE",
    "amount": 12000,
    "categoryId": 3,
    "categoryName": "식비",
    "description": "점심 - 김치찌개",
    "transactionDate": "2026-07-08",
    "createdAt": "2026-07-08T12:31:05"
  }
}
```

### 거래 목록 — GET /api/transactions?yearMonth=2026-07&page=0&size=20

응답 (200):
```json
{
  "success": true,
  "message": "거래내역 목록을 조회했습니다.",
  "data": {
    "transactions": [
      { "id": 42, "type": "EXPENSE", "amount": 12000, "categoryId": 3, "categoryName": "식비", "description": "점심 - 김치찌개", "transactionDate": "2026-07-08" }
    ]
  },
  "meta": {
    "pagination": { "page": 0, "size": 20, "totalItems": 1, "totalPages": 1, "hasNext": false, "hasPrev": false }
  }
}
```

### 월별 통계 — GET /api/statistics/monthly?yearMonth=2026-07

응답 (200):
```json
{
  "success": true,
  "message": "월별 통계를 조회했습니다.",
  "data": {
    "income": 2500000,
    "expense": 830000,
    "balance": 1670000,
    "byCategory": [
      { "categoryName": "식비", "total": 420000 },
      { "categoryName": "교통", "total": 180000 }
    ]
  }
}
```

## 4. 공통 응답 규약

### 성공 응답

```json
{ "success": true, "message": "...에 성공했습니다.", "data": { } }
```

목록 조회는 `meta.pagination`을 추가한다: `page`, `size`, `totalItems`, `totalPages`, `hasNext`, `hasPrev`.

### 에러 응답

```json
{ "success": false, "code": "TRANSACTION_NOT_FOUND", "message": "거래내역을 찾을 수 없습니다.", "data": null }
```

### 표준 에러 코드

| HTTP 상태 | code | 상황 |
|---|---|---|
| 400 | VALIDATION_ERROR | 입력 검증 실패(금액 ≤ 0, 날짜 누락 등) |
| 401 | INVALID_CREDENTIALS | 로그인 시 이메일/비밀번호 불일치 |
| 401 | UNAUTHORIZED | 토큰 없음/만료 |
| 403 | FORBIDDEN | 본인 데이터가 아님 |
| 409 | DUPLICATE_EMAIL | 이미 가입된 이메일 |
| 404 | CATEGORY_NOT_FOUND | 존재하지 않는 카테고리 |
| 404 | TRANSACTION_NOT_FOUND | 존재하지 않는 거래 |

## 5. 화면 흐름 및 화면-API 매핑

```
[로그인 화면] --(로그인 성공, 토큰 저장)--> [거래 목록 화면]
                                                │
                                    ┌───────────┴───────────┐
                                    ▼                       ▼
                            [거래 등록 화면]         [월별 통계 화면(여유 시)]
```

| 화면 | 사용자 행동 | 호출 API |
|---|---|---|
| 로그인 | 회원가입 | POST /api/auth/signup |
| 로그인 | 로그인 → 토큰 저장 | POST /api/auth/login |
| 거래 목록 | 이번 달 목록 로드 | GET /api/transactions?yearMonth=... |
| 거래 목록 | 필터용 카테고리 로드 | GET /api/categories |
| 거래 목록 | 항목 삭제 | DELETE /api/transactions/{id} |
| 거래 등록 | 카테고리 선택지 로드 | GET /api/categories |
| 거래 등록 | 저장 | POST /api/transactions |
| 월별 통계(여유) | 이번 달 집계 로드 | GET /api/statistics/monthly?yearMonth=... |

## 6. 개발 우선순위

1. 인증 (signup, login)
2. 카테고리 (거래가 참조하므로 먼저)
3. 거래 CRUD
4. 목록 필터/페이징 (여유 시)
5. 통계
6. 프론트 연동 (화면 2종)
