package team.nongchun.hororog.domain.diet.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.diet.exception.DietNotFoundException
import team.nongchun.hororog.domain.diet.repository.DietRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder

@Service
@Transactional
class DeleteDietServiceImpl(
    private val dietRepository: DietRepository,
    private val authenticationHolder: AuthenticationHolder,
) : DeleteDietService {
    override fun execute(dietId: Long) {
        val diet =
            dietRepository.findByIdAndMemberSchoolName(dietId, authenticationHolder.getCurrentUserSchoolName())
                ?: throw DietNotFoundException()
        dietRepository.delete(diet)
    }
}
