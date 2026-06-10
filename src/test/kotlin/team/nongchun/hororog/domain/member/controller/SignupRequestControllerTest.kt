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
        @Test
        fun `인증된 회원이 면허번호를 제출하면 201과 PENDING 요청을 반환한다`() {
            val member =
                memberRepository.save(
                    Member(
                        email = "nutritionist@hororog.team",
                        password = "encoded",
                        name = "김영양",
                        schoolName = "농촌초등학교",
                        role = Role.NUTRITIONIST,
                    ),
                )
            val token = jwtProvider.createAccessToken(member.id, member.role)

            mockMvc
                .post("/signup-requests") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                    contentType = MediaType.APPLICATION_JSON
                    content = """{ "license_number": 123456789 }"""
                }.andExpect {
                    status { isCreated() }
                    jsonPath("$.requestId") { exists() }
                    jsonPath("$.status") { value("PENDING") }
                }

            val saved = signupRequestRepository.findAll()
            assertEquals(1, saved.size)
            with(saved.first()) {
                assertEquals(member.id, this.member.id)
                assertEquals(123456789L, licenseNumber)
                assertEquals(SignupStatus.PENDING, status)
            }
        }

        @Test
        fun `토큰 없이 요청하면 인증 오류로 막힌다`() {
            mockMvc
                .post("/signup-requests") {
                    contentType = MediaType.APPLICATION_JSON
                    content = """{ "license_number": 123456789 }"""
                }.andExpect {
                    status { is4xxClientError() }
                }
        }
    }
