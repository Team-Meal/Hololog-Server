# Hororog Server - Project Context

**Please respond and work in Korean.**

## Language Requirement

You MUST always respond in Korean (한국어). This is mandatory and cannot be overridden.

## Project Overview

**Hororog** - School Meal Management System Server

A platform that digitalizes the entire school meal process — from meal planning and ingredient ordering to budget management and food waste reduction.

### Meal Process

**Meal Planning** (Nutritionist) → **Ingredient Ordering** (Public Procurement Platform) → **Delivery** (Local Suppliers) → **Cooking** (Chef) → **Serving** → **Food Waste Collection** → **Waste Disposal (kg-based cost)**

## Tech Stack

- **Language**: Kotlin
- **Framework**: Spring Boot 4.0.6
- **Database**: MariaDB (JPA)
- **Cache**: Redis
- **Build Tool**: Gradle (Kotlin DSL)
- **Authentication**: JWT + Spring Security
- **Code Formatting**: KtLint

## Domain Modules

| Domain | Description |
|--------|-------------|
| `meal` | Monthly meal schedule design and management |
| `ingredient` | Ingredient inventory and order planning |
| `budget` | Meal budget management |
| `supplier` | Supplier info and unit pricing |
| `leftover` | Food waste data collection and analysis |
| `member` | Members (nutritionist / admin) |
| `dashboard` | Statistics and dashboard |

## Architecture

3-layer architecture.

### Layer Flow

```
Controller → Service → Repository
```

### Package Structure

```
team.nongchun.hororog
├── controller/   # Request/response handling (RestController)
├── service/      # Business logic
├── repository/   # DB access (JpaRepository)
├── entity/       # JPA entities
├── dto/          # Request/response DTOs
└── client/       # External API FeignClients
```

## Naming Conventions

- **Controller**: `Domain + Controller` (e.g. `MealPlanController`)
- **Service**: `Verb + Domain + Service` — one class per use case, single `execute()` method (e.g. `CreateMealPlanService`, `DeleteMealPlanService`)
- **Repository**: `Domain + Repository` (e.g. `MealPlanRepository`)
- **Entity**: `Domain` (e.g. `MealPlan`)
- **Request DTO**: `Verb + Domain + Request` (e.g. `CreateMealPlanRequest`)
- **Response DTO**: `Domain + Response` (e.g. `MealPlanResponse`)

```kotlin
@Service
@Transactional
class CreateMealPlanService(
    private val mealPlanRepository: MealPlanRepository,
) {
    fun execute(request: CreateMealPlanRequest): MealPlanResponse {
        // ...
    }
}
```

## Transaction Management

- Open transactions in the `service` layer only.
- Never open transactions in the `repository` layer.

## Git Conventions

Format: `type(scope): Korean description`

| Type | Description |
|------|-------------|
| `feat` | New feature |
| `fix` | Bug fix |
| `refactor` | Refactoring (no functional change) |
| `docs` | Documentation |
| `test` | Add/update tests |
| `chore` | Build config, dependencies, etc. |

- Description in Korean, no period, noun ending, under 50 characters

## Code Conventions

### Kotlin

- Use `data class` for DTOs, avoid mutable `var` — prefer `val`
- Use `companion object` for constants and factory methods
- Prefer named arguments for constructors with 3+ parameters
- Use `?.let`, `?.run` for null-safe chaining — avoid `!!` unless guaranteed non-null
- Use `enum class` for fixed status values (e.g. meal category, order status)

### Entity

- Use `@Column(nullable = false)` explicitly — never rely on defaults
- Audit fields (`createdAt`, `updatedAt`) go in a `BaseEntity` superclass with `@MappedSuperclass`
- Avoid bidirectional relationships unless necessary — use unidirectional with `@ManyToOne`
- Use `@Enumerated(EnumType.STRING)` for enum columns

```kotlin
@MappedSuperclass
abstract class BaseEntity {
    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    var updatedAt: LocalDateTime = LocalDateTime.now()
}
```

### DTO

- Separate Request and Response DTOs — never reuse the same class for both
- Use `from()` companion factory on Response DTOs to convert from entity

```kotlin
data class MealPlanResponse(
    val id: Long,
    val name: String,
) {
    companion object {
        fun from(entity: MealPlan) = MealPlanResponse(
            id = entity.id,
            name = entity.name,
        )
    }
}
```

### Exception Handling

- Define custom exceptions per domain (e.g. `MealPlanNotFoundException`)
- Handle globally via `@RestControllerAdvice`

```kotlin
class MealPlanNotFoundException : RuntimeException("Meal plan not found")
```

### Logging

- Use SLF4J via `private val logger = LoggerFactory.getLogger(javaClass)`
- Log at `info` for key business events, `error` for exceptions with stack trace

## New Feature Checklist

- [ ] Create Entity (`entity/`)
- [ ] Create Repository (`repository/`)
- [ ] Create Service (`service/`)
- [ ] Create Controller (`controller/`)
- [ ] Create Request/Response DTOs (`dto/`)
- [ ] Apply KtLint formatting
