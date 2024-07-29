package dev.rohith.health.models

import kotlinx.datetime.Instant
import kotlin.time.Duration

data class ActivityRecord(
    val id: Int,
    val type: String,
    val healthRecord: List<Record>,
    val duration: Duration,
    val timeStamp: Instant
)