package team.nongchun.hororog.domain.supplier.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.nongchun.hororog.domain.supplier.entity.Supplier

interface SupplierRepository : JpaRepository<Supplier, Long> {
    fun findAllByMemberSchoolNameOrderByIdDesc(schoolName: String): List<Supplier>

    fun findByIdAndMemberSchoolName(
        id: Long,
        schoolName: String,
    ): Supplier?
}
