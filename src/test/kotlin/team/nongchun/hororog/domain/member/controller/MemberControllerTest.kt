package team.nongchun.hororog.domain.member.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.patch
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.member.entity.Member
import team.nongchun.hororog.domain.member.entity.Role
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.JwtProvider
import team.nongchun.hororog.global.auth.RefreshTokenRepository
import kotlin.test.assertEquals

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MemberControllerTest
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
        private val memberRepository: MemberRepository,
        private val passwordEncoder: PasswordEncoder,
        private val jwtProvider: JwtProvider,
    ) {
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

        private fun accessToken(member: Member) = jwtProvider.createAccessToken(member.id, member.role)

        @Test
        fun `영양사가 학교명을 직접 수정하면 403을 반환하고 학교명은 변경되지 않는다`() {
            val member = saveMember()

            mockMvc
                .patch("/members/me/school-name") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(member)}")
                    contentType = MediaType.APPLICATION_JSON
                    content = """{ "schoolName": "도시초등학교" }"""
                }.andExpect {
                    status { isForbidden() }
                }

            assertEquals("농촌초등학교", memberRepository.findById(member.id).get().schoolName)
        }
    }
