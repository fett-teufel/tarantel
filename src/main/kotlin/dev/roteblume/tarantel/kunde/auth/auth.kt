package dev.roteblume.tarantel.kunde.auth

import dev.roteblume.tarantel.api.Authentifikator
import dev.roteblume.tarantel.api.DiePaketfabrik
import dev.roteblume.tarantel.kunde.protokoll.DasPaketwerk

fun authentifikatorVon(
    name: String = "",
    parole: String = "",
    dasPaketwerk: DiePaketfabrik = DasPaketwerk()
): Authentifikator {
    if (name.isEmpty() && parole.isEmpty()) return GuestAuthentifikator()
    return ReferenzenAuthentifikator(name, parole, dasPaketwerk)
}
