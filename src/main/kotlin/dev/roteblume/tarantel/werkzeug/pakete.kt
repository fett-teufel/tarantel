package dev.roteblume.tarantel.werkzeug

import dev.roteblume.enigma.DerEnigmafluss
import dev.roteblume.netz.DieLuftschlange
import dev.roteblume.tarantel.api.modelle.Paket
import io.vertx.core.buffer.Buffer
import io.vertx.core.net.NetSocket

suspend fun DerEnigmafluss.liestPaket(): Paket {
    val dieKopfzeile = auspacken() as Map<Int, Any>
    val derKörper = auspacken() as Map<Int, Any>
    val paket = Paket(dieKopfzeile, derKörper)
    return paket
}