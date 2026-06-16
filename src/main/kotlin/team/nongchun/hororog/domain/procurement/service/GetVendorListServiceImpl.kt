package team.nongchun.hororog.domain.procurement.service

import org.springframework.stereotype.Service
import team.nongchun.hororog.domain.procurement.client.AiServerClient
import team.nongchun.hororog.domain.procurement.dto.VendorResponse

@Service
class GetVendorListServiceImpl(
    private val aiServerClient: AiServerClient,
) : GetVendorListService {
    override fun execute(
        latitude: Double,
        longitude: Double,
        radius: Int?,
    ): List<VendorResponse> = aiServerClient.getVendors(latitude = latitude, longitude = longitude, radius = radius)
}
