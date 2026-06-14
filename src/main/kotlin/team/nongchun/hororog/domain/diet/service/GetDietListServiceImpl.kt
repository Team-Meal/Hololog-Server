package team.nongchun.hororog.domain.diet.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.diet.dto.DietListResponse
import team.nongchun.hororog.domain.diet.repository.DietRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder

@Service
@Transactional(readOnly = true)
class GetDietListServiceImpl(
    private val dietRepository: DietRepository,
    private val authenticationHolder: AuthenticationHolder,
) : GetDietListService {
    override fun execute(): List<DietListResponse> =
        dietRepository
            .findAllByMemberId(authenticationHolder.getCurrentUserId())
            .map { DietListResponse.from(it) }
}
