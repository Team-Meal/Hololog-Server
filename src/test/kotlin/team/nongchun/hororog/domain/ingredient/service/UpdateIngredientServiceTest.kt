package team.nongchun.hororog.domain.ingredient.service

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import team.nongchun.hororog.domain.ingredient.dto.UpdateIngredientRequest
import team.nongchun.hororog.domain.ingredient.entity.Ingredient
import team.nongchun.hororog.domain.ingredient.repository.IngredientRepository
import team.nongchun.hororog.domain.member.entity.Member
import team.nongchun.hororog.domain.member.entity.Role
import team.nongchun.hororog.domain.order.repository.OrderPlanItemRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder
import team.nongchun.hororog.global.common.QuantityUnit
import java.time.LocalDateTime

class UpdateIngredientServiceTest :
    BehaviorSpec({
        isolationMode = IsolationMode.InstancePerLeaf

        val ingredientRepository = mockk<IngredientRepository>()
        val orderPlanItemRepository = mockk<OrderPlanItemRepository>()
        val authenticationHolder = mockk<AuthenticationHolder>()
        val service = UpdateIngredientServiceImpl(ingredientRepository, orderPlanItemRepository, authenticationHolder)

        val member =
            Member(
                id = 1L,
                email = "nutritionist@hororog.team",
                password = "encoded",
                name = "김영양",
                schoolName = "농촌초등학교",
                role = Role.NUTRITIONIST,
            )

        fun ingredient() =
            Ingredient(
                id = 10L,
                member = member,
                name = "감자",
                quantity = 10,
                unit = QuantityUnit.KG,
                expirationDate = LocalDateTime.of(2026, 7, 31, 0, 0),
                category = "채소",
                origin = "강원도",
                supplier = "지역 농협",
            )

        Given("원산지와 공급처 수정 요청이 주어졌을 때") {
            val ingredient = ingredient()
            every { authenticationHolder.getCurrentUserSchoolName() } returns member.schoolName
            every { ingredientRepository.findByIdAndMemberSchoolName(10L, member.schoolName) } returns ingredient
            every { ingredientRepository.saveAndFlush(ingredient) } returns ingredient

            When("식자재를 수정하면") {
                val response =
                    service.execute(
                        10L,
                        UpdateIngredientRequest(
                            origin = "제주도",
                            supplier = "제주 농협",
                        ),
                    )

                Then("원산지와 공급처만 변경된다") {
                    ingredient.origin shouldBe "제주도"
                    ingredient.supplier shouldBe "제주 농협"
                    ingredient.name shouldBe "감자"
                    ingredient.quantity shouldBe 10
                    response.origin shouldBe "제주도"
                    response.supplier shouldBe "제주 농협"
                    verify(exactly = 0) { orderPlanItemRepository.findAllByIngredientId(any()) }
                }
            }
        }

        Given("원산지와 공급처가 null인 수정 요청이 주어졌을 때") {
            val ingredient = ingredient()
            every { authenticationHolder.getCurrentUserSchoolName() } returns member.schoolName
            every { ingredientRepository.findByIdAndMemberSchoolName(10L, member.schoolName) } returns ingredient
            every { ingredientRepository.saveAndFlush(ingredient) } returns ingredient

            When("식자재를 수정하면") {
                val response = service.execute(10L, UpdateIngredientRequest(name = "햇감자"))

                Then("기존 원산지와 공급처를 유지한다") {
                    ingredient.name shouldBe "햇감자"
                    ingredient.origin shouldBe "강원도"
                    ingredient.supplier shouldBe "지역 농협"
                    response.origin shouldBe "강원도"
                    response.supplier shouldBe "지역 농협"
                }
            }
        }
    })
