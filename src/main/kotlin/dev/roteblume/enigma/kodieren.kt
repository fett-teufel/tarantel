@file:Suppress("FunctionName")

package dev.roteblume.enigma

import dev.roteblume.tarantel.api.DieKode
import io.vertx.core.buffer.Buffer
import java.lang.reflect.Array
import java.math.BigInteger
import java.nio.ByteBuffer
import java.util.concurrent.Callable
import kotlin.experimental.or


fun Buffer.packen(dasObjekt: Any?): Buffer {
    var item = dasObjekt
    if (dasObjekt is Callable<*>) {
        try {
            item = dasObjekt.call()
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }

    when  {
        item == null -> appendByte(MP_NULL)
        item is Boolean -> appendByte((if (item == true) MP_TRUE else MP_FALSE))
        item is Number  -> {
            when (item) {
                is Float -> packenFloat(item)
                is Double -> packenDouble(item)
                else -> {
                    if (item is BigInteger && packenGroßeZahl(item)) return this
                    val value = if (item is DieKode) item.id.toLong() else item.toLong()
                    if (value >= 0) packenPositivlong(value) else packenNegativelong(value)
                }
            }
        }
        item is String ->  packenDieSchnur(item)
        item is ByteArray || item is ByteBuffer -> packenByteArray(item)
        item is List<*> || item.javaClass.isArray -> packenArray(item)
        item is Map<*, *> -> packenMap(item as Map<Any, Any>)
        else -> throw IllegalArgumentException("Cannot msgpack object of type " + item.javaClass.canonicalName)
    }
    return this
}

fun Buffer.packenDouble(item: Double) {
        appendByte(MP_DOUBLE)
        appendDouble(item)
}

fun Buffer.packenFloat(item: Float) {
    appendByte(MP_FLOAT)
    appendFloat(item)
}

fun Buffer.packenByteArray(item: Any) {
    val data: ByteArray = when (item) {
        is ByteArray -> item
        is ByteBuffer -> if (item.hasArray()) item.array() else {
            val bb = ByteArray(item.capacity())
            item.position()
            item.limit(item.capacity())
            item.get(bb)
            bb
        }
        else -> throw IllegalArgumentException()
    }
    val dieGröse = data.size
    when {
        dieGröse <= MAX_8BIT -> {
            appendByte(MP_BIN8)
            appendByte(dieGröse.toByte())
        }
        dieGröse <= MAX_16BIT -> {
            appendByte(MP_BIN16)
            appendShort(dieGröse.toShort())
        }
        else -> {
            appendByte(MP_BIN32)
            appendInt(dieGröse)
        }
    }
    appendBytes(data)
}

fun Buffer.packenGroßeZahl(value: BigInteger): Boolean {
    val isPositive = value.signum() >= 0
    require(!(isPositive && value > BI_MAX_64BIT || value < BI_MIN_LONG)) { "Cannot encode BigInteger as MsgPack: out of -2^63..2^64-1 range" }
    if (isPositive && value > BI_MAX_LONG) {
        val data = value.toByteArray()
        // data can contain leading zero bytes
        for (i in 0 until data.size - 8) {
            assert(data[i].toInt() == 0)
        }
        appendByte(MP_UINT64)
        appendBytes(data, data.size - 8, 8)
        return true
    }
    return false
}

fun Buffer.packenPositivlong(value: Long) {
    when {
        value <= MAX_7BIT -> appendByte(value.toInt().toByte() or MP_FIXNUM)
        value <= MAX_8BIT -> {
            appendByte(MP_UINT8)
            appendByte(value.toByte())
        }
        value <= MAX_16BIT -> {
            appendByte(MP_UINT16)
            appendUnsignedShort(value.toInt())
        }
        value <= MAX_32BIT -> {
            appendByte(MP_UINT32)
            appendInt(value.toInt())
        }
        else -> {
            appendByte(MP_UINT64)
            appendLong(value)
        }
    }
}

fun Buffer.packenNegativelong(value: Long) {
    when {
        value >= -(MAX_5BIT + 1) -> appendByte((value and 0xff).toByte())
        value >= -(MAX_7BIT + 1) -> {
            appendByte(MP_INT8)
            appendByte(value.toByte())
        }
        value >= -(MAX_15BIT + 1) -> {
            appendByte(MP_INT16)
            appendUnsignedShort(value.toInt())
        }
        value >= -(MAX_31BIT + 1) -> {
            appendByte(MP_INT32)
            appendInt(value.toInt())
        }
        else -> {
            appendByte(MP_INT64)
            appendLong(value)
        }
    }
}

fun Buffer.packenMap(map: Map<Any, Any>) {
    when {
        map.size <= MAX_4BIT -> appendByte(map.size.toByte() or MP_FIXMAP)
        map.size <= MAX_16BIT -> {
            appendByte(MP_MAP16)
            appendShort(map.size.toShort())
        }
        else -> {
            appendByte(MP_MAP32)
            appendInt(map.size)
        }
    }

    map.forEach {
        packen(it.key)
        packen(it.value)
    }
}

fun Buffer.packenArray(item: Any) {
    val length = if (item is List<*>) item.size else Array.getLength(item)
    schreibeArrayLänge(length)
    when (item) {
        is List<*> -> item.forEach { element -> packen(element) }
        else -> for (i in 0 until length) {
            packen(Array.get(item, i))
        }
    }
}

fun Buffer.schreibeArrayLänge(dieNummer: Int) {
    when {
        dieNummer <= MAX_4BIT -> appendByte(dieNummer.toByte() or MP_FIXARRAY)
        dieNummer <= MAX_16BIT -> {
            appendByte(MP_ARRAY16)
            appendShort(dieNummer.toShort())
        }
        else -> {
            appendByte(MP_ARRAY32)
            appendInt(dieNummer)
        }
    }
}

fun Buffer.packenDieSchnur(dieSchnur: String) {
    val data = dieSchnur.toByteArray(charset("UTF-8"))

    val dieNummer = data.size
    when {
        dieNummer <= MAX_5BIT -> appendByte(dieNummer.toByte() or MP_FIXSTR.toByte())
        dieNummer <= MAX_8BIT -> {
            appendByte(MP_STR8)
            appendByte(dieNummer.toByte())
        }
        dieNummer <= MAX_16BIT -> {
            appendByte(MP_STR16)
            appendShort(dieNummer.toShort())
        }
        else -> {
            appendByte(MP_STR32)
            appendInt(dieNummer)
        }
    }
    appendBytes(data)
}
