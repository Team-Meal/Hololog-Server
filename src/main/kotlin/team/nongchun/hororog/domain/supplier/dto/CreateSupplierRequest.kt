package team.nongchun.hororog.domain.supplier.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateSupplierRequest(
    @field:NotBlank
    @field:Size(max = 100)
    val name: String,
    @field:Size(max = 500)
    val contactInfo: String? = null,
)
