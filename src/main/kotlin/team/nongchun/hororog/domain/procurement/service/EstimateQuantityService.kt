package team.nongchun.hororog.domain.procurement.service

import team.nongchun.hororog.domain.procurement.dto.QuantityEstimationRequest
import team.nongchun.hororog.domain.procurement.dto.QuantityEstimationResponse

interface EstimateQuantityService {
    fun execute(request: QuantityEstimationRequest): QuantityEstimationResponse
}
