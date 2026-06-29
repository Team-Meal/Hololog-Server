package team.nongchun.hororog.domain.diet.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.diet.dto.DietResponse
import team.nongchun.hororog.domain.diet.exception.DietNotFoundException
import team.nongchun.hororog.domain.diet.repository.DietRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder

@Service
@Transactional(readOnly = true)
class GetDietServiceImpl(
    private val dietRepository: DietRepository,
    private val authenticationHolder: AuthenticationHolder,
) : GetDietService {
    override fun execute(dietId: Long): DietResponse {
        val diet =
            dietRepository.findByIdAndMemberSchoolName(dietId, authenticationHolder.getCurrentUserSchoolName())
                ?: throw DietNotFoundException()
        return DietResponse.from(diet)
    }
}
