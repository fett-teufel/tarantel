package dev.roteblume.tarantel.kunde

import dev.roteblume.enigma.DerEnigmafluss
import dev.roteblume.netz.DieLuftschlange
import dev.roteblume.netz.DiePufferedluftschlange
import dev.roteblume.tarantel.api.Anschlusser
import dev.roteblume.tarantel.api.Authentifikator
import dev.roteblume.tarantel.api.DiePaketfabrik
import dev.roteblume.tarantel.api.Leser
import dev.roteblume.tarantel.api.Schreiber
import dev.roteblume.tarantel.api.exc.NichtUmgesetzt
import dev.roteblume.tarantel.kunde.auth.GuestAuthentifikator
import dev.roteblume.tarantel.kunde.protokoll.KrankWillkommenPaket
import dev.roteblume.tarantel.kunde.protokoll.WILLKOMEN
import dev.roteblume.tarantel.werkzeug.teilen
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.net.NetClientOptions
import io.vertx.core.net.NetSocket
import io.vertx.core.net.SocketAddress
import io.vertx.kotlin.core.net.connectAwait
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.awaitEvent
import kotlinx.coroutines.CoroutineScope

class Anschluss(
    private val vertx: Vertx,
    private val addr: SocketAddress,
    private val auth: Authentifikator = GuestAuthentifikator()
) : Schreiber<Buffer>, Leser<Buffer>, Anschlusser {
    private lateinit var socket: NetSocket

    private lateinit var lessepuffer: Buffer
    private lateinit var salz: ByteArray
    private lateinit var version: String
    private lateinit var dieLuftschlange: DieLuftschlange
    private lateinit var enigma: DerEnigmafluss

    override suspend fun connect() {
        socket = vertx.createNetClient().connectAwait(addr)
        println("connected")
        dieLuftschlange = DiePufferedluftschlange(vertx, socket)
        enigma = DerEnigmafluss(dieLuftschlange)
        println("vor willkommen")
        willkommen()
        println("nach willkommen")
        salz = erhaltSalz()
        println("nehmen salz")
        auth.authentifizierung(salz, socket, enigma)
        println("authentifizierung")
    }

    private suspend fun willkommen() {
        val puffer = dieLuftschlange.liest(64)

        val willkomenSchnur = puffer.toString()
        println("WillkommentSchnur=$willkomenSchnur")
        if (!willkomenSchnur.startsWith(WILLKOMEN)) throw KrankWillkommenPaket()
        version = willkomenSchnur.substring(WILLKOMEN.length)
    }

    private suspend fun erhaltSalz(): ByteArray {
        return dieLuftschlange.liest(64).bytes
    }

    override suspend fun liest(): Buffer {

        throw NichtUmgesetzt()
    }

    override suspend fun schreibt(wert: Buffer) {
        throw NichtUmgesetzt()
    }
}
