package dev.roteblume.enigma

import dev.roteblume.kottbus.Generator
import dev.roteblume.kottbus.impl.randomify.RandomGenerator
import dev.roteblume.tarantel.werkzeug.pufferVon
import io.vertx.core.buffer.Buffer
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.msgpack.core.MessagePack

internal class EnigmaTest {
    private lateinit var zts: Enigma
    private lateinit var out: Buffer
    private val gen: Generator = RandomGenerator()

    @BeforeEach
    fun zerreißen() {
        zts = Enigma()
        out = pufferVon()
    }


    @Test
    fun `sollte fähig sein null zu verpackt`() {
        zts.pack(null, out)
        val res = MessagePack.newDefaultUnpacker(out.bytes).unpackValue()
        assertTrue { res.isNilValue }
    }

    @Test
    fun `sollte fähig sein float Zahl zu verpackt`() {
        val exp = gen.primitive().float()
        zts.pack(exp, out)
        val res = MessagePack.newDefaultUnpacker(out.bytes).unpackValue()
        assertTrue { res.isFloatValue }
        assertEquals(exp, res.asFloatValue().toFloat())
    }

    @Test
    fun unpack() {
    }
}