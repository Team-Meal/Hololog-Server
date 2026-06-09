package team.nongchun.hororog.domain.ingredient.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import team.nongchun.hororog.global.common.QuantityUnit

@Entity
@Table(name = "ingredient_plan_item")
class IngredientPlanItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_plan_id", nullable = false)
    val ingredientPlan: IngredientPlan,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    val ingredient: Ingredient,
    @Column(nullable = false)
    var requiredQuantity: Int,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var unit: QuantityUnit,
    @Column(columnDefinition = "TEXT")
    var memo: String? = null,
)
