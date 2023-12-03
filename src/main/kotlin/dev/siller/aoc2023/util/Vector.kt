package dev.siller.aoc2023.util

typealias Point = Vector

data class Vector(
    val x: Int,
    val y: Int
) {
    companion object {
        val NORTH = Vector(0, -1)
        val NORTH_EAST = Vector(1, -1)
        val EAST = Vector(1, 0)
        val SOUTH_EAST = Vector(1, 1)
        val SOUTH = Vector(0, 1)
        val SOUTH_WEST = Vector(-1, 1)
        val WEST = Vector(-1, 0)
        val NORTH_WEST = Vector(-1, -1)

        val ALL_DIRECTION_VECTORS =
            listOf(
                NORTH,
                NORTH_EAST,
                EAST,
                SOUTH_EAST,
                SOUTH,
                SOUTH_WEST,
                WEST,
                NORTH_WEST
            )
    }

    fun getAdjacentPoints(
        minX: Int = Int.MIN_VALUE,
        maxX: Int = Int.MAX_VALUE,
        minY: Int = Int.MIN_VALUE,
        maxY: Int = Int.MAX_VALUE
    ): Set<Point> =
        ALL_DIRECTION_VECTORS
            .map { vector -> this + vector }
            .filter { point -> point.x in minX..maxX && point.y in minY..maxY }
            .toSet()

    operator fun plus(vector: Vector): Vector = Vector(x + vector.x, y + vector.y)

    operator fun times(scale: Int): Vector = Vector(x * scale, y * scale)
}
