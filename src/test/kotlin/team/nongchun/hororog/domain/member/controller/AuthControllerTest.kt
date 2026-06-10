package team.nongchun.hororog.domain.member.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.member.entity.Member
import team.nongchun.hororog.domain.member.entity.Role
import team.nongchun.hororog.domain.member.repository.MemberRepository
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
        private val passwordEncoder: PasswordEncoder,
    ) {
        @Test
        fun `유효한 요청이면 204를 반환하고 NUTRITIONIST 회원을 저장한다`() {
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
                assertEquals(Role.NUTRITIONIST, role)
                assertTrue(password != "password1234", "비밀번호가 암호화되어 저장되어야 한다")
            }
        }

        @Test
        fun `이미 가입된 이메일이면 409를 반환한다`() {
            memberRepository.save(
                Member(
                    email = "dup@hororog.team",
                    password = requireNotNull(passwordEncoder.encode("password1234")),
                    name = "기존",
                    schoolName = "농촌초등학교",
                    role = Role.NUTRITIONIST,
                ),
            )

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
    }
