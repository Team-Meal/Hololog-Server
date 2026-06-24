package team.nongchun.hororog.domain.ingredient.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import team.nongchun.hororog.domain.ingredient.dto.CreateIngredientPlanRequest
import team.nongchun.hororog.domain.ingredient.dto.IngredientPlanResponse
import team.nongchun.hororog.domain.ingredient.dto.IngredientPlanUpdateResponse
import team.nongchun.hororog.domain.ingredient.dto.UpdateIngredientPlanRequest
import team.nongchun.hororog.domain.ingredient.service.CreateIngredientPlanService
import team.nongchun.hororog.domain.ingredient.service.DeleteIngredientPlanService
import team.nongchun.hororog.domain.ingredient.service.GetIngredientPlanListService
import team.nongchun.hororog.domain.ingredient.service.GetIngredientPlanService
import team.nongchun.hororog.domain.ingredient.service.UpdateIngredientPlanService

@Tag(name = "IngredientPlan", description = "식자재 계획표 API")
@RestController
@RequestMapping("/ingredients/plans")
class IngredientPlanController(
    private val createIngredientPlanService: CreateIngredientPlanService,
    private val getIngredientPlanService: GetIngredientPlanService,
    private val getIngredientPlanListService: GetIngredientPlanListService,
    private val updateIngredientPlanService: UpdateIngredientPlanService,
    private val deleteIngredientPlanService: DeleteIngredientPlanService,
) {
    @Operation(summary = "식자재 계획표 작성", description = "식자재 계획표를 등록합니다.")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "등록 성공"),
        ApiResponse(responseCode = "400", description = "유효하지 않은 요청 값"),
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @Valid @RequestBody request: CreateIngredientPlanRequest,
    ) {
        createIngredientPlanService.execute(request)
    }

    @Operation(summary = "식자재 계획표 조회", description = "계획표 ID로 단건 조회합니다.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "조회 성공"),
        ApiResponse(responseCode = "404", description = "계획표를 찾을 수 없음"),
    )
    @GetMapping("/{planId}")
    fun get(
        @PathVariable planId: Long,
    ): IngredientPlanResponse = getIngredientPlanService.execute(planId)

    @Operation(summary = "식자재 계획표 목록 조회", description = "같은 학교의 식자재 계획표 목록을 조회합니다.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "조회 성공"),
    )
    @GetMapping
    fun getList(): List<IngredientPlanResponse> = getIngredientPlanListService.execute()

    @Operation(summary = "식자재 계획표 수정", description = "계획표 정보를 부분 수정합니다. 전달된 필드만 변경됩니다.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "수정 성공"),
        ApiResponse(responseCode = "400", description = "유효하지 않은 요청 값"),
        ApiResponse(responseCode = "404", description = "계획표를 찾을 수 없음"),
    )
    @PatchMapping("/{planId}")
    fun update(
        @PathVariable planId: Long,
        @Valid @RequestBody request: UpdateIngredientPlanRequest,
    ): IngredientPlanUpdateResponse = updateIngredientPlanService.execute(planId, request)

    @Operation(summary = "식자재 계획표 삭제", description = "계획표를 삭제합니다.")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "삭제 성공"),
        ApiResponse(responseCode = "404", description = "계획표를 찾을 수 없음"),
    )
    @DeleteMapping("/{planId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable planId: Long,
    ) {
        deleteIngredientPlanService.execute(planId)
    }
}
