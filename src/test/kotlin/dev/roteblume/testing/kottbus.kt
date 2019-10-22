package dev.roteblume.testing

import dev.roteblume.kottbus.Primitive
import dev.roteblume.kottbus.Strings

fun Primitive.long(von: Long, nach: Long): Long = kotlin.random.Random.nextLong(von, nach)

fun Primitive.dieByteListe(dieLänge: Int = 8): ByteArray = (0..dieLänge)
    .asSequence()
    .map { int().toByte() }
    .toList()
    .toByteArray()

fun Strings.array(dieLänge: Int = 10) = (0..dieLänge)
    .asSequence()
    .map { string() }
    .toList()
    .toTypedArray()

fun Strings.dieKarte(dieLänge: Int): Map<String, String> = (0..dieLänge)
    .asSequence()
    .map { Pair(it.toString(), string()) }
    .toMap()
