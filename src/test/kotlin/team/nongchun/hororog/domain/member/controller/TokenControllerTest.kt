package team.nongchun.hororog.domain.member.controller

import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.HttpHeaders
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.member.entity.Member
import team.nongchun.hororog.domain.member.entity.Role
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.JwtProvider
import team.nongchun.hororog.global.auth.RefreshToken
import team.nongchun.hororog.global.auth.RefreshTokenRepository
import java.util.Optional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TokenControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var memberRepository: MemberRepository

    @Autowired
    lateinit var jwtProvider: JwtProvider

    @MockitoBean
    lateinit var refreshTokenRepository: RefreshTokenRepository

    private fun saveMember() =
        memberRepository.save(
            Member(
                email = "nutritionist@hororog.team",
                password = "encoded",
                name = "김영양",
                schoolName = "농촌초등학교",
                role = Role.NUTRITIONIST,
            ),
        )

    @Test
    fun `유효한 refresh 토큰이면 200과 새 토큰을 반환한다`() {
        val member = saveMember()
        val refreshToken = jwtProvider.createRefreshToken(member.id)
        given(refreshTokenRepository.findById(member.id))
            .willReturn(Optional.of(RefreshToken(member.id, refreshToken, 1_209_600L)))

        mockMvc
            .post("/reissue") {
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
            .post("/reissue") {
                header("Refresh-Token", refreshToken)
            }.andExpect {
                status { isUnauthorized() }
                jsonPath("$.status") { value(401) }
            }
    }

    @Test
    fun `인증된 회원이 로그아웃하면 204와 함께 refresh를 삭제한다`() {
        val member = saveMember()
        val accessToken = jwtProvider.createAccessToken(member.id, member.role)

        mockMvc
            .post("/logout") {
                header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            }.andExpect {
                status { isNoContent() }
            }

        verify(refreshTokenRepository).deleteById(member.id)
    }

    @Test
    fun `토큰 없이 로그아웃하면 인증 오류로 막힌다`() {
        mockMvc
            .post("/logout") {
            }.andExpect {
                status { is4xxClientError() }
            }
    }
}
