package team.nongchun.hororog.domain.diet.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.ConstraintViolationException
import jakarta.validation.Valid
import jakarta.validation.Validator
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import team.nongchun.hororog.domain.diet.dto.CreateDietLeftoverRequest
import team.nongchun.hororog.domain.diet.dto.CreateDietRequest
import team.nongchun.hororog.domain.diet.dto.DietExportResponse
import team.nongchun.hororog.domain.diet.dto.DietLeftoverResponse
import team.nongchun.hororog.domain.diet.dto.DietListResponse
import team.nongchun.hororog.domain.diet.dto.DietResponse
import team.nongchun.hororog.domain.diet.dto.ExportDietRequest
import team.nongchun.hororog.domain.diet.dto.UpdateDietRequest
import team.nongchun.hororog.domain.diet.service.CreateDietLeftoverService
import team.nongchun.hororog.domain.diet.service.CreateDietService
import team.nongchun.hororog.domain.diet.service.DeleteDietService
import team.nongchun.hororog.domain.diet.service.ExportDietService
import team.nongchun.hororog.domain.diet.service.GetDietLeftoverService
import team.nongchun.hororog.domain.diet.service.GetDietListService
import team.nongchun.hororog.domain.diet.service.GetDietService
import team.nongchun.hororog.domain.diet.service.UpdateDietService
import team.nongchun.hororog.domain.meal.dto.CreateAiMealSlotRequest
import team.nongchun.hororog.domain.meal.service.CreateAiMealSlotService
import tools.jackson.databind.JsonNode
import tools.jackson.databind.ObjectMapper

@Tag(name = "Diet", description = "식단 API")
@RestController
@RequestMapping("/diets")
class DietController(
    private val createDietService: CreateDietService,
    private val getDietService: GetDietService,
    private val getDietListService: GetDietListService,
    private val updateDietService: UpdateDietService,
    private val deleteDietService: DeleteDietService,
    private val createDietLeftoverService: CreateDietLeftoverService,
    private val getDietLeftoverService: GetDietLeftoverService,
    private val exportDietService: ExportDietService,
    private val createAiMealSlotService: CreateAiMealSlotService,
    private val objectMapper: ObjectMapper,
    private val validator: Validator,
) {
    @Operation(
        summary = "식단 작성",
        description =
            "meal_type 필드가 있으면 AI 서버 콜백(급식 슬롯 생성)으로 처리합니다. " +
                "Hololog-AI 서버의 콜백 경로가 /diets로 고정돼 있어 같은 경로를 공유한다.",
    )
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "식단 작성 성공"),
        ApiResponse(responseCode = "201", description = "AI 콜백 — 급식 슬롯 생성 성공"),
        ApiResponse(responseCode = "400", description = "유효하지 않은 요청 값"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
    )
    @PostMapping
    fun createDiet(
        @RequestBody body: JsonNode,
    ): ResponseEntity<Any> =
        if (body.has("meal_type")) {
            val request = objectMapper.treeToValue(body, CreateAiMealSlotRequest::class.java)
            val mealId = createAiMealSlotService.execute(request)
            ResponseEntity.status(HttpStatus.CREATED).body(mapOf("id" to mealId))
        } else {
            val request = objectMapper.treeToValue(body, CreateDietRequest::class.java)
            val violations = validator.validate(request)
            if (violations.isNotEmpty()) throw ConstraintViolationException(violations)
            createDietService.execute(request)
            ResponseEntity.noContent().build()
        }

    @Operation(summary = "식단 목록 조회")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "조회 성공"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
    )
    @GetMapping
    fun getDietList(): List<DietListResponse> = getDietListService.execute()

    @Operation(summary = "식단 단건 조회")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "조회 성공"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
        ApiResponse(responseCode = "404", description = "식단 없음"),
    )
    @GetMapping("/{dietId}")
    fun getDiet(
        @PathVariable dietId: Long,
    ): DietResponse = getDietService.execute(dietId)

    @Operation(summary = "식단 수정")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "수정 성공"),
        ApiResponse(responseCode = "400", description = "유효하지 않은 요청 값"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
        ApiResponse(responseCode = "404", description = "식단 없음"),
    )
    @PatchMapping("/{dietId}")
    fun updateDiet(
        @PathVariable dietId: Long,
        @Valid @RequestBody request: UpdateDietRequest,
    ): DietResponse = updateDietService.execute(dietId, request)

    @Operation(summary = "식단 삭제")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "삭제 성공"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
        ApiResponse(responseCode = "404", description = "식단 없음"),
    )
    @DeleteMapping("/{dietId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteDiet(
        @PathVariable dietId: Long,
    ) {
        deleteDietService.execute(dietId)
    }

    @Operation(summary = "식단 출력")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "출력 성공"),
        ApiResponse(responseCode = "400", description = "유효하지 않은 요청 값"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
        ApiResponse(responseCode = "404", description = "식단 없음"),
    )
    @PostMapping("/{dietId}/exports")
    fun exportDiet(
        @PathVariable dietId: Long,
        @Valid @RequestBody request: ExportDietRequest,
    ): DietExportResponse = exportDietService.execute(dietId, request)

    @Operation(summary = "식단별 잔반량 입력")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "입력 성공"),
        ApiResponse(responseCode = "400", description = "유효하지 않은 요청 값"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
        ApiResponse(responseCode = "404", description = "식단 없음"),
    )
    @PostMapping("/{dietId}/leftovers")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun createDietLeftover(
        @PathVariable dietId: Long,
        @Valid @RequestBody request: CreateDietLeftoverRequest,
    ) {
        createDietLeftoverService.execute(dietId, request)
    }

    @Operation(summary = "식단별 잔반량 조회")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "조회 성공"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
        ApiResponse(responseCode = "404", description = "식단 없음"),
    )
    @GetMapping("/{dietId}/leftovers")
    fun getDietLeftover(
        @PathVariable dietId: Long,
    ): DietLeftoverResponse = getDietLeftoverService.execute(dietId)
}
