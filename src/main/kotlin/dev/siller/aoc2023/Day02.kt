package dev.siller.aoc2023

data object Day02 : AocDayTask<Int, Int>(
    day = 2,
    exampleInput =
        """
        |Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
        |Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
        |Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
        |Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
        |Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green
        """.trimMargin(),
    expectedExampleOutputPart1 = 8,
    expectedExampleOutputPart2 = UNKNOWN_RESULT
) {
    data class CubeSet(val red: Int, val green: Int, val blue: Int)

    data class Game(val id: Int, val grabbedCubeSets: List<CubeSet>)

    override fun runPart1(input: List<String>): Int =
        input.map(::toGame).filterNot { game ->
            game.grabbedCubeSets.any { cs ->
                cs.red > 12 || cs.green > 13 || cs.blue > 14
            }
        }.map(Game::id).sum()

    override fun runPart2(input: List<String>): Int {
        TODO("Not yet implemented")
    }

    private fun toGame(line: String): Game {
        val (gamePart, setsPart) = line.split(':', limit = 2)

        val gameId = gamePart.filter { it in '0'..'9' }.toInt()

        val grabbedCubeSets =
            setsPart.split(';')
                .map { cubeSet ->
                    val cubes =
                        cubeSet.split(',').map { c ->
                            val (count, color) = c.split(' ', limit = 2)
                            color to count.toInt()
                        }.toMap()

                    CubeSet(cubes.getOrDefault("red", 0), cubes.getOrDefault("green", 0), cubes.getOrDefault("blue", 0))
                }
	
        return Game(gameId, grabbedCubeSets)
    }
}
