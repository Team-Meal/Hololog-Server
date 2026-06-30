package team.nongchun.hororog.domain.ingredient.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import team.nongchun.hororog.domain.ingredient.cache.KamisPriceCache
import team.nongchun.hororog.domain.ingredient.cache.KamisPriceCacheRepository
import team.nongchun.hororog.domain.ingredient.client.KamisClient
import team.nongchun.hororog.domain.ingredient.dto.IngredientPriceResponse
import team.nongchun.hororog.global.config.KamisProperties
import tools.jackson.databind.ObjectMapper
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class SyncKamisPriceService(
    private val kamisClient: KamisClient,
    private val kamisPriceCacheRepository: KamisPriceCacheRepository,
    private val kamisProperties: KamisProperties,
    private val objectMapper: ObjectMapper,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        val TARGET_ITEMS = setOf("감자", "양파", "당근", "오이", "토마토", "수박", "배추", "무", "고구마", "파")
        private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd")
    }

    fun execute(): List<IngredientPriceResponse> {
        val (regDay, raw200) = resolveRegDay()
        val items = mutableListOf<KamisPriceCache>()

        // categoryCode 200은 resolveRegDay()에서 이미 조회함 — 재활용
        if (raw200 != null) {
            items += parseItems(raw200, regDay)
        }

        for (categoryCode in listOf("400", "100")) {
            try {
                val raw =
                    kamisClient.getDailyPriceByCategory(
                        action = "dailyPriceByCategoryList",
                        certKey = kamisProperties.apiKey,
                        certId = kamisProperties.certId,
                        returnType = "json",
                        categoryCode = categoryCode,
                        countryCode = "1101",
                        regDay = regDay,
                        convertKgYn = "Y",
                    )
                items += parseItems(raw, regDay)
            } catch (e: Exception) {
                logger.error("KAMIS 카테고리 $categoryCode 조회 실패: ${e.message}")
            }
        }

        if (items.size >= TARGET_ITEMS.size) {
            kamisPriceCacheRepository.saveAll(items)
        } else {
            logger.warn("KAMIS 조회 품목 부족 (${items.size}/${TARGET_ITEMS.size}), 캐시 저장 건너뜀")
        }
        return items.map { IngredientPriceResponse.from(it) }
    }

    private fun resolveRegDay(): Pair<String, String?> {
        val today = LocalDate.now().format(DATE_FORMATTER)
        val yesterday = LocalDate.now().minusDays(1).format(DATE_FORMATTER)
        return try {
            val raw =
                kamisClient.getDailyPriceByCategory(
                    action = "dailyPriceByCategoryList",
                    certKey = kamisProperties.apiKey,
                    certId = kamisProperties.certId,
                    returnType = "json",
                    categoryCode = "200",
                    countryCode = "1101",
                    regDay = today,
                    convertKgYn = "Y",
                )
            if (hasData(raw)) Pair(today, raw) else Pair(yesterday, null)
        } catch (e: Exception) {
            Pair(yesterday, null)
        }
    }

    private fun hasData(raw: String): Boolean {
        val root = objectMapper.readTree(raw)
        val dataNode = root.get("data") ?: return false
        if (dataNode.isArray) return false
        val errorCode = dataNode.get("error_code")?.asText()
        return errorCode == "000" && dataNode.get("item") != null
    }

    private fun parseItems(
        raw: String,
        regDay: String,
    ): List<KamisPriceCache> {
        val root = objectMapper.readTree(raw)
        val dataNode = root.get("data") ?: return emptyList()
        if (dataNode.isArray) return emptyList()

        val errorCode = dataNode.get("error_code")?.asText()
        if (errorCode != "000") return emptyList()

        val itemsNode = dataNode.get("item") ?: return emptyList()
        val result = mutableListOf<KamisPriceCache>()

        itemsNode
            .filter { TARGET_ITEMS.contains(it.get("productName")?.asText()) }
            .groupBy { it.get("productName")?.asText() }
            .forEach { (name, list) ->
                val item = list.find { it.get("gradeName")?.asText() == "상" } ?: list.first()
                val current =
                    item
                        .get("dpr1")
                        ?.asText()
                        ?.replace(",", "")
                        ?.toIntOrNull() ?: return@forEach
                val previous =
                    item
                        .get("dpr2")
                        ?.asText()
                        ?.replace(",", "")
                        ?.toIntOrNull()
                result +=
                    KamisPriceCache(
                        itemName = name ?: return@forEach,
                        pricePerKg = current,
                        unit = item.get("unit")?.asText() ?: "1kg",
                        baseDate = item.get("day1")?.asText() ?: regDay,
                        previousPricePerKg = previous,
                    )
            }
        return result
    }
}
