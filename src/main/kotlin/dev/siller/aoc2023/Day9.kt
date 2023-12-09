package dev.siller.aoc2023

data object Day9 : AocDayTask<Int, Int>(
    day = 9,
    exampleInput =
        """
        |0 3 6 9 12 15
        |1 3 6 10 15 21
        |10 13 16 21 30 45
        """.trimMargin(),
    expectedExampleOutputPart1 = 114,
    expectedExampleOutputPart2 = 2
) {
    override fun runPart1(input: List<String>): Int =
        input.sumOf { line ->
            val series = line.split(' ').map(String::toInt)

            var diffs = series.zipWithNext { a, b -> b - a }
            val lastElements = mutableListOf(series.last(), diffs.last())

            while (diffs.distinct().size > 1) {
                diffs = diffs.zipWithNext { a, b -> b - a }
                lastElements += diffs.last()
            }

            lastElements.sum()
        }

    override fun runPart2(input: List<String>): Int =
        input.sumOf { line ->
            val series = line.split(' ').map(String::toInt)

            var diffs = series.zipWithNext { a, b -> b - a }
            val firstElements = mutableListOf(series.first(), diffs.first())

            while (diffs.distinct().size > 1) {
                diffs = diffs.zipWithNext { a, b -> b - a }
                firstElements += diffs.first()
            }

            firstElements.reversed().fold(0) { acc, current ->
                current - acc
            }.toInt()
        }
}
