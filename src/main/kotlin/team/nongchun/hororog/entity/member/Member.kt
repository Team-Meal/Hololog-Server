package team.nongchun.hororog.entity.member

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import team.nongchun.hororog.entity.common.BaseEntity

@Entity
@Table(name = "member")
class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false, unique = true, length = 255)
    var email: String,
    @Column(nullable = false, length = 255)
    var password: String,
    @Column(nullable = false, length = 50)
    var name: String,
    @Column(nullable = false, length = 255)
    var schoolName: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: Role,
) : BaseEntity()
