package dev.roteblume.netz

import io.vertx.core.buffer.Buffer

interface DieLuftschlange {
    suspend fun liest(dieLange: Int): Buffer
    suspend fun liestPuffer(): Buffer
    suspend fun liestEinByte(): Byte
}