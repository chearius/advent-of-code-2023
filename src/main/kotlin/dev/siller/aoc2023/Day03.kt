package dev.siller.aoc2023

import dev.siller.aoc2023.util.Point
import dev.siller.aoc2023.util.Vector

data object Day03 : AocDayTask<UInt, UInt>(
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
    expectedExampleOutputPart1 = 4361u,
    expectedExampleOutputPart2 = 467835u
) {
    private data class Schematic(
        val partNumbers: List<PartNumber>,
        val symbols: List<Symbol>,
        val width: UInt,
        val height: UInt
    )

    private data class PartNumber(
        val number: UInt,
        val startPosition: Point
    ) {
        val points: List<Point> =
            (0..<number.toString().length).map { i -> startPosition + Vector(i, 0) }
    }

    private data class Symbol(
        val symbol: Char,
        val position: Point
    )

    override fun runPart1(input: List<String>): UInt =
        parseSchematic(input).let { schematic ->
            schematic.symbols.flatMap { symbol ->
                val adjacentPoints =
                    symbol.position.getAdjacentPoints(
                        minX = 0,
                        maxX = schematic.width.toInt() - 1,
                        minY = 0,
                        maxY = schematic.height.toInt() - 1
                    )

                schematic.partNumbers.filter { partNumber ->
                    partNumber.points.any { point ->
                        point in adjacentPoints
                    }
                }
            }.sumOf(PartNumber::number)
        }

    override fun runPart2(input: List<String>): UInt =
        parseSchematic(input).let { schematic ->
            schematic.symbols.sumOf { symbol ->
                val adjacentPoints =
                    symbol.position.getAdjacentPoints(
                        minX = 0,
                        maxX = schematic.width.toInt() - 1,
                        minY = 0,
                        maxY = schematic.height.toInt() - 1
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
                    0u
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
                            partNumbers += PartNumber(partNumber.toUInt(), Point(startX, y))
                            partNumber = ""
                        }

                        if (character != '.') {
                            symbols += Symbol(character, Point(x, y))
                        }
                    }
                }
            }

            if (partNumber.isNotBlank()) {
                partNumbers += PartNumber(partNumber.toUInt(), Point(startX, y))
            }
        }

        return Schematic(partNumbers, symbols, input[0].length.toUInt(), input.size.toUInt())
    }
}
