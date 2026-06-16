package team.nongchun.hororog.domain.diet.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.diet.dto.DietLeftoverResponse
import team.nongchun.hororog.domain.diet.exception.DietNotFoundException
import team.nongchun.hororog.domain.diet.repository.DietRepository
import team.nongchun.hororog.domain.leftover.repository.LeftoverRepository

@Service
@Transactional(readOnly = true)
class GetDietLeftoverServiceImpl(
    private val dietRepository: DietRepository,
    private val leftoverRepository: LeftoverRepository,
) : GetDietLeftoverService {
    override fun execute(dietId: Long): DietLeftoverResponse {
        if (!dietRepository.existsById(dietId)) throw DietNotFoundException()
        val leftover =
            leftoverRepository.findByDietId(dietId)
                ?: throw DietNotFoundException()
        return DietLeftoverResponse.from(leftover)
    }
}
