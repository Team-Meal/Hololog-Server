package team.nongchun.hororog.domain.procurement.service

import team.nongchun.hororog.domain.procurement.dto.VendorResponse

interface GetVendorListService {
    fun execute(
        latitude: Double,
        longitude: Double,
        radius: Int?,
    ): List<VendorResponse>
}
