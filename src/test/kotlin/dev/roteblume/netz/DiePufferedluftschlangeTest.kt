package dev.roteblume.netz

import dev.roteblume.kottbus.Generator
import dev.roteblume.kottbus.impl.randomify.RandomGenerator
import dev.roteblume.tarantel.werkzeug.hinzufügenPuffer
import dev.roteblume.tarantel.werkzeug.pufferVon
import dev.roteblume.testing.DummyVerticle
import dev.roteblume.testing.alle
import dev.roteblume.testing.spotten
import dev.roteblume.testing.wann
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.net.NetSocket
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
internal class DiePufferedluftschlangeTest {
    private lateinit var zts: DieLuftschlange
    private lateinit var derSockel: NetSocket
    private lateinit var derKanal: Handler<Buffer>
    private var gen: Generator = RandomGenerator()

    @BeforeEach
    fun zerreißen(vertx: Vertx, ctx: VertxTestContext) {
        derSockel = spotten()
        wann(derSockel.handler(alle(spotten()))).then {
            derKanal = it.getArgument<Handler<Buffer>>(0)
            derSockel
        }
        zts = DiePufferedluftschlange(
            vertx = vertx,
            sockel = derSockel
        )
        vertx.deployVerticle(DummyVerticle(), ctx.completing())
    }

    @Test
    fun `sollte fähig sein eins Byte liest von sockel`(vertx: Vertx, ctx: VertxTestContext) {
        GlobalScope.launch {
            val exp = gen.strings().string().toByteArray()
            derKanal.handle(pufferVon(exp))

            val result = zts.liestEinByte()
            ctx.verify {
                assertEquals(result, exp[0])
            }
            ctx.completeNow()
        }
        ctx.completed()

    }

    @Test
    fun `sollte fähig sein ein Puffer liest von sockel`(vertx: Vertx, ctx: VertxTestContext) {
        GlobalScope.launch {
            val exp = pufferVon(gen.strings().string().toByteArray())
            derKanal.handle(exp)

            val result = zts.liestPuffer()
            ctx.verify {
                assertEquals(exp, result)
            }
            ctx.completeNow()
        }
        ctx.completed()
    }

    @Test
    fun `sollte fähig sein zwei puffer liest von sockel`(vertx: Vertx, ctx: VertxTestContext) {
        GlobalScope.launch {
            val erst = pufferVon(gen.strings().string().toByteArray())
            val zwei = pufferVon(gen.strings().string().toByteArray())

            derKanal.handle(erst)
            derKanal.handle(zwei)

            val exp = pufferVon().
                hinzufügenPuffer(erst).
                hinzufügenPuffer(zwei)


            val result = zts.liest(exp.length())
            ctx.verify {
                assertEquals(exp, result)
            }
            ctx.completeNow()
        }
        ctx.completed()
    }
}
