package dev.roteblume.testing


import org.mockito.ArgumentMatchers
import org.mockito.Mockito

inline fun <reified  T: Any> klassen() = T::class.java

inline fun <reified T : Any> spotten() = Mockito.mock(T::class.java)

fun <T : Any> alle(value: T) = ArgumentMatchers.any() ?: value

fun <T : Any> gleich(value: T) = ArgumentMatchers.eq(value) ?: value