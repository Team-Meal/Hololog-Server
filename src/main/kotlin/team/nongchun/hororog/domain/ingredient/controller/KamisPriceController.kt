package team.nongchun.hororog.domain.ingredient.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import team.nongchun.hororog.domain.ingredient.dto.IngredientPriceResponse
import team.nongchun.hororog.domain.ingredient.service.GetKamisPriceAlertsService
import team.nongchun.hororog.domain.ingredient.service.GetKamisPriceService
import team.nongchun.hororog.domain.ingredient.service.SyncKamisPriceService

@Tag(name = "KamisPrice", description = "KAMIS 농산물 가격 API")
@RestController
@RequestMapping("/ingredients/prices")
class KamisPriceController(
    private val getKamisPriceService: GetKamisPriceService,
    private val syncKamisPriceService: SyncKamisPriceService,
    private val getKamisPriceAlertsService: GetKamisPriceAlertsService,
) {
    @Operation(summary = "KAMIS 단가 목록 조회", description = "캐시된 KAMIS 실시간 단가를 반환합니다. 캐시가 없으면 API를 직접 호출합니다.")
    @ApiResponses(ApiResponse(responseCode = "200", description = "조회 성공"))
    @GetMapping("/kamis")
    fun getPrices(): List<IngredientPriceResponse> = getKamisPriceService.execute()

    @Operation(summary = "KAMIS 단가 수동 동기화", description = "KAMIS API를 즉시 호출해 단가를 갱신합니다.")
    @ApiResponses(ApiResponse(responseCode = "200", description = "동기화 성공"))
    @PostMapping("/sync")
    fun syncPrices(): List<IngredientPriceResponse> = syncKamisPriceService.execute()

    @Operation(summary = "가격 급등 품목 조회", description = "전일 대비 18% 이상 상승한 품목과 대체 추천 품목을 반환합니다.")
    @ApiResponses(ApiResponse(responseCode = "200", description = "조회 성공"))
    @GetMapping("/alerts")
    fun getPriceAlerts(): List<IngredientPriceResponse> = getKamisPriceAlertsService.execute()
}
