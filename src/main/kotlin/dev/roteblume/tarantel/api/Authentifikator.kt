package dev.roteblume.tarantel.api

import io.vertx.core.buffer.Buffer
import io.vertx.core.net.NetSocket

interface Authentifikator {
    suspend fun authentifizierung(dasSalz: String, derSockel: NetSocket): Buffer
}
