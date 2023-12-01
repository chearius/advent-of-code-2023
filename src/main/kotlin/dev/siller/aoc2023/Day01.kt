package dev.siller.aoc2023

data object Day01 : AocDayTask<Int, Int>(
    day = 1,
    exampleInput =
        """
        1abc2
        pqr3stu8vwx
        a1b2c3d4e5f
        treb7uchet
        """.trimIndent(),
    expectedExampleOutputPart1 = 142,
    expectedExampleOutputPart2 = 281
) {
    override fun runPart1(input: List<String>): Int =
        input.map { line ->
            val digits =
                line.filter { c ->
                    c in '0'..'9'
                }

            "${digits.first()}${digits.last()}".toInt()
        }.sum()

    override fun runPart2(input: List<String>): Int =
        input.map { line ->
            replaceWordsWithDigits(line)
        }
            .map { line ->
                val digits =
                    line.filter { c ->
                        c in '0'..'9'
                    }

                "${digits.first()}${digits.last()}".toInt()
            }.sum()

    private fun replaceWordsWithDigits(line: String): String =
        mapOf(
            "one" to "1",
            "two" to "2",
            "three" to "3",
            "four" to "4",
            "five" to "5",
            "six" to "6",
            "seven" to "7",
            "eight" to "8",
            "nine" to "9",
            "zero" to "0"
        ).fold(line) { acc, (word, digit) ->
            acc.replaceAll(word, digit)
        }
}
