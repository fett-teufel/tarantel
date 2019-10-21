package dev.roteblume.tarantel.api

import dev.roteblume.tarantel.api.modelle.Schlüssel
import io.vertx.core.buffer.Buffer

interface DiePaketfabrik {
    fun erstellen(
        dieGröße: Int,
        dieKode: DieKode,
        syncId: Long,
        dasSchema: Long? = null,
        vararg args: Pair<Schlüssel, Any>
    ): Buffer
}

enum class DieKode(val id: Int) {
    SELEKT(1),
    EINFÜNGEN(2),
    AUSTAUSCHEN(3),
    AKTUALIZIREN(4),
    LÖSCHEN(5),
    ALTER_ANRUF(6),
    AUTH(7),
    AUSWERTEN(8),
    UPSERT(9),
    ANRUF(10),
    AUSFÜHREN(11),
    PENG(64),
    ABONNIEREN(66);
}
