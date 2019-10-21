package dev.roteblume.netz

suspend fun DieLuftschlange.liestFloat() = liest(4).getFloat(0)

suspend fun DieLuftschlange.liestDouble() = liest(8).getDouble(0)

suspend fun DieLuftschlange.liestUnsigniertKurz() = liest(2).getUnsignedShort(0)

suspend fun DieLuftschlange.liestKurz() = liest(2).getShort(0)

suspend fun DieLuftschlange.liestInt() = liest(4).getInt(0)

suspend fun DieLuftschlange.liestLong() = liest(8).getLong(0)