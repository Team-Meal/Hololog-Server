package team.nongchun.hororog.domain.budget.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.budget.entity.Budget
import team.nongchun.hororog.domain.budget.repository.BudgetRepository
import team.nongchun.hororog.domain.member.entity.Member
import team.nongchun.hororog.domain.member.entity.Role
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.JwtProvider
import team.nongchun.hororog.global.auth.RefreshTokenRepository
import java.time.LocalDate
import kotlin.test.assertFalse

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BudgetControllerTest
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
        private val memberRepository: MemberRepository,
        private val budgetRepository: BudgetRepository,
        private val passwordEncoder: PasswordEncoder,
        private val jwtProvider: JwtProvider,
    ) {
        @MockitoBean
        lateinit var refreshTokenRepository: RefreshTokenRepository

        private fun saveMember(
            role: Role = Role.NUTRITIONIST,
            email: String = "nutritionist@hororog.team",
            schoolName: String = "농촌초등학교",
        ) = memberRepository.save(
            Member(
                email = email,
                password = requireNotNull(passwordEncoder.encode("password1234")),
                name = "김영양",
                schoolName = schoolName,
                role = role,
            ),
        )

        private fun saveBudget(
            member: Member,
            name: String = "6월 급식 예산",
        ) = budgetRepository.save(
            Budget(
                member = member,
                name = name,
                totalBudget = 1_000_000,
                usedBudget = 100_000,
                startDate = LocalDate.of(2026, 6, 1),
                endDate = LocalDate.of(2026, 6, 30),
            ),
        )

        private fun accessToken(member: Member) = jwtProvider.createAccessToken(member.id, member.role)

        @Test
        fun `인증된 영양사가 예산을 입력하면 204를 반환한다`() {
            val member = saveMember()

            mockMvc
                .post("/budgets") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(member)}")
                    contentType = MediaType.APPLICATION_JSON
                    content =
                        """
                        {
                          "title": "6월 급식 예산",
                          "totalAmount": 1000000,
                          "usedAmount": 100000,
                          "startDate": "2026-06-01",
                          "endDate": "2026-06-30"
                        }
                        """.trimIndent()
                }.andExpect {
                    status { isNoContent() }
                }
        }

        @Test
        fun `예산 목록은 같은 학교 예산만 반환한다`() {
            val member = saveMember()
            val sameSchoolMember = saveMember(email = "same@hororog.team")
            val otherSchoolMember = saveMember(email = "other@hororog.team", schoolName = "도시초등학교")
            saveBudget(member, "내 예산")
            saveBudget(sameSchoolMember, "같은 학교 예산")
            saveBudget(otherSchoolMember, "다른 학교 예산")

            mockMvc
                .get("/budgets") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(member)}")
                }.andExpect {
                    status { isOk() }
                    jsonPath("$[0].title") { value("내 예산") }
                    jsonPath("$[1].title") { value("같은 학교 예산") }
                    jsonPath("$.length()") { value(2) }
                }
        }

        @Test
        fun `같은 학교 예산을 단건 조회하면 200과 상세를 반환한다`() {
            val member = saveMember()
            val budget = saveBudget(member)

            mockMvc
                .get("/budgets/${budget.id}") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(member)}")
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.id") { value(budget.id) }
                    jsonPath("$.title") { value("6월 급식 예산") }
                    jsonPath("$.totalAmount") { value(1_000_000) }
                    jsonPath("$.usedAmount") { value(100_000) }
                    jsonPath("$.startDate") { value("2026-06-01") }
                    jsonPath("$.endDate") { value("2026-06-30") }
                }
        }

        @Test
        fun `다른 학교 예산을 조회하면 404를 반환한다`() {
            val member = saveMember()
            val otherSchoolMember = saveMember(email = "other@hororog.team", schoolName = "도시초등학교")
            val budget = saveBudget(otherSchoolMember)

            mockMvc
                .get("/budgets/${budget.id}") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(member)}")
                }.andExpect {
                    status { isNotFound() }
                }
        }

        @Test
        fun `예산을 부분 수정하면 200과 id를 반환한다`() {
            val member = saveMember()
            val budget = saveBudget(member)

            mockMvc
                .patch("/budgets/${budget.id}") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(member)}")
                    contentType = MediaType.APPLICATION_JSON
                    content = """{ "title": "수정된 예산", "usedAmount": 200000 }"""
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.id") { value(budget.id) }
                }
        }

        @Test
        fun `사용 금액이 전체 예산을 초과하면 400을 반환한다`() {
            val member = saveMember()

            mockMvc
                .post("/budgets") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(member)}")
                    contentType = MediaType.APPLICATION_JSON
                    content =
                        """
                        {
                          "title": "6월 급식 예산",
                          "totalAmount": 1000000,
                          "usedAmount": 1000001,
                          "startDate": "2026-06-01",
                          "endDate": "2026-06-30"
                        }
                        """.trimIndent()
                }.andExpect {
                    status { isBadRequest() }
                    jsonPath("$.status") { value(400) }
                }
        }

        @Test
        fun `예산을 삭제하면 204를 반환하고 데이터가 삭제된다`() {
            val member = saveMember()
            val budget = saveBudget(member)

            mockMvc
                .delete("/budgets/${budget.id}") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(member)}")
                }.andExpect {
                    status { isNoContent() }
                }

            assertFalse(budgetRepository.existsById(budget.id))
        }
    }
