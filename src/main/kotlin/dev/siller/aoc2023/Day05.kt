package dev.siller.aoc2023

import dev.siller.aoc2023.util.Interval

data object Day05 : AocDayTask<ULong, ULong>(
    day = 5,
    exampleInput =
        """
        |seeds: 79 14 55 13
        |
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
    expectedExampleOutputPart1 = 35u,
    expectedExampleOutputPart2 = 46u
) {
    private data class Almanac(
        val seeds: List<ULong>,
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
        fun map(source: Interval): List<Interval> =
            transformations.mapNotNull { transformation ->
                transformation.sourceInterval.intersect(source)?.offset(transformation.destinationOffset)
            }
    }

    private data class Transformation(
        val sourceInterval: Interval,
        val destinationOffset: Long
    )

    override fun runPart1(input: List<String>): ULong =
        parseAlmanac(input).run {
            seeds
                .map { seed -> Interval(seed, seed) }
                .asSequence()
                .flatMap(seedToSoil::map)
                .flatMap(soilToFertilizer::map)
                .flatMap(fertilizerToWater::map)
                .flatMap(waterToLight::map)
                .flatMap(lightToTemperature::map)
                .flatMap(temperatureToHumidity::map)
                .flatMap(humidityToLocation::map)
                .minOf(Interval::start)
        }

    override fun runPart2(input: List<String>): ULong =
        parseAlmanac(input).run {
            seeds
                .chunked(2)
                .map { seed -> Interval(seed[0], seed[0] + seed[1] - 1u) }
                .asSequence()
                .flatMap(seedToSoil::map)
                .flatMap(soilToFertilizer::map)
                .flatMap(fertilizerToWater::map)
                .flatMap(waterToLight::map)
                .flatMap(lightToTemperature::map)
                .flatMap(temperatureToHumidity::map)
                .flatMap(humidityToLocation::map)
                .minOf(Interval::start)
        }

    private fun parseAlmanac(input: List<String>): Almanac {
        val seeds = mutableListOf<ULong>()
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
                seeds.addAll(line.split("\\s+".toRegex()).drop(1).map(String::toULong))
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
                    line.split("\\s+".toRegex(), limit = 3).map(String::toULong)

                currentTransformationList +=
                    Transformation(
                        sourceInterval =
                            Interval(
                                start = sourceRangeStart,
                                end = sourceRangeStart + rangeLength - 1u
                            ),
                        destinationOffset = destinationRangeStart.toLong() - sourceRangeStart.toLong()
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
            listOf(Transformation(Interval(0u, ULong.MAX_VALUE), destinationOffset = 0))
        }

        val sortedTransformations = transformations.sortedBy { transformation -> transformation.sourceInterval.start }
        val allTransformations = mutableListOf<Transformation>()

        var lastEnd = 0uL

        if (sortedTransformations[0].sourceInterval.start > 0u) {
            lastEnd = sortedTransformations[0].sourceInterval.start - 1u
            allTransformations.add(Transformation(Interval(0u, lastEnd), destinationOffset = 0))
        }

        for (transformation in sortedTransformations) {
            if (transformation.sourceInterval.start > lastEnd + 1u) {
                allTransformations.add(
                    Transformation(
                        Interval(lastEnd + 1u, transformation.sourceInterval.start - 1u),
                        destinationOffset = 0
                    )
                )
            }

            allTransformations.add(transformation)
            lastEnd = transformation.sourceInterval.end
        }

        if (lastEnd < ULong.MAX_VALUE) {
            allTransformations.add(Transformation(Interval(lastEnd + 1u, ULong.MAX_VALUE), destinationOffset = 0))
        }

        return allTransformations
    }
}
