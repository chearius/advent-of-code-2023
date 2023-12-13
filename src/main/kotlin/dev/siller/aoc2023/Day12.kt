package dev.siller.aoc2023

data object Day12 : AocDayTask<UInt, UInt>(
    day = 12,
    exampleInput =
        """
        |???.### 1,1,3
        |.??..??...?##. 1,1,3
        |?#?#?#?#?#?#?#? 1,3,1,6
        |????.#...#... 4,1,1
        |????.######..#####. 1,6,5
        |?###???????? 3,2,1
        """.trimMargin(),
    expectedExampleOutputPart1 = 21u,
    expectedExampleOutputPart2 = UNKNOWN_RESULT
) {
    private enum class SpringCondition(val symbol: Char) {
        OPERATIONAL('.'),
        DAMAGED('#'),
        UNKNOWN('?')
    }

    private data class ConditionRecord(
        val springConditions: List<SpringCondition>,
        val groupsOfDamagedSprings: List<UInt>
    )

    override fun runPart1(input: List<String>): UInt {
        TODO("Not yet implemented")
    }

    override fun runPart2(input: List<String>): UInt {
        TODO("Not yet implemented")
    }

    private fun parseConditionRecord(input: String): ConditionRecord {
        val (conditionPart, groupPart) = input.split(' ', limit = 2)

        val symbols = SpringCondition.entries.associateBy(SpringCondition::symbol)

        val springConditions = conditionPart.mapNotNull { symbol -> symbols[symbol] }
        val groups = groupPart.split(',').map { length -> length.toUInt() }

        return ConditionRecord(springConditions, groups)
    }
}
