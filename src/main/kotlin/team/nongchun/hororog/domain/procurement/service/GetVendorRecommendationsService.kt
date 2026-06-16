package team.nongchun.hororog.domain.procurement.service

import team.nongchun.hororog.domain.procurement.dto.VendorRecommendationResponse

interface GetVendorRecommendationsService {
    fun execute(): VendorRecommendationResponse
}
