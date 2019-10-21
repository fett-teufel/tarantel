package dev.roteblume.tarantel.kunde.auth

import dev.roteblume.tarantel.api.Authentifikator
import dev.roteblume.tarantel.api.DieKode
import dev.roteblume.tarantel.api.DiePaketfabrik
import dev.roteblume.tarantel.api.modelle.Schlüssel
import dev.roteblume.tarantel.kunde.protokoll.DEFAULT_INITIAL_REQUEST_SIZE
import dev.roteblume.tarantel.kunde.protokoll.KeinSchema
import dev.roteblume.tarantel.werkzeug.anschribt
import dev.roteblume.tarantel.werkzeug.liestPaket
import dev.roteblume.tarantel.werkzeug.paarVon
import dev.roteblume.tarantel.werkzeug.pufferVon
import io.vertx.core.buffer.Buffer
import io.vertx.core.net.NetSocket
import java.security.MessageDigest
import java.util.Base64
import kotlin.experimental.xor

class ReferenzenAuthentifikator(
    private val name: String,
    private val parole: String,
    private val einPaketfabrik: DiePaketfabrik
    ) : Authentifikator {
    override suspend fun authentifizierung(dasSalz: String, derSockel: NetSocket): Buffer {
        val sha1 = MessageDigest.getInstance("SHA-1")
        val gehaschenDieParole = sha1.digest(parole.toByteArray())
        val dieWertmarke = sha1.rückzetsen().verdauen(gehaschenDieParole)
        val dasGerangel = sha1.rückzetsen()
            .aktualiziren(Base64.getDecoder().decode(dasSalz), 20, 0)
            .aktualiziren(gehaschenDieParole)
            .verdauen()
        (0..20).forEach { gehaschenDieParole[it] = gehaschenDieParole[it] xor dasGerangel[it] }
        val auth = listOf("chap-sha1", gehaschenDieParole)
        val derAntrag = einPaketfabrik.erstellen(
            DEFAULT_INITIAL_REQUEST_SIZE,
            DieKode.AUTH,
            0L,
            KeinSchema,
            paarVon(Schlüssel.USER_NAME, name),
            paarVon(Schlüssel.TUPLE, auth)
        )
        derAntrag.anschribt(derSockel)

        val paar = pufferVon().liestPaket(derSockel)
        paar.second
        return paar.first
    }
}

private fun MessageDigest.aktualiziren(schnur: ByteArray) = apply {
    update(schnur)
}

private fun MessageDigest.aktualiziren(schnur: ByteArray, len: Int, offset: Int) = apply {
    update(schnur)
}

@Suppress("FunctionName")
private fun MessageDigest.rückzetsen() = apply {
    reset()
}

@Suppress("FunctionName")
private fun MessageDigest.verdauen(schnur: ByteArray) = digest(schnur)

@Suppress("FunctionName")
private fun MessageDigest.verdauen() = digest()