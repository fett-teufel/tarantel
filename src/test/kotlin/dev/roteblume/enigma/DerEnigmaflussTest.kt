package dev.roteblume.enigma

import com.nhaarman.mockitokotlin2.eq
import dev.roteblume.kottbus.Generator
import dev.roteblume.kottbus.Strings
import dev.roteblume.kottbus.impl.randomify.RandomGenerator
import dev.roteblume.netz.DieLuftschlange
import dev.roteblume.tarantel.werkzeug.pufferVon
import dev.roteblume.testing.alle
import dev.roteblume.testing.long
import dev.roteblume.testing.spotten
import dev.roteblume.testing.wann
import dev.roteblume.testing.array
import dev.roteblume.testing.dieKarte
import io.vertx.core.buffer.Buffer
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.stubbing.OngoingStubbing
import java.util.concurrent.atomic.AtomicInteger

internal class DerEnigmaflussTest {
    private lateinit var zts: DerEnigmafluss
    private lateinit var luftschlange: DieLuftschlange
    private lateinit var exp: Buffer
    private val gen: Generator = RandomGenerator()

    @BeforeEach
    fun zerreißen() {
        luftschlange = spotten()
        zts = DerEnigmafluss(luftschlange)
        exp = pufferVon()
    }

    @Test
    fun `sollte fähig sein null auspacken`() {
        runBlocking {
            wann(luftschlange.liestEinByte()).then { MP_NULL }
            assertNull(zts.auspacken())
        }
    }

    @Test
    fun `sollte fähig sein FALSE auspacken`() {
        runBlocking {
            wann(luftschlange.liestEinByte()).then { MP_FALSE }
            val result = zts.auspacken() as Boolean
            assertFalse { result }
        }
    }

    @Test
    fun `sollte fähig sein TRUE auspacken`() {
        runBlocking {
            wann(luftschlange.liestEinByte()).then { MP_TRUE }
            val result = zts.auspacken() as Boolean
            assertTrue { result }
        }
    }

    @Test
    fun `sollte fähig sein float auspacken`() {
        runBlocking {
            val orig = gen.primitive().float()
            exp.packenFloat(orig)
            assertEquals(exp.getByte(0), MP_FLOAT)
            assertEquals(exp.length(), 5)

            wann(luftschlange.liestEinByte()).then { exp.getByte(0) }
            wann(luftschlange.liest(eq(4))).then { exp.slice(1, 5) }

            val result = zts.auspacken() as Float
            assertEquals(orig, result)
        }
    }

    @Test
    fun `sollte fähig sein double auspacken`() {
        runBlocking {
            val orig = gen.primitive().double()
            exp.packenDouble(orig)

            wann(luftschlange.liestEinByte()).then { exp.getByte(0) }
            wann(luftschlange.liest(eq(8))).then { exp.slice(1, 9) }

            val result = zts.auspacken() as Double
            assertEquals(orig, result)
        }
    }

    @Test
    fun `sollte fähig sein int auspacken`() {
        runBlocking {
            val orig = gen.primitive().int(0, 10)
            exp.packen(orig)
            var i = 0
            wann(luftschlange.liestEinByte()).then {
                val res = exp.getByte(i)
                i++
                res
            }

            val result = zts.auspacken() as Int
            assertEquals(orig, result)
        }
    }

    @Test
    fun `sollte fähig sein Int8bit auspacken`() {
        runBlocking {
            val orig = gen.primitive().int(MAX_8BIT - 10, MAX_8BIT - 1)
            exp.packen(orig)
            var i = 0
            wann(luftschlange.liestEinByte()).then {
                val res = exp.getByte(i)
                i++
                res
            }

            val result = zts.auspacken() as Int
            assertEquals(orig, result)
        }
    }

    @Test
    fun `sollte fähig sein unsignert kurz zu auspacken`() {
        runBlocking {
            val orig = gen.primitive().int(MAX_16BIT - 10, MAX_16BIT - 1)
            exp.packen(orig)
            wann(luftschlange.liestEinByte()).then { exp.getByte(0) }
            wann(luftschlange.liest(2)).then { exp.slice(1, 3) }

            val result = zts.auspacken()
            assertEquals(orig, (result as Int).toInt())
        }
    }

    @Test
    fun `sollte fähig sein 8byte Integer zu auspacken`() {
        runBlocking {
            val orig = gen.primitive().int(-(MAX_7BIT + 1), -(MAX_7BIT) + 10)
            exp.packen(orig)
            var i = 0
            wann(luftschlange.liestEinByte()).then { exp.getByte(i++) }

            val result = zts.auspacken()
            assertEquals(orig, (result as Int).toInt())
        }
    }

