package team.nongchun.hororog.domain.meal.controller

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
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.meal.entity.Meal
import team.nongchun.hororog.domain.meal.entity.MealSuggestion
import team.nongchun.hororog.domain.meal.entity.MealType
import team.nongchun.hororog.domain.meal.entity.SuggestionStatus
import team.nongchun.hororog.domain.meal.repository.MealRepository
import team.nongchun.hororog.domain.meal.repository.MealSuggestionRepository
import team.nongchun.hororog.domain.member.entity.Member
import team.nongchun.hororog.domain.member.entity.Role
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.JwtProvider
import team.nongchun.hororog.global.auth.RefreshTokenRepository
import java.time.LocalDate
import kotlin.test.assertEquals

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MealControllerTest
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
        private val memberRepository: MemberRepository,
        private val mealRepository: MealRepository,
        private val mealSuggestionRepository: MealSuggestionRepository,
        private val passwordEncoder: PasswordEncoder,
        private val jwtProvider: JwtProvider,
    ) {
        @MockitoBean
        lateinit var refreshTokenRepository: RefreshTokenRepository

        private fun saveMember(
            role: Role,
            email: String,
            schoolName: String = "농촌초등학교",
        ) = memberRepository.save(
            Member(
                email = email,
                password = requireNotNull(passwordEncoder.encode("password1234")),
                name = "김호로록",
                schoolName = schoolName,
                role = role,
            ),
        )

        private fun accessToken(member: Member) = jwtProvider.createAccessToken(member.id, member.role)

        private fun saveMeal(
            member: Member,
            name: String,
            mealType: MealType = MealType.LUNCH,
        ) = mealRepository.save(
            Meal(
                member = member,
                name = name,
                mealType = mealType,
                mealDate = LocalDate.now().atTime(12, 0),
                totalCalories = 100,
                nutritionInfo = "단백질",
                originInfo = "쌀: 국내산",
            ),
        )

        private fun saveSuggestion(
            member: Member,
            title: String,
            status: SuggestionStatus = SuggestionStatus.PENDING,
        ) = mealSuggestionRepository.save(
            MealSuggestion(
                member = member,
                title = title,
                content = null,
                status = status,
            ),
        )

        @Test
        fun `학생이 오늘 급식을 조회하면 200과 메뉴 목록을 반환한다`() {
            val student = saveMember(Role.STUDENT, "student@hororog.team")
            saveMeal(student, "현미밥")
            saveMeal(student, "된장국")

            mockMvc
                .get("/meals/today") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(student)}")
                    param("mealType", "LUNCH")
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.mealType") { value("LUNCH") }
                    jsonPath("$.menuNames[0]") { value("현미밥") }
                    jsonPath("$.menuNames[1]") { value("된장국") }
                    jsonPath("$.calorie") { value("200") }
                }
        }

        @Test
        fun `학생이 먹고 싶은 급식을 추천하면 204를 반환한다`() {
            val student = saveMember(Role.STUDENT, "student@hororog.team")

            mockMvc
                .post("/meals/suggestions") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(student)}")
                    contentType = MediaType.APPLICATION_JSON
                    content =
                        """
                        {
                          "title": "제육볶음",
                          "content": "매운맛으로 먹고 싶어요.",
                          "mealSuggestionStatus": "PENDING"
                        }
                        """.trimIndent()
                }.andExpect {
                    status { isNoContent() }
                }

            assertEquals(1, mealSuggestionRepository.findAll().size)
        }

        @Test
        fun `학생이 승인 상태로 급식을 추천하면 400을 반환한다`() {
            val student = saveMember(Role.STUDENT, "student@hororog.team")

            mockMvc
                .post("/meals/suggestions") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(student)}")
                    contentType = MediaType.APPLICATION_JSON
                    content =
                        """
                        {
                          "title": "제육볶음",
                          "content": "바로 승인되면 안 됩니다.",
                          "mealSuggestionStatus": "APPROVED"
                        }
                        """.trimIndent()
                }.andExpect {
                    status { isBadRequest() }
                }

            assertEquals(0, mealSuggestionRepository.findAll().size)
        }

        @Test
        fun `영양사가 급식 추천 목록을 조회하면 같은 학교 추천만 반환한다`() {
            val nutritionist = saveMember(Role.NUTRITIONIST, "nutritionist@hororog.team")
            val student = saveMember(Role.STUDENT, "student@hororog.team")
            val otherStudent = saveMember(Role.STUDENT, "other@hororog.team", schoolName = "도시초등학교")
            saveSuggestion(student, "카레")
            saveSuggestion(otherStudent, "돈가스")

            mockMvc
                .get("/meals/suggestions") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(nutritionist)}")
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.length()") { value(1) }
                    jsonPath("$[0].title") { value("카레") }
                }
        }

        @Test
        fun `영양사가 급식 추천 상태를 변경하면 204를 반환하고 상태가 변경된다`() {
            val nutritionist = saveMember(Role.NUTRITIONIST, "nutritionist@hororog.team")
            val student = saveMember(Role.STUDENT, "student@hororog.team")
            val suggestion = saveSuggestion(student, "카레")

            mockMvc
                .patch("/meals/suggestions/${suggestion.id}") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(nutritionist)}")
                    contentType = MediaType.APPLICATION_JSON
                    content = """{ "mealSuggestionStatus": "APPROVED" }"""
                }.andExpect {
                    status { isNoContent() }
                }

            assertEquals(SuggestionStatus.APPROVED, mealSuggestionRepository.findById(suggestion.id).get().status)
        }

        @Test
        fun `학생이 급식 추천 목록을 조회하면 403을 반환한다`() {
            val student = saveMember(Role.STUDENT, "student@hororog.team")

            mockMvc
                .get("/meals/suggestions") {
                    header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken(student)}")
                }.andExpect {
                    status { isForbidden() }
                }
        }
    }
