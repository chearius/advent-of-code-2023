package dev.siller.aoc2023

import kotlin.math.max

data object Day08 : AocDayTask<UInt, ULong>(
    day = 8,
    exampleInputsPart1 =
        listOf(
            """
            |RL
            |
            |AAA = (BBB, CCC)
            |BBB = (DDD, EEE)
            |CCC = (ZZZ, GGG)
            |DDD = (DDD, DDD)
            |EEE = (EEE, EEE)
            |GGG = (GGG, GGG)
            |ZZZ = (ZZZ, ZZZ)
            """.trimMargin(),
            """
            |LLR
            |
            |AAA = (BBB, BBB)
            |BBB = (AAA, ZZZ)
            |ZZZ = (ZZZ, ZZZ)
            """.trimMargin()
        ),
    exampleInputsPart2 =
        listOf(
            """
            |LR
            |
            |11A = (11B, XXX)
            |11B = (XXX, 11Z)
            |11Z = (11B, XXX)
            |22A = (22B, XXX)
            |22B = (22C, 22C)
            |22C = (22Z, 22Z)
            |22Z = (22B, 22B)
            |XXX = (XXX, XXX)
            """.trimMargin()
        ),
    expectedExampleOutputsPart1 = listOf(2u, 6u),
    expectedExampleOutputsPart2 = listOf(6uL)
) {
    private const val START_NODE = "AAA"

    private const val START_NODE_SUFFIX = 'A'

    private const val END_NODE = "ZZZ"

    private const val END_NODE_SUFFIX = 'Z'

    private data class NavigationMap(
        private val blueprint: List<Direction>,
        val nodes: Map<String, Node>
    ) {
        fun instructions() = generateSequence { blueprint.asIterable() }.flatten()
    }

    private data class Node(
        val id: String,
        val nextLeft: String,
        val nextRight: String
    )

    private enum class Direction(val char: Char) {
        LEFT('L'),
        RIGHT('R')
    }

    override fun runPart1(input: List<String>): UInt =
        parseInput(input).let { map ->
            val instructionSequence = map.instructions().iterator()

            var currentNode = map.nodes.getValue(START_NODE)
            var steps = 0u

            while (currentNode.id != END_NODE) {
                steps++
                val instruction = instructionSequence.next()

                currentNode =
                    when (instruction) {
                        Direction.LEFT -> map.nodes.getValue(currentNode.nextLeft)
                        Direction.RIGHT -> map.nodes.getValue(currentNode.nextRight)
                    }
            }

            steps
        }

    override fun runPart2(input: List<String>): ULong =
        parseInput(input).let { map ->
            map.nodes.values.filter { node -> node.id.endsWith(START_NODE_SUFFIX) }.map { node ->
                findCycleLength(map, node.id)
            }.fold(1uL) { acc, cycleLength ->
                findLeastCommonMultiple(acc, cycleLength)
            }
        }

    private fun findCycleLength(
        map: NavigationMap,
        startNode: String
    ): ULong {
        val instructionSequence = map.instructions().iterator()
        val ends = mutableListOf<ULong>()

        var currentNode = startNode
        var steps = 0uL

        // check a few cycles to be sure, all cycles have the same length
        while (ends.size <= 5) {
            steps++
            val instruction = instructionSequence.next()

            currentNode =
                when (instruction) {
                    Direction.LEFT -> map.nodes.getValue(currentNode).nextLeft
                    Direction.RIGHT -> map.nodes.getValue(currentNode).nextRight
                }

            if (currentNode.endsWith(END_NODE_SUFFIX)) {
                ends += steps
            }
        }

        val cycleLength = ends.zipWithNext().map { (a, b) -> b - a }.distinct()

        if (cycleLength.size == 1) {
            return cycleLength.first()
        } else {
            error("Not a single cycle length! Cannot continue!")
        }
    }

    private fun parseInput(input: List<String>): NavigationMap {
        val instructions =
            input.take(1).first().map { char ->
                Direction.entries.first { direction -> direction.char == char }
            }

        val nodes =
            input.drop(2)
                .map { line ->
                    val (nodeId, targets) = line.split(" = ", limit = 2)
                    val (nextLeft, nextRight) = targets.trim(' ', '(', ')').split(", ")

                    Node(nodeId, nextLeft, nextRight)
                }
                .associateBy(Node::id)

        return NavigationMap(instructions, nodes)
    }

    private fun findLeastCommonMultiple(
        a: ULong,
        b: ULong
    ): ULong {
        val larger = max(a, b)
        val maxLcm = a * b

        var lcm = larger

        while (lcm <= maxLcm) {
            if (lcm % a == 0uL && lcm % b == 0uL) {
                return lcm
            }

            lcm += larger
        }

        return maxLcm
    }
}
