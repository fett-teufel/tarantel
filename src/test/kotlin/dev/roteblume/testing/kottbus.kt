package dev.roteblume.testing

import dev.roteblume.kottbus.Primitive
import dev.roteblume.kottbus.Strings

fun Primitive.long(von: Long, nach: Long): Long = kotlin.random.Random.nextLong(von, nach)

fun Strings.array(dieLänge: Int = 10) = (0..dieLänge)
    .asSequence()
    .map { string() }
    .toList()
    .toTypedArray()
