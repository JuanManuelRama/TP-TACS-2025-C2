package com.g7.usuario

import com.g7.evento.Evento
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestUsuario {
    val usuario = Usuario(java.util.UUID.randomUUID(), "", "", UserType.PARTICIPANTE)
    val evento = mockk<Evento>(relaxed = true)

    @Test
    fun inscribirEvento() {
        val result = usuario.inscribir(evento)
        assertTrue(result.isSuccess)
        assertTrue(evento in usuario.inscripciones)
        assertTrue(usuario.esperas.isEmpty())
    }

    @Test
    fun noSePuedeInscribirDosVeces() {
        usuario.inscribir(evento)
        val result = usuario.inscribir(evento)
        assertTrue(result.isFailure)
        assertEquals(setOf(evento), usuario.inscripciones)
        assertTrue(usuario.esperas.isEmpty())
    }

    @Test
    fun inscribirEspera() {
        val result = usuario.inscribir(evento, espera = true)
        assertTrue(result.isSuccess)
        assertTrue(evento in usuario.esperas)
        assertTrue(usuario.inscripciones.isEmpty())
    }

    @Test
    fun noSePuedeCancelarEventoNoInscripto() {
        assertTrue(usuario.cancelarInscripcion(evento).isFailure)
    }

    @Test
    fun cancelarInscripcion() {
        usuario.inscribir(evento)
        val resultado = usuario.cancelarInscripcion(evento)
        assertTrue(resultado.isSuccess)
        assertTrue(usuario.inscripciones.isEmpty())
    }

    @Test
    fun cancelarEspera() {
        usuario.inscribir(evento, true)
        val resultado = usuario.cancelarInscripcion(evento)
        assertTrue(resultado.isSuccess)
        assertTrue(usuario.esperas.isEmpty())
    }
}