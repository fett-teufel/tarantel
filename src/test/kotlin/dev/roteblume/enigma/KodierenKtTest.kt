package dev.roteblume.enigma

import dev.roteblume.kottbus.Generator
import dev.roteblume.kottbus.impl.randomify.RandomGenerator
import dev.roteblume.tarantel.werkzeug.pufferVon
import io.vertx.core.buffer.Buffer
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.msgpack.core.MessagePack

internal class KodierenKtTest {

    private lateinit var zts: Buffer
    private val gen: Generator = RandomGenerator()

    @BeforeEach
    fun zerreißen() {
        zts = pufferVon()
    }

    @Test
    fun `sollte fähig sein null zu verpaken`() {
        val res = zts.repacken(null)
        assertTrue { res.isNilValue }
    }

    @Test
    fun `sollte fähig sein TRUE zu verpaken`() {
        val res = zts.repacken(true)
        assertTrue { res.isBooleanValue }
        assertTrue { res.asBooleanValue().boolean }
    }

    @Test
    fun `sollte fähig sein FALSE zu verpaken`() {
        val res = zts.repacken(false)
        assertTrue { res.isBooleanValue }
        assertFalse() { res.asBooleanValue().boolean }
    }

    @Test
    fun `sollte fähig sein float zu verpaken`() {
        val exp = gen.primitive().float()
        val res = zts.repacken(exp)
        assertTrue { res.isFloatValue }
        assertEquals(exp, res.asFloatValue().toFloat())
    }

    @Test
    fun `sollte fähig sein double zu verpaken`() {
        val exp = gen.primitive().double()
        val res = zts.repacken(exp)
        assertTrue { res.isNumberValue }
        assertEquals(exp, res.asNumberValue().toDouble())
    }

    @Test
    fun `sollte fähig sein long zu verpaken`() {
        val exp = gen.primitive().int()
        val res = zts.repacken(exp.toLong())
        assertTrue { res.isIntegerValue }
        assertEquals(exp, res.asIntegerValue().toInt())
    }

    @Test
    fun `sollte fähig sein klein Zahl zu verpaken`() {
        val exp = 250
        val orig = zts.packen(exp).bytes
        val res = MessagePack.newDefaultUnpacker(orig).unpackValue()
        assertTrue { res.isIntegerValue }
        assertEquals(exp, res.asIntegerValue().toInt())
    }

    @Test
    fun `sollte fähig sein unsigniert kurz Zahl zu verpaken`() {
        val exp: Long = (MAX_16BIT - 2).toLong()
        val orig = zts.packen(exp).bytes
        val res = MessagePack.newDefaultUnpacker(orig).unpackValue()
        assertTrue { res.isNumberValue }
        assertEquals(exp, res.asIntegerValue().asLong())
    }

    @Test
    fun `sollte fähig sein 32bit Zahl zu verpaken`() {
        val exp: Long = (MAX_32BIT - 2)
        val orig = zts.packen(exp).bytes
        val res = MessagePack.newDefaultUnpacker(orig).unpackValue()
        assertTrue { res.isNumberValue }
        assertEquals(exp, res.asIntegerValue().asLong())
    }

    @Test
    fun `sollte fähig sein long Zahl zu verpaken`() {
        val exp: Long = (MAX_32BIT + 2)
        val res = MessagePack.newDefaultUnpacker(zts.packen(exp).bytes).unpackValue()
        assertTrue { res.isNumberValue }
        assertEquals(exp, res.asIntegerValue().asLong())
    }

    @Test
    fun `sollte fähig sein eine Schnur zu verpaken`() {
        val exp = gen.strings().string()
        val res = zts.repacken(exp)
        assertTrue { res.isStringValue }
        assertEquals(exp, res.asStringValue().toString())
    }

    @Test
    fun `sollte fähig sein lange Schnur zu verpaken`() {
        val exp = gen.strings().string(MAX_8BIT - 1)
        val res = MessagePack.newDefaultUnpacker(zts.packen(exp).bytes).unpackValue()
        assertTrue { res.isStringValue }
        assertEquals(exp, res.asStringValue().toString())
    }

    @Test
    fun `sollte fähig sein große Schnur zu verpaken`() {
        val exp = gen.strings().string(MAX_16BIT - 1)
        val res = MessagePack.newDefaultUnpacker(zts.packen(exp).bytes).unpackValue()
        assertTrue { res.isStringValue }
        assertEquals(exp, res.asStringValue().toString())
    }

