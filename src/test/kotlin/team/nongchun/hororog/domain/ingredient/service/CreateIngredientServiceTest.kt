package team.nongchun.hororog.domain.ingredient.service

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import team.nongchun.hororog.domain.ingredient.dto.CreateIngredientRequest
import team.nongchun.hororog.domain.ingredient.entity.Ingredient
import team.nongchun.hororog.domain.ingredient.repository.IngredientRepository
import team.nongchun.hororog.domain.member.entity.Member
import team.nongchun.hororog.domain.member.entity.Role
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder
import team.nongchun.hororog.global.common.QuantityUnit
import java.time.LocalDateTime
import java.util.Optional

class CreateIngredientServiceTest :
    BehaviorSpec({
        isolationMode = IsolationMode.InstancePerLeaf

        val ingredientRepository = mockk<IngredientRepository>()
        val memberRepository = mockk<MemberRepository>()
        val authenticationHolder = mockk<AuthenticationHolder>()
        val service = CreateIngredientServiceImpl(ingredientRepository, memberRepository, authenticationHolder)

        val member =
            Member(
                id = 1L,
                email = "nutritionist@hororog.team",
                password = "encoded",
                name = "김영양",
                schoolName = "농촌초등학교",
                role = Role.NUTRITIONIST,
            )

        Given("원산지와 공급처가 포함된 식자재 생성 요청이 주어졌을 때") {
            val saved = slot<Ingredient>()
            every { authenticationHolder.getCurrentUserId() } returns member.id
            every { memberRepository.findById(member.id) } returns Optional.of(member)
            every { ingredientRepository.save(capture(saved)) } answers { saved.captured }

            When("식자재를 생성하면") {
                service.execute(
                    CreateIngredientRequest(
                        name = "감자",
                        quantity = 10,
                        unit = "KG",
                        expirationDate = LocalDateTime.of(2026, 7, 31, 0, 0),
                        category = "채소",
                        origin = "강원도",
                        supplier = "지역 농협",
                    ),
                )

                Then("원산지와 공급처가 함께 저장된다") {
                    verify { ingredientRepository.save(any()) }
                    saved.captured.unit shouldBe QuantityUnit.KG
                    saved.captured.origin shouldBe "강원도"
                    saved.captured.supplier shouldBe "지역 농협"
                }
            }
        }
    })
