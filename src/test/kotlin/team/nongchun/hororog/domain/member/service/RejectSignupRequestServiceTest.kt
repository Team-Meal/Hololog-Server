package team.nongchun.hororog.domain.member.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import team.nongchun.hororog.domain.member.entity.Member
import team.nongchun.hororog.domain.member.entity.NutritionistSignupRequest
import team.nongchun.hororog.domain.member.entity.Role
import team.nongchun.hororog.domain.member.entity.SignupStatus
import team.nongchun.hororog.domain.member.exception.SignupRequestAlreadyProcessedException
import team.nongchun.hororog.domain.member.exception.SignupRequestNotFoundException
import team.nongchun.hororog.domain.member.repository.NutritionistSignupRequestRepository
import java.util.Optional

class RejectSignupRequestServiceTest :
    BehaviorSpec({
        isolationMode = IsolationMode.InstancePerLeaf

        val signupRequestRepository = mockk<NutritionistSignupRequestRepository>()
        val service = RejectSignupRequestServiceImpl(signupRequestRepository)

        fun member() =
            Member(
                id = 1L,
                email = "nutritionist@hororog.team",
                password = "encoded",
                name = "김영양",
                schoolName = "농촌초등학교",
                role = Role.PENDING_NUTRITIONIST,
            )

        Given("PENDING 상태의 가입 요청이 있을 때") {
            val pendingMember = member()
            val signupRequest =
                NutritionistSignupRequest(
                    id = 10L,
                    member = pendingMember,
                    licenseNumber = "123456789",
                    status = SignupStatus.PENDING,
                )
            every { signupRequestRepository.findById(10L) } returns Optional.of(signupRequest)

            When("거절하면") {
                val response = service.execute(10L)

                Then("요청이 REJECTED가 되고 회원 역할은 그대로 유지된다") {
                    response.status shouldBe SignupStatus.REJECTED
                    signupRequest.status shouldBe SignupStatus.REJECTED
                    pendingMember.role shouldBe Role.PENDING_NUTRITIONIST
                }
            }
        }

        Given("가입 요청이 존재하지 않을 때") {
            every { signupRequestRepository.findById(99L) } returns Optional.empty()

            When("거절하면") {
                Then("SignupRequestNotFoundException이 발생한다") {
                    shouldThrow<SignupRequestNotFoundException> { service.execute(99L) }
                }
            }
        }

        Given("이미 처리된 가입 요청일 때") {
            val signupRequest =
                NutritionistSignupRequest(
                    id = 10L,
                    member = member(),
                    licenseNumber = "123456789",
                    status = SignupStatus.APPROVED,
                )
            every { signupRequestRepository.findById(10L) } returns Optional.of(signupRequest)

            When("거절하면") {
                Then("SignupRequestAlreadyProcessedException이 발생한다") {
                    shouldThrow<SignupRequestAlreadyProcessedException> { service.execute(10L) }
                }
            }
        }
    })
