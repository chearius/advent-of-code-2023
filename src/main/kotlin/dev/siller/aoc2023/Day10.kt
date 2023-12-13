package dev.siller.aoc2023

import dev.siller.aoc2023.util.Point
import dev.siller.aoc2023.util.Vector
import kotlin.math.ceil
import kotlin.math.roundToInt

data object Day10 : AocDayTask<UInt, UInt>(
    day = 10,
    exampleInputsPart1 =
        listOf(
            """
            |.....
            |.S-7.
            |.|.|.
            |.L-J.
            |.....
            """.trimMargin(),
            """
            |7-F7-
            |.FJ|7
            |SJLL7
            ||F--J
            |LJ.LJ
            """.trimMargin()
        ),
    exampleInputsPart2 =
        listOf(
            """
            |...........
            |.S-------7.
            |.|F-----7|.
            |.||.....||.
            |.||.....||.
            |.|L-7.F-J|.
            |.|..|.|..|.
            |.L--J.L--J.
            |...........
            """.trimMargin(),
            """
            |.F----7F7F7F7F-7....
            |.|F--7||||||||FJ....
            |.||.FJ||||||||L7....
            |FJL7L7LJLJ||LJ.L-7..
            |L--J.L7...LJS7F-7L7.
            |....F-J..F7FJ|L7L7L7
            |....L7.F7||L7|.L7L7|
            |.....|FJLJ|FJ|F7|.LJ
            |....FJL-7.||.||||...
            |....L---J.LJ.LJLJ...
            """.trimMargin(),
            """
            |FF7FSF7F7F7F7F7F---7
            |L|LJ||||||||||||F--J
            |FL-7LJLJ||||||LJL-77
            |F--JF--7||LJLJ7F7FJ-
            |L---JF-JLJ.||-FJLJJ7
            ||F|F-JF---7F7-L7L|7|
            ||FFJF7L7F-JF7|JL---7
            |7-L-JL7||F7|L7F-7F7|
            |L.L7LFJ|||||FJL7||LJ
            |L7JLJL-JLJLJL--JLJ.L
            """.trimMargin()
        ),
    expectedExampleOutputsPart1 = listOf(4u, 8u),
    expectedExampleOutputsPart2 = listOf(4u, 8u, 10u)
) {
    private enum class Direction(val vector: Vector) {
        NORTH(Vector(0, -1)),
        EAST(Vector(1, 0)),
        SOUTH(Vector(0, 1)),
        WEST(Vector(-1, 0));

        fun reverse(): Direction =
            when (this) {
                NORTH -> SOUTH
                SOUTH -> NORTH
                WEST -> EAST
                EAST -> WEST
            }
    }

    private enum class PipeType(val symbol: Char, val connections: Set<Direction>) {
        VERTICAL('|', setOf(Direction.NORTH, Direction.SOUTH)),
        HORIZONTAL('-', setOf(Direction.WEST, Direction.EAST)),
        NORTH_EAST_BEND('L', setOf(Direction.NORTH, Direction.EAST)),
        NORTH_WEST_BEND('J', setOf(Direction.NORTH, Direction.WEST)),
        SOUTH_WEST_BEND('7', setOf(Direction.SOUTH, Direction.WEST)),
        SOUTH_EAST_BEND('F', setOf(Direction.SOUTH, Direction.EAST)),
        START_PIPE('S', setOf(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST))
    }

    private data class Pipe(
        val position: Point,
        val type: PipeType
    )

    override fun runPart1(input: List<String>): UInt {
        val pipes = parsePipeMap(input)
        val longestLoop = getLongestLoop(pipes)

        return ceil(longestLoop.size.toDouble() / 2.0).roundToInt().toUInt()
    }

    override fun runPart2(input: List<String>): UInt {
        TODO("Not yet implemented")
    }

    private fun parsePipeMap(input: List<String>): List<Pipe> {
        val symbols = PipeType.entries.associateBy(PipeType::symbol)

        return input
            .flatMapIndexed { y, line ->
                line.mapIndexedNotNull { x, c ->
                    if (c in symbols) {
                        Pipe(
                            position = Point(x, y),
                            type = symbols.getValue(c)
                        )
                    } else {
                        null
                    }
                }
            }
    }

    private fun getLongestLoop(pipes: List<Pipe>): List<Pipe> {
        val startPipe = pipes.first { pipe -> pipe.type == PipeType.START_PIPE }

        val loops = mutableListOf<List<Pipe>>()

        for (direction in Direction.entries) {
            val nextPoint = startPipe.position + direction.vector

            if (loops.any { loop -> loop.last().position == nextPoint }) {
                continue
            }

            val loop = getLoop(pipes, startPipe, direction)

            if (loop.isNotEmpty()) {
                loops += loop
            }
        }

        val longestLoop = loops.maxBy { loop -> loop.size }
        return longestLoop
    }

    private fun getLoop(
        pipes: List<Pipe>,
        startPipe: Pipe,
        initialDirection: Direction
    ): List<Pipe> {
        val path = mutableListOf<Pipe>()
        val map = pipes.associateBy(Pipe::position)

        var nextDirection = initialDirection
        var currentPipe = map[startPipe.position + nextDirection.vector]

        while (currentPipe != null && currentPipe != startPipe && currentPipe !in path) {
            path += currentPipe

            nextDirection =
                currentPipe.type.connections.filterNot { connection ->
                    connection == nextDirection.reverse()
                }.first()

            currentPipe = map[currentPipe.position + nextDirection.vector]
        }

        return path.toList()
    }
}
