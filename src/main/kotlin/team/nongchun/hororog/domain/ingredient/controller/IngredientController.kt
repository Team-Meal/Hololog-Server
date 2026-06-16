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
import team.nongchun.hororog.domain.ingredient.dto.CreateIngredientRequest
import team.nongchun.hororog.domain.ingredient.dto.IngredientListResponse
import team.nongchun.hororog.domain.ingredient.dto.IngredientResponse
import team.nongchun.hororog.domain.ingredient.dto.IngredientUpdateResponse
import team.nongchun.hororog.domain.ingredient.dto.UpdateIngredientRequest
import team.nongchun.hororog.domain.ingredient.service.CreateIngredientService
import team.nongchun.hororog.domain.ingredient.service.DeleteIngredientService
import team.nongchun.hororog.domain.ingredient.service.GetIngredientListService
import team.nongchun.hororog.domain.ingredient.service.GetIngredientService
import team.nongchun.hororog.domain.ingredient.service.UpdateIngredientService

@Tag(name = "Ingredient", description = "보유 식자재 API")
@RestController
@RequestMapping("/ingredients")
class IngredientController(
    private val createIngredientService: CreateIngredientService,
    private val getIngredientService: GetIngredientService,
    private val getIngredientListService: GetIngredientListService,
    private val updateIngredientService: UpdateIngredientService,
    private val deleteIngredientService: DeleteIngredientService,
) {
    @Operation(summary = "보유 식자재 작성", description = "보유 중인 식자재를 등록합니다.")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "등록 성공"),
        ApiResponse(responseCode = "400", description = "유효하지 않은 요청 값 또는 단위"),
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @Valid @RequestBody request: CreateIngredientRequest,
    ) {
        createIngredientService.execute(request)
    }

    @Operation(summary = "보유 식자재 조회", description = "식자재 ID로 단건 조회합니다.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "조회 성공"),
        ApiResponse(responseCode = "404", description = "식자재를 찾을 수 없음"),
    )
    @GetMapping("/{ingredientId}")
    fun get(
        @PathVariable ingredientId: Long,
    ): IngredientResponse = getIngredientService.execute(ingredientId)

    @Operation(summary = "보유 식자재 목록 조회", description = "같은 학교의 보유 식자재 목록을 조회합니다.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "조회 성공"),
    )
    @GetMapping
    fun getList(): List<IngredientListResponse> = getIngredientListService.execute()

    @Operation(summary = "보유 식자재 수정", description = "식자재 정보를 부분 수정합니다. 전달된 필드만 변경됩니다.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "수정 성공"),
        ApiResponse(responseCode = "400", description = "유효하지 않은 요청 값 또는 단위"),
        ApiResponse(responseCode = "404", description = "식자재를 찾을 수 없음"),
    )
    @PatchMapping("/{ingredientId}")
    fun update(
        @PathVariable ingredientId: Long,
        @Valid @RequestBody request: UpdateIngredientRequest,
    ): IngredientUpdateResponse = updateIngredientService.execute(ingredientId, request)

    @Operation(summary = "보유 식자재 삭제", description = "식자재를 삭제합니다.")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "삭제 성공"),
        ApiResponse(responseCode = "404", description = "식자재를 찾을 수 없음"),
    )
    @DeleteMapping("/{ingredientId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable ingredientId: Long,
    ) {
        deleteIngredientService.execute(ingredientId)
    }
}
