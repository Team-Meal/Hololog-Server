package team.nongchun.hororog.domain.diet.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
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
    @Value("\${internal.api-key}") private val internalApiKey: String,
) {
    @Operation(summary = "식단 작성")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "식단 작성 성공"),
        ApiResponse(responseCode = "400", description = "유효하지 않은 요청 값"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
    )
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun createDiet(
        @Valid @RequestBody request: CreateDietRequest,
    ) {
        createDietService.execute(request)
    }

    @Operation(
        summary = "AI 서버 콜백 — 급식 슬롯 생성",
        description = "AI 서버가 식단 생성을 완료한 뒤 호출하는 전용 콜백 엔드포인트. X-Internal-Token 헤더 인증 필요.",
    )
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "급식 슬롯 생성 성공"),
        ApiResponse(responseCode = "400", description = "유효하지 않은 요청 값"),
        ApiResponse(responseCode = "401", description = "내부 인증 실패"),
    )
    @PostMapping("/ai-callback")
    fun createAiMealSlot(
        @RequestHeader("X-Internal-Token") token: String,
        @Valid @RequestBody request: CreateAiMealSlotRequest,
    ): ResponseEntity<Map<String, Long>> {
        if (token != internalApiKey) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        val mealId = createAiMealSlotService.execute(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(mapOf("id" to mealId))
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
