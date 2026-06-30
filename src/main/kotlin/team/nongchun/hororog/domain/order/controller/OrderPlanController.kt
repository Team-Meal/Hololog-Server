package team.nongchun.hororog.domain.order.controller

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
import team.nongchun.hororog.domain.order.dto.AddOrderPlanItemRequest
import team.nongchun.hororog.domain.order.dto.CreateOrderPlanRequest
import team.nongchun.hororog.domain.order.dto.OrderPlanDetailResponse
import team.nongchun.hororog.domain.order.dto.OrderPlanItemResponse
import team.nongchun.hororog.domain.order.dto.OrderPlanListResponse
import team.nongchun.hororog.domain.order.dto.UpdateOrderPlanItemRequest
import team.nongchun.hororog.domain.order.dto.UpdateOrderPlanRequest
import team.nongchun.hororog.domain.order.service.AddOrderPlanItemService
import team.nongchun.hororog.domain.order.service.CreateOrderPlanService
import team.nongchun.hororog.domain.order.service.DeleteOrderPlanItemService
import team.nongchun.hororog.domain.order.service.DeleteOrderPlanService
import team.nongchun.hororog.domain.order.service.GetOrderPlanListService
import team.nongchun.hororog.domain.order.service.GetOrderPlanService
import team.nongchun.hororog.domain.order.service.UpdateOrderPlanItemService
import team.nongchun.hororog.domain.order.service.UpdateOrderPlanService

@Tag(name = "OrderPlan", description = "발주 계획 API")
@RestController
@RequestMapping("/order-plans")
class OrderPlanController(
    private val createOrderPlanService: CreateOrderPlanService,
    private val getOrderPlanService: GetOrderPlanService,
    private val getOrderPlanListService: GetOrderPlanListService,
    private val updateOrderPlanService: UpdateOrderPlanService,
    private val deleteOrderPlanService: DeleteOrderPlanService,
    private val addOrderPlanItemService: AddOrderPlanItemService,
    private val updateOrderPlanItemService: UpdateOrderPlanItemService,
    private val deleteOrderPlanItemService: DeleteOrderPlanItemService,
) {
    @Operation(summary = "발주 계획 생성")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "생성 성공"),
        ApiResponse(responseCode = "400", description = "유효하지 않은 요청 값"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @Valid @RequestBody request: CreateOrderPlanRequest,
    ): OrderPlanDetailResponse = createOrderPlanService.execute(request)

    @Operation(summary = "발주 계획 목록 조회")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "조회 성공"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
    )
    @GetMapping
    fun getList(): List<OrderPlanListResponse> = getOrderPlanListService.execute()

    @Operation(summary = "발주 계획 상세 조회 (발주 계획표 포함)")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "조회 성공"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
        ApiResponse(responseCode = "404", description = "발주 계획 없음"),
    )
    @GetMapping("/{orderPlanId}")
    fun get(
        @PathVariable orderPlanId: Long,
    ): OrderPlanDetailResponse = getOrderPlanService.execute(orderPlanId)

    @Operation(summary = "발주 계획 수정")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "수정 성공"),
        ApiResponse(responseCode = "400", description = "유효하지 않은 요청 값"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
        ApiResponse(responseCode = "404", description = "발주 계획 없음"),
    )
    @PatchMapping("/{orderPlanId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(
        @PathVariable orderPlanId: Long,
        @Valid @RequestBody request: UpdateOrderPlanRequest,
    ) {
        updateOrderPlanService.execute(orderPlanId, request)
    }

    @Operation(summary = "발주 계획 삭제")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "삭제 성공"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
        ApiResponse(responseCode = "404", description = "발주 계획 없음"),
    )
    @DeleteMapping("/{orderPlanId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable orderPlanId: Long,
    ) {
        deleteOrderPlanService.execute(orderPlanId)
    }

    @Operation(summary = "발주 계획 항목 추가 (자동 계산)")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "추가 성공"),
        ApiResponse(responseCode = "400", description = "유효하지 않은 요청 값"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
        ApiResponse(responseCode = "404", description = "발주 계획 또는 식자재 없음"),
    )
    @PostMapping("/{orderPlanId}/items")
    @ResponseStatus(HttpStatus.CREATED)
    fun addItem(
        @PathVariable orderPlanId: Long,
        @Valid @RequestBody request: AddOrderPlanItemRequest,
    ): OrderPlanItemResponse = addOrderPlanItemService.execute(orderPlanId, request)

    @Operation(summary = "발주 계획 항목 수정 (재계산)")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "수정 성공"),
        ApiResponse(responseCode = "400", description = "유효하지 않은 요청 값"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
        ApiResponse(responseCode = "404", description = "항목 없음"),
    )
    @PatchMapping("/{orderPlanId}/items/{itemId}")
    fun updateItem(
        @PathVariable orderPlanId: Long,
        @PathVariable itemId: Long,
        @Valid @RequestBody request: UpdateOrderPlanItemRequest,
    ): OrderPlanItemResponse = updateOrderPlanItemService.execute(orderPlanId, itemId, request)

    @Operation(summary = "발주 계획 항목 삭제")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "삭제 성공"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
        ApiResponse(responseCode = "404", description = "항목 없음"),
    )
    @DeleteMapping("/{orderPlanId}/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteItem(
        @PathVariable orderPlanId: Long,
        @PathVariable itemId: Long,
    ) {
        deleteOrderPlanItemService.execute(orderPlanId, itemId)
    }
}
