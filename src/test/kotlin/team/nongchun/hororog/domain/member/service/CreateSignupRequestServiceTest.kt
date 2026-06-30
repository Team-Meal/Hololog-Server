package team.nongchun.hororog.domain.member.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import team.nongchun.hororog.domain.member.dto.CreateSignupRequest
import team.nongchun.hororog.domain.member.entity.Member
import team.nongchun.hororog.domain.member.entity.NutritionistSignupRequest
import team.nongchun.hororog.domain.member.entity.Role
import team.nongchun.hororog.domain.member.entity.SignupStatus
import team.nongchun.hororog.domain.member.exception.MemberNotFoundException
import team.nongchun.hororog.domain.member.exception.SignupRequestAlreadyPendingException
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.domain.member.repository.NutritionistSignupRequestRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder
import java.util.Optional

class CreateSignupRequestServiceTest :
    BehaviorSpec({
        isolationMode = IsolationMode.InstancePerLeaf

        val memberRepository = mockk<MemberRepository>()
        val signupRequestRepository = mockk<NutritionistSignupRequestRepository>()
        val authenticationHolder = mockk<AuthenticationHolder>()
        val service =
            CreateSignupRequestServiceImpl(memberRepository, signupRequestRepository, authenticationHolder)

        val request = CreateSignupRequest(licenseNumber = "123456789")
        val member =
            Member(
                id = 1L,
                email = "nutritionist@hororog.team",
                password = "encoded",
                name = "김영양",
                schoolName = "농촌초등학교",
                role = Role.PENDING_NUTRITIONIST,
            )

        Given("인증된 회원이 면허번호를 제출할 때") {
            every { authenticationHolder.getCurrentUserId() } returns 1L
            every { memberRepository.findById(1L) } returns Optional.of(member)
            every {
                signupRequestRepository.existsByMemberIdAndStatus(1L, SignupStatus.PENDING)
            } returns false
            val savedSlot = slot<NutritionistSignupRequest>()
            every { signupRequestRepository.save(capture(savedSlot)) } answers { savedSlot.captured }

            When("가입 요청을 생성하면") {
                val response = service.execute(request)

                Then("현재 회원과 연결된 PENDING 요청이 생성되고 응답으로 반환된다") {
                    savedSlot.captured.member shouldBe member
                    savedSlot.captured.licenseNumber shouldBe "123456789"
                    savedSlot.captured.status shouldBe SignupStatus.PENDING
                    response.licenseNumber shouldBe "123456789"
                    response.status shouldBe SignupStatus.PENDING
                }
            }
        }

        Given("이미 처리 대기 중인 가입 요청이 있을 때") {
            every { authenticationHolder.getCurrentUserId() } returns 1L
            every { memberRepository.findById(1L) } returns Optional.of(member)
            every {
                signupRequestRepository.existsByMemberIdAndStatus(1L, SignupStatus.PENDING)
            } returns true

            When("가입 요청을 생성하면") {
                Then("SignupRequestAlreadyPendingException이 발생한다") {
                    shouldThrow<SignupRequestAlreadyPendingException> {
                        service.execute(request)
                    }
                    verify(exactly = 0) { signupRequestRepository.save(any()) }
                }
            }
        }

        Given("인증된 userId의 회원이 존재하지 않을 때") {
            every { authenticationHolder.getCurrentUserId() } returns 99L
            every { memberRepository.findById(99L) } returns Optional.empty()

            When("가입 요청을 생성하면") {
                Then("MemberNotFoundException이 발생한다") {
                    shouldThrow<MemberNotFoundException> {
                        service.execute(request)
                    }
                }
            }
        }
    })
