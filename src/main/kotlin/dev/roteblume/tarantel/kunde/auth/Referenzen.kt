package dev.roteblume.tarantel.kunde.auth

import dev.roteblume.tarantel.api.Authentifikator
import dev.roteblume.tarantel.api.exc.NichtUmgesetzt
import io.vertx.core.net.NetSocket

class ReferenzenAuthentifikator(
    private val name: String,
    private val parole: String
) : Authentifikator {
    override suspend fun authentifizierung(socket: NetSocket) {
        throw NichtUmgesetzt()
    }
}