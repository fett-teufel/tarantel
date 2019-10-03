package dev.roteblume.tarantel.kunde.auth

import dev.roteblume.tarantel.api.Authentifikator
import io.vertx.core.net.NetSocket

class GuestAuthentifikator : Authentifikator {
    override suspend fun authentifizierung(socket: NetSocket) = Unit
}
