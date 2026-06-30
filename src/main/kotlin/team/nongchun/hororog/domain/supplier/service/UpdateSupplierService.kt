package team.nongchun.hororog.domain.supplier.service

import team.nongchun.hororog.domain.supplier.dto.SupplierResponse
import team.nongchun.hororog.domain.supplier.dto.UpdateSupplierRequest

interface UpdateSupplierService {
    fun execute(
        supplierId: Long,
        request: UpdateSupplierRequest,
    ): SupplierResponse
}
