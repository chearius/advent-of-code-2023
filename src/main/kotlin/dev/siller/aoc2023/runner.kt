@file:Suppress("ktlint:standard:filename")

package dev.siller.aoc2023

import java.time.LocalDate
import kotlin.reflect.KClass

fun main(args: Array<String>) =
    AocDayTask::class.sealedSubclasses
        .mapNotNull(KClass<out AocDayTask<*, *>>::objectInstance)
        .filter { aocDayTask ->
            filterAocTaskForToday(args, aocDayTask) || filterAocTaskForSpecificDay(args, aocDayTask) || isRunAll(args)
        }
        .sortedBy(AocDayTask<*, *>::day)
        .forEach { aocDayTask ->
            aocDayTask.run()
        }

private fun filterAocTaskForSpecificDay(
    args: Array<String>,
    accDayTask: AocDayTask<*, *>
) = args.isNotEmpty() && args.first().matches("[0-9]+".toRegex()) && args.first().toInt() == accDayTask.day

private fun filterAocTaskForToday(
    args: Array<String>,
    accDayTask: AocDayTask<*, *>
) = args.isEmpty() && LocalDate.now().dayOfMonth == accDayTask.day

private fun isRunAll(args: Array<String>) = args.isNotEmpty() && args.first() == "all"
