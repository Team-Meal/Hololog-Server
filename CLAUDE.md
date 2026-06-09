# Hororog Server - 프로젝트 컨텍스트

**항상 한국어로 응답하고 작업하세요.**

## 언어 요구사항

모든 응답, 설명, 주석은 반드시 한국어(Korean)로 작성해야 합니다.

## 프로젝트 개요

**Hororog** - 학교 급식 통합 관리 시스템 서버

영양사가 식단을 설계하고, 식재료를 발주하고, 예산을 관리하고, 잔반을 줄이는 전 과정을 디지털화하는 급식 관리 플랫폼입니다.

### 급식 프로세스

**식단 설계** (영양사) → **식재료 발주** (공공급식플랫폼/나라장터) → **납품** (지역 공급업체) → **조리** (조리사) → **배식** → **잔반 수거** → **처리업체 위탁**

## 기술 스택

- **언어**: Kotlin
- **프레임워크**: Spring Boot 4.0.6
- **데이터베이스**: MariaDB (JPA)
- **캐시**: Redis
- **빌드 도구**: Gradle (Kotlin DSL)
- **인증**: JWT + Spring Security
- **코드 포맷**: KtLint

## 도메인 모듈

| 도메인 | 설명 |
|--------|------|
| `meal-plan` | 월간 식단 설계 및 관리 |
| `ingredient` | 식재료 보유 현황 및 발주 계획 |
| `budget` | 급식 예산 관리 |
| `supplier` | 공급업체 정보 및 단가 |
| `leftover` | 잔반 데이터 수집 및 분석 |
| `member` | 회원 (영양사/관리자) |
| `dashboard` | 통계 및 대시보드 |

## 아키텍처

3계층 아키텍처를 사용합니다.

### 레이어 흐름

```
Controller → Service → Repository
```

## 네이밍 컨벤션

- **Controller**: `도메인 + Controller` (예: `MealPlanController`)
- **Service**: `도메인 + Service` (예: `MealPlanService`)
- **Repository**: `도메인 + Repository` (예: `MealPlanRepository`)
- **Entity**: `도메인` (예: `MealPlan`)
- **Request DTO**: `동사 + 도메인 + Request` (예: `CreateMealPlanRequest`)
- **Response DTO**: `도메인 + Response` (예: `MealPlanResponse`)

## 트랜잭션 관리

- 트랜잭션은 `service` 레이어에서만 시작합니다.
- `repository` 레이어에서 트랜잭션을 열지 않습니다.

## Git 컨벤션

형식: `type(scope): 한국어 설명`

| Type | 설명 |
|------|------|
| `feat` | 새 기능 추가 |
| `fix` | 버그 수정 |
| `refactor` | 리팩토링 (기능 변경 없음) |
| `docs` | 문서 변경 |
| `test` | 테스트 추가/수정 |
| `chore` | 빌드 설정, 의존성 등 |

- 설명은 한국어, 마침표 없음, 명사형 종료, 50자 이내

## 신규 기능 체크리스트

- [ ] Entity 생성 (`entity/`)
- [ ] Repository 생성 (`repository/`)
- [ ] Service 생성 (`service/`)
- [ ] Controller 생성 (`controller/`)
- [ ] Request/Response DTO 생성 (`dto/`)
- [ ] KtLint 포맷 적용
