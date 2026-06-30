package team.nongchun.hororog.domain.supplier.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.domain.supplier.dto.CreateSupplierRequest
import team.nongchun.hororog.domain.supplier.dto.SupplierResponse
import team.nongchun.hororog.domain.supplier.entity.Supplier
import team.nongchun.hororog.domain.supplier.repository.SupplierRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder

@Service
@Transactional
class CreateSupplierServiceImpl(
    private val supplierRepository: SupplierRepository,
    private val memberRepository: MemberRepository,
    private val authenticationHolder: AuthenticationHolder,
) : CreateSupplierService {
    override fun execute(request: CreateSupplierRequest): SupplierResponse {
        val member = memberRepository.getReferenceById(authenticationHolder.getCurrentUserId())

        val supplier =
            supplierRepository.save(
                Supplier(
                    member = member,
                    name = request.name,
                    contactInfo = request.contactInfo,
                ),
            )

        return SupplierResponse.from(supplier)
    }
}
