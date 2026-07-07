package team.nongchun.hororog.domain.diet.controller

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
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.diet.entity.Diet
import team.nongchun.hororog.domain.diet.repository.DietRepository
import team.nongchun.hororog.domain.leftover.entity.Leftover
import team.nongchun.hororog.domain.leftover.repository.LeftoverRepository
import team.nongchun.hororog.domain.meal.entity.MealType
import team.nongchun.hororog.domain.meal.repository.MealRepository
import team.nongchun.hororog.domain.member.entity.Member
import team.nongchun.hororog.domain.member.entity.Role
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.JwtProvider
import team.nongchun.hororog.global.auth.RefreshTokenRepository
import team.nongchun.hororog.global.common.QuantityUnit
import java.time.LocalDate
import kotlin.test.assertEquals

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DietControllerTest
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
        private val memberRepository: MemberRepository,
        private val dietRepository: DietRepository,
        private val leftoverRepository: LeftoverRepository,
        private val mealRepository: MealRepository,
        private val passwordEncoder: PasswordEncoder,
        private val jwtProvider: JwtProvider,
    ) {
        @MockitoBean
        lateinit var refreshTokenRepository: RefreshTokenRepository

        private fun saveMember(
            email: String,
            schoolName: String = "농촌초등학교",
        ) = memberRepository.save(
            Member(
                email = email,
                password = requireNotNull(passwordEncoder.encode("password1234")),
                name = "김영양",
                schoolName = schoolName,
                role = Role.NUTRITIONIST,
            ),
        )

        private fun saveDiet(
            member: Member,
            name: String,
        ) = dietRepository.save(
            Diet(
                member = member,
                name = name,
                description = null,
                dietDate = LocalDate.of(2026, 6, 29),
            ),
        )

        private fun accessToken(member: Member) = jwtProvider.createAccessToken(member.id, member.role)

        @Test
        fun `AI 콜백은 내부 토큰만으로 급식 슬롯을 생성한다`() {
            val member = saveMember("nutritionist@hororog.team")

            mockMvc
                .post("/diets/ai-callback") {
                    header("X-Internal-Token", "test-internal-key")
                    contentType = MediaType.APPLICATION_JSON
                    content =
                        """
                        {
                          "date": "2026-06-29",
                          "meal_type": "LUNCH",
                          "school_id": ${member.id}
                        }
                        """.trimIndent()
                }.andExpect {
                    status { isCreated() }
                }

            val meals = mealRepository.findAll()
            assertEquals(1, meals.size)
            assertEquals(member.id, meals[0].member.id)
            assertEquals("", meals[0].name)
            assertEquals(MealType.LUNCH, meals[0].mealType)
        }

        @Test
        fun `AI 콜백 내부 토큰이 틀리면 401을 반환한다`() {
            val member = saveMember("nutritionist@hororog.team")

            mockMvc
                .post("/diets/ai-callback") {
                    header("X-Internal-Token", "wrong-token")
                    contentType = MediaType.APPLICATION_JSON
                    content =
                        """
                        {
                          "date": "2026-06-29",
                          "meal_type": "LUNCH",
                          "school_id": ${member.id}
                        }
                        """.trimIndent()
                }.andExpect {
                    status { isUnauthorized() }
                }

            assertEquals(0, mealRepository.findAll().size)
        }

        @Test
        fun `식단 목록은 같은 학교 식단만 반환한다`() {
            val member = saveMember("nutritionist@hororog.team")
            val sameSchoolMember = saveMember("same@hororog.team")
            val otherSchoolMember = saveMember("other@hororog.team", "도시초등학교")
            saveDiet(member, "내 식단")
            saveDiet(sameSchoolMember, "같은 학교 식단")
            saveDiet(otherSchoolMember, "다른 학교 식단")

            mockMvc
                .get("/diets") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(member)}")
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.length()") { value(2) }
                    jsonPath("$[0].name") { value("같은 학교 식단") }
                    jsonPath("$[1].name") { value("내 식단") }
                }
        }

        @Test
        fun `다른 학교 식단을 조회하면 404를 반환한다`() {
            val member = saveMember("nutritionist@hororog.team")
            val otherSchoolMember = saveMember("other@hororog.team", "도시초등학교")
            val diet = saveDiet(otherSchoolMember, "다른 학교 식단")

            mockMvc
                .get("/diets/${diet.id}") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(member)}")
                }.andExpect {
                    status { isNotFound() }
                }
        }

        @Test
        fun `잔반을 두 번 입력하면 기존 잔반을 갱신한다`() {
            val member = saveMember("nutritionist@hororog.team")
            val diet = saveDiet(member, "점심 식단")
            leftoverRepository.save(
                Leftover(
                    diet = diet,
                    amount = 10,
                    unit = QuantityUnit.KG,
                    memo = "기존",
                ),
            )

            mockMvc
                .post("/diets/${diet.id}/leftovers") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(member)}")
                    contentType = MediaType.APPLICATION_JSON
                    content = """{ "amount": 20, "unit": "KG", "memo": "수정" }"""
                }.andExpect {
                    status { isNoContent() }
                }

            val leftovers = leftoverRepository.findAll()
            assertEquals(1, leftovers.size)
            assertEquals(20, leftovers[0].amount)
            assertEquals("수정", leftovers[0].memo)
        }
    }
