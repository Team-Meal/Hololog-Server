package team.nongchun.hororog.domain.procurement.dto

data class VendorResponse(
    val supplierId: String,
    val name: String,
    val address: String,
    val contact: String?,
    val distance: Double,
    val category: String?,
)
