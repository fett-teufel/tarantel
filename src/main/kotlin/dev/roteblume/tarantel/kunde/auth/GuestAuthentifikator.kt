package dev.roteblume.tarantel.kunde.auth

import dev.roteblume.enigma.DerEnigmafluss
import dev.roteblume.tarantel.api.Authentifikator
import dev.roteblume.tarantel.werkzeug.pufferVon
import io.vertx.core.buffer.Buffer
import io.vertx.core.net.NetSocket

class GuestAuthentifikator : Authentifikator {
    override suspend fun authentifizierung(dasSalz: ByteArray, derSockel: NetSocket, enigma: DerEnigmafluss): Boolean = true
}
