package dev.roteblume.tarantel.werkzeug

import dev.roteblume.enigma.packen
import dev.roteblume.kottbus.Generator
import dev.roteblume.kottbus.impl.randomify.RandomGenerator
import dev.roteblume.tarantel.api.DieKode
import dev.roteblume.tarantel.api.modelle.Schlüssel
import dev.roteblume.testing.alle
import dev.roteblume.testing.spotten
import dev.roteblume.testing.wann
import io.vertx.core.Handler
import io.vertx.core.buffer.Buffer
import io.vertx.core.net.NetSocket
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class PuffersKtTest {
    private val gen: Generator = RandomGenerator()

    private val derKopfzeile = mapOf(
        Schlüssel.CODE.id to gen.strings().string(),
        Schlüssel.USER_NAME.id to gen.strings().string()
    )
    private val derKörper = mapOf(
        DieKode.AUSWERTEN.id to gen.id().serial(),
        DieKode.ABONNIEREN.id to gen.strings().string()
    )

    private lateinit var zts: Buffer

    private lateinit var derSockel: NetSocket

    @BeforeEach
    fun zerreißen() {
        val derKopfzeilepuffer = pufferVon().packen(derKopfzeile)
        val derKörperpuffer = pufferVon().packen(derKörper)
        val lange = derKopfzeilepuffer.length() + derKörperpuffer.length()
        zts = pufferVon().packen(derKopfzeilepuffer.length() + derKörperpuffer.length())
            .appendBuffer(derKopfzeilepuffer)
            .appendBuffer(derKörperpuffer)
        derSockel = spotten()
    }

    @Test
    fun `correct divide puffer`() {
        val zts = neuPufferAusSchnur(gen.strings().string())
        val geteiltDurch = zts.length() / 2
        val paar = zts.teilen(geteiltDurch)
        assertEquals(zts.getBuffer(0, geteiltDurch).toString(), paar.first.toString())
        assertEquals(zts.getBuffer(geteiltDurch, zts.length()).toString(), paar.second.toString())
    }

    @Test
    fun `sollte fähig sein richtig lest paket von puffer`() {
        val derHehler: Handler<Buffer> = spotten()
        runBlocking {
            wann(derSockel.handler(alle(derHehler))).then {
                println("callen")
                it.getArgument<Handler<Buffer>>(0).handle(zts)
                derSockel
            }

            val paar = zts.liestPaket(derSockel)
            assertEquals(paar.second.headers, derKopfzeile)
            assertEquals(paar.second.body, derKörper)
        }
    }
}