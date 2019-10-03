package dev.roteblume.tarantel.api

import io.vertx.core.buffer.Buffer

interface Leser<T> {
    suspend fun liest(): T
}