package team.nongchun.hororog.domain.admin.service

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import team.nongchun.hororog.domain.member.entity.Member
import team.nongchun.hororog.domain.member.entity.NutritionistSignupRequest
import team.nongchun.hororog.domain.member.entity.Role
import team.nongchun.hororog.domain.member.entity.SignupStatus
import team.nongchun.hororog.domain.member.repository.NutritionistSignupRequestRepository

class GetSignupRequestListServiceTest :
    BehaviorSpec({
        isolationMode = IsolationMode.InstancePerLeaf

        val repository = mockk<NutritionistSignupRequestRepository>()
        val service = GetSignupRequestListServiceImpl(repository)

        val pageable = PageRequest.of(0, 10)

        fun member(id: Long = 1L) =
            Member(
                id = id,
                email = "nutritionist@hororog.team",
                password = "encoded",
                name = "김영양",
                schoolName = "농촌초등학교",
                role = Role.PENDING_NUTRITIONIST,
            )

        Given("PENDING 상태의 가입 요청이 있을 때") {
            val entity =
                NutritionistSignupRequest(
                    id = 10L,
                    member = member(1L),
                    licenseNumber = "123456789",
                    status = SignupStatus.PENDING,
                )
            every { repository.findByStatus(SignupStatus.PENDING, pageable) } returns PageImpl(listOf(entity))

            When("목록을 조회하면") {
                val result = service.execute(pageable)

                Then("PENDING 요청이 포함된 페이지를 반환한다") {
                    result.totalElements shouldBe 1
                    with(result.content.first()) {
                        requestId shouldBe 10L
                        memberId shouldBe 1L
                        name shouldBe "김영양"
                        licenseNumber shouldBe "123456789"
                        status shouldBe SignupStatus.PENDING
                    }
                }
            }
        }

        Given("PENDING 상태의 가입 요청이 없을 때") {
            every { repository.findByStatus(SignupStatus.PENDING, pageable) } returns PageImpl(emptyList())

            When("목록을 조회하면") {
                val result = service.execute(pageable)

                Then("빈 페이지를 반환한다") {
                    result.totalElements shouldBe 0
                    result.content shouldBe emptyList()
                }
            }
        }
    })
