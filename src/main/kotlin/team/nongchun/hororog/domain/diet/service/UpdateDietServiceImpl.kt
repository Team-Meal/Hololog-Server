package team.nongchun.hororog.domain.diet.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.diet.dto.DietResponse
import team.nongchun.hororog.domain.diet.dto.UpdateDietRequest
import team.nongchun.hororog.domain.diet.exception.DietNotFoundException
import team.nongchun.hororog.domain.diet.repository.DietRepository

@Service
@Transactional
class UpdateDietServiceImpl(
    private val dietRepository: DietRepository,
) : UpdateDietService {
    override fun execute(
        dietId: Long,
        request: UpdateDietRequest,
    ): DietResponse {
        val diet = dietRepository.findById(dietId).orElseThrow { DietNotFoundException() }
        diet.name = request.name
        diet.description = request.description
        diet.dietDate = request.dietDate
        return DietResponse.from(diet)
    }
}
