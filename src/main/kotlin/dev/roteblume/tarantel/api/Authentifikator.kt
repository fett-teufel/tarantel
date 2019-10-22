package dev.roteblume.tarantel.api

import dev.roteblume.enigma.DerEnigmafluss
import dev.roteblume.netz.DieLuftschlange
import dev.roteblume.tarantel.api.modelle.Paket
import io.vertx.core.buffer.Buffer
import io.vertx.core.net.NetSocket

interface Authentifikator {
    suspend fun authentifizierung(
        dasSalz: ByteArray,
        derSockel: NetSocket,
        enigma: DerEnigmafluss): Boolean
}
