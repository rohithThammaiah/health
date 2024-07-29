package dev.rohith.health.models

import kotlinx.datetime.LocalDate

data class HeatMapData(
    val date: LocalDate,
    val count: Int,
)