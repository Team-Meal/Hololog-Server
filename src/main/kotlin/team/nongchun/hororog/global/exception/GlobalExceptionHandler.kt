package team.nongchun.hororog.global.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import team.nongchun.hororog.domain.budget.exception.BudgetNotFoundException
import team.nongchun.hororog.domain.budget.exception.InvalidBudgetException
import team.nongchun.hororog.domain.diet.exception.DietNotFoundException
import team.nongchun.hororog.domain.ingredient.exception.IngredientNotFoundException
import team.nongchun.hororog.domain.ingredient.exception.IngredientPlanNotFoundException
import team.nongchun.hororog.domain.ingredient.exception.InvalidQuantityUnitException
import team.nongchun.hororog.domain.meal.exception.MealSuggestionNotFoundException
import team.nongchun.hororog.domain.member.exception.EmailAlreadyExistsException
import team.nongchun.hororog.domain.member.exception.InvalidCredentialsException
import team.nongchun.hororog.domain.member.exception.InvalidTokenException
import team.nongchun.hororog.domain.member.exception.MemberNotFoundException
import team.nongchun.hororog.domain.member.exception.SignupRequestAlreadyPendingException
import team.nongchun.hororog.domain.member.exception.SignupRequestAlreadyProcessedException
import team.nongchun.hororog.domain.member.exception.SignupRequestNotFoundException

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

    @ExceptionHandler(InvalidQuantityUnitException::class)
    fun handleInvalidQuantityUnit(e: InvalidQuantityUnitException): ResponseEntity<ErrorResponse> {
        logger.info("잘못된 단위 입력: {}", e.message)
        return toResponse(HttpStatus.BAD_REQUEST, e.message)
    }

    @ExceptionHandler(MealSuggestionNotFoundException::class)
    fun handleMealSuggestionNotFound(e: MealSuggestionNotFoundException): ResponseEntity<ErrorResponse> {
        logger.info("급식 제안 조회 실패: {}", e.message)
        return toResponse(HttpStatus.NOT_FOUND, e.message)
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
