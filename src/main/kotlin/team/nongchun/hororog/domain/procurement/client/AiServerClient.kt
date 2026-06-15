package team.nongchun.hororog.domain.procurement.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import team.nongchun.hororog.domain.procurement.client.dto.AiVendorRecommendationRequest
import team.nongchun.hororog.domain.procurement.dto.QuantityEstimationRequest
import team.nongchun.hororog.domain.procurement.dto.QuantityEstimationResponse
import team.nongchun.hororog.domain.procurement.dto.VendorRecommendationResponse
import team.nongchun.hororog.domain.procurement.dto.VendorResponse

// TODO: AI 서버 스펙 확정 후 엔드포인트 경로 및 요청/응답 구조 조정
@FeignClient(name = "ai-server", url = "\${ai.server.url}")
interface AiServerClient {
    @PostMapping("/vendor-recommendations")
    fun getVendorRecommendations(
        @RequestBody request: AiVendorRecommendationRequest,
    ): VendorRecommendationResponse

    @PostMapping("/quantity-estimations")
    fun estimateQuantity(
        @RequestBody request: QuantityEstimationRequest,
    ): QuantityEstimationResponse

    @GetMapping("/vendors")
    fun getVendors(
        @RequestParam latitude: Double,
        @RequestParam longitude: Double,
        @RequestParam(required = false) radius: Int?,
    ): List<VendorResponse>
}
