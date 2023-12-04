package dev.siller.aoc2023

data object Day01 : AocDayTask<Int, Int>(
    day = 1,
    exampleInputPart1 =
        """
        |1abc2
        |pqr3stu8vwx
        |a1b2c3d4e5f
        |treb7uchet
        """.trimMargin(),
    exampleInputPart2 =
        """
        |two1nine
        |eightwothree
        |abcone2threexyz
        |xtwone3four
        |4nineeightseven2
        |zoneight234
        |7pqrstsixteen
        """.trimMargin(),
    expectedExampleOutputPart1 = 142,
    expectedExampleOutputPart2 = 281
) {
    private val WORDS_TO_DIGITS =
        mapOf(
            "one" to '1',
            "two" to '2',
            "three" to '3',
            "four" to '4',
            "five" to '5',
            "six" to '6',
            "seven" to '7',
            "eight" to '8',
            "nine" to '9',
            "zero" to '0'
        )

    override fun runPart1(input: List<String>): Int = input.sumOf(::getCalibrationValue)

    override fun runPart2(input: List<String>): Int = input.map(::replaceWordsWithDigits).sumOf(::getCalibrationValue)

    private fun getCalibrationValue(line: String): Int {
        val digits = line.filter(Char::isDigit)

        return "${digits.first()}${digits.last()}".toInt()
    }

    private fun replaceWordsWithDigits(line: String): String =
        line.mapIndexed { index, c ->
            WORDS_TO_DIGITS.filter { (word, _) -> line.startsWith(word, index) }.values.firstOrNull() ?: c
        }.joinToString()
}