    @Test
    fun `sollte fähig sein 16 byte integer zu auspacken`() {
        runBlocking {
            val orig = gen.primitive().int(-(MAX_15BIT + 1), -(MAX_15BIT) + 10)
            exp.packen(orig)
            wann(luftschlange.liestEinByte()).then { exp.getByte(0) }
            wann(luftschlange.liest(2)).then { exp.slice(1, 3) }

            val dasResultat = (zts.auspacken() as Short).toInt()
            assertEquals(orig, dasResultat)
        }
    }

    @Test
    fun `sollte fähig sein 64 byte integer zu auspacken`() {
        runBlocking {
            val orig = gen.primitive().long(-(MAX_31BIT).toLong(), (-(MAX_31BIT) + 10).toLong())
            exp.packen(orig)
            wann(luftschlange.liestEinByte()).then { exp.getByte(0) }
            wann(luftschlange.liest(4)).then { exp.slice(1, 5) }

            val dasResultat = (zts.auspacken() as Int).toLong()
            assertEquals(orig, dasResultat)
        }
    }

    @Test
    fun `sollte fähig sein eine Schnur zu auspacken`() {
        val orig = gen.strings().string(10)
        exp.packen(orig)
        runBlocking {
            wann(luftschlange.liestEinByte()).then { exp.getByte(0) }
            wann(luftschlange.liest(eq(exp.length() - 1))).then { exp.slice(1, exp.length()) }
            val dasResultat = zts.auspacken() as String
            assertEquals(orig, dasResultat)
        }
    }

    @Test
    fun `sollte fähig sein eine kurz Schnur zu auspacken`() {
        val orig = gen.strings().string(MAX_5BIT + 1)
        exp.packen(orig)
        runBlocking {
            val index = AtomicInteger(0)
            wannLiestEinByte(index, luftschlange.liestEinByte(), exp)
            wann(luftschlange.liest(eq(exp.length() - 2))).then { exp.slice(2, exp.length()) }
            val dasResultat = zts.auspacken() as String
            assertEquals(orig, dasResultat)
        }
    }

    @Test
    fun `sollte fähig sein eine lange Schnur zu auspacken`() {
        val orig = gen.strings().string(MAX_8BIT + 1)
        val index = AtomicInteger(0)

        exp.packen(orig)
        runBlocking {
            wannLiestEinByte(index, luftschlange.liestEinByte(), exp)
            wann(luftschlange.liest(eq(2))).then { exp.slice(1, 3) }
            wann(luftschlange.liest(eq(exp.length() - 3))).then { exp.slice(3, exp.length()) }
            val dasResultat = zts.auspacken() as String
            assertEquals(orig, dasResultat)
        }
    }

    @Test
    fun `sollte fähig sein array mit die Schnure zu auspacken`() {
        val orig = gen.strings().array(10).toList()
        exp.packen(orig)
        val index = AtomicInteger(0)

        runBlocking {
            wannLiestEinByte(index, luftschlange.liestEinByte(), exp)
            wannLiest(index, luftschlange.liest(alle(0)), exp)

            val dasResultat = zts.auspacken() as List<String>
            assertEquals(orig.size, dasResultat.size)
            repeat(dasResultat.size) {
                assertEquals(orig[it].length, dasResultat[it].length, it.toString())
            }
        }
    }

    @Test
    fun `sollte fähig sein 16bit länge array mit integers zu auspacken`() {
        val orig = gen.strings().array(MAX_16BIT).toList()
        exp.packen(orig)
        val index = AtomicInteger(0)

        runBlocking {
            wannLiestEinByte(index, luftschlange.liestEinByte(), exp)
            wannLiest(index, luftschlange.liest(alle(0)), exp)

            val dasResultat = zts.auspacken() as List<String>
            assertEquals(orig.size, dasResultat.size)
            repeat(dasResultat.size) {
                assertEquals(orig[it].length, dasResultat[it].length, it.toString())
            }
        }
    }

    @Test
    fun `sollte fähig sein 32bit länge array mit die Schnurs zu auspacken`() {
        val orig = gen.strings().array(MAX_16BIT + 1).toList()
        exp.packen(orig)
        val index = AtomicInteger(0)

        runBlocking {
            wannLiestEinByte(index, luftschlange.liestEinByte(), exp)
            wannLiest(index, luftschlange.liest(alle(0)), exp)

            val dasResultat = zts.auspacken() as List<String>
            assertEquals(orig.size, dasResultat.size)
            repeat(dasResultat.size) {
                assertEquals(orig[it].length, dasResultat[it].length, it.toString())
            }
        }
    }

