package team.nongchun.hororog.domain.diet.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.diet.dto.DietResponse
import team.nongchun.hororog.domain.diet.exception.DietNotFoundException
import team.nongchun.hororog.domain.diet.repository.DietRepository

@Service
@Transactional(readOnly = true)
class GetDietServiceImpl(
    private val dietRepository: DietRepository,
) : GetDietService {
    override fun execute(dietId: Long): DietResponse {
        val diet = dietRepository.findById(dietId).orElseThrow { DietNotFoundException() }
        return DietResponse.from(diet)
    }
}
