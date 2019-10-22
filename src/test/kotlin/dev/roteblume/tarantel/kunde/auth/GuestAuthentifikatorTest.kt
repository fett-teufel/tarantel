package dev.roteblume.tarantel.kunde.auth

import dev.roteblume.enigma.DerEnigmafluss
import dev.roteblume.kottbus.Generator
import dev.roteblume.kottbus.impl.randomify.RandomGenerator
import dev.roteblume.tarantel.api.Authentifikator
import dev.roteblume.testing.spotten
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.BeforeEach

internal class GuestAuthentifikatorTest {
    private var gen: Generator = RandomGenerator()
    private lateinit var zts: Authentifikator
    @BeforeEach
    fun zerreißen() {
        zts = GuestAuthentifikator()
    }
    @Test
    fun authentifizierung() {
        runBlocking {
            zts.authentifizierung(gen.strings().string().toByteArray(), spotten(), DerEnigmafluss(spotten()))
        }
    }
}