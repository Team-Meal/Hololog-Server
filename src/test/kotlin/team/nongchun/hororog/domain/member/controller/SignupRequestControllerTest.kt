package team.nongchun.hororog.domain.member.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.member.entity.Member
import team.nongchun.hororog.domain.member.entity.NutritionistSignupRequest
import team.nongchun.hororog.domain.member.entity.Role
import team.nongchun.hororog.domain.member.entity.SignupStatus
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.domain.member.repository.NutritionistSignupRequestRepository
import team.nongchun.hororog.global.auth.JwtProvider
import kotlin.test.assertEquals

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SignupRequestControllerTest
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
        private val memberRepository: MemberRepository,
        private val signupRequestRepository: NutritionistSignupRequestRepository,
        private val jwtProvider: JwtProvider,
    ) {
        private fun saveMember(
            role: Role = Role.PENDING_NUTRITIONIST,
            email: String = "nutritionist@hororog.team",
        ) = memberRepository.save(
            Member(
                email = email,
                password = "encoded",
                name = "김영양",
                schoolName = "농촌초등학교",
                role = role,
            ),
        )

        private fun savePendingRequest(member: Member) =
            signupRequestRepository.save(
                NutritionistSignupRequest(
                    member = member,
                    licenseNumber = "123456789",
                    status = SignupStatus.PENDING,
                ),
            )

        private fun accessToken(member: Member) = jwtProvider.createAccessToken(member.id, member.role)

        @Test
        fun `PENDING_NUTRITIONIST 회원이 면허번호를 제출하면 201과 PENDING 요청을 반환한다`() {
            val member = saveMember()

            mockMvc
                .post("/signup-requests") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(member)}")
                    contentType = MediaType.APPLICATION_JSON
                    content = """{ "licenseNumber": "123456789" }"""
                }.andExpect {
                    status { isCreated() }
                    jsonPath("$.requestId") { exists() }
                    jsonPath("$.status") { value("PENDING") }
                }

            val saved = signupRequestRepository.findAll()
            assertEquals(1, saved.size)
            with(saved.first()) {
                assertEquals(member.id, this.member.id)
                assertEquals("123456789", licenseNumber)
                assertEquals(SignupStatus.PENDING, status)
            }
        }

        @Test
        fun `이미 승인된 NUTRITIONIST는 가입 요청을 제출할 수 없다`() {
            val member = saveMember(role = Role.NUTRITIONIST)

            mockMvc
                .post("/signup-requests") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(member)}")
                    contentType = MediaType.APPLICATION_JSON
                    content = """{ "licenseNumber": "123456789" }"""
                }.andExpect {
                    status { isForbidden() }
                }
        }

        @Test
        fun `이미 PENDING 요청이 있으면 409를 반환한다`() {
            val member = saveMember()
            savePendingRequest(member)

            mockMvc
                .post("/signup-requests") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(member)}")
                    contentType = MediaType.APPLICATION_JSON
                    content = """{ "licenseNumber": "987654321" }"""
                }.andExpect {
                    status { isConflict() }
                    jsonPath("$.status") { value(409) }
                }

            assertEquals(1, signupRequestRepository.findAll().size)
        }

        @Test
        fun `면허번호가 비어 있으면 400을 반환한다`() {
            val member = saveMember()

            mockMvc
                .post("/signup-requests") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(member)}")
                    contentType = MediaType.APPLICATION_JSON
                    content = """{ "licenseNumber": "" }"""
                }.andExpect {
                    status { isBadRequest() }
                }
        }

        @Test
        fun `토큰 없이 요청하면 인증 오류로 막힌다`() {
            mockMvc
                .post("/signup-requests") {
                    contentType = MediaType.APPLICATION_JSON
                    content = """{ "licenseNumber": "123456789" }"""
                }.andExpect {
                    status { is4xxClientError() }
                }
        }

        @Test
        fun `관리자가 승인하면 200과 APPROVED를 반환하고 회원이 NUTRITIONIST가 된다`() {
            val member = saveMember()
            val request = savePendingRequest(member)
            val admin = saveMember(role = Role.ADMIN, email = "admin@hororog.team")

            mockMvc
                .post("/signup-requests/${request.id}/approve") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(admin)}")
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.status") { value("APPROVED") }
                }

            assertEquals(SignupStatus.APPROVED, signupRequestRepository.findById(request.id).get().status)
            assertEquals(Role.NUTRITIONIST, memberRepository.findById(member.id).get().role)
        }

        @Test
        fun `관리자가 거절하면 200과 REJECTED를 반환하고 회원은 PENDING_NUTRITIONIST로 남는다`() {
            val member = saveMember()
            val request = savePendingRequest(member)
            val admin = saveMember(role = Role.ADMIN, email = "admin@hororog.team")

            mockMvc
                .post("/signup-requests/${request.id}/reject") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(admin)}")
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.status") { value("REJECTED") }
                }

            assertEquals(SignupStatus.REJECTED, signupRequestRepository.findById(request.id).get().status)
            assertEquals(Role.PENDING_NUTRITIONIST, memberRepository.findById(member.id).get().role)
        }

        @Test
        fun `관리자가 아니면 승인할 수 없다`() {
            val member = saveMember()
            val request = savePendingRequest(member)

            mockMvc
                .post("/signup-requests/${request.id}/approve") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(member)}")
                }.andExpect {
                    status { isForbidden() }
                }
        }

        @Test
        fun `존재하지 않는 가입 요청을 승인하면 404를 반환한다`() {
            val admin = saveMember(role = Role.ADMIN, email = "admin@hororog.team")

            mockMvc
                .post("/signup-requests/999999/approve") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(admin)}")
                }.andExpect {
                    status { isNotFound() }
                }
        }

        @Test
        fun `이미 처리된 가입 요청을 다시 승인하면 409를 반환한다`() {
            val member = saveMember()
            val request = savePendingRequest(member)
            val admin = saveMember(role = Role.ADMIN, email = "admin@hororog.team")

            mockMvc.post("/signup-requests/${request.id}/reject") {
                header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(admin)}")
            }

            mockMvc
                .post("/signup-requests/${request.id}/approve") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(admin)}")
                }.andExpect {
                    status { isConflict() }
                }
        }
    }
