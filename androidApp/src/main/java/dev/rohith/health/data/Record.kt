package dev.rohith.health.data

data class Record(
    val name: String,
    val value: Double,
    val type: HealthStat,
    val prettyValue: String = name,
    val unit: String = "",
)