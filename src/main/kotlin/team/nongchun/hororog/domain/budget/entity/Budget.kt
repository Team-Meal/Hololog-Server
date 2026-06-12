package team.nongchun.hororog.domain.budget.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
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
@Table(name = "budget")
class Budget(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,
    @Column(nullable = false, length = 100)
    var name: String,
    @Column(nullable = false)
    var totalBudget: Int,
    @Column(nullable = false)
    var usedBudget: Int,
    @Column(nullable = false)
    var startDate: LocalDateTime,
    @Column(nullable = false)
    var endDate: LocalDateTime,
) : BaseEntity()
