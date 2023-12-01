package dev.siller.aoc2023

import java.io.File

sealed class AocDayTask<RESULT_TASK_1, RESULT_TASK_2>(
    val day: Int,
    private val exampleInputsPart1: List<String> = emptyList(),
    private val exampleInputsPart2: List<String> = emptyList(),
    private val expectedExampleOutputsPart1: List<RESULT_TASK_1> = emptyList(),
    private val expectedExampleOutputsPart2: List<RESULT_TASK_2> = emptyList()
) {
    constructor(
        day: Int,
        exampleInputPart1: String = "",
        exampleInputPart2: String = "",
        expectedExampleOutputPart1: RESULT_TASK_1? = null,
        expectedExampleOutputPart2: RESULT_TASK_2? = null
    ) : this(
        day,
        listOf(exampleInputPart1),
        listOf(exampleInputPart2),
        listOfNotNull(expectedExampleOutputPart1),
        listOfNotNull(expectedExampleOutputPart2)
    )

    constructor(
        day: Int,
        exampleInput: String = "",
        expectedExampleOutputPart1: RESULT_TASK_1? = null,
        expectedExampleOutputPart2: RESULT_TASK_2? = null
    ) : this(
        day,
        listOf(exampleInput),
        listOf(exampleInput),
        listOfNotNull(expectedExampleOutputPart1),
        listOfNotNull(expectedExampleOutputPart2)
    )

    companion object {
        @JvmStatic
        val UNKNOWN_RESULT = null

        @JvmStatic
        private val AOC_YEAR = 2023
    }

    private val inputFile = "input/${AOC_YEAR}_day_${day.toString().padStart(2, '0')}.txt"

    abstract fun runPart1(input: List<String>): RESULT_TASK_1

    abstract fun runPart2(input: List<String>): RESULT_TASK_2

    @Suppress("detekt:style:SwallowedException")
    fun run(): Pair<RESULT_TASK_1?, RESULT_TASK_2?> {
        log.info("=============================")
        log.info("Day {} of Advent of Code $AOC_YEAR", day.toString().padStart(2, '0'))
        log.info("=============================")

        val part1result =
            try {
                runPart(1, inputFile, exampleInputsPart1, expectedExampleOutputsPart1, ::runPart1)
            } catch (_: NotImplementedError) {
                log.warn("No solution for part 1, yet!")

                null
            }

        val part2result =
            try {
                runPart(2, inputFile, exampleInputsPart2, expectedExampleOutputsPart2, ::runPart2)
            } catch (_: NotImplementedError) {
                log.warn("No solution for part 2, yet!\n")

                null
            }

        if (part1result != null && part2result != null) {
            log.info("[DONE] :-)")
        }

        return Pair(part1result, part2result)
    }

    private fun <R> runPart(
        partNumber: Int,
        input: String,
        exampleInput: List<String>,
        expectedOutput: List<R>,
        part: (List<String>) -> R
    ): R? {
        val states =
            exampleInput.mapIndexed { index, i ->
                log.info("Running part {} for example {} of {}:", partNumber, index + 1, exampleInput.size)

                val lines = i.lines()

                val result = part(lines)

                val status =
                    if (result == expectedOutput.getOrNull(index)) {
                        ResultStatus.SUCCESS
                    } else {
                        ResultStatus.ERROR
                    }

                log.info("\tExpected result: {}", expectedOutput.getOrNull(index) ?: "<not specified>")
                log.info("\tActual result:   {} ({})", result, status)

                status
            }

        println()

        return if (states.any { rs -> rs == ResultStatus.ERROR }) {
            log.error("There were some incorrect results running the examples!")

            null
        } else if (!File(input).run { exists() && canRead() }) {
            log.error(
                "Input file {} does not exist or is not readable! Did you forget to download your puzzle input?",
                input
            )

            null
        } else {
            log.info("Running Part {} with puzzle input ...", partNumber)
            val partResult = part(File(input).readLines())

            log.info("Part {} result is: {}", partNumber, partResult)

            partResult
        }
    }
}

private enum class ResultStatus {
    SUCCESS,
    ERROR
}
