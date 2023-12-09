package dev.siller.aoc2023

data object Day09 : AocDayTask<Int, Int>(
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
            calculateSeriesElement(
                series = line.split(' ').map(String::toInt),
                elementAccessor = { it.last() },
                accumulateOperation = { acc, current -> acc + current }
            )
        }

    override fun runPart2(input: List<String>): Int =
        input.sumOf { line ->
            calculateSeriesElement(
                series = line.split(' ').map(String::toInt),
                elementAccessor = { it.first() },
                accumulateOperation = { acc, current -> current - acc }
            )
        }

    private fun calculateSeriesElement(
        series: List<Int>,
        elementAccessor: (List<Int>) -> Int,
        accumulateOperation: (Int, Int) -> Int
    ): Int {
        var diffs = series.zipWithNext { a, b -> b - a }
        val elements = mutableListOf(elementAccessor(series), elementAccessor(diffs))

        while (diffs.distinct().size > 1) {
            diffs = diffs.zipWithNext { a, b -> b - a }
            elements += elementAccessor(diffs)
        }

        return elements.reversed().fold(0, accumulateOperation)
    }
}
