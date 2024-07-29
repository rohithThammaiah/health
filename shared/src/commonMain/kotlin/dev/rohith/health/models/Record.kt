package dev.rohith.health.models

data class Record(
    val name: String,
    val value: Double,
    val type: HealthStat,
    val prettyValue: String = name,
    val unit: String = "",
)