    @Test
    fun `sollte fähig sein lang große Schnur zu verpaken`() {
        val exp = gen.strings().string(MAX_16BIT + 2)
        val res = MessagePack.newDefaultUnpacker(zts.packen(exp).bytes).unpackValue()
        assertTrue { res.isStringValue }
        assertEquals(exp, res.asStringValue().toString())
    }

    @Test
    fun `sollte fähig sein eine klein Byteaufstellung zu verpaken`() {
        val exp = gen.strings().string()
        zts.packen(exp.toByteArray())

        val value = MessagePack.newDefaultUnpacker(zts.bytes).unpackValue()
        assertTrue { value.isBinaryValue }
        assertEquals(exp, value.asBinaryValue().asByteArray().toString(Charsets.UTF_8))
    }

    @Test
    fun `sollte fähig sein eine Byteaufstellung zu verpaken`() {
        val exp = gen.strings().string(MAX_16BIT - 1)
        zts.packen(exp.toByteArray())

        val value = MessagePack.newDefaultUnpacker(zts.bytes).unpackValue()
        assertTrue { value.isBinaryValue }
        assertEquals(exp, value.asBinaryValue().asByteArray().toString(Charsets.UTF_8))
    }

    @Test
    fun `sollte fähig sein eine große Byteaufstellung zu verpaken`() {
        val exp = gen.strings().string(MAX_16BIT + 1)
        zts.packen(exp.toByteArray())

        val value = MessagePack.newDefaultUnpacker(zts.bytes).unpackValue()
        assertTrue { value.isBinaryValue }
        assertEquals(exp, value.asBinaryValue().asByteArray().toString(Charsets.UTF_8))
    }

    @Test
    fun `sollte fähig sein eine Aufstellung zu verpaken`() {
        val exp = (0..10).asSequence().map {
            gen.strings().string()
        }.toList()

        zts.packen(exp)

        val value = MessagePack.newDefaultUnpacker(zts.bytes).unpackValue()
        assertTrue { value.isArrayValue }
    }

    @Test
    fun `sollte fähig sein eine kurze Aufstellung zu verpaken`() {
        val exp = (0..MAX_16BIT-2).asSequence().map {
            gen.strings().string()
        }.toList()
        zts.packen(exp)

        val value = MessagePack.newDefaultUnpacker(zts.bytes).unpackValue()
        assertTrue { value.isArrayValue }
        assertEquals(exp, value.asArrayValue().asSequence().map { it.toString() }.toList())
    }

    @Test
    fun `sollte fähig sein eine long Aufstellung zu verpaken`() {
        val exp = (0..MAX_16BIT+2).asSequence().map {
            gen.strings().string()
        }.toList()
        zts.packen(exp)

        val value = MessagePack.newDefaultUnpacker(zts.bytes).unpackValue()
        assertTrue { value.isArrayValue }
        assertEquals(exp, value.asArrayValue().asSequence().map { it.toString() }.toList())
    }

    @Test
    fun `sollte fähig sein eine kurz Karte zu verpaken`() {
        val exp = (0..10).asSequence().map { Pair(it, gen.strings().string()) }.toMap()
        zts.packen(exp)

        val value = MessagePack.newDefaultUnpacker(zts.bytes).unpackValue()
        assertTrue { value.isMapValue }
    }

    @Test
    fun `sollte fähig sein eine Karte zu verpaken`() {
        val exp = (0..MAX_4BIT+1).asSequence().map { Pair(it, gen.strings().string()) }.toMap()
        zts.packen(exp)

        val value = MessagePack.newDefaultUnpacker(zts.bytes).unpackValue()
        assertTrue { value.isMapValue }
    }

    @Test
    fun `sollte fähig sein eine lange Karte zu verpaken`() {
        val exp = (0..MAX_16BIT+1).asSequence().map { Pair(it, gen.strings().string()) }.toMap()
        zts.packen(exp)

        val value = MessagePack.newDefaultUnpacker(zts.bytes).unpackValue()
        assertTrue { value.isMapValue }
    }
}

private fun Buffer.repacken(value: Any?) = MessagePack.newDefaultUnpacker(packen(value).bytes).unpackValue()