    @Test
    fun `sollte fähig sein klein map mit die Schnurs zu auspacken`() {
        val orig = gen.strings().dieKarte(10)
        exp.packen(orig)
        val index = AtomicInteger(0)
        runBlocking {
            wannLiestEinByte(index, luftschlange.liestEinByte(), exp)
            wannLiest(index, luftschlange.liest(alle(0)), exp)

            val dasResultat = zts.auspacken() as Map<String, String>
            assertEquals(orig.size, dasResultat.size)
            dasResultat.entries.forEach {
                assertTrue(orig.containsKey(it.key))
                assertEquals(orig[it.key], it.value)
            }
        }
    }

    @Test
    fun `sollte fähig sein map mit die Schnurs zu auspacken`() {
        val orig = gen.strings().dieKarte(MAX_16BIT)
        exp.packen(orig)
        val index = AtomicInteger(0)
        runBlocking {
            wannLiestEinByte(index, luftschlange.liestEinByte(), exp)
            wannLiest(index, luftschlange.liest(alle(0)), exp)

            val dasResultat = zts.auspacken() as Map<String, String>
            assertEquals(orig.size, dasResultat.size)
            dasResultat.entries.forEach {
                assertTrue(orig.containsKey(it.key))
                assertEquals(orig[it.key], it.value)
            }
        }
    }

    @Test
    fun `sollte fähig sein große map mit die Schnurs zu auspacken`() {
        val orig = gen.strings().dieKarte(MAX_16BIT + 1)
        exp.packen(orig)
        val index = AtomicInteger(0)
        runBlocking {
            wannLiestEinByte(index, luftschlange.liestEinByte(), exp)
            wannLiest(index, luftschlange.liest(alle(0)), exp)

            val dasResultat = zts.auspacken() as Map<String, String>
            assertEquals(orig.size, dasResultat.size)
            dasResultat.entries.forEach {
                assertTrue(orig.containsKey(it.key))
                assertEquals(orig[it.key], it.value)
            }
        }
    }

    @Test
    fun `sollte fähig sein klein byte liste zu auspacken`() {
        val orig = gen.strings().string(5)
        exp.packen(orig.toByteArray())
        val index = AtomicInteger(0)
        runBlocking {
            wannLiestEinByte(index, luftschlange.liestEinByte(), exp)
            wannLiest(index, luftschlange.liest(alle(0)), exp)

            val dasResultat = zts.auspacken() as ByteArray
            assertEquals(orig, dasResultat.toString(Charsets.UTF_8))
        }
    }

    @Test
    fun `sollte fähig sein byte liste zu auspacken`() {
        val orig = gen.strings().string(MAX_8BIT + 1)
        exp.packen(orig.toByteArray())
        val index = AtomicInteger(0)
        runBlocking {
            wannLiestEinByte(index, luftschlange.liestEinByte(), exp)
            wannLiest(index, luftschlange.liest(alle(0)), exp)

            val dasResultat = zts.auspacken() as ByteArray
            assertEquals(orig, dasResultat.toString(Charsets.UTF_8))
        }
    }

    @Test
    fun `sollte fähig sein große byte liste zu auspacken`() {
        val orig = gen.strings().string(MAX_16BIT + 1)
        exp.packen(orig.toByteArray())
        val index = AtomicInteger(0)
        runBlocking {
            wannLiestEinByte(index, luftschlange.liestEinByte(), exp)
            wannLiest(index, luftschlange.liest(alle(0)), exp)

            val dasResultat = zts.auspacken() as ByteArray
            assertEquals(orig, dasResultat.toString(Charsets.UTF_8))
        }
    }

}

fun <T> wannLiestEinByte(index: AtomicInteger, methodCall: T, puffer: Buffer): OngoingStubbing<T> {
    return wann(methodCall).then {
        val res = puffer.getByte(index.getAndIncrement())
        res
    }
}

fun <T> wannLiest(index: AtomicInteger, methodCall: T, puffer: Buffer): OngoingStubbing<T> {
    return wann(methodCall).then { invocation ->
        val dieLänge = invocation.getArgument<Int>(0)
        val von = index.get()
        puffer.slice(von, index.addAndGet(dieLänge))
    }
}
