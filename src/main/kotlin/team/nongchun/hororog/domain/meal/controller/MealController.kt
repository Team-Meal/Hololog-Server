package team.nongchun.hororog.domain.meal.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import team.nongchun.hororog.domain.meal.dto.CreateMealSuggestionRequest
import team.nongchun.hororog.domain.meal.dto.MealSuggestionResponse
import team.nongchun.hororog.domain.meal.dto.TodayMealResponse
import team.nongchun.hororog.domain.meal.dto.UpdateMealSuggestionStatusRequest
import team.nongchun.hororog.domain.meal.entity.MealType
import team.nongchun.hororog.domain.meal.service.CreateMealSuggestionService
import team.nongchun.hororog.domain.meal.service.GetMealSuggestionListService
import team.nongchun.hororog.domain.meal.service.GetTodayMealService
import team.nongchun.hororog.domain.meal.service.UpdateMealSuggestionStatusService

@Tag(name = "Meal", description = "급식 API")
@RestController
@RequestMapping("/meals")
class MealController(
    private val getTodayMealService: GetTodayMealService,
    private val createMealSuggestionService: CreateMealSuggestionService,
    private val getMealSuggestionListService: GetMealSuggestionListService,
    private val updateMealSuggestionStatusService: UpdateMealSuggestionStatusService,
) {
    @Operation(summary = "오늘 급식 메뉴 조회")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "조회 성공"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
        ApiResponse(responseCode = "403", description = "권한 없음"),
    )
    @GetMapping("/today")
    fun getTodayMeal(
        @RequestParam mealType: MealType,
    ): TodayMealResponse = getTodayMealService.execute(mealType)

    @Operation(summary = "먹고 싶은 급식 추천")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "추천 성공"),
        ApiResponse(responseCode = "400", description = "유효하지 않은 요청 값"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
        ApiResponse(responseCode = "403", description = "권한 없음"),
    )
    @PostMapping("/suggestions")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun createMealSuggestion(
        @Valid @RequestBody request: CreateMealSuggestionRequest,
    ) {
        createMealSuggestionService.execute(request)
    }

    @Operation(summary = "먹고 싶은 급식 목록 조회")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "조회 성공"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
        ApiResponse(responseCode = "403", description = "권한 없음"),
    )
    @GetMapping("/suggestions")
    fun getMealSuggestionList(): List<MealSuggestionResponse> = getMealSuggestionListService.execute()

    @Operation(summary = "먹고 싶은 급식 제안 처리 상태 변경")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "상태 변경 성공"),
        ApiResponse(responseCode = "400", description = "유효하지 않은 요청 값"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
        ApiResponse(responseCode = "403", description = "권한 없음"),
        ApiResponse(responseCode = "404", description = "급식 제안 없음"),
    )
    @PatchMapping("/suggestions/{suggestionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateMealSuggestionStatus(
        @PathVariable suggestionId: Long,
        @Valid @RequestBody request: UpdateMealSuggestionStatusRequest,
    ) {
        updateMealSuggestionStatusService.execute(suggestionId, request)
    }
}
