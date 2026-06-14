package team.nongchun.hororog.domain.diet.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.diet.dto.CreateDietRequest
import team.nongchun.hororog.domain.diet.entity.Diet
import team.nongchun.hororog.domain.diet.repository.DietRepository
import team.nongchun.hororog.domain.member.exception.MemberNotFoundException
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder

@Service
@Transactional
class CreateDietServiceImpl(
    private val dietRepository: DietRepository,
    private val memberRepository: MemberRepository,
    private val authenticationHolder: AuthenticationHolder,
) : CreateDietService {
    override fun execute(request: CreateDietRequest) {
        val member =
            memberRepository
                .findById(authenticationHolder.getCurrentUserId())
                .orElseThrow { MemberNotFoundException() }
        dietRepository.save(
            Diet(
                member = member,
                name = request.name,
                description = request.description,
                dietDate = request.dietDate,
            ),
        )
    }
}
