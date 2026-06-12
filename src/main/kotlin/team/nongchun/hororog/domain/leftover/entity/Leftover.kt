package team.nongchun.hororog.domain.leftover.entity

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
import team.nongchun.hororog.domain.meal.entity.MealPlan
import team.nongchun.hororog.global.common.BaseEntity
import team.nongchun.hororog.global.common.QuantityUnit

@Entity
@Table(name = "leftover")
class Leftover(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_plan_id", nullable = false)
    val mealPlan: MealPlan,
    @Column(nullable = false)
    var amount: Int,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var unit: QuantityUnit,
    @Column(columnDefinition = "TEXT")
    var memo: String? = null,
) : BaseEntity()
