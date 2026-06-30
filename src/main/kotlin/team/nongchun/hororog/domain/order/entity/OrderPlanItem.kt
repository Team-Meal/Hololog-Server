package team.nongchun.hororog.domain.order.entity

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
import team.nongchun.hororog.domain.ingredient.entity.Ingredient
import team.nongchun.hororog.global.common.QuantityUnit

@Entity
@Table(name = "order_plan_item")
class OrderPlanItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_plan_id", nullable = false)
    val orderPlan: OrderPlan,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    val ingredient: Ingredient,
    @Column(nullable = false, length = 100)
    var menuName: String,
    @Column(nullable = false)
    var perPersonUsage: Double,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var unit: QuantityUnit,
    @Column(nullable = false)
    var requiredQuantity: Double,
    @Column(nullable = false)
    var currentStock: Double,
    @Column(nullable = false)
    var shortageQuantity: Double,
    @Column(nullable = false)
    var orderQuantity: Double,
    @Column(length = 100)
    var supplierName: String? = null,
    var unitPrice: Double? = null,
    var estimatedCost: Double? = null,
    @Column(columnDefinition = "TEXT")
    var basis: String? = null,
)
