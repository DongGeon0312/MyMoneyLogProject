# 회고 (Retrospective)

## Keep — 잘돼서 계속 유지하고 싶은 것

- 프로젝트 개발 시작 전에 요구사항 정의서를 쓰는게 큰 도움이 되는 것 같다.
- Git으로 계속 커밋하면서 기록을 남기는게 좋은 습관같다.

## Problem — 문제였던 것, 아쉬웠던 것

- 배포를 하려는데 프리티어로만 사용하다보니, 용량 문제가 많았다.
- 교안을 보며 진행하는데 까먹은 용어도 많고 막히는 부분이 꽤 많았다.

## Try — 다음엔 시도해볼 것

- 내가 원하는 기능들을 추가적으로 조금 더 구현해보고 추가해보고 싶다.

## 이번 프로젝트에서 직접 겪은 일 (참고용 메모)

- Windows에서 `.pem` 키 파일 권한 문제로 SSH 접속이 거부됨 → `icacls`로 권한을 제한해 해결
- Gradle Wrapper가 없어 로컬에 캐시된 Gradle 배포판으로 직접 생성
- `DataInitializer`에서 LAZY 로딩된 연관 엔티티를 트랜잭션 밖에서 접근해 `LazyInitializationException` 발생 → `@Transactional` 추가로 해결
- 운영(MySQL) 프로파일에서 `ddl-auto: validate`로 두었다가 스키마가 없어 부팅 실패 → 마이그레이션 도구가 없는 캡스톤 규모에 맞춰 `update`로 조정
- GitHub Actions에서 GHCR 이미지 이름에 대문자가 섞여 push 실패 → 소문자로 변환하는 스텝 추가
- SSH(22) 보안그룹을 "내 IP"로 제한했더니 GitHub Actions 러너가 접속하지 못해 배포가 타임아웃 → Anywhere(0.0.0.0/0)로 변경

## 자기평가 체크리스트

- [x] 회원가입/로그인(JWT, BCrypt)
- [x] 카테고리 기본 시드 + CRUD
- [x] 거래내역 CRUD
- [x] 월별 목록 조회
- [x] 월별 통계
- [x] 본인 데이터만 접근하는 인가
- [x] 입력 검증 + 전역 예외 처리
- [x] 프론트 필수 화면 2종 이상
- [x] Swagger 문서화
- [x] Docker + GitHub Actions + EC2 배포
