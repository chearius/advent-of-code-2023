@file:Suppress("ktlint:standard:filename")

package dev.siller.aoc2023

import org.slf4j.LoggerFactory

inline val <reified T> T.log
    get() = LoggerFactory.getLogger(T::class.java)
