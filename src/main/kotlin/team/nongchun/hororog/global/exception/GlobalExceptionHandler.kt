package team.nongchun.hororog.global.exception

import feign.FeignException
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import team.nongchun.hororog.domain.budget.exception.BudgetNotFoundException
import team.nongchun.hororog.domain.budget.exception.InvalidBudgetException
import team.nongchun.hororog.domain.diet.exception.DietNotFoundException
import team.nongchun.hororog.domain.ingredient.exception.IngredientNotFoundException
import team.nongchun.hororog.domain.ingredient.exception.IngredientPlanNotFoundException
import team.nongchun.hororog.domain.ingredient.exception.InvalidIngredientPlanException
import team.nongchun.hororog.domain.leftover.exception.LeftoverNotFoundException
import team.nongchun.hororog.domain.meal.exception.AiMealGenerationAlreadyInProgressException
import team.nongchun.hororog.domain.meal.exception.AiMealGenerationAlreadySucceededException
import team.nongchun.hororog.domain.meal.exception.InvalidMealSuggestionStatusException
import team.nongchun.hororog.domain.meal.exception.MealAiGenerationNotFoundException
import team.nongchun.hororog.domain.meal.exception.MealNotFoundException
import team.nongchun.hororog.domain.meal.exception.MealSuggestionNotFoundException
import team.nongchun.hororog.domain.member.exception.EmailAlreadyExistsException
import team.nongchun.hororog.domain.member.exception.InvalidCredentialsException
import team.nongchun.hororog.domain.member.exception.InvalidSignupRoleException
import team.nongchun.hororog.domain.member.exception.InvalidTokenException
import team.nongchun.hororog.domain.member.exception.MemberNotFoundException
import team.nongchun.hororog.domain.member.exception.SignupRequestAlreadyPendingException
import team.nongchun.hororog.domain.member.exception.SignupRequestAlreadyProcessedException
import team.nongchun.hororog.domain.member.exception.SignupRequestNotFoundException
import team.nongchun.hororog.domain.order.exception.OrderPlanItemNotFoundException
import team.nongchun.hororog.domain.order.exception.OrderPlanNotFoundException
import team.nongchun.hororog.domain.supplier.exception.SupplierNotFoundException
import team.nongchun.hororog.global.exception.InvalidQuantityUnitException
import tools.jackson.core.JacksonException

