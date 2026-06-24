package team.nongchun.hororog.domain.member.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.springframework.security.crypto.password.PasswordEncoder
import team.nongchun.hororog.domain.member.dto.SignupRequest
import team.nongchun.hororog.domain.member.entity.Member
import team.nongchun.hororog.domain.member.entity.Role
import team.nongchun.hororog.domain.member.exception.EmailAlreadyExistsException
import team.nongchun.hororog.domain.member.exception.InvalidSignupRoleException
import team.nongchun.hororog.domain.member.repository.MemberRepository

class SignupServiceTest :
    BehaviorSpec({
        isolationMode = IsolationMode.InstancePerLeaf

        val memberRepository = mockk<MemberRepository>()
        val passwordEncoder = mockk<PasswordEncoder>()
        val signupService = SignupServiceImpl(memberRepository, passwordEncoder)

        val request =
            SignupRequest(
                email = "nutritionist@hororog.team",
                name = "김영양",
                schoolName = "농촌초등학교",
                password = "password1234",
                role = Role.PENDING_NUTRITIONIST,
            )

        Given("유효한 회원가입 요청이 들어왔을 때") {
            every { memberRepository.existsByEmail(request.email) } returns false
            every { passwordEncoder.encode(request.password) } returns "encoded-password"
            val savedSlot = slot<Member>()
            every { memberRepository.save(capture(savedSlot)) } answers { savedSlot.captured }

            When("회원가입을 실행하면") {
                signupService.execute(request)

                Then("요청한 역할의 회원이 암호화된 비밀번호로 저장된다") {
                    verify(exactly = 1) { memberRepository.save(any()) }
                    savedSlot.captured.email shouldBe request.email
                    savedSlot.captured.role shouldBe Role.PENDING_NUTRITIONIST
                    savedSlot.captured.password shouldBe "encoded-password"
                }
            }
        }

        Given("학생 역할로 회원가입 요청이 들어왔을 때") {
            val studentRequest = request.copy(email = "student@hororog.team", role = Role.STUDENT)
            every { memberRepository.existsByEmail(studentRequest.email) } returns false
            every { passwordEncoder.encode(studentRequest.password) } returns "encoded-password"
            val savedSlot = slot<Member>()
            every { memberRepository.save(capture(savedSlot)) } answers { savedSlot.captured }

            When("회원가입을 실행하면") {
                signupService.execute(studentRequest)

                Then("STUDENT 역할의 회원이 저장된다") {
                    savedSlot.captured.role shouldBe Role.STUDENT
                }
            }
        }

        Given("이미 가입된 이메일로 가입을 시도할 때") {
            every { memberRepository.existsByEmail(request.email) } returns true

            When("회원가입을 실행하면") {
                Then("EmailAlreadyExistsException이 발생한다") {
                    shouldThrow<EmailAlreadyExistsException> {
                        signupService.execute(request)
                    }
                    verify(exactly = 0) { memberRepository.save(any()) }
                }
            }
        }

        Given("ADMIN 권한으로 회원가입을 시도할 때") {
            val adminRequest = request.copy(role = Role.ADMIN)

            When("회원가입을 실행하면") {
                Then("InvalidSignupRoleException이 발생한다") {
                    shouldThrow<InvalidSignupRoleException> {
                        signupService.execute(adminRequest)
                    }
                    verify(exactly = 0) { memberRepository.save(any()) }
                }
            }
        }

        Given("NUTRITIONIST 권한으로 회원가입을 시도할 때") {
            val nutritionistRequest = request.copy(role = Role.NUTRITIONIST)

            When("회원가입을 실행하면") {
                Then("InvalidSignupRoleException이 발생한다") {
                    shouldThrow<InvalidSignupRoleException> {
                        signupService.execute(nutritionistRequest)
                    }
                    verify(exactly = 0) { memberRepository.save(any()) }
                }
            }
        }
    })
