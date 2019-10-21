package dev.roteblume.enigma

import dev.roteblume.netz.DieLuftschlange
import dev.roteblume.netz.liestDouble
import dev.roteblume.netz.liestFloat
import dev.roteblume.netz.liestInt
import dev.roteblume.netz.liestKurz
import dev.roteblume.netz.liestUnsigniertKurz
import dev.roteblume.netz.liestLong
import dev.roteblume.netz.liestUnsigniertKurz
import java.math.BigInteger
import java.util.ArrayList
import java.util.HashMap

class DerEnigmafluss(
    private val luftschlange: DieLuftschlange
) {
    suspend fun auspacken(): Any? {
        val klass = luftschlange.liestEinByte()
        return when (klass) {
            MP_NULL ->  null
            MP_FALSE ->  false
            MP_TRUE ->  true
            MP_FLOAT ->  luftschlange.liestFloat()
            MP_DOUBLE -> luftschlange.liestDouble()
            MP_UINT8 -> luftschlange.liestEinByte().toInt().asUint()
            MP_UINT16 -> luftschlange.liestUnsigniertKurz()
            MP_UINT32 -> luftschlange.liestInt() and MAX_32BIT.toInt()
            MP_INT8 -> luftschlange.liestEinByte().toInt()
            MP_INT16 -> luftschlange.liestKurz()
            MP_INT32 -> luftschlange.liestInt()
            MP_INT64 -> luftschlange.liestLong()
            MP_UINT64 -> {
                val v = luftschlange.liestLong()
                if (v >= 0) {
                    return v
                }
                // this is a little bit more tricky, since we don't have unsigned longs
                val bytes = byteArrayOf((v shr 56 and 0xff).toByte(), (v shr 48 and 0xff).toByte(), (v shr 40 and 0xff).toByte(), (v shr 32 and 0xff).toByte(), (v shr 24 and 0xff).toByte(), (v shr 16 and 0xff).toByte(), (v shr 8 and 0xff).toByte(), (v and 0xff).toByte())
                return BigInteger(1, bytes)
            }
            MP_ARRAY16 -> auspackenDieListe(luftschlange.liestUnsigniertKurz())
            MP_ARRAY32 -> auspackenDieListe(luftschlange.liestInt())
            MP_MAP16 -> auspackenDieKarte(luftschlange.liestUnsigniertKurz() and MAX_16BIT)
            MP_MAP32 -> auspackenDieKarte(luftschlange.liestInt())
            MP_STR8 -> auspackenDieSchnur(luftschlange.liestEinByte().toInt() and MAX_8BIT)
            MP_STR16 -> auspackenDieSchnur(luftschlange.liestUnsigniertKurz())
            MP_STR32 -> auspackenDieSchnur(luftschlange.liestInt())
            MP_BIN8 -> auspackenDieByteliste( luftschlange.liestEinByte().toInt() and MAX_8BIT)
            MP_BIN16 -> auspackenDieByteliste(luftschlange.liestUnsigniertKurz() and MAX_16BIT)
            MP_BIN32 -> auspackenDieByteliste(luftschlange.liestInt())
            else -> {
                val uint = klass.toInt().asUint()

                return if (uint >= MP_NEGATIVE_FIXNUM_INT && uint <= MP_NEGATIVE_FIXNUM_INT + MAX_5BIT) {
                    klass.toInt()
                } else if (uint >= MP_FIXARRAY_INT && uint <= MP_FIXARRAY_INT + MAX_4BIT) {
                    auspackenDieListe(uint - MP_FIXARRAY_INT)
                } else if (uint >= MP_FIXMAP_INT && uint <= MP_FIXMAP_INT + MAX_4BIT) {
                    auspackenDieKarte(uint - MP_FIXMAP_INT)
                } else if (uint >= MP_FIXSTR_INT && uint <= MP_FIXSTR_INT + MAX_5BIT) {
                    auspackenDieSchnur(uint - MP_FIXSTR_INT)
                } else if (uint <= MAX_7BIT) {
                    // MP_FIXNUM - the value is value as an int
                    klass.toInt()
                } else {
                    throw IllegalArgumentException("Input contains invalid type value $klass")
                }
            }
        }
    }

    private suspend fun auspackenDieListe(dieGröse: Int): ArrayList<Any?> {
        require(dieGröse >= 0) { "Array to unpack too large for Java (more than 2^31 elements)!" }
        val ret = ArrayList<Any?>(dieGröse)
        repeat(dieGröse) { ret.add(auspacken()) }
        return ret
    }

    private suspend fun auspackenDieByteliste(dieGröse: Int): ByteArray {
        require(dieGröse >= 0) { "byte[] to unpack too large for Java (more than 2^31 elements)!" }
        return luftschlange.liest(dieGröse).bytes
    }

    private suspend fun auspackenDieKarte(dieGröse: Int): Map<Any?, Any?> {
        require(dieGröse >= 0) { "Map to unpack too large for Java (more than 2^31 elements)!" }
        val ret = HashMap<Any?, Any?>(dieGröse)
        repeat(dieGröse) { ret[auspacken()] = auspacken() }
        return ret
    }

    private suspend fun auspackenDieSchnur(dieGröse: Int): String {
        require(dieGröse >= 0) { "byte[] to unpack too large for Java (more than 2^31 elements)!" }
        return String(luftschlange.liest(dieGröse).bytes, charset("UTF-8"))
    }
}

fun Int.asUint(): Int {
    if (this < 0) return 255 + this  + 1
    return this
}

