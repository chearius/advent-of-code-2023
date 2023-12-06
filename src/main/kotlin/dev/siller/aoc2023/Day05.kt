package dev.siller.aoc2023

import kotlin.math.max
import kotlin.math.min

data object Day05 : AocDayTask<Long, Long>(
    day = 5,
    exampleInput =
        """
        |seeds: 79 14 55 13

        |seed-to-soil map:
        |50 98 2
        |52 50 48
        |
        |soil-to-fertilizer map:
        |0 15 37
        |37 52 2
        |39 0 15
        |
        |fertilizer-to-water map:
        |49 53 8
        |0 11 42
        |42 0 7
        |57 7 4
        |
        |water-to-light map:
        |88 18 7
        |18 25 70
        |
        |light-to-temperature map:
        |45 77 23
        |81 45 19
        |68 64 13
        |
        |temperature-to-humidity map:
        |0 69 1
        |1 0 69
        |
        |humidity-to-location map:
        |60 56 37
        |56 93 4            
        """.trimMargin(),
    expectedExampleOutputPart1 = 35,
    expectedExampleOutputPart2 = 46
) {
    private data class Almanac(
        val seeds: List<Long>,
        val seedToSoil: Mapping,
        val soilToFertilizer: Mapping,
        val fertilizerToWater: Mapping,
        val waterToLight: Mapping,
        val lightToTemperature: Mapping,
        val temperatureToHumidity: Mapping,
        val humidityToLocation: Mapping
    )

    private data class Mapping(
        val transformations: List<Transformation>
    ) {
        operator fun invoke(source: Interval): List<Interval> =
            transformations.mapNotNull { transformation ->
                transformation.sourceInterval.intersect(source)?.offset(transformation.destinationOffset)
            }
    }

    private data class Transformation(
        val sourceInterval: Interval,
        val destinationOffset: Long
    )

    private data class Interval(
        val start: Long,
        val end: Long
    ) {
        fun intersect(other: Interval): Interval? =
            if (this.end >= other.start && this.start <= other.end) {
                Interval(max(start, other.start), min(end, other.end))
            } else {
                null
            }

        fun offset(offset: Long): Interval = Interval(start + offset, end + offset)
    }

    override fun runPart1(input: List<String>): Long =
        parseAlmanac(input).run {
            seeds
                .map { seed -> Interval(seed, seed) }
                .asSequence()
                .flatMap(seedToSoil::invoke)
                .flatMap(soilToFertilizer::invoke)
                .flatMap(fertilizerToWater::invoke)
                .flatMap(waterToLight::invoke)
                .flatMap(lightToTemperature::invoke)
                .flatMap(temperatureToHumidity::invoke)
                .flatMap(humidityToLocation::invoke)
                .map(Interval::start)
                .min()
        }

    override fun runPart2(input: List<String>): Long =
        parseAlmanac(input).run {
            seeds
                .chunked(2)
                .map { seed -> Interval(seed[0], seed[0] + seed[1] - 1) }
                .asSequence()
                .flatMap(seedToSoil::invoke)
                .flatMap(soilToFertilizer::invoke)
                .flatMap(fertilizerToWater::invoke)
                .flatMap(waterToLight::invoke)
                .flatMap(lightToTemperature::invoke)
                .flatMap(temperatureToHumidity::invoke)
                .flatMap(humidityToLocation::invoke)
                .minOf(Interval::start)
        }

    private fun parseAlmanac(input: List<String>): Almanac {
        val seeds = mutableListOf<Long>()
        val seedToSoil = mutableListOf<Transformation>()
        val soilToFertilizer = mutableListOf<Transformation>()
        val fertilizerToWater = mutableListOf<Transformation>()
        val waterToLight = mutableListOf<Transformation>()
        val lightToTemperature = mutableListOf<Transformation>()
        val temperatureToHumidity = mutableListOf<Transformation>()
        val humidityToLocation = mutableListOf<Transformation>()

        var currentTransformationList = seedToSoil

        for (line in input) {
            if (line.startsWith("seeds: ")) {
                seeds.addAll(line.split("\\s+".toRegex()).drop(1).map(String::toLong))
            } else if (line == "seed-to-soil map:") {
                currentTransformationList = seedToSoil
            } else if (line == "soil-to-fertilizer map:") {
                currentTransformationList = soilToFertilizer
            } else if (line == "fertilizer-to-water map:") {
                currentTransformationList = fertilizerToWater
            } else if (line == "water-to-light map:") {
                currentTransformationList = waterToLight
            } else if (line == "light-to-temperature map:") {
                currentTransformationList = lightToTemperature
            } else if (line == "temperature-to-humidity map:") {
                currentTransformationList = temperatureToHumidity
            } else if (line == "humidity-to-location map:") {
                currentTransformationList = humidityToLocation
            } else if (line.matches("\\d+ \\d+ \\d+".toRegex())) {
                val (destinationRangeStart, sourceRangeStart, rangeLength) =
                    line.split("\\s+".toRegex(), limit = 3).map(String::toLong)

                currentTransformationList +=
                    Transformation(
                        sourceInterval =
                            Interval(
                                start = sourceRangeStart,
                                end = sourceRangeStart + rangeLength - 1
                            ),
                        destinationOffset = destinationRangeStart - sourceRangeStart
                    )
            }
        }

        return Almanac(
            seeds = seeds,
            seedToSoil = Mapping(fillSourceIntervals(seedToSoil)),
            soilToFertilizer = Mapping(fillSourceIntervals(soilToFertilizer)),
            fertilizerToWater = Mapping(fillSourceIntervals(fertilizerToWater)),
            waterToLight = Mapping(fillSourceIntervals(waterToLight)),
            lightToTemperature = Mapping(fillSourceIntervals(lightToTemperature)),
            temperatureToHumidity = Mapping(fillSourceIntervals(temperatureToHumidity)),
            humidityToLocation = Mapping(fillSourceIntervals(humidityToLocation))
        )
    }

    private fun fillSourceIntervals(transformations: List<Transformation>): List<Transformation> {
        if (transformations.isEmpty()) {
            listOf(Transformation(Interval(0, Long.MAX_VALUE), destinationOffset = 0))
        }

        val sortedTransformations = transformations.sortedBy { transformation -> transformation.sourceInterval.start }
        val allTransformations = mutableListOf<Transformation>()

        var lastEnd = 0L

        if (sortedTransformations[0].sourceInterval.start > 0) {
            lastEnd = sortedTransformations[0].sourceInterval.start - 1
            allTransformations.add(Transformation(Interval(0, lastEnd), destinationOffset = 0))
        }

        for (transformation in sortedTransformations) {
            if (transformation.sourceInterval.start > lastEnd + 1) {
                allTransformations.add(
                    Transformation(
                        Interval(lastEnd + 1, transformation.sourceInterval.start - 1),
                        destinationOffset = 0
                    )
                )
            }

            allTransformations.add(transformation)
            lastEnd = transformation.sourceInterval.end
        }

        if (lastEnd < Long.MAX_VALUE) {
            allTransformations.add(Transformation(Interval(lastEnd + 1, Long.MAX_VALUE), destinationOffset = 0))
        }

        return allTransformations
    }
}
