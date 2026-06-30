package team.nongchun.hororog.domain.supplier.dto

import jakarta.validation.constraints.Size

data class UpdateSupplierRequest(
    @field:Size(max = 100)
    val name: String? = null,
    val contactInfo: String? = null,
)
