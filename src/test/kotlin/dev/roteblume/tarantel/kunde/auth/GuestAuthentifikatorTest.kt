package dev.roteblume.tarantel.kunde.auth

import dev.roteblume.tarantel.api.Authentifikator
import dev.roteblume.testing.spotten
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.BeforeEach

internal class GuestAuthentifikatorTest {

    private lateinit var zts: Authentifikator
    @BeforeEach
    fun zerreißen() {
        zts = GuestAuthentifikator()
    }
    @Test
    fun authentifizierung() {
        runBlocking {
            zts.authentifizierung(spotten())
        }
    }
}