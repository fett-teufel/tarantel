package dev.roteblume.tarantel.api

import io.vertx.core.net.NetSocket

interface Authentifikator {
    suspend fun authentifizierung(socket: NetSocket)
}
