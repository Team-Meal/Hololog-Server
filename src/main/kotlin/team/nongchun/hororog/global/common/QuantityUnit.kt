package team.nongchun.hororog.global.common

import team.nongchun.hororog.global.exception.InvalidQuantityUnitException

enum class QuantityUnit {
    KG,
    G,
    L,
    ML,
    EA,
    BOX,
    ;

    fun convertTo(
        target: QuantityUnit,
        value: Double,
    ): Double {
        if (this == target) return value
        return when {
            this == KG && target == G -> value * 1000
            this == G && target == KG -> value / 1000
            this == L && target == ML -> value * 1000
            this == ML && target == L -> value / 1000
            else -> throw InvalidQuantityUnitException()
        }
    }

    companion object {
        fun fromOrNull(value: String): QuantityUnit? = entries.find { it.name == value.uppercase() }
    }
}
