# 수상권 강화 구현 TODO

> 대상·최우수 조건: **발주 자동계산 + KAMIS 실연동** 둘 다 실제로 동작해야 함
> 우선순위: 코어 실동작 → 포장 순서 절대 뒤집지 않기

---

## Day 1 — 발주 자동계산 코어 (필수·실동작 ①)

### 1-1. 엔티티 필드 추가

**`Ingredient` 엔티티** (`domain/ingredient/entity/Ingredient.kt`)
- [ ] `origin: String?` — 원산지/지역 (예: "전남 지역농가", "광주 로컬푸드")
- [ ] `isLocalProduce: Boolean = false` — 지역 농산물 여부
- [ ] `unitPrice: Int? = null` — kg당 단가 (KAMIS 연동 전 수동 입력, 연동 후 자동 갱신)

**`IngredientPlanItem` 엔티티** (`domain/ingredient/entity/IngredientPlanItem.kt`)
- [ ] `shortfallQuantity: Double = 0.0` — 부족량 = max(필요량 - 재고, 0)
- [ ] `orderQuantity: Double = 0.0` — 발주량 = 부족량 × 1.05
- [ ] `estimatedCost: Int? = null` — 예상비용 = 발주량 × 단가
- [ ] `reason: String? = null` — 발주 근거 (예: "유통기한 임박", "6월 제철", "재고 부족")

### 1-2. 발주 자동계산 서비스

**`CalculateOrderPlanService`** (신규, `domain/ingredient/service/`)

계산식:
```
필요량   = MealIngredient.usedQuantity × 급식 인원 (MealPlan 기준)
부족량   = max(필요량 - Ingredient.quantity, 0)
발주량   = 부족량 × 1.05
예상비용 = 발주량 × Ingredient.unitPrice  (unitPrice 없으면 null)
```

- [ ] `CalculateOrderPlanService` 인터페이스
- [ ] `CalculateOrderPlanServiceImpl` — 위 계산식으로 IngredientPlanItem 자동 갱신
- [ ] 재고(Ingredient.quantity) 변경 시 연결된 IngredientPlanItem 재계산

**`GenerateOrderPlanFromMealService`** (신규, `domain/ingredient/service/`)

- [ ] `GenerateOrderPlanFromMealService` 인터페이스
- [ ] `GenerateOrderPlanFromMealServiceImpl`
  - Meal → MealIngredient 목록 조회
  - 각 식재료별 필요량 집계
  - Ingredient 재고와 비교 → 부족량/발주량/예상비용 계산
  - IngredientPlan + IngredientPlanItem 자동 생성
  - reason 필드 자동 설정 (유통기한 3일 이내 → "유통기한 임박", 제철 → "제철 우선")

### 1-3. DTO 추가

- [ ] `OrderCalculationResponse` — 발주 계획 계산 결과 응답
  ```
  필드: ingredientName, requiredQuantity, currentStock, shortfallQuantity,
        orderQuantity, unitPrice, estimatedCost, reason, origin, isLocalProduce
  ```
- [ ] `GenerateOrderPlanRequest` — 식단 ID + 급식 인원 수

### 1-4. API 엔드포인트 (IngredientPlanController)

- [ ] `POST /ingredients/plans/generate-from-meal` — 식단 기반 발주 계획 자동 생성
- [ ] `GET /ingredients/plans/{planId}/calculation` — 계획별 계산 결과 조회 (부족량/발주량/비용)
- [ ] `PATCH /ingredients/{id}/quantity` — 재고 수정 → 연결 계획 자동 재계산

---

## Day 2 — KAMIS 가격 실연동 (필수·실동작 ②)

### 2-1. KAMIS FeignClient

**`KamisClient`** (신규, `domain/ingredient/client/`)

- [x] KAMIS API 키 환경변수로 설정 (`application.properties` — `KAMIS_API_KEY`, `KAMIS_CERT_ID`)
- [x] `KamisProperties` ConfigurationProperties (`global/config/KamisProperties.kt`)
- [x] `KamisClient` FeignClient 구현 — 부류별 조회, String 응답으로 받아 ObjectMapper 직접 파싱
  - 대상 품목: 감자, 양파, 당근, 오이, 토마토, 수박, 배추, 무, 고구마, 파
- [x] `KamisApiResponse` / `KamisData` / `KamisItem` DTO

### 2-2. 가격 서비스

**`KamisPriceService`** (신규, `domain/ingredient/service/`)

