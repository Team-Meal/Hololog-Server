package team.nongchun.hororog.domain.budget.controller

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
import team.nongchun.hororog.domain.budget.dto.BudgetListResponse
import team.nongchun.hororog.domain.budget.dto.BudgetResponse
import team.nongchun.hororog.domain.budget.dto.BudgetUpdateResponse
import team.nongchun.hororog.domain.budget.dto.CreateBudgetRequest
import team.nongchun.hororog.domain.budget.dto.UpdateBudgetRequest
import team.nongchun.hororog.domain.budget.service.CreateBudgetService
import team.nongchun.hororog.domain.budget.service.DeleteBudgetService
import team.nongchun.hororog.domain.budget.service.GetBudgetListService
import team.nongchun.hororog.domain.budget.service.GetBudgetService
import team.nongchun.hororog.domain.budget.service.UpdateBudgetService

@Tag(name = "Budget", description = "예산 API")
@RestController
@RequestMapping("/budgets")
class BudgetController(
    private val createBudgetService: CreateBudgetService,
    private val getBudgetListService: GetBudgetListService,
    private val getBudgetService: GetBudgetService,
    private val updateBudgetService: UpdateBudgetService,
    private val deleteBudgetService: DeleteBudgetService,
) {
    @Operation(summary = "예산 입력")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "입력 성공"),
        ApiResponse(responseCode = "400", description = "유효하지 않은 요청 값"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
    )
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun create(
        @Valid @RequestBody request: CreateBudgetRequest,
    ) {
        createBudgetService.execute(request)
    }

    @Operation(summary = "예산 목록 조회")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "조회 성공"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
    )
    @GetMapping
    fun getList(): List<BudgetListResponse> = getBudgetListService.execute()

    @Operation(summary = "예산 조회")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "조회 성공"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
        ApiResponse(responseCode = "404", description = "예산 없음"),
    )
    @GetMapping("/{budgetId}")
    fun get(
        @PathVariable budgetId: Long,
    ): BudgetResponse = getBudgetService.execute(budgetId)

    @Operation(summary = "예산 수정")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "수정 성공"),
        ApiResponse(responseCode = "400", description = "유효하지 않은 요청 값"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
        ApiResponse(responseCode = "404", description = "예산 없음"),
    )
    @PatchMapping("/{budgetId}")
    fun update(
        @PathVariable budgetId: Long,
        @Valid @RequestBody request: UpdateBudgetRequest,
    ): BudgetUpdateResponse = updateBudgetService.execute(budgetId, request)

    @Operation(summary = "예산 삭제")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "삭제 성공"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
        ApiResponse(responseCode = "404", description = "예산 없음"),
    )
    @DeleteMapping("/{budgetId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable budgetId: Long,
    ) {
        deleteBudgetService.execute(budgetId)
    }
}
