package dev.roteblume.netz

import dev.roteblume.tarantel.werkzeug.pufferVon
import dev.roteblume.tarantel.werkzeug.teilen
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.net.NetSocket
import io.vertx.core.streams.ReadStream
import io.vertx.kotlin.coroutines.ReceiveChannelHandler
import io.vertx.kotlin.coroutines.receiveChannelHandler
import io.vertx.kotlin.coroutines.toChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel

class DiePufferedluftschlange(
    vertx: Vertx,
    sockel: NetSocket,
    private var puffered: Buffer = pufferVon()
) : DieLuftschlange {

    private val channel: ReceiveChannel<Buffer> = (sockel as ReadStream<Buffer>).toChannel(vertx)

    override suspend fun liest(dieLange: Int): Buffer {
        val ch = Channel<String>()
        ch.
        while (puffered.length() < dieLange) puffered.appendBuffer(channel.receive())
        val result = puffered.slice(0, dieLange)
        puffered = puffered.slice(dieLange, puffered.length())
        return result
    }

    override suspend fun liestPuffer(): Buffer {
        while (puffered.length() == 0) puffered.appendBuffer(channel.receive())
        val result = puffered
        puffered = pufferVon()
        return result
    }

    override suspend fun liestEinByte(): Byte {
        return liest(1).getByte(0)
    }
}