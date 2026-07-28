# 회고 (Retrospective)

## Keep — 잘돼서 계속 유지하고 싶은 것

(예: 실제로 앱을 실행/API 호출까지 해보며 검증한 뒤 커밋한 습관, EC2 트러블슈팅을 스스로 해결한 것 등)

-
-

## Problem — 문제였던 것, 아쉬웠던 것

(구체적으로 적을수록 좋습니다. 예: "SecurityConfig에서 permitAll() 순서 때문에 401이 났고, 필터 순서를 조정해 해결했다"처럼)

-
-

## Try — 다음엔 시도해볼 것

(예: 도전 과제로 넘긴 부분, 다음 프로젝트에서 미리 준비하고 싶은 것)

-
-

## 이번 프로젝트에서 직접 겪은 일 (참고용 메모)

아래는 실제로 진행하며 마주쳤던 이슈들입니다. 회고에 참고하세요.

- Windows에서 `.pem` 키 파일 권한 문제로 SSH 접속이 거부됨 → `icacls`로 권한을 제한해 해결
- Gradle Wrapper가 없어 로컬에 캐시된 Gradle 배포판으로 직접 생성
- `DataInitializer`에서 LAZY 로딩된 연관 엔티티를 트랜잭션 밖에서 접근해 `LazyInitializationException` 발생 → `@Transactional` 추가로 해결
- 운영(MySQL) 프로파일에서 `ddl-auto: validate`로 두었다가 스키마가 없어 부팅 실패 → 마이그레이션 도구가 없는 캡스톤 규모에 맞춰 `update`로 조정
- GitHub Actions에서 GHCR 이미지 이름에 대문자가 섞여 push 실패 → 소문자로 변환하는 스텝 추가
- SSH(22) 보안그룹을 "내 IP"로 제한했더니 GitHub Actions 러너가 접속하지 못해 배포가 타임아웃 → Anywhere(0.0.0.0/0)로 변경

## 자기평가 체크리스트

- [ ] 회원가입/로그인(JWT, BCrypt)
- [ ] 카테고리 기본 시드 + CRUD
- [ ] 거래내역 CRUD
- [ ] 월별 목록 조회
- [ ] 월별 통계
- [ ] 본인 데이터만 접근하는 인가
- [ ] 입력 검증 + 전역 예외 처리
- [ ] 프론트 필수 화면 2종 이상
- [ ] Swagger 문서화
- [ ] Docker + GitHub Actions + EC2 배포