- [x] `KamisPriceService` 인터페이스 (getPrices / syncPrices / getPriceAlerts)
- [x] `KamisPriceServiceImpl`
  - Redis `@RedisHash` 캐시 적용 (TTL 1시간, `KamisPriceCache`)
  - 전일 대비 18% 이상 상승 시 가격 급등 플래그
  - 가격 급등 품목 → 대체 품목 추천 맵 (`IngredientPriceResponse.ALTERNATIVES_MAP`)
- [x] `KamisPriceCacheRepository` (`CrudRepository<KamisPriceCache, String>`)

### 2-3. 단가 연동

- [ ] `Ingredient.unitPrice` 자동 갱신 — syncPrices 호출 시 재고 단가 일괄 업데이트
- [ ] `IngredientPlanItem.estimatedCost` 재계산 — 단가 갱신 후 연동 (Day 1 완료 후)

### 2-4. 예산 검증 연동

- [ ] `ValidateBudgetService` (신규 or 기존 BudgetValidator 확장)
  - 1인당 예상 단가 = 총 식재료비 ÷ 급식 인원
  - 예산 초과 여부 반환
  - 가격 급등 품목 + 대체 추천 포함

### 2-5. API 엔드포인트

- [x] `GET /ingredients/prices/kamis` — KAMIS 단가 목록 조회 (캐시 우선)
- [x] `POST /ingredients/prices/sync` — 단가 수동 동기화 트리거
- [x] `GET /ingredients/prices/alerts` — 가격 급등 품목 + 대체 추천 목록

> ⚠️ `KAMIS_CERT_ID` 환경변수 미설정 시 부팅 실패 — API 신청 시 발급된 인증 ID 확인 필요

---

## Day 3 — AI 식단 추천 룰 스코어링 (필수 ③)

### 3-1. 룰 스코어링 엔진

**`MealScoringService`** (신규, `domain/meal/service/`)

점수화 항목 (가중치 합산):

| 조건 | 가중치 | 근거 데이터 |
|---|---|---|
| 제철 품목 | 30 | 농사로 API or 하드코딩 월별 제철표 |
| KAMIS 가격 안정 | 25 | KAMIS 가격 (전주 대비 ±5% 이내) |
| 유통기한 임박 재고 우선 | 20 | Ingredient.expirationDate 3일 이내 |
| 예산 범위 이내 | 15 | Budget vs estimatedCost |
| 학생 선호도 | 10 | MealSuggestion 이력 or 하드코딩 |

- [ ] `MealScoringService` 인터페이스
- [ ] `MealScoringServiceImpl` — 위 가중치 기반 스코어 계산
- [ ] `MealScoreResult` DTO — 메뉴별 점수 + 각 항목별 점수 내역

### 3-2. 추천 이유 생성

**`GenerateMealReasonService`** (신규, `domain/meal/service/`)

- [ ] 스코어 결과를 데이터 출처 명시 문장으로 변환
  ```
  예시:
  "[학교 재고] 감자 8kg 유통기한 3일 → 카레에 우선 반영"
  "[농사로 제철] 오이는 6월 제철, 가격 변동 안정"
  "[KAMIS 가격] 양파·당근 현재 단가 안정 → 발주 부담 낮음"
  "[예산 검증] 1인당 ₩1,850 / 설정 ₩1,900 이내"
  ```
- [ ] AI 서버(`AiServerClient`)에 스코어 결과 + 이유 전달 → 자연어 문장화

### 3-3. 기존 GenerateAiMealPlanServiceImpl 연동

- [ ] `GenerateAiMealPlanRequest`에 스코어링 조건 필드 추가
  - `prioritizeLocalProduce: Boolean`
  - `prioritizeSeasonal: Boolean`
  - `prioritizeExpiringStock: Boolean`
  - `budgetPerPerson: Int?`
- [ ] `AiGeneratePlanRequest`에 스코어 결과 포함하여 AI 서버 전달

---

## Day 4 — 데이터 출처 가시화 + 산식 정의

### 4-1. 제철 정보 처리

- [ ] 월별 제철 품목 테이블 (하드코딩 or 농사로 API)
  ```kotlin
  // 예시
  val SEASONAL_MAP = mapOf(
      6 to listOf("오이", "수박", "토마토", "감자", "양상추"),
      7 to listOf("수박", "옥수수", "토마토", "가지", "오이"),
      ...
  )
  ```
- [ ] `SeasonalIngredientService` — 월 기준 제철 품목 여부 판단

### 4-2. 지역 농산물 활용률 계산

- [ ] `DashboardStatsService` (신규, `domain/dashboard/service/`)
  - 지역 농산물 사용률 = 지역 농산물 식재료비 ÷ 전체 식재료비 × 100
  - 제철 반영률 = 제철 품목 수 ÷ 전체 사용 품목 수 × 100
  - 폐기 위험 재고 수 = expirationDate 3일 이내 Ingredient 수
  - 예산 절감 예상액 = Σ(가격 급등 품목 대체로 줄인 비용)

### 4-3. dashboard 도메인 생성

- [ ] `domain/dashboard/` 패키지 생성
- [ ] `DashboardController`, `DashboardResponse`, `DashboardStatsService`

---

## Day 5 — 포장: 화면 + 더미 정리

### 5-1. 재고 뱃지용 필드 확인

- [ ] `IngredientResponse`에 `origin`, `isLocalProduce`, 유통기한 상태 포함

### 5-2. 더미 데이터 전량 제거

- [ ] 테스트/더미 데이터 제거 확인
  - `ㅇㅇㅇ`, `123`, `Merge pull request`, `₩1`, 의미없는 수치

### 5-3. 내보내기 리포트 보완

- [ ] `ExportDietService` stub → 실제 발주 계획서 출력 연결
- [ ] 지역 농산물 활용 리포트 출력 포맷 정의

---

## Day 6 — 발표 준비

### 6-1. 시연 시나리오 (3분)

```
1. 재고 화면에서 감자 8kg → 5kg으로 수정
2. 발주 계획표 → 부족량/발주량/비용이 자동 재계산됨을 보여줌
3. KAMIS 가격 탭 → 토마토 가격 급등, 대체 품목 오이 추천
4. AI 식단 → 추천 이유에 데이터 출처 명시 확인
5. 예산 검증 → 1인 단가 / 설정 예산 비교
```

### 6-2. 질의응답 답변 준비

- [ ] "AI가 식단 짠 거 아닌가요?" → 룰 스코어링 엔진 설명 준비
- [ ] "10.5kg 발주량은 어떻게?" → 계산식 슬라이드 준비
- [ ] "가격 데이터 실제인가요?" → KAMIS API 화면 준비
- [ ] "지역 농산물 사용률 68%, 근거는?" → 산식 툴팁 준비
- [ ] "잔반이 실제로 줄어드나요?" → "예측 아닌 위험 신호" 포지셔닝 준비

---

## 완성도 체크리스트

```
[필수·실동작]
[ ] 식단→발주 부족량·발주량·비용이 실제로 계산되는가?
[ ] 재고를 바꾸면 발주량이 실시간 재계산되는가?
[ ] KAMIS 가격이 실연동되어 비용·예산에 반영되는가?
[ ] AI 추천이 룰 스코어링으로 동작하고 이유가 데이터에 근거하는가?

[포장·완성도]
[ ] 모든 지표에 산식이 정의되어 있는가?
[ ] 공공데이터 출처가 화면/슬라이드에 명확한가?
[ ] 지역 농산물 사용률·폐기 감소·예산 절감이 수치로 보이는가?
[ ] 더미데이터가 전량 정리됐는가?
[ ] Ingredient에 origin, isLocalProduce, unitPrice 필드가 있는가?

[발표]
[ ] 3분 시연 시나리오가 막힘없이 돌아가는가?
[ ] 7개 예상 질의응답을 전부 답할 수 있는가?
[ ] KAMIS API 장애 대비 Redis 캐시 스냅샷이 있는가?
```

---

## 핵심 계산식 (발표·질의응답 필수 암기)

```
필요량   = MealIngredient.usedQuantity × 급식 인원
부족량   = max(필요량 - Ingredient.quantity, 0)
발주량   = 부족량 × 1.05
예상비용 = 발주량 × KAMIS 단가

지역 농산물 사용률 = 지역 농산물 식재료비 ÷ 전체 식재료비 × 100
제철 반영률       = 제철 품목 수 ÷ 전체 사용 품목 수 × 100
1인당 단가        = 총 식재료비 ÷ 급식 인원
폐기 감소 예상량   = (직전 평균 폐기율 - AI 식단 적용 후 예상 폐기율) × 식수량
예산 절감 예상액   = Σ(가격 급등 품목 대체 시 절감 비용)
```
