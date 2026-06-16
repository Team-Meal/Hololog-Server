package team.nongchun.hororog.domain.procurement.service

import org.springframework.stereotype.Service
import team.nongchun.hororog.domain.procurement.client.AiServerClient
import team.nongchun.hororog.domain.procurement.client.dto.AiQuantityEstimationRequest
import team.nongchun.hororog.domain.procurement.dto.QuantityEstimationRequest
import team.nongchun.hororog.domain.procurement.dto.QuantityEstimationResponse

@Service
class EstimateQuantityServiceImpl(
    private val aiServerClient: AiServerClient,
) : EstimateQuantityService {
    override fun execute(request: QuantityEstimationRequest): QuantityEstimationResponse {
        val aiRequest =
            AiQuantityEstimationRequest(
                ingredients =
                    request.ingredients.map {
                        AiQuantityEstimationRequest.IngredientItem(
                            name = it.name,
                            quantity = it.quantity,
                            unit = it.unit,
                        )
                    },
            )
        return aiServerClient.estimateQuantity(aiRequest)
    }
}
