package dev.siller.aoc2023

import java.util.LinkedList
import kotlin.math.pow

data object Day04 : AocDayTask<Int, Int>(
    day = 4,
    exampleInput =
        """
        |Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
        |Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
        |Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
        |Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
        |Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
        |Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11
        """.trimMargin(),
    expectedExampleOutputPart1 = 13,
    expectedExampleOutputPart2 = 30
) {
    private val WHITESPACES = "\\s+".toRegex()

    data class ScratchCard(
        val id: Int,
        val winningNumbers: Set<Int>,
        val ownNumbers: Set<Int>
    ) {
        private val matchingNumbers = winningNumbers.intersect(ownNumbers)

        val matchingNumbersCount = matchingNumbers.size

        val wonScratchCardCopiesIds = (id + 1)..(id + matchingNumbersCount)
    }

    override fun runPart1(input: List<String>): Int =
        input.map(::parseScratchCard)
            .sumOf { scratchCard -> (2.0).pow(scratchCard.matchingNumbersCount - 1).toInt() }

    override fun runPart2(input: List<String>): Int =
        input.map(::parseScratchCard)
            .associateBy { scratchCard -> scratchCard.id }
            .let { scratchCards ->
                val queue = LinkedList(scratchCards.values)
                val scratchCardInstances = scratchCards.keys.associateWith { _ -> 1 }.toMutableMap()

                while (queue.isNotEmpty()) {
                    queue.poll().wonScratchCardCopiesIds.mapNotNull { id ->
                        scratchCards[id]
                    }.forEach { wonScratchCardCopy ->
                        scratchCardInstances[wonScratchCardCopy.id] =
                            scratchCardInstances.getOrDefault(wonScratchCardCopy.id, 0) + 1
                        queue.add(wonScratchCardCopy)
                    }
                }

                scratchCardInstances.values.sum()
            }

    private fun parseScratchCard(line: String): ScratchCard {
        val (cardPart, winningNumbersPart, ownNumbersPart) =
            line
                .split("\\s?[:|]\\s".toRegex(), limit = 3)
                .map { part ->
                    part.trim().replace(WHITESPACES, " ")
                }

        return ScratchCard(
            id = cardPart.filter { c -> c in '0'..'9' }.toInt(),
            winningNumbers = winningNumbersPart.split(' ').map(String::toInt).toSet(),
            ownNumbers = ownNumbersPart.split(' ').map(String::toInt).toSet()
        )
    }
}