@RestControllerAdvice
class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(BudgetNotFoundException::class)
    fun handleBudgetNotFound(e: BudgetNotFoundException): ResponseEntity<ErrorResponse> {
        logger.info("예산 조회 실패: {}", e.message)
        return toResponse(HttpStatus.NOT_FOUND, e.message)
    }

    @ExceptionHandler(InvalidBudgetException::class)
    fun handleInvalidBudget(e: InvalidBudgetException): ResponseEntity<ErrorResponse> {
        logger.info("잘못된 예산 입력: {}", e.message)
        return toResponse(HttpStatus.BAD_REQUEST, e.message)
    }

    @ExceptionHandler(DietNotFoundException::class)
    fun handleDietNotFound(e: DietNotFoundException): ResponseEntity<ErrorResponse> {
        logger.info("식단 조회 실패: {}", e.message)
        return toResponse(HttpStatus.NOT_FOUND, e.message)
    }

    @ExceptionHandler(LeftoverNotFoundException::class)
    fun handleLeftoverNotFound(e: LeftoverNotFoundException): ResponseEntity<ErrorResponse> {
        logger.info("잔반 조회 실패: {}", e.message)
        return toResponse(HttpStatus.NOT_FOUND, e.message)
    }

    @ExceptionHandler(EmailAlreadyExistsException::class)
    fun handleEmailAlreadyExists(e: EmailAlreadyExistsException): ResponseEntity<ErrorResponse> {
        logger.info("이메일 중복 가입 시도: {}", e.message)
        return toResponse(HttpStatus.CONFLICT, e.message)
    }

    @ExceptionHandler(MemberNotFoundException::class)
    fun handleMemberNotFound(e: MemberNotFoundException): ResponseEntity<ErrorResponse> {
        logger.info("회원 조회 실패: {}", e.message)
        return toResponse(HttpStatus.NOT_FOUND, e.message)
    }

    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleInvalidCredentials(e: InvalidCredentialsException): ResponseEntity<ErrorResponse> {
        logger.info("로그인 실패: {}", e.message)
        return toResponse(HttpStatus.UNAUTHORIZED, e.message)
    }

    @ExceptionHandler(InvalidTokenException::class)
    fun handleInvalidToken(e: InvalidTokenException): ResponseEntity<ErrorResponse> {
        logger.info("토큰 검증 실패: {}", e.message)
        return toResponse(HttpStatus.UNAUTHORIZED, e.message)
    }

    @ExceptionHandler(InvalidSignupRoleException::class)
    fun handleInvalidSignupRole(e: InvalidSignupRoleException): ResponseEntity<ErrorResponse> {
        logger.info("잘못된 회원가입 권한 선택: {}", e.message)
        return toResponse(HttpStatus.BAD_REQUEST, e.message)
    }

    @ExceptionHandler(SignupRequestAlreadyPendingException::class)
    fun handleSignupRequestAlreadyPending(e: SignupRequestAlreadyPendingException): ResponseEntity<ErrorResponse> {
        logger.info("가입 요청 중복 제출: {}", e.message)
        return toResponse(HttpStatus.CONFLICT, e.message)
    }

    @ExceptionHandler(SignupRequestNotFoundException::class)
    fun handleSignupRequestNotFound(e: SignupRequestNotFoundException): ResponseEntity<ErrorResponse> {
        logger.info("가입 요청 조회 실패: {}", e.message)
        return toResponse(HttpStatus.NOT_FOUND, e.message)
    }

    @ExceptionHandler(SignupRequestAlreadyProcessedException::class)
    fun handleSignupRequestAlreadyProcessed(e: SignupRequestAlreadyProcessedException): ResponseEntity<ErrorResponse> {
        logger.info("처리 완료된 가입 요청 재처리 시도: {}", e.message)
        return toResponse(HttpStatus.CONFLICT, e.message)
    }

    @ExceptionHandler(IngredientNotFoundException::class)
    fun handleIngredientNotFound(e: IngredientNotFoundException): ResponseEntity<ErrorResponse> {
        logger.info("식자재 조회 실패: {}", e.message)
        return toResponse(HttpStatus.NOT_FOUND, e.message)
    }

    @ExceptionHandler(IngredientPlanNotFoundException::class)
    fun handleIngredientPlanNotFound(e: IngredientPlanNotFoundException): ResponseEntity<ErrorResponse> {
        logger.info("식자재 계획표 조회 실패: {}", e.message)
        return toResponse(HttpStatus.NOT_FOUND, e.message)
    }

    @ExceptionHandler(InvalidIngredientPlanException::class)
    fun handleInvalidIngredientPlan(e: InvalidIngredientPlanException): ResponseEntity<ErrorResponse> {
        logger.info("잘못된 식자재 계획표 입력: {}", e.message)
        return toResponse(HttpStatus.BAD_REQUEST, e.message)
    }

    @ExceptionHandler(InvalidQuantityUnitException::class)
    fun handleInvalidQuantityUnit(e: InvalidQuantityUnitException): ResponseEntity<ErrorResponse> {
        logger.info("잘못된 단위 입력: {}", e.message)
        return toResponse(HttpStatus.BAD_REQUEST, e.message)
    }

    @ExceptionHandler(InvalidMealSuggestionStatusException::class)
    fun handleInvalidMealSuggestionStatus(e: InvalidMealSuggestionStatusException): ResponseEntity<ErrorResponse> {
        logger.info("잘못된 급식 추천 처리 상태 입력: {}", e.message)
        return toResponse(HttpStatus.BAD_REQUEST, e.message)
    }

    @ExceptionHandler(MealSuggestionNotFoundException::class)
    fun handleMealSuggestionNotFound(e: MealSuggestionNotFoundException): ResponseEntity<ErrorResponse> {
        logger.info("급식 제안 조회 실패: {}", e.message)
        return toResponse(HttpStatus.NOT_FOUND, e.message)
    }

    @ExceptionHandler(MealNotFoundException::class)
    fun handleMealNotFound(e: MealNotFoundException): ResponseEntity<ErrorResponse> {
        logger.info("급식 조회 실패: {}", e.message)
        return toResponse(HttpStatus.NOT_FOUND, e.message)
    }

    @ExceptionHandler(MealAiGenerationNotFoundException::class)
    fun handleMealAiGenerationNotFound(e: MealAiGenerationNotFoundException): ResponseEntity<ErrorResponse> {
        logger.info("AI 식단 생성 작업 조회 실패: {}", e.message)
        return toResponse(HttpStatus.NOT_FOUND, e.message)
    }

    @ExceptionHandler(AiMealGenerationAlreadyInProgressException::class)
    fun handleAiMealGenerationAlreadyInProgress(e: AiMealGenerationAlreadyInProgressException): ResponseEntity<ErrorResponse> {
        logger.info("AI 식단 생성 중복 요청: {}", e.message)
        return toResponse(HttpStatus.CONFLICT, e.message)
    }

    @ExceptionHandler(AiMealGenerationAlreadySucceededException::class)
    fun handleAiMealGenerationAlreadySucceeded(e: AiMealGenerationAlreadySucceededException): ResponseEntity<ErrorResponse> {
        logger.info("AI 식단 생성 재생성 요청: {}", e.message)
        return toResponse(HttpStatus.CONFLICT, e.message)
    }

    @ExceptionHandler(SupplierNotFoundException::class)
    fun handleSupplierNotFound(e: SupplierNotFoundException): ResponseEntity<ErrorResponse> {
        logger.info("공급처 조회 실패: {}", e.message)
        return toResponse(HttpStatus.NOT_FOUND, e.message)
    }

    @ExceptionHandler(OrderPlanNotFoundException::class)
    fun handleOrderPlanNotFound(e: OrderPlanNotFoundException): ResponseEntity<ErrorResponse> {
        logger.info("발주 계획 조회 실패: {}", e.message)
        return toResponse(HttpStatus.NOT_FOUND, e.message)
    }

    @ExceptionHandler(OrderPlanItemNotFoundException::class)
    fun handleOrderPlanItemNotFound(e: OrderPlanItemNotFoundException): ResponseEntity<ErrorResponse> {
        logger.info("발주 계획 항목 조회 실패: {}", e.message)
        return toResponse(HttpStatus.NOT_FOUND, e.message)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(e: ConstraintViolationException): ResponseEntity<ErrorResponse> {
        val message =
            e.constraintViolations
                .joinToString(", ") { "${it.propertyPath}: ${it.message}" }
                .ifBlank { "잘못된 요청입니다." }
        return toResponse(HttpStatus.BAD_REQUEST, message)
    }

    @ExceptionHandler(JacksonException::class)
    fun handleJsonProcessing(e: JacksonException): ResponseEntity<ErrorResponse> {
        logger.info("요청 본문 파싱 실패: {}", e.message)
        return toResponse(HttpStatus.BAD_REQUEST, "잘못된 요청 형식입니다.")
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleMessageNotReadable(e: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        logger.info("요청 본문 읽기 실패: {}", e.message)
        return toResponse(HttpStatus.BAD_REQUEST, "잘못된 요청 형식입니다.")
    }

    @ExceptionHandler(AuthenticationRequiredException::class)
    fun handleAuthenticationRequired(e: AuthenticationRequiredException): ResponseEntity<ErrorResponse> {
        logger.info("인증 정보 누락: {}", e.message)
        return toResponse(HttpStatus.UNAUTHORIZED, e.message)
    }

    @ExceptionHandler(FeignException::class)
    fun handleFeignException(e: FeignException): ResponseEntity<ErrorResponse> {
        logger.error("외부 AI 서버 호출 실패", e)
        return toResponse(HttpStatus.BAD_GATEWAY, "AI 서버와 통신할 수 없습니다.")
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val message =
            e.bindingResult.fieldErrors
                .joinToString(", ") { "${it.field}: ${it.defaultMessage}" }
                .ifBlank { "잘못된 요청입니다." }
        return toResponse(HttpStatus.BAD_REQUEST, message)
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(e: Exception): ResponseEntity<ErrorResponse> {
        logger.error("예상치 못한 예외 발생", e)
        return toResponse(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.")
    }

    private fun toResponse(
        status: HttpStatus,
        message: String?,
    ): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(status)
            .body(ErrorResponse.of(status, message ?: status.reasonPhrase))
}
