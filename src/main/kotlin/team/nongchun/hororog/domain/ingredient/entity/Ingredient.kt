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
import team.nongchun.hororog.domain.member.entity.Member
import team.nongchun.hororog.global.common.BaseEntity
import team.nongchun.hororog.global.common.QuantityUnit
import java.time.LocalDateTime

@Entity
@Table(name = "ingredient")
class Ingredient(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,
    @Column(nullable = false, length = 100)
    var name: String,
    @Column(nullable = false)
    var quantity: Int,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var unit: QuantityUnit,
    @Column(nullable = false)
    var expirationDate: LocalDateTime,
    @Column(nullable = false, length = 50)
    var category: String,
) : BaseEntity()
