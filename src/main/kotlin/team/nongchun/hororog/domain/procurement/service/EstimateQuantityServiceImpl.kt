package team.nongchun.hororog.domain.procurement.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.procurement.client.AiServerClient
import team.nongchun.hororog.domain.procurement.dto.QuantityEstimationRequest
import team.nongchun.hororog.domain.procurement.dto.QuantityEstimationResponse

@Service
@Transactional(readOnly = true)
class EstimateQuantityServiceImpl(
    private val aiServerClient: AiServerClient,
) : EstimateQuantityService {
    override fun execute(request: QuantityEstimationRequest): QuantityEstimationResponse = aiServerClient.estimateQuantity(request)
}
