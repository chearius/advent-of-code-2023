package dev.siller.aoc2023

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt

data object Day06 : AocDayTask<Long, Long>(
    day = 6,
    exampleInput =
        """
        |Time:      7  15   30
        |Distance:  9  40  200
        """.trimMargin(),
    expectedExampleOutputPart1 = 288,
    expectedExampleOutputPart2 = 71503
) {
    private val WHITESPACES = "\\s+".toRegex()

    private data class Race(
        val duration: Long,
        val distance: Long
    )

    override fun runPart1(input: List<String>): Long =
        parseRaces(input, ignoreSpaces = false).run(::determineNumberOfWaysToWin)

    override fun runPart2(input: List<String>): Long =
        parseRaces(input, ignoreSpaces = true).run(::determineNumberOfWaysToWin)

    private fun parseRaces(
        input: List<String>,
        ignoreSpaces: Boolean = false
    ): List<Race> {
        if (input.size != 2) {
            error("Invalid number of lines in the input data.")
        }

        val replacement =
            if (ignoreSpaces) {
                ""
            } else {
                " "
            }

        val durations = input[0].substringAfter(':').trim().replace(WHITESPACES, replacement).split(' ')
        val distances = input[1].substringAfter(':').trim().replace(WHITESPACES, replacement).split(' ')

        if (durations.size != distances.size) {
            error("The number of race durations is not equal to the number of race distance records.")
        }

        return durations.mapIndexed { index, duration ->
            Race(duration.toLong(), distances[index].toLong())
        }
    }

    private fun determineNumberOfWaysToWin(races: List<Race>): Long =
        races
            .map { race ->
                // Equation to solve: x * (race.duration - x) - race.distance > 0
                //                   -x^2 + race.duration * x - race.distance > 0
                //                    x^2 - race.duration * x - race.distance > 0
                // Solutions: x_1 next integer, x_2 previous integer
                val intervalHalf = sqrt((race.duration / 2.0).pow(2) - race.distance)

                val intervalStart = race.duration / 2.0 - intervalHalf
                val intervalEnd = race.duration / 2.0 + intervalHalf

                val firstWinningTime =
                    if (intervalStart == ceil(intervalStart)) {
                        intervalStart.toLong() + 1L
                    } else {
                        ceil(intervalStart).toLong()
                    }

                val lastWinningTime =
                    if (intervalEnd == floor(intervalEnd)) {
                        intervalEnd.toLong() - 1L
                    } else {
                        floor(intervalEnd).toLong()
                    }

                lastWinningTime - firstWinningTime + 1
            }.fold(1L) { acc, i ->
                acc * i
            }
}
