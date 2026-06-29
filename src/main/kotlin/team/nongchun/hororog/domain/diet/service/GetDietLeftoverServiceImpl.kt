package team.nongchun.hororog.domain.diet.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.diet.dto.DietLeftoverResponse
import team.nongchun.hororog.domain.diet.exception.DietNotFoundException
import team.nongchun.hororog.domain.diet.repository.DietRepository
import team.nongchun.hororog.domain.leftover.repository.LeftoverRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder

@Service
@Transactional(readOnly = true)
class GetDietLeftoverServiceImpl(
    private val dietRepository: DietRepository,
    private val leftoverRepository: LeftoverRepository,
    private val authenticationHolder: AuthenticationHolder,
) : GetDietLeftoverService {
    override fun execute(dietId: Long): DietLeftoverResponse {
        val diet =
            dietRepository.findByIdAndMemberSchoolName(dietId, authenticationHolder.getCurrentUserSchoolName())
                ?: throw DietNotFoundException()
        val leftover =
            leftoverRepository.findByDietId(diet.id)
                ?: throw DietNotFoundException()
        return DietLeftoverResponse.from(leftover)
    }
}
