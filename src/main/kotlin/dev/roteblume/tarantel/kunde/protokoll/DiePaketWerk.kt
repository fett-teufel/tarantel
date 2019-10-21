package dev.roteblume.tarantel.kunde.protokoll

import dev.roteblume.enigma.packen
import dev.roteblume.tarantel.api.DieKode
import dev.roteblume.tarantel.api.DiePaketfabrik
import dev.roteblume.tarantel.api.modelle.Schlüssel
import dev.roteblume.tarantel.werkzeug.pufferVon
import io.vertx.core.buffer.Buffer
import java.util.EnumMap

class DasPaketwerk(
) : DiePaketfabrik {
    override fun erstellen(
        dieGröße: Int,
        dieKode: DieKode,
        syncId: Long, dasSchema: Long?,
        vararg args: Pair<Schlüssel, Any>
    ): Buffer {
        val puffer = pufferVon(dieGröße)
        puffer.appendBytes(ByteArray(5))

        val dieKopfzeile = dasSchema?.let {
            mapOf(Schlüssel.CODE to dieKode, Schlüssel.SYNC to syncId, Schlüssel.SCHEMA_ID to it)
        }?: mapOf(Schlüssel.CODE to dieKode, Schlüssel.SYNC to syncId)

        val derKörper = EnumMap<Schlüssel, Any>(Schlüssel::class.java)
        args.forEach { derKörper[it.first] = it.second }
        puffer.packen(dieKopfzeile)
        puffer.packen(derKörper)
        puffer.setByte(0, 0xce.toByte())
        puffer.setInt(1, puffer.length())
        return puffer
    }
}