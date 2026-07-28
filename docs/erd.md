# 머니로그(MoneyLog) ERD / 도메인 설계

## 1. 엔티티 개요
| 엔티티 | 설명 |
|---|---|
| User | 로그인 주체이자 모든 데이터의 소유자 |
| Category | 거래를 분류하는 카테고리 (사용자별 소유) |
| Transaction | 수입/지출 거래 1건 |

## 2. 엔티티 상세

### User (사용자)

| 컬럼 | 타입 | 제약 | 설명 |
|---|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT | 사용자 식별자 |
| email | VARCHAR(255) | NOT NULL, UNIQUE | 로그인 ID |
| password | VARCHAR(255) | NOT NULL | BCrypt 해시 저장 |
| nickname | VARCHAR(50) | NOT NULL | 표시용 이름 |
| created_at | DATETIME | NOT NULL | 가입 시각 |

### Category (카테고리)

| 컬럼 | 타입 | 제약 | 설명 |
|---|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT | 카테고리 식별자 |
| user_id | BIGINT | FK → users.id, NOT NULL | 소유 사용자 |
| name | VARCHAR(50) | NOT NULL | 카테고리 이름 (식비, 급여 등) |
| type | ENUM('INCOME','EXPENSE') | NOT NULL | 수입/지출 구분 |
| created_at | DATETIME | NOT NULL | 생성 시각 |

### Transaction (거래내역)

| 컬럼 | 타입 | 제약 | 설명 |
|---|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT | 거래 식별자 |
| user_id | BIGINT | FK → users.id, NOT NULL | 기록한 사용자 |
| category_id | BIGINT | FK → categories.id, NOT NULL | 분류 카테고리 |
| type | ENUM('INCOME','EXPENSE') | NOT NULL | 수입/지출 |
| amount | BIGINT | NOT NULL, > 0 | 금액(원 단위) |
| description | VARCHAR(255) | NULL 허용 | 메모/설명 |
| transaction_date | DATE | NOT NULL | 거래 발생일 |
| created_at | DATETIME | NOT NULL | 등록 시각 |
| updated_at | DATETIME | NOT NULL | 수정 시각 |

## 3. 관계

모든 관계는 1:N이며, FK는 N쪽 테이블에 둔다.

| 관계 | 의미 |
|---|---|
| User 1:N Category | 한 사용자가 여러 카테고리를 가진다 |
| User 1:N Transaction | 한 사용자가 여러 거래를 가진다 |
| Category 1:N Transaction | 한 카테고리에 여러 거래가 속한다 |

Transaction은 user_id(누구의 거래인지 — 인가의 근거)와 category_id(무슨 분류인지 — 통계·필터의 근거)를 모두 가진다.

## 4. ERD 다이어그램

```
┌─────────────────┐
│      USER       │
│──────────────────│
│ PK id            │
│    email (UQ)    │
│    password      │
│    nickname      │
│    created_at    │
└────────┬─────────┘
     1   │   1
   ┌─────┴─────┐
   │ N         │ N
┌──▼──────────┐  ┌──▼───────────────────┐
│  CATEGORY   │  │     TRANSACTION       │
│─────────────│  │───────────────────────│
│ PK id       │1 │ PK id                 │
│ FK user_id  │──<│ FK user_id           │
│    name     │N │ FK category_id        │
│    type     │  │    type               │
│    created_at│  │    amount             │
└─────────────┘  │    description        │
                  │    transaction_date   │
                  │    created_at         │
                  │    updated_at         │
                  └───────────────────────┘
```

## 5. DDL (MySQL 8 기준)

```sql
CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE categories (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    type ENUM('INCOME','EXPENSE') NOT NULL,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_categories_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE transactions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    type ENUM('INCOME','EXPENSE') NOT NULL,
    amount BIGINT NOT NULL,
    description VARCHAR(255) NULL,
    transaction_date DATE NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_transactions_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_transactions_category FOREIGN KEY (category_id) REFERENCES categories(id),
    KEY idx_tx_user_date (user_id, transaction_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

## 6. 설계 결정 사항

| 고민 | 결정 | 이유 |
|---|---|---|
| 금액 타입 | long(BIGINT), 원 단위 정수 | 원화는 소수점이 없음. double 오차 위험 |
| 날짜 타입 | transactionDate는 LocalDate(DATE), 생성/수정 시각은 LocalDateTime(DATETIME) | 거래일은 날짜만 중요, 이력은 시각까지 필요 |
| type을 ENUM으로 | `@Enumerated(EnumType.STRING)` | 값이 2개로 고정, 문자열 저장으로 순서 변경 위험 제거 |
| 삭제 정책 | hard delete | 개인 가계부라 삭제 이력 추적 요구 없음 |
| 카테고리 삭제 시 거래 처리 | (결정 필요 — 구현 단계에서 정책 확정) | 삭제 막기 / 미분류 이동 / 함께 삭제 중 선택 |

## 7. 향후 확장 (도전 과제)

기본 과제를 모두 마친 뒤, 예산 기능을 도전 과제로 추가한다면 `Budget` 엔티티(user_id, category_id nullable, year_month, limit_amount)를 이 문서에 추가한다.
