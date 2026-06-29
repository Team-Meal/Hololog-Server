package team.nongchun.hororog.domain.ingredient.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "kamis", url = "\${kamis.url}")
interface KamisClient {
    @GetMapping("/service/price/xml.do")
    fun getDailyPriceByCategory(
        @RequestParam("action") action: String,
        @RequestParam("p_cert_key") certKey: String,
        @RequestParam("p_cert_id") certId: String,
        @RequestParam("p_returntype") returnType: String,
        @RequestParam("p_item_category_code") categoryCode: String,
        @RequestParam("p_country_code") countryCode: String,
        @RequestParam("p_regday") regDay: String,
        @RequestParam("p_convert_kg_yn") convertKgYn: String,
    ): String
}
