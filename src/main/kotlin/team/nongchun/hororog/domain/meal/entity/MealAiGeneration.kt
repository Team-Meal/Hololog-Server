package team.nongchun.hororog.domain.meal.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import team.nongchun.hororog.global.common.BaseEntity

@Entity
@Table(name = "meal_ai_generation")
class MealAiGeneration(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false)
    val schoolName: String,
    @Column(nullable = false, length = 7)
    val month: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: MealAiGenerationStatus = MealAiGenerationStatus.PENDING,
    @Column(columnDefinition = "TEXT")
    var errorMessage: String? = null,
) : BaseEntity()
