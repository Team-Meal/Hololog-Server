package team.nongchun.hororog.domain.supplier.service

import team.nongchun.hororog.domain.supplier.dto.SupplierResponse

interface GetSupplierListService {
    fun execute(): List<SupplierResponse>
}
