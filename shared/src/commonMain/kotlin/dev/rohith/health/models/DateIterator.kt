package dev.rohith.health.models

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus


operator fun LocalDate.rangeTo(other: LocalDate) = DateProgression(this, other)

class DateIterator(
    private val startDate: LocalDate,
    private val endDateInclusive: LocalDate,
    private val stepDays: Long
) : Iterator<LocalDate> {
    private var currentDate = startDate

    override fun hasNext() = currentDate <= endDateInclusive

    override fun next(): LocalDate {
        val next = currentDate
        currentDate = currentDate.plus(stepDays, DateTimeUnit.DAY)
        return next
    }

}