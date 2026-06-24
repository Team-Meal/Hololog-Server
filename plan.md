# diets 도메인 개발 계획

## API 명세

| 메서드 | 엔드포인트 | 기능 | 권한 | 응답 |
|--------|-----------|------|------|------|
| POST | `/diets` | 식단 작성 | NUTRITIONIST | 204 |
| PATCH | `/diets/{dietId}` | 식단 수정 | NUTRITIONIST | 200 |
| DELETE | `/diets/{dietId}` | 식단 삭제 | NUTRITIONIST | 204 |
| GET | `/diets` | 식단 목록 조회 | NUTRITIONIST | 200 |
| GET | `/diets/{dietId}` | 식단 단건 조회 | NUTRITIONIST | 200 |
| POST | `/diets/{dietId}/exports` | 식단 출력 (PDF/EXCEL/IMAGE) | NUTRITIONIST | 200 |
| POST | `/diets/{dietId}/leftovers` | 식단별 잔반량 입력 | NUTRITIONIST | 204 |
| GET | `/diets/{dietId}/leftovers` | 식단별 잔반량 조회 | NUTRITIONIST | 200 |

> AI 식단 추천 (`POST /diets/recommendations`) 은 별도 일정 진행

---

## 패키지 구조

```
domain/diet/
├── controller/   DietController.kt
├── dto/          CreateDietRequest, UpdateDietRequest, DietResponse, DietListResponse
│                 CreateDietLeftoverRequest, DietLeftoverResponse
│                 ExportDietRequest, DietExportResponse
├── entity/       Diet.kt, DietExportFormat.kt
├── exception/    DietNotFoundException.kt
├── repository/   DietRepository.kt
└── service/      CreateDiet, GetDiet, GetDietList, UpdateDiet, DeleteDiet
                  CreateDietLeftover, GetDietLeftover
                  ExportDiet

domain/leftover/
├── entity/       Leftover.kt  ← mealPlan nullable, diet: Diet? FK 추가
└── repository/   LeftoverRepository.kt  ← 신규 생성
```

---

## 엔티티 설계

### Diet
```kotlin
@Entity @Table(name = "diet")
class Diet(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false) val member: Member,
    @Column(nullable = false, length = 100) var name: String,
    @Column(columnDefinition = "TEXT") var description: String? = null,
    @Column(nullable = false) var dietDate: LocalDate,
) : BaseEntity()
```

### Leftover (기존 엔티티 수정)
```kotlin
// mealPlan → nullable, diet: Diet? 추가
@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "meal_plan_id") val mealPlan: MealPlan? = null,
@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "diet_id") val diet: Diet? = null,
```

### DietExportFormat
```kotlin
enum class DietExportFormat { PDF, EXCEL, IMAGE }
```

---

## 구현 체크리스트

### Phase 1 — Entity & Repository
- [x] `Diet` 엔티티 (`domain/diet/entity/Diet.kt`)
- [x] `DietExportFormat` enum (`domain/diet/entity/DietExportFormat.kt`)
- [x] `Leftover` 엔티티 수정 — `diet: Diet?` FK 추가, `mealPlan` nullable 처리
- [x] `DietRepository` (`domain/diet/repository/DietRepository.kt`)
- [x] `LeftoverRepository` (`domain/leftover/repository/LeftoverRepository.kt`)

### Phase 2 — 예외 처리
- [x] `DietNotFoundException` (`domain/diet/exception/DietNotFoundException.kt`)
- [x] `GlobalExceptionHandler`에 `DietNotFoundException` 핸들러 추가

### Phase 3 — 기본 CRUD
- [x] `CreateDietRequest` / `CreateDietService` + Impl
- [x] `DietResponse` / `GetDietService` + Impl
- [x] `DietListResponse` / `GetDietListService` + Impl
- [x] `UpdateDietRequest` / `UpdateDietService` + Impl
- [x] `DeleteDietService` + Impl

### Phase 4 — 잔반량
- [x] `CreateDietLeftoverRequest` / `CreateDietLeftoverService` + Impl
- [x] `DietLeftoverResponse` / `GetDietLeftoverService` + Impl

### Phase 5 — Controller
- [x] `DietController` (CRUD 5개 + 잔반량 2개 + exports 1개)

### Phase 6 — 식단 출력
- [x] export 라이브러리 의존성 추가 (`build.gradle.kts`) — poi-ooxml 5.3.0, itext-core 9.1.0, jfreechart 1.5.5
- [x] `ExportDietRequest` / `DietExportResponse`
- [x] `ExportDietService` + Impl (stub: URL 포맷만 반환, 파일 생성 TODO)
- [x] `DietController`에 `POST /diets/{dietId}/exports` 추가
