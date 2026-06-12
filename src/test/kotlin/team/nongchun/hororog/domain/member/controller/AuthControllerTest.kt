package team.nongchun.hororog.domain.member.controller

import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.bean.override.mockito.MockitoBean
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
import team.nongchun.hororog.global.auth.RefreshToken
import team.nongchun.hororog.global.auth.RefreshTokenRepository
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
        private val memberRepository: MemberRepository,
        private val signupRequestRepository: NutritionistSignupRequestRepository,
        private val passwordEncoder: PasswordEncoder,
        private val jwtProvider: JwtProvider,
    ) {
        // Redis 실연결 없이 검증하기 위해 refresh 토큰 저장소를 목으로 대체
        @MockitoBean
        lateinit var refreshTokenRepository: RefreshTokenRepository

        private fun saveMember(
            role: Role = Role.NUTRITIONIST,
            email: String = "nutritionist@hororog.team",
            rawPassword: String? = null,
        ) = memberRepository.save(
            Member(
                email = email,
                password = rawPassword?.let { requireNotNull(passwordEncoder.encode(it)) } ?: "encoded",
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

        // --- 회원가입 ---

        @Test
        fun `유효한 요청이면 204를 반환하고 PENDING_NUTRITIONIST 회원을 저장한다`() {
            val body =
                """
                {
                  "email": "nutritionist@hororog.team",
                  "name": "김영양",
                  "schoolName": "농촌초등학교",
                  "password": "password1234"
                }
                """.trimIndent()

            mockMvc
                .post("/auth/signup") {
                    contentType = MediaType.APPLICATION_JSON
                    content = body
                }.andExpect {
                    status { isNoContent() }
                }

            val saved = memberRepository.findAll()
            assertEquals(1, saved.size)
            with(saved.first()) {
                assertEquals("nutritionist@hororog.team", email)
                assertEquals(Role.PENDING_NUTRITIONIST, role)
                assertTrue(password != "password1234", "비밀번호가 암호화되어 저장되어야 한다")
            }
        }

        @Test
        fun `이미 가입된 이메일이면 409를 반환한다`() {
            saveMember(email = "dup@hororog.team", rawPassword = "password1234")

            val body =
                """
                {
                  "email": "dup@hororog.team",
                  "name": "신규",
                  "schoolName": "농촌초등학교",
                  "password": "password1234"
                }
                """.trimIndent()

            mockMvc
                .post("/auth/signup") {
                    contentType = MediaType.APPLICATION_JSON
                    content = body
                }.andExpect {
                    status { isConflict() }
                    jsonPath("$.status") { value(409) }
                }
        }

        @Test
        fun `이메일 형식이 잘못되면 400을 반환한다`() {
            val body =
                """
                {
                  "email": "not-an-email",
                  "name": "김영양",
                  "schoolName": "농촌초등학교",
                  "password": "password1234"
                }
                """.trimIndent()

            mockMvc
                .post("/auth/signup") {
                    contentType = MediaType.APPLICATION_JSON
                    content = body
                }.andExpect {
                    status { isBadRequest() }
                }
        }

        @Test
        fun `비밀번호가 8자 미만이면 400을 반환한다`() {
            val body =
                """
                {
                  "email": "short@hororog.team",
                  "name": "김영양",
                  "schoolName": "농촌초등학교",
                  "password": "123"
                }
                """.trimIndent()

            mockMvc
                .post("/auth/signup") {
                    contentType = MediaType.APPLICATION_JSON
                    content = body
                }.andExpect {
                    status { isBadRequest() }
                }
        }

        // --- 로그인 ---

        @Test
        fun `올바른 자격증명이면 200과 토큰들을 반환한다`() {
            saveMember(rawPassword = "password1234")

            mockMvc
                .post("/auth/signin") {
                    contentType = MediaType.APPLICATION_JSON
                    content = """{ "email": "nutritionist@hororog.team", "password": "password1234" }"""
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.accessToken") { exists() }
                    jsonPath("$.refreshToken") { exists() }
                    jsonPath("$.accessTokenExpiresAt") { exists() }
                    jsonPath("$.refreshTokenExpiresAt") { exists() }
                    jsonPath("$.role") { value("NUTRITIONIST") }
                }
        }

        @Test
        fun `비밀번호가 틀리면 401을 반환한다`() {
            saveMember(rawPassword = "password1234")

            mockMvc
                .post("/auth/signin") {
                    contentType = MediaType.APPLICATION_JSON
                    content = """{ "email": "nutritionist@hororog.team", "password": "wrong-password" }"""
                }.andExpect {
                    status { isUnauthorized() }
                    jsonPath("$.status") { value(401) }
                }
        }

        // --- 토큰 재발급 ---

        @Test
        fun `유효한 refresh 토큰이면 200과 새 토큰을 반환한다`() {
            val member = saveMember()
            val refreshToken = jwtProvider.createRefreshToken(member.id)
            given(refreshTokenRepository.findById(member.id))
                .willReturn(Optional.of(RefreshToken(member.id, refreshToken, 1_209_600L)))

            mockMvc
                .post("/auth/reissue") {
                    header("Refresh-Token", refreshToken)
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.accessToken") { exists() }
                    jsonPath("$.refreshToken") { exists() }
                    jsonPath("$.role") { value("NUTRITIONIST") }
                }
        }

        @Test
        fun `Redis에 없는 refresh 토큰이면 401을 반환한다`() {
            val member = saveMember()
            val refreshToken = jwtProvider.createRefreshToken(member.id)
            given(refreshTokenRepository.findById(member.id)).willReturn(Optional.empty())

            mockMvc
                .post("/auth/reissue") {
                    header("Refresh-Token", refreshToken)
                }.andExpect {
                    status { isUnauthorized() }
                    jsonPath("$.status") { value(401) }
                }
        }

        @Test
        fun `access 토큰으로는 재발급할 수 없다`() {
            val member = saveMember()

            mockMvc
                .post("/auth/reissue") {
                    header("Refresh-Token", accessToken(member))
                }.andExpect {
                    status { isUnauthorized() }
                    jsonPath("$.status") { value(401) }
                }
        }

        // --- 로그아웃 ---

        @Test
        fun `refresh 토큰으로는 보호된 API에 접근할 수 없다`() {
            val member = saveMember()
            val refreshToken = jwtProvider.createRefreshToken(member.id)

            mockMvc
                .post("/auth/logout") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer $refreshToken")
                }.andExpect {
                    status { is4xxClientError() }
                }
        }

        @Test
        fun `인증된 회원이 로그아웃하면 204와 함께 refresh를 삭제한다`() {
            val member = saveMember()

            mockMvc
                .post("/auth/logout") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(member)}")
                }.andExpect {
                    status { isNoContent() }
                }

            verify(refreshTokenRepository).deleteById(member.id)
        }

        @Test
        fun `토큰 없이 로그아웃하면 인증 오류로 막힌다`() {
            mockMvc
                .post("/auth/logout") {
                }.andExpect {
                    status { is4xxClientError() }
                }
        }

        // --- 가입 요청 제출 ---

        @Test
        fun `PENDING_NUTRITIONIST 회원이 면허번호를 제출하면 201과 PENDING 요청을 반환한다`() {
            val member = saveMember(role = Role.PENDING_NUTRITIONIST)

            mockMvc
                .post("/auth/signup-requests") {
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
                .post("/auth/signup-requests") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(member)}")
                    contentType = MediaType.APPLICATION_JSON
                    content = """{ "licenseNumber": "123456789" }"""
                }.andExpect {
                    status { isForbidden() }
                }
        }

        @Test
        fun `이미 PENDING 요청이 있으면 409를 반환한다`() {
            val member = saveMember(role = Role.PENDING_NUTRITIONIST)
            savePendingRequest(member)

            mockMvc
                .post("/auth/signup-requests") {
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
            val member = saveMember(role = Role.PENDING_NUTRITIONIST)

            mockMvc
                .post("/auth/signup-requests") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(member)}")
                    contentType = MediaType.APPLICATION_JSON
                    content = """{ "licenseNumber": "" }"""
                }.andExpect {
                    status { isBadRequest() }
                }
        }

        @Test
        fun `토큰 없이 가입 요청을 제출하면 인증 오류로 막힌다`() {
            mockMvc
                .post("/auth/signup-requests") {
                    contentType = MediaType.APPLICATION_JSON
                    content = """{ "licenseNumber": "123456789" }"""
                }.andExpect {
                    status { is4xxClientError() }
                }
        }

        // --- 가입 요청 승인/거절 ---

        @Test
        fun `관리자가 승인하면 200과 APPROVED를 반환하고 회원이 NUTRITIONIST가 된다`() {
            val member = saveMember(role = Role.PENDING_NUTRITIONIST)
            val request = savePendingRequest(member)
            val admin = saveMember(role = Role.ADMIN, email = "admin@hororog.team")

            mockMvc
                .post("/auth/signup-requests/${request.id}/approve") {
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
            val member = saveMember(role = Role.PENDING_NUTRITIONIST)
            val request = savePendingRequest(member)
            val admin = saveMember(role = Role.ADMIN, email = "admin@hororog.team")

            mockMvc
                .post("/auth/signup-requests/${request.id}/reject") {
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
            val member = saveMember(role = Role.PENDING_NUTRITIONIST)
            val request = savePendingRequest(member)

            mockMvc
                .post("/auth/signup-requests/${request.id}/approve") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(member)}")
                }.andExpect {
                    status { isForbidden() }
                }
        }

        @Test
        fun `존재하지 않는 가입 요청을 승인하면 404를 반환한다`() {
            val admin = saveMember(role = Role.ADMIN, email = "admin@hororog.team")

            mockMvc
                .post("/auth/signup-requests/999999/approve") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(admin)}")
                }.andExpect {
                    status { isNotFound() }
                }
        }

        @Test
        fun `이미 처리된 가입 요청을 다시 승인하면 409를 반환한다`() {
            val member = saveMember(role = Role.PENDING_NUTRITIONIST)
            val request = savePendingRequest(member)
            val admin = saveMember(role = Role.ADMIN, email = "admin@hororog.team")

            mockMvc.post("/auth/signup-requests/${request.id}/reject") {
                header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(admin)}")
            }

            mockMvc
                .post("/auth/signup-requests/${request.id}/approve") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(admin)}")
                }.andExpect {
                    status { isConflict() }
                }
        }
    }
