package team.nongchun.hororog.domain.supplier.service

import team.nongchun.hororog.domain.supplier.dto.CreateSupplierRequest
import team.nongchun.hororog.domain.supplier.dto.SupplierResponse

interface CreateSupplierService {
    fun execute(request: CreateSupplierRequest): SupplierResponse
}
