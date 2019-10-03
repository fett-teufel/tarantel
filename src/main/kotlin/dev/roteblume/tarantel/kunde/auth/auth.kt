package dev.roteblume.tarantel.kunde.auth

fun authentifikatorVon(name: String = "", parole: String = "") =
    if (name.isEmpty() && parole.isEmpty()) GuestAuthentifikator() else ReferenzenAuthentifikator(name, parole)
