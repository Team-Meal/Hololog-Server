package team.nongchun.hororog.entity.meal

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
import team.nongchun.hororog.entity.common.BaseEntity
import team.nongchun.hororog.entity.member.Member

@Entity
@Table(name = "meal_suggestion")
class MealSuggestion(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,
    @Column(nullable = false, length = 100)
    var title: String,
    @Column(columnDefinition = "TEXT")
    var content: String? = null,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: SuggestionStatus,
) : BaseEntity()
