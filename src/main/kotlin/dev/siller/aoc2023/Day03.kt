package dev.siller.aoc2023

import dev.siller.aoc2023.util.Point
import dev.siller.aoc2023.util.Vector

data object Day03 : AocDayTask<Int, Int>(
    day = 3,
    exampleInput =
        """
        |467..114..
        |...*......
        |..35..633.
        |......#...
        |617*......
        |.....+.58.
        |..592.....
        |......755.
        |...$.*....
        |.664.598..
        """.trimMargin(),
    expectedExampleOutputPart1 = 4361,
    expectedExampleOutputPart2 = 467835
) {
    data class Schematic(
        val partNumbers: List<PartNumber>,
        val symbols: List<Symbol>,
        val width: Int,
        val height: Int
    )

    data class PartNumber(
        val number: Int,
        val startPosition: Point
    ) {
        val points: List<Point> =
            (0..<number.toString().length).map { i -> startPosition + Vector(i, 0) }
    }

    data class Symbol(
        val symbol: Char,
        val position: Point
    )

    override fun runPart1(input: List<String>): Int =
        parseSchematic(input).let { schematic ->
            schematic.symbols.flatMap { symbol ->
                val adjacentPoints =
                    symbol.position.getAdjacentPoints(
                        minX = 0,
                        maxX = schematic.width - 1,
                        minY = 0,
                        maxY = schematic.height - 1
                    )

                schematic.partNumbers.filter { partNumber ->
                    partNumber.points.any { point ->
                        point in adjacentPoints
                    }
                }
            }.sumOf(PartNumber::number)
        }

    override fun runPart2(input: List<String>): Int =
        parseSchematic(input).let { schematic ->
            schematic.symbols.sumOf { symbol ->
                val adjacentPoints =
                    symbol.position.getAdjacentPoints(
                        minX = 0,
                        maxX = schematic.width - 1,
                        minY = 0,
                        maxY = schematic.height - 1
                    )

                val adjacentPartNumbers =
                    schematic.partNumbers.filter { partNumber ->
                        partNumber.points.any { point ->
                            point in adjacentPoints
                        }
                    }

                if (adjacentPartNumbers.size == 2) {
                    adjacentPartNumbers[0].number * adjacentPartNumbers[1].number
                } else {
                    0
                }
            }
        }

    private fun parseSchematic(input: List<String>): Schematic {
        val partNumbers = mutableListOf<PartNumber>()
        val symbols = mutableListOf<Symbol>()

        input.forEachIndexed { y, line ->
            var partNumber = ""
            var startX = -1

            line.forEachIndexed { x, character ->
                when (character) {
                    in '0'..'9' -> {
                        if (partNumber.isBlank()) {
                            startX = x
                        }
                        partNumber += character
                    }

                    else -> {
                        if (partNumber.isNotBlank()) {
                            partNumbers += PartNumber(partNumber.toInt(), Point(startX, y))
                            partNumber = ""
                        }

                        if (character != '.') {
                            symbols += Symbol(character, Point(x, y))
                        }
                    }
                }
            }

            if (partNumber.isNotBlank()) {
                partNumbers += PartNumber(partNumber.toInt(), Point(startX, y))
            }
        }

        return Schematic(partNumbers, symbols, input[0].length, input.size)
    }
}
