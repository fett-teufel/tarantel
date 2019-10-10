package dev.roteblume.tarantel.werkzeug

import dev.roteblume.tarantel.api.exc.NichtUmgesetzt
import dev.roteblume.tarantel.api.modelle.Paket
import io.vertx.core.buffer.Buffer
import io.vertx.core.net.NetSocket
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.awaitEvent

fun Buffer.teilen(position: Int): Pair<Buffer, Buffer> {
    val erste = slice(0, position)
    val zwite = slice(position, length())
    return Pair(erste, zwite)
}

fun pufferVon(): Buffer = Buffer.buffer()

fun pufferVon(initGröße: Int): Buffer = Buffer.buffer(initGröße)

fun pufferVon(dieDaten: ByteArray): Buffer = Buffer.buffer(dieDaten)

fun neuPufferAusSchnur(schnur: String): Buffer = Buffer.buffer(schnur)

suspend fun Buffer.liestAus(derSockel: NetSocket, dieLänge: Int) = also {
    println("dieLänge=$dieLänge und lange=${length()}")
    while (length() < dieLänge) appendBuffer(awaitEvent<Buffer> { derSockel.handler(it) })
}

suspend fun Buffer.liestEineNummerAus(derSockel: NetSocket): Pair<Int, Buffer> {
    val paar = liestAus(derSockel, 1).teilen(1)
    val eineNummer = paar.first.dekodierenDerNummer()
    return paarVon(eineNummer, paar.second)
}

suspend fun Buffer.anschribt(derSockel: NetSocket) {
    derSockel.write(this).await()
}

suspend fun Buffer.liestPaket(derSockel: NetSocket): Pair<Buffer,Paket> {
    val paar = liestEineNummerAus(derSockel)
    println("Länge: ${paar.first}")
    paar.second.liestAus(derSockel, paar.first)
    var puffers = paar.second.teilen(paar.first.toInt())
    val puffer = puffers.second // das ist ein Byteschwanz

    val dieKopfzeile = puffers.first.dekodierenEnObject() as Map<Int, Any>

    puffers = puffers.first.teilenObjekt()
    val derKörper = puffers.first.dekodierenEnObject() as Map<Int, Any>
    val paket = Paket(dieKopfzeile, derKörper)
    return paarVon(puffer, paket)
}

private fun Buffer.dekodierenEnObject(): Any? {
    throw NichtUmgesetzt()
}

fun Buffer.teilenObjekt(): Pair<Buffer, Buffer> {
    val dieGröße = dekodierenDerNummer()
    return teilen(dieGröße + 1)
}

private fun Buffer.dekodierenDerNummer(): Int {
    throw NichtUmgesetzt()
}

fun Buffer.hinzufügenPuffer(puffer: Buffer): Buffer = appendBuffer(puffer)
