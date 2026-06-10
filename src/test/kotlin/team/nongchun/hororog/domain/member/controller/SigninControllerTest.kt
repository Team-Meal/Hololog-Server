package team.nongchun.hororog.domain.member.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.member.entity.Member
import team.nongchun.hororog.domain.member.entity.Role
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.RefreshTokenRepository

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SigninControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var memberRepository: MemberRepository

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    // Redis 실연결 없이 검증하기 위해 refresh 토큰 저장소를 목으로 대체
    @MockitoBean
    lateinit var refreshTokenRepository: RefreshTokenRepository

    private fun saveMember() =
        memberRepository.save(
            Member(
                email = "nutritionist@hororog.team",
                password = requireNotNull(passwordEncoder.encode("password1234")),
                name = "김영양",
                schoolName = "농촌초등학교",
                role = Role.NUTRITIONIST,
            ),
        )

    @Test
    fun `올바른 자격증명이면 200과 토큰들을 반환한다`() {
        saveMember()

        mockMvc
            .post("/signin") {
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
        saveMember()

        mockMvc
            .post("/signin") {
                contentType = MediaType.APPLICATION_JSON
                content = """{ "email": "nutritionist@hororog.team", "password": "wrong-password" }"""
            }.andExpect {
                status { isUnauthorized() }
                jsonPath("$.status") { value(401) }
            }
    }
}
