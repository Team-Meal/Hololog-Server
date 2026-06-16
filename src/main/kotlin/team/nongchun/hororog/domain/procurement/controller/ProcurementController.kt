package team.nongchun.hororog.domain.procurement.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import team.nongchun.hororog.domain.procurement.dto.QuantityEstimationRequest
import team.nongchun.hororog.domain.procurement.dto.QuantityEstimationResponse
import team.nongchun.hororog.domain.procurement.dto.VendorRecommendationResponse
import team.nongchun.hororog.domain.procurement.dto.VendorResponse
import team.nongchun.hororog.domain.procurement.service.EstimateQuantityService
import team.nongchun.hororog.domain.procurement.service.GetVendorListService
import team.nongchun.hororog.domain.procurement.service.GetVendorRecommendationsService

@Tag(name = "Procurement", description = "조달 API")
@RestController
@RequestMapping("/procurements")
class ProcurementController(
    private val getVendorRecommendationsService: GetVendorRecommendationsService,
    private val estimateQuantityService: EstimateQuantityService,
    private val getVendorListService: GetVendorListService,
) {
    @Operation(summary = "납품처 추천")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "추천 성공"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
    )
    @GetMapping("/vendor-recommendations")
    fun getVendorRecommendations(): VendorRecommendationResponse = getVendorRecommendationsService.execute()

    @Operation(summary = "적정 발주량 자동 계산")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "계산 성공"),
        ApiResponse(responseCode = "400", description = "유효하지 않은 요청 값"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
    )
    @PostMapping("/quantity-estimations")
    fun estimateQuantity(
        @Valid @RequestBody request: QuantityEstimationRequest,
    ): QuantityEstimationResponse = estimateQuantityService.execute(request)

    @Operation(summary = "납품처 목록 조회")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "조회 성공"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
    )
    @GetMapping("/vendors")
    fun getVendors(
        @RequestParam latitude: Double,
        @RequestParam longitude: Double,
        @RequestParam(required = false) radius: Int?,
    ): List<VendorResponse> = getVendorListService.execute(latitude = latitude, longitude = longitude, radius = radius)
}
