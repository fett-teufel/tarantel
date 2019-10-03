package dev.roteblume.tarantel.api

import io.vertx.core.buffer.Buffer

interface Schreiber<T> {
    suspend fun schreibt(wert: T)
}
