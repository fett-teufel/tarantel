@file:Suppress("FunctionName")

package dev.roteblume.enigma

import com.sun.org.apache.xpath.internal.operations.Bool
import dev.roteblume.tarantel.api.DieKode
import io.vertx.core.buffer.Buffer
import java.io.DataInputStream
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.Array
import java.math.BigInteger
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.HashMap
import java.util.concurrent.Callable
import kotlin.experimental.and
import kotlin.experimental.or

class Enigma : DerKodex {
    override fun pack(dasObjekt: Any?, out: Buffer) {
        out.packen(dasObjekt)
    }

    override fun unpack(stream: InputStream): Any? {
        val input = DataInputStream(stream)
        val value = input.read()
        require(value >= 0) { "No more input available when expecting a value" }

        when (value.toByte()) {
            MP_NULL -> return null
            MP_FALSE -> return false
            MP_TRUE -> return true
            MP_FLOAT -> return input.readFloat()
            MP_DOUBLE -> return input.readDouble()
            MP_UINT8 -> return input.read() // read single byte, return as int
            MP_UINT16 -> return input.readShort() and MAX_16BIT.toShort() // read short, trick Java into treating it as unsigned, return int
            MP_UINT32 -> return input.readInt() and MAX_32BIT.toInt() // read int, trick Java into treating it as unsigned, return long
            MP_UINT64 -> {
                val v = input.readLong()
                if (v >= 0) {
                    return v
                }
                // this is a little bit more tricky, since we don't have unsigned longs
                val bytes = byteArrayOf((v shr 56 and 0xff).toByte(), (v shr 48 and 0xff).toByte(), (v shr 40 and 0xff).toByte(), (v shr 32 and 0xff).toByte(), (v shr 24 and 0xff).toByte(), (v shr 16 and 0xff).toByte(), (v shr 8 and 0xff).toByte(), (v and 0xff).toByte())
                return BigInteger(1, bytes)
            }
            MP_INT8 -> return input.read().toByte()
            MP_INT16 -> return input.readShort()
            MP_INT32 -> return input.readInt()
            MP_INT64 -> return input.readLong()
            MP_ARRAY16 -> return unpackList(input.readShort().toInt() and MAX_16BIT, input)
            MP_ARRAY32 -> return unpackList(input.readInt(), input)
            MP_MAP16 -> return unpackMap(input.readShort().toInt() and MAX_16BIT, input)
            MP_MAP32 -> return unpackMap(input.readInt(), input)
            MP_STR8 -> return unpackStr(input.readByte().toInt() and MAX_8BIT, input)
            MP_STR16 -> return unpackStr(input.readShort().toInt() and MAX_16BIT, input)
            MP_STR32 -> return unpackStr(input.readInt(), input)
            MP_BIN8 -> return unpackBin(input.readByte().toInt() and MAX_8BIT, input)
            MP_BIN16 -> return unpackBin(input.readShort().toInt() and MAX_16BIT, input)
            MP_BIN32 -> return unpackBin(input.readInt(), input)
        }
        throw IllegalArgumentException()
    }

    private fun unpackList(size: Int, `in`: DataInputStream): List<*> {
        require(size >= 0) { "Array to unpack too large for Java (more than 2^31 elements)!" }
        val ret = ArrayList<Any?>(size)
        for (i in 0 until size) {
            ret.add(unpack(`in`))
        }
        return ret
    }

    private fun unpackMap(size: Int, `in`: DataInputStream): Map<*, *> {
        require(size >= 0) { "Map to unpack too large for Java (more than 2^31 elements)!" }
        val ret = HashMap<Any?, Any?>(size)
        for (i in 0 until size) {
            val key = unpack(`in`)
            val value = unpack(`in`)
            ret[key] = value
        }
        return ret
    }

    private fun unpackStr(size: Int, `in`: DataInputStream): Any {
        require(size >= 0) { "byte[] to unpack too large for Java (more than 2^31 elements)!" }

        val data = ByteArray(size)
        `in`.readFully(data)
        return String(data, charset("UTF-8"))
    }

    @Throws(IOException::class)
    private fun unpackBin(size: Int, `in`: DataInputStream): Any {
        require(size >= 0) { "byte[] to unpack too large for Java (more than 2^31 elements)!" }

        val data = ByteArray(size)
        `in`.readFully(data)
        return data
    }
}
