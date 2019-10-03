package dev.roteblume.tarantel.werkzeug

import io.vertx.core.buffer.Buffer

fun Buffer.teilen(position: Int): Pair<Buffer, Buffer> {
    val erste = slice(0, position)
    val zwite = slice(position, length())
    return Pair(erste, zwite)
}

fun neuPuffer(): Buffer = Buffer.buffer()

fun neuPufferAusSchnur(schnur: String): Buffer = Buffer.buffer(schnur)