package team.nongchun.hororog.domain.supplier.dto

import team.nongchun.hororog.domain.supplier.entity.Supplier
import java.time.LocalDateTime

data class SupplierResponse(
    val id: Long,
    val name: String,
    val contactInfo: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(entity: Supplier) =
            SupplierResponse(
                id = entity.id,
                name = entity.name,
                contactInfo = entity.contactInfo,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
            )
    }
}
