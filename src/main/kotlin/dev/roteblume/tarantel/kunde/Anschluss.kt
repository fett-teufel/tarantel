package dev.roteblume.tarantel.kunde

import dev.roteblume.tarantel.api.Anschlusser
import dev.roteblume.tarantel.api.Authentifikator
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
import io.vertx.kotlin.coroutines.awaitEvent

class Anschluss(
    private val vertx: Vertx,
    private val opts: NetClientOptions,
    private val addr: SocketAddress,
    private val auth: Authentifikator = GuestAuthentifikator()
) : Schreiber<Buffer>, Leser<Buffer>, Anschlusser {
    private lateinit var socket: NetSocket

    private lateinit var lessepuffer: Buffer
    private lateinit var salz: String
    private lateinit var version: String

    override suspend fun connect() {
        socket = vertx.createNetClient().connectAwait(addr)
        lessepuffer = Buffer.buffer()
        willkommen()
        salz = erhaltSalz()
        println("version: ´$version´")
        println("salz: ´salz´")
    }

    private suspend fun willkommen() {
        val puffer = liestN(64)
        val willkomenSchnur = puffer.toString()
        if (!willkomenSchnur.startsWith(WILLKOMEN)) throw KrankWillkommenPaket()
        version = willkomenSchnur.substring(WILLKOMEN.length)
    }

    private suspend fun erhaltSalz(): String {
        return liestN(64).toString()
    }

    private suspend fun liestN(Länge: Int): Buffer {
        while (lessepuffer.length() < Länge) {
            lessepuffer.appendBuffer(awaitEvent<Buffer> { socket.handler(it) })
        }
        val paar = lessepuffer.teilen(Länge)
        lessepuffer = paar.second
        return paar.first
    }

    private suspend fun authentifizierung() {
        auth.authentifizierung(socket)
    }

    override suspend fun liest(): Buffer {

        throw NichtUmgesetzt()
    }

    override suspend fun schreibt(wert: Buffer) {
        throw NichtUmgesetzt()
    }
}
