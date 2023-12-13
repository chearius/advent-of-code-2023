package dev.siller.aoc2023

import kotlin.math.min

data object Day13 : AocDayTask<UInt, UInt>(
    day = 13,
    exampleInput =
        """
        |#.##..##.
        |..#.##.#.
        |##......#
        |##......#
        |..#.##.#.
        |..##..##.
        |#.#.##.#.
        |
        |#...##..#
        |#....#..#
        |..##..###
        |#####.##.
        |#####.##.
        |..##..###
        |#....#..#
        """.trimMargin(),
    expectedExampleOutputPart1 = 405u,
    expectedExampleOutputPart2 = 400u
) {
    private class AshRockPattern(lines: List<String>) {
        val rows =
            lines
                .map { line ->
                    line.replace('#', '0').replace('.', '1').trimStart('0').toULong(2)
                }

        val columns =
            lines
                .first()
                .mapIndexed { x, _ ->
                    lines.joinToString(separator = "") { line -> "${line[x]}" }
                }
                .map { line ->
                    line.replace('#', '0').replace('.', '1').trimStart('0').toULong(2)
                }
    }

    override fun runPart1(input: List<String>): UInt =
        parsePatterns(input).sumOf { pattern ->
            (getMirrorLocation(pattern.rows) ?: 0u) * 100u +
                (getMirrorLocation(pattern.columns) ?: 0u)
        }

    override fun runPart2(input: List<String>): UInt =
        parsePatterns(input).sumOf { pattern ->
            (getMirrorLocation(pattern.rows, 1u) ?: 0u) * 100u +
                (getMirrorLocation(pattern.columns, 1u) ?: 0u)
        }

    private fun getMirrorLocation(
        list: List<ULong>,
        smudgeCount: UInt = 0u
    ): UInt? {
        var index: UInt? = null

        for (i in 0..<(list.size - 1)) {
            var count = 0u

            for (j in 0..<min(i + 1, list.size - i - 1)) {
                count += getNumberOfDifferentBits(list[i - j], list[i + j + 1])

                if (count > smudgeCount) {
                    break
                }
            }

            if (count == smudgeCount) {
                index = (i + 1).toUInt()
                break
            }
        }

        return index
    }

    private fun getNumberOfDifferentBits(
        a: ULong,
        b: ULong
    ): UInt {
        var count = 0u
        var n = a xor b

        while (n > 0uL) {
            count += (n and 1uL).toUInt()
            n = n shr 1
        }

        return count
    }

    private fun parsePatterns(input: List<String>): MutableList<AshRockPattern> {
        var inputToBeProcessed = input
        val patterns = mutableListOf<AshRockPattern>()

        while (inputToBeProcessed.isNotEmpty()) {
            patterns += AshRockPattern(inputToBeProcessed.takeWhile { it.isNotBlank() })
            inputToBeProcessed = inputToBeProcessed.dropWhile { it.isNotBlank() }.drop(1)
        }
        return patterns
    }
}
