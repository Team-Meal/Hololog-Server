package team.nongchun.hororog.domain.supplier.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.supplier.dto.SupplierResponse
import team.nongchun.hororog.domain.supplier.repository.SupplierRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder

@Service
@Transactional(readOnly = true)
class GetSupplierListServiceImpl(
    private val supplierRepository: SupplierRepository,
    private val authenticationHolder: AuthenticationHolder,
) : GetSupplierListService {
    override fun execute(): List<SupplierResponse> {
        val schoolName = authenticationHolder.getCurrentUserSchoolName()

        return supplierRepository
            .findAllByMemberSchoolNameOrderByIdDesc(schoolName)
            .map { SupplierResponse.from(it) }
    }
}
