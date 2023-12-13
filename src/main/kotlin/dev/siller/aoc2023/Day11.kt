package dev.siller.aoc2023

import dev.siller.aoc2023.util.Point
import kotlin.math.abs

data object Day11 : AocDayTask<ULong, ULong>(
    day = 11,
    exampleInput =
        """
        |...#......
        |.......#..
        |#.........
        |..........
        |......#...
        |.#........
        |.........#
        |..........
        |.......#..
        |#...#.....
        """.trimMargin(),
    expectedExampleOutputPart1 = 374u,
    expectedExampleOutputPart2 = 82000210uL
) {
    private data class Universe(
        val width: ULong,
        val height: ULong,
        val galaxies: List<Point>
    ) {
        fun grow(steps: ULong = 1u): Universe {
            val emptyX = (0..width.toInt()).toSet() - galaxies.map(Point::x).distinct().toSet()
            val emptyY = (0..height.toInt()).toSet() - galaxies.map(Point::y).distinct().toSet()

            val newWidth = width + (emptyX.size.toULong() * steps)
            val newHeight = height + (emptyY.size.toULong() * steps)

            val newGalaxies =
                galaxies.map { galaxy ->
                    val newX = galaxy.x + (emptyX.count { x -> x < galaxy.x } * steps.toInt())
                    val newY = galaxy.y + (emptyY.count { y -> y < galaxy.y } * steps.toInt())

                    Point(newX, newY)
                }

            return Universe(newWidth, newHeight, newGalaxies)
        }
    }

    override fun runPart1(input: List<String>): ULong = parseUniverse(input).grow().galaxies.let(::calculateDistances)

    override fun runPart2(input: List<String>): ULong =
        parseUniverse(input).grow(steps = 1000000uL - 1uL).galaxies.let(::calculateDistances)

    private fun calculateDistances(galaxies: List<Point>) =
        galaxies.foldIndexed(0uL) { index, acc, firstGalaxy ->
            acc +
                galaxies.drop(index + 1).fold(0uL) { subAcc, secondGalaxy ->
                    val vector = secondGalaxy - firstGalaxy

                    subAcc + abs(vector.x).toULong() + abs(vector.y).toULong()
                }
        }

    private fun parseUniverse(input: List<String>): Universe {
        val height = input.size.toULong()
        val width = input[0].length.toULong()

        val galaxies =
            input
                .flatMapIndexed { y, line ->
                    line.mapIndexedNotNull { x, c ->
                        if (c == '#') {
                            Point(x, y)
                        } else {
                            null
                        }
                    }
                }

        return Universe(width, height, galaxies)
    }
}
