package dev.roteblume.tarantel.api.modelle

import dev.roteblume.tarantel.api.exc.UngPaket

data class Paket(
    val headers: Map<Int, Any>,
    val body: Map<Int, Any>
) {
    val code: Long by lazy { kopfzeile(Schlüssel.CODE) }

    val sync: Long by lazy { kopfzeile(Schlüssel.SYNC) }

    val hasBody: Boolean
    get() = body.isNotEmpty()

    private fun kopfzeile(schlüssel: Schlüssel): Long = (headers[schlüssel] ?: throw UngPaket()) as Long
}
