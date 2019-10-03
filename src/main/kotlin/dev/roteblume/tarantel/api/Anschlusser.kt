package dev.roteblume.tarantel.api

interface Anschlusser {
    suspend fun connect()
}