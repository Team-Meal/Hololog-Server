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
import org.springframework.test.web.servlet.get
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

        private fun accessToken(member: Member) = jwtProvider.createAccessToken(member.id, member.role)

        @Test
        fun `학생 교사 영양사가 내 프로필을 조회하면 200을 반환한다`() {
            listOf(
                saveMember(Role.STUDENT, "student@hororog.team"),
                saveMember(Role.TEACHER, "teacher@hororog.team"),
                saveMember(Role.NUTRITIONIST, "nutritionist@hororog.team"),
            ).forEach { member ->
                mockMvc
                    .get("/members/me") {
                        header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(member)}")
                    }.andExpect {
                        status { isOk() }
                        jsonPath("$.name") { value("김영양") }
                        jsonPath("$.role") { value(member.role.name) }
                        jsonPath("$.schoolName") { value("농촌초등학교") }
                    }
            }
        }

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
