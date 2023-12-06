package dev.siller.aoc2023.util

import kotlin.math.max
import kotlin.math.min

data class Interval(
    val start: ULong,
    val end: ULong
) {
    fun intersect(other: Interval): Interval? =
        if (this.end >= other.start && this.start <= other.end) {
            Interval(max(start, other.start), min(end, other.end))
        } else {
            null
        }

    fun offset(offset: Long): Interval =
        Interval(
            (start.toLong() + offset).toULong(),
            (end.toLong() + offset).toULong()
        )
}
