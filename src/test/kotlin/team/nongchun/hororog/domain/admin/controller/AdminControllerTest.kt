package team.nongchun.hororog.domain.admin.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.HttpHeaders
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.member.entity.Member
import team.nongchun.hororog.domain.member.entity.NutritionistSignupRequest
import team.nongchun.hororog.domain.member.entity.Role
import team.nongchun.hororog.domain.member.entity.SignupStatus
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.domain.member.repository.NutritionistSignupRequestRepository
import team.nongchun.hororog.global.auth.JwtProvider
import team.nongchun.hororog.global.auth.RefreshTokenRepository
import kotlin.test.assertEquals

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdminControllerTest
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
        private val memberRepository: MemberRepository,
        private val signupRequestRepository: NutritionistSignupRequestRepository,
        private val passwordEncoder: PasswordEncoder,
        private val jwtProvider: JwtProvider,
    ) {
        @MockitoBean
        lateinit var refreshTokenRepository: RefreshTokenRepository

        private fun saveMember(
            role: Role = Role.NUTRITIONIST,
            email: String = "nutritionist@hororog.team",
        ) = memberRepository.save(
            Member(
                email = email,
                password = requireNotNull(passwordEncoder.encode("password1234")),
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

        // --- 목록 조회 ---

        @Test
        fun `관리자가 목록을 조회하면 200과 PENDING 요청 목록을 반환한다`() {
            val member = saveMember(role = Role.PENDING_NUTRITIONIST, email = "pending@hororog.team")
            savePendingRequest(member)
            val admin = saveMember(role = Role.ADMIN, email = "admin@hororog.team")

            mockMvc
                .get("/admin/signup-requests") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(admin)}")
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.content[0].requestId") { exists() }
                    jsonPath("$.content[0].memberId") { exists() }
                    jsonPath("$.content[0].name") { value("김영양") }
                    jsonPath("$.content[0].licenseNumber") { value("123456789") }
                    jsonPath("$.content[0].status") { value("PENDING") }
                }
        }

        @Test
        fun `PENDING 요청이 없으면 빈 목록을 반환한다`() {
            val admin = saveMember(role = Role.ADMIN, email = "admin@hororog.team")

            mockMvc
                .get("/admin/signup-requests") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(admin)}")
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.content") { isEmpty() }
                }
        }

        @Test
        fun `관리자가 아니면 목록을 조회할 수 없다`() {
            val member = saveMember(role = Role.NUTRITIONIST)

            mockMvc
                .get("/admin/signup-requests") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(member)}")
                }.andExpect {
                    status { isForbidden() }
                }
        }

        @Test
        fun `토큰 없이 목록을 조회하면 인증 오류로 막힌다`() {
            mockMvc
                .get("/admin/signup-requests")
                .andExpect {
                    status { is4xxClientError() }
                }
        }

        // --- 승인 ---

        @Test
        fun `관리자가 승인하면 200과 APPROVED를 반환하고 회원이 NUTRITIONIST가 된다`() {
            val member = saveMember(role = Role.PENDING_NUTRITIONIST, email = "pending@hororog.team")
            val request = savePendingRequest(member)
            val admin = saveMember(role = Role.ADMIN, email = "admin@hororog.team")

            mockMvc
                .post("/admin/signup-requests/${request.id}/approve") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(admin)}")
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.status") { value("APPROVED") }
                }

            assertEquals(SignupStatus.APPROVED, signupRequestRepository.findById(request.id).get().status)
            assertEquals(Role.NUTRITIONIST, memberRepository.findById(member.id).get().role)
        }

        @Test
        fun `관리자가 아니면 승인할 수 없다`() {
            val member = saveMember(role = Role.PENDING_NUTRITIONIST, email = "pending@hororog.team")
            val request = savePendingRequest(member)
            val nutritionist = saveMember(role = Role.NUTRITIONIST)

            mockMvc
                .post("/admin/signup-requests/${request.id}/approve") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(nutritionist)}")
                }.andExpect {
                    status { isForbidden() }
                }
        }

        @Test
        fun `존재하지 않는 가입 요청을 승인하면 404를 반환한다`() {
            val admin = saveMember(role = Role.ADMIN, email = "admin@hororog.team")

            mockMvc
                .post("/admin/signup-requests/999999/approve") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(admin)}")
                }.andExpect {
                    status { isNotFound() }
                }
        }

        @Test
        fun `이미 처리된 가입 요청을 다시 승인하면 409를 반환한다`() {
            val member = saveMember(role = Role.PENDING_NUTRITIONIST, email = "pending@hororog.team")
            val request = savePendingRequest(member)
            val admin = saveMember(role = Role.ADMIN, email = "admin@hororog.team")

            mockMvc.post("/admin/signup-requests/${request.id}/reject") {
                header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(admin)}")
            }

            mockMvc
                .post("/admin/signup-requests/${request.id}/approve") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(admin)}")
                }.andExpect {
                    status { isConflict() }
                }
        }

        // --- 거절 ---

        @Test
        fun `관리자가 거절하면 200과 REJECTED를 반환하고 회원은 PENDING_NUTRITIONIST로 남는다`() {
            val member = saveMember(role = Role.PENDING_NUTRITIONIST, email = "pending@hororog.team")
            val request = savePendingRequest(member)
            val admin = saveMember(role = Role.ADMIN, email = "admin@hororog.team")

            mockMvc
                .post("/admin/signup-requests/${request.id}/reject") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(admin)}")
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.status") { value("REJECTED") }
                }

            assertEquals(SignupStatus.REJECTED, signupRequestRepository.findById(request.id).get().status)
            assertEquals(Role.PENDING_NUTRITIONIST, memberRepository.findById(member.id).get().role)
        }
    }
