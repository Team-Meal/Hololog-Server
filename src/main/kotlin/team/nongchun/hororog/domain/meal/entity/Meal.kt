package team.nongchun.hororog.domain.meal.entity

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
import team.nongchun.hororog.domain.member.entity.Member
import team.nongchun.hororog.global.common.BaseEntity
import java.time.LocalDateTime

@Entity
@Table(name = "meal")
class Meal(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,
    @Column(nullable = false, length = 100)
    var name: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var mealType: MealType,
    @Column(nullable = false)
    var mealDate: LocalDateTime,
    @Column
    var totalCalories: Int? = null,
    @Column(columnDefinition = "TEXT")
    var memo: String? = null,
    @Column(columnDefinition = "TEXT")
    var nutritionInfo: String? = null,
    @Column(columnDefinition = "TEXT")
    var originInfo: String? = null,
) : BaseEntity()
