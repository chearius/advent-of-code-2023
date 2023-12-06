package dev.siller.aoc2023

import java.util.LinkedList
import kotlin.math.pow

data object Day04 : AocDayTask<UInt, UInt>(
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
    expectedExampleOutputPart1 = 13u,
    expectedExampleOutputPart2 = 30u
) {
    private val WHITESPACES = "\\s+".toRegex()

    private data class ScratchCard(
        val id: UInt,
        val winningNumbers: Set<UInt>,
        val ownNumbers: Set<UInt>
    ) {
        private val matchingNumbers = winningNumbers.intersect(ownNumbers)

        val matchingNumbersCount = matchingNumbers.size.toUInt()

        val wonScratchCardCopiesIds = (id + 1u)..(id + matchingNumbersCount)
    }

    override fun runPart1(input: List<String>): UInt =
        input.map(::parseScratchCard)
            .sumOf { scratchCard -> (2.0).pow((scratchCard.matchingNumbersCount - 1u).toInt()).toUInt() }

    override fun runPart2(input: List<String>): UInt =
        input.map(::parseScratchCard)
            .associateBy { scratchCard -> scratchCard.id }
            .let { scratchCards ->
                val queue = LinkedList(scratchCards.values)
                val scratchCardInstances = scratchCards.keys.associateWith { _ -> 1u }.toMutableMap()

                while (queue.isNotEmpty()) {
                    queue.poll().wonScratchCardCopiesIds.mapNotNull { id ->
                        scratchCards[id]
                    }.forEach { wonScratchCardCopy ->
                        scratchCardInstances[wonScratchCardCopy.id] =
                            scratchCardInstances.getOrDefault(wonScratchCardCopy.id, 0u) + 1u
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
            id = cardPart.filter { c -> c in '0'..'9' }.toUInt(),
            winningNumbers = winningNumbersPart.split(' ').map(String::toUInt).toSet(),
            ownNumbers = ownNumbersPart.split(' ').map(String::toUInt).toSet()
        )
    }
}
