package team.nongchun.hororog.domain.supplier.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import team.nongchun.hororog.domain.supplier.dto.CreateSupplierRequest
import team.nongchun.hororog.domain.supplier.dto.SupplierResponse
import team.nongchun.hororog.domain.supplier.dto.UpdateSupplierRequest
import team.nongchun.hororog.domain.supplier.service.CreateSupplierService
import team.nongchun.hororog.domain.supplier.service.DeleteSupplierService
import team.nongchun.hororog.domain.supplier.service.GetSupplierListService
import team.nongchun.hororog.domain.supplier.service.UpdateSupplierService

@Tag(name = "Supplier", description = "공급처 API")
@RestController
@RequestMapping("/suppliers")
class SupplierController(
    private val createSupplierService: CreateSupplierService,
    private val getSupplierListService: GetSupplierListService,
    private val updateSupplierService: UpdateSupplierService,
    private val deleteSupplierService: DeleteSupplierService,
) {
    @Operation(summary = "공급처 등록")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "등록 성공"),
        ApiResponse(responseCode = "400", description = "유효하지 않은 요청 값"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @Valid @RequestBody request: CreateSupplierRequest,
    ): SupplierResponse = createSupplierService.execute(request)

    @Operation(summary = "공급처 목록 조회")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "조회 성공"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
    )
    @GetMapping
    fun getList(): List<SupplierResponse> = getSupplierListService.execute()

    @Operation(summary = "공급처 수정")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "수정 성공"),
        ApiResponse(responseCode = "400", description = "유효하지 않은 요청 값"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
        ApiResponse(responseCode = "404", description = "공급처 없음"),
    )
    @PatchMapping("/{supplierId}")
    fun update(
        @PathVariable supplierId: Long,
        @Valid @RequestBody request: UpdateSupplierRequest,
    ): SupplierResponse = updateSupplierService.execute(supplierId, request)

    @Operation(summary = "공급처 삭제")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "삭제 성공"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
        ApiResponse(responseCode = "404", description = "공급처 없음"),
    )
    @DeleteMapping("/{supplierId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable supplierId: Long,
    ) {
        deleteSupplierService.execute(supplierId)
    }
}
