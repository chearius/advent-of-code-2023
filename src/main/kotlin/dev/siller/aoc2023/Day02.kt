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
    expectedExampleOutputPart2 = 2286
) {
    private const val MAX_RED_CUBES = 12

    private const val MAX_GREEN_CUBES = 13

    private const val MAX_BLUE_CUBES = 14

    data class CubeSet(val red: Int, val green: Int, val blue: Int)

    data class Game(val id: Int, val grabbedCubeSets: List<CubeSet>)

    override fun runPart1(input: List<String>): Int =
        input.map(::toGame).filterNot { game ->
            game.grabbedCubeSets.any { cs ->
                cs.red > MAX_RED_CUBES || cs.green > MAX_GREEN_CUBES || cs.blue > MAX_BLUE_CUBES
            }
        }.sumOf(Game::id)

    override fun runPart2(input: List<String>): Int =
        input.map(::toGame).sumOf { game ->
            val cubes = game.grabbedCubeSets

            cubes.maxOf(CubeSet::red) * cubes.maxOf(CubeSet::blue) * cubes.maxOf(CubeSet::green)
        }

    private fun toGame(line: String): Game {
        val (gamePart, setsPart) = line.split(':', limit = 2)

        val gameId = gamePart.filter { it in '0'..'9' }.toInt()

        val grabbedCubeSets =
            setsPart.split(';')
                .map { cubeSet ->
                    val cubes =
                        cubeSet.split(',').map(String::trim).associate { c ->
                            val (count, color) = c.split(' ', limit = 2)
                            color to count.toInt()
                        }

                    CubeSet(
                        red = cubes.getOrDefault("red", 0),
                        green = cubes.getOrDefault("green", 0),
                        blue = cubes.getOrDefault("blue", 0)
                    )
                }

        return Game(gameId, grabbedCubeSets)
    }
}
