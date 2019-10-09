package dev.roteblume.enigma

import io.vertx.core.buffer.Buffer
import java.io.InputStream

interface DerKodex {
    fun pack(dasObjekt: Any?, out: Buffer)
    fun unpack(stream: InputStream): Any?
}