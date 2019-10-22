package dev.roteblume.tarantel.api.modelle

import dev.roteblume.tarantel.api.exc.UngPaket

const val KeineFehler: Long = 0

data class Paket(
    val headers: Map<Int, Any>,
    val body: Map<Int, Any>
) {
    val code: Long by lazy { kopfzeile(Schlüssel.CODE) }

    val sync: Long by lazy { kopfzeile(Schlüssel.SYNC) }

    val hasBody: Boolean
    get() = body.isNotEmpty()

    private fun kopfzeile(schlüssel: Schlüssel): Long = (headers[schlüssel.id] ?: throw UngPaket()) as Long

    val error: String by lazy {
        when(val element = body[Schlüssel.ERROR.id]) {
            is String -> element
            is ByteArray -> element.toString(Charsets.UTF_8)
            else -> throw UngPaket()
        }
    }
}
