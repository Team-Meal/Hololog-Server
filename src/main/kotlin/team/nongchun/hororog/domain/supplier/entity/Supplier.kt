package team.nongchun.hororog.domain.supplier.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import team.nongchun.hororog.global.common.BaseEntity

@Entity
@Table(name = "supplier")
class Supplier(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false, length = 100)
    var name: String,
    @Column(length = 30)
    var phoneNumber: String? = null,
    @Column(length = 255)
    var address: String? = null,
    @Column(length = 50)
    var itemCategory: String? = null,
    @Column(columnDefinition = "TEXT")
    var memo: String? = null,
) : BaseEntity()
