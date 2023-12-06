package dev.siller.aoc2023

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt

data object Day06 : AocDayTask<ULong, ULong>(
    day = 6,
    exampleInput =
        """
        |Time:      7  15   30
        |Distance:  9  40  200
        """.trimMargin(),
    expectedExampleOutputPart1 = 288u,
    expectedExampleOutputPart2 = 71503u
) {
    private val WHITESPACES = "\\s+".toRegex()

    private data class Race(
        val duration: ULong,
        val distance: ULong
    )

    override fun runPart1(input: List<String>): ULong =
        parseRaces(input, ignoreSpaces = false).run(::determineNumberOfWaysToWin)

    override fun runPart2(input: List<String>): ULong =
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
            Race(duration.toULong(), distances[index].toULong())
        }
    }

    private fun determineNumberOfWaysToWin(races: List<Race>): ULong =
        races
            .map { race ->
                // Equation to solve: x * (race.duration - x) - race.distance > 0
                //                   -x^2 + race.duration * x - race.distance > 0
                //                    x^2 - race.duration * x - race.distance > 0
                // Solutions: x_1 next integer, x_2 previous integer
                val intervalHalf = sqrt((race.duration.toDouble() / 2.0).pow(2) - race.distance.toDouble())

                val intervalStart = race.duration.toDouble() / 2.0 - intervalHalf
                val intervalEnd = race.duration.toDouble() / 2.0 + intervalHalf

                val firstWinningTime =
                    if (intervalStart == ceil(intervalStart)) {
                        intervalStart.toULong() + 1u
                    } else {
                        ceil(intervalStart).toULong()
                    }

                val lastWinningTime =
                    if (intervalEnd == floor(intervalEnd)) {
                        intervalEnd.toULong() - 1u
                    } else {
                        floor(intervalEnd).toULong()
                    }

                lastWinningTime - firstWinningTime + 1u
            }.fold(1u) { acc, i ->
                acc * i
            }
}
