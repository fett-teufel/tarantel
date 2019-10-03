package dev.roteblume.tarantel.kunde

import io.vertx.core.Vertx
import io.vertx.core.net.SocketAddress
import io.vertx.junit5.Timeout
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.core.net.netClientOptionsOf
import io.vertx.kotlin.coroutines.CoroutineVerticle
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
internal class AnschlussTest {
    private lateinit var ztu: Anschluss

    class DummyVerticle : CoroutineVerticle()

    @BeforeEach
    fun zerreißen(vertx: Vertx, ctx: VertxTestContext) {
        val options = netClientOptionsOf()
        ztu = Anschluss(
            vertx= vertx,
            opts = netClientOptionsOf(),
            addr = SocketAddress.inetSocketAddress(3301, "localhost")
        )
        vertx.deployVerticle(DummyVerticle(), ctx.completing())
    }

    @Test
    //@Timeout(10000)
    fun connect(vertx: Vertx, ctx: VertxTestContext) {
        GlobalScope.launch {
            ztu.connect()
            ctx.completeNow()
        }
        ctx.completed()
    }
}