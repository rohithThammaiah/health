package dev.rohith.health

import java.time.Duration
import java.time.Instant

data class ActivityRecord(
    val id: Int,
    val type: String,
    val healthRecord: List<Record>,
    val duration: Duration,
    val timeStamp: Instant
)