package team.nongchun.hororog.domain.procurement.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.ingredient.repository.IngredientRepository
import team.nongchun.hororog.domain.procurement.client.AiServerClient
import team.nongchun.hororog.domain.procurement.client.dto.AiVendorRecommendationRequest
import team.nongchun.hororog.domain.procurement.dto.VendorRecommendationResponse
import team.nongchun.hororog.global.auth.AuthenticationHolder

@Service
@Transactional(readOnly = true)
class GetVendorRecommendationsServiceImpl(
    private val ingredientRepository: IngredientRepository,
    private val authenticationHolder: AuthenticationHolder,
    private val aiServerClient: AiServerClient,
) : GetVendorRecommendationsService {
    override fun execute(): VendorRecommendationResponse {
        val schoolName = authenticationHolder.getCurrentUserSchoolName()
        val ingredients = ingredientRepository.findAllByMemberSchoolName(schoolName)
        val request =
            AiVendorRecommendationRequest(
                ingredients =
                    ingredients.map {
                        AiVendorRecommendationRequest.IngredientInfo(
                            ingredientId = it.id,
                            name = it.name,
                            category = it.category,
                            quantity = it.quantity,
                            unit = it.unit.name,
                        )
                    },
            )
        return aiServerClient.getVendorRecommendations(request)
    }
}
