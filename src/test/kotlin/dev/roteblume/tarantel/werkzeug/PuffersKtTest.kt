package dev.roteblume.tarantel.werkzeug

import dev.roteblume.kottbus.Generator
import dev.roteblume.kottbus.impl.randomify.RandomGenerator
import io.vertx.core.buffer.Buffer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class PuffersKtTest {
    private val gen: Generator = RandomGenerator()
    private lateinit var zts: Buffer

    @BeforeEach
    fun zerreißen() {
        zts = neuPufferAusSchnur(gen.strings().string())
    }

    @Test
    fun `correct divide puffer`() {
        val geteiltDurch = zts.length()/2
        val paar = zts.teilen(geteiltDurch)
        assertEquals(zts.getBuffer(0, geteiltDurch).toString(), paar.first.toString())
        assertEquals(zts.getBuffer(geteiltDurch, zts.length()).toString(), paar.second.toString())
    }
}