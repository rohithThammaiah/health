package dev.rohith.health.data

import java.time.LocalDate

data class HeatMapData(
    val date: LocalDate,
    val count: Int,
)