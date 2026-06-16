package team.nongchun.hororog.global.common

enum class QuantityUnit {
    KG,
    G,
    L,
    ML,
    EA,
    BOX,
    ;

    companion object {
        fun fromOrNull(value: String): QuantityUnit? = entries.find { it.name == value.uppercase() }
    }
}
