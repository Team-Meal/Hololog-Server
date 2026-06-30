package team.nongchun.hororog.domain.supplier.service

import team.nongchun.hororog.domain.supplier.dto.CreateSupplierRequest

interface CreateSupplierService {
    fun execute(request: CreateSupplierRequest)
}
