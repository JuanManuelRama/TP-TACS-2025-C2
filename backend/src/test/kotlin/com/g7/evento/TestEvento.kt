package com.g7.evento

import com.g7.usuario.UserType
import com.g7.usuario.Usuario
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.toKotlinDuration

class TestEvento {
    val evento = Evento(
        id = UUID.randomUUID(),
        organizador = usuario(),
        titulo = "Rolling Stones",
        descripcion = "Estadio Monumental",
        inicio = LocalDateTime.now(),
        duracion = java.time.Duration.ofMinutes(30).toKotlinDuration(),
        cupoMaximo = 1,
        cupoMinimio = 2,
        precio = 0.toFloat(),
        categorias = listOf()
    )

    @Test
    fun inscribeAlUsuario() {
        val usuario = usuario()
        val resultado = evento.inscribir(usuario)
        assertTrue(resultado.isSuccess)
        assertContains(evento.inscriptos.map { it.usuario }, usuario)
        assertContains(usuario.inscripciones, evento)
        assertTrue(evento.enEspera.isEmpty())
        assertTrue(usuario.esperas.isEmpty())
    }

    @Test
    fun seLlena() {
        val usuario = usuario()
        assertFalse(evento.isFull())
        evento.inscribir(usuario)
        assertTrue(evento.isFull())
    }

    @Test
    fun siNoHayCupoLoDejaEnEspera() {
        val usuario = usuario()
        evento.inscribir(usuario)
        val otroUsuario =  usuario()
        val resultado = evento.inscribir(otroUsuario)
        assertTrue(resultado.isSuccess)
        assertContains(evento.enEspera.map { it.usuario }, otroUsuario)
        assertContains(otroUsuario.esperas, evento)
        assertFalse(otroUsuario in evento.inscriptos.map { it.usuario })
        assertFalse(evento in otroUsuario.inscripciones)
    }

    @Test
    fun cancelarEliminaInscripcion() {
        val usuario = usuario()
        evento.inscribir(usuario)
        val result = evento.cancelar(usuario)
        assertTrue(result.isSuccess)
        assertFalse(evento.isFull())
        assertTrue(evento.inscriptos.isEmpty())
        assertTrue(usuario.inscripciones.isEmpty())
    }

    @Test
    fun cancelarLiberaCupo() {
        val usuario = usuario()
        evento.inscribir(usuario)
        val otroUsuario = usuario()
        evento.inscribir(otroUsuario)
        evento.cancelar(usuario)
        assertContains(evento.inscriptos.map { it.usuario }, otroUsuario)
        assertTrue(evento.isFull())
        assertTrue(evento.enEspera.isEmpty())
        assertContains(otroUsuario.inscripciones, evento)
        assertTrue(otroUsuario.esperas.isEmpty())
    }

    @Test
    fun noSePuedeInscribirUnInscripto() {
        val usuario = usuario()
        val result = evento.inscribir(usuario)
        assertTrue(result.isSuccess)
        assertTrue { evento.inscribir(usuario).isFailure }
        assertEquals(evento.inscriptos.map { it.usuario }, listOf(usuario))
        assertTrue(evento.enEspera.isEmpty())
        assertEquals(usuario.inscripciones, setOf(evento))
        assertTrue(usuario.esperas.isEmpty()
        )
    }

    @Test
    fun seRespetaElOrdenDeEspera() {
        val usuario = usuario()
        evento.inscribir(usuario)
        val otroUsuario = usuario()
        evento.inscribir(otroUsuario)
        val tercerUsuario = usuario()
        evento.inscribir(tercerUsuario)
        evento.cancelar(usuario)
        assertContains(evento.inscriptos.map { it.usuario }, otroUsuario)
        assertContains(evento.enEspera.map { it.usuario }, tercerUsuario)
    }

    private fun usuario() = Usuario(UUID.randomUUID(), "", "", UserType.PARTICIPANTE)}
