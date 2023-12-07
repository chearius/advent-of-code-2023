package dev.siller.aoc2023

data object Day07 : AocDayTask<UInt, UInt>(
    day = 7,
    exampleInput =
        """
        |32T3K 765
        |T55J5 684
        |KK677 28
        |KTJJT 220
        |QQQJA 483
        """.trimMargin(),
    expectedExampleOutputPart1 = 6440u,
    expectedExampleOutputPart2 = 5905u
) {
    private enum class Card(val id: Char, val value: UInt) {
        ACE('A', 14u),
        KING('K', 13u),
        QUEEN('Q', 12u),
        JACK('J', 11u),
        TEN('T', 10u),
        NINE('9', 9u),
        EIGHT('8', 8u),
        SEVEN('7', 7u),
        SIX('6', 6u),
        FIVE('5', 5u),
        FOUR('4', 4u),
        THREE('3', 3u),
        TWO('2', 2u),
        JOKER('*', 1u)
    }

    private enum class HandType {
        FIVE_OF_A_KIND,
        FOUR_OF_A_KIND,
        FULL_HOUSE,
        THREE_OF_A_KIND,
        TWO_PAIR,
        ONE_PAIR,
        HIGH_CARD
    }

    private class Hand(
        val cards: List<Card>
    ) {
        init {
            check(cards.size == 5) { "A hand must contain exactly 5 cards!" }
        }

        val type: HandType = getHandType()

        @Suppress("detekt:CyclomaticComplexMethod")
        private fun getHandType(): HandType {
            // occurrences without jokers
            val cardOccurrencesHigherThanOne =
                cards
                    .filterNot { card -> card == Card.JOKER }
                    .groupBy(Card::id)
                    .values
                    .map(List<Card>::size)
                    .filter { size -> size > 1 }
                    .sortedDescending()

            val jokersCount = cards.count { card -> card == Card.JOKER }

            return when (cardOccurrencesHigherThanOne.size) {
                2 -> { // Possible combinations - 3,2 ; 2,2
                    val (firstOccurrencesCount, secondOccurrencesCount) = cardOccurrencesHigherThanOne

                    if (firstOccurrencesCount + jokersCount == 3 && secondOccurrencesCount == 2) {
                        HandType.FULL_HOUSE
                    } else {
                        HandType.TWO_PAIR
                    }
                }
                1 -> {
                    when (cardOccurrencesHigherThanOne.single() + jokersCount) {
                        5 -> HandType.FIVE_OF_A_KIND
                        4 -> HandType.FOUR_OF_A_KIND
                        3 -> HandType.THREE_OF_A_KIND
                        else -> HandType.ONE_PAIR
                    }
                }
                else ->
                    when (jokersCount) {
                        5, 4 -> HandType.FIVE_OF_A_KIND
                        3 -> HandType.FOUR_OF_A_KIND
                        2 -> HandType.THREE_OF_A_KIND
                        1 -> HandType.ONE_PAIR
                        else -> HandType.HIGH_CARD
                    }
            }
        }

        override fun toString(): String {
            return "Hand(cards=$cards, type=$type)"
        }
    }

    private data class HandAndBid(
        val hand: Hand,
        val bid: UInt
    )

    override fun runPart1(input: List<String>): UInt =
        input
            .map(::parseHandWithBid)
            .sortedWith(compareHands)
            .mapIndexed { index, handWithBid ->
                handWithBid.bid * (index.toUInt() + 1u)
            }
            .sum()

    override fun runPart2(input: List<String>): UInt =
        input
            .asSequence()
            .map { line -> line.replace('J', '*') }
            .map(::parseHandWithBid)
            .sortedWith(compareHands)
            .mapIndexed { index, handWithBid ->
                handWithBid.bid * (index.toUInt() + 1u)
            }
            .sum()

    private fun parseHandWithBid(line: String): HandAndBid {
        val (cardIds, bid) = line.split(' ', limit = 2)

        val cards = cardIds.map { cardId -> Card.entries.first { card -> card.id == cardId } }

        return HandAndBid(
            hand = Hand(cards),
            bid = bid.toUInt()
        )
    }

    private val compareHands: (hand1: HandAndBid, hand2: HandAndBid) -> Int = { (hand1, _), (hand2, _) ->
        val compareTypes = hand2.type.compareTo(hand1.type)

        if (compareTypes == 0) {
            hand2.cards
                .zip(hand1.cards)
                .map { (card1, card2) ->
                    card2.value.compareTo(card1.value)
                }
                .firstOrNull { result -> result != 0 } ?: 0
        } else {
            compareTypes
        }
    }
}
