package dev.rohith.health

import java.time.LocalDate

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

        currentDate = currentDate.plusDays(stepDays)

        return next

    }

}