package team.nongchun.hororog.domain.member.entity

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
import team.nongchun.hororog.global.common.BaseEntity

@Entity
@Table(name = "nutritionist_signup_request")
class NutritionistSignupRequest(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,
    @Column(nullable = false)
    var licenseNumber: Long,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: SignupStatus,
) : BaseEntity()
