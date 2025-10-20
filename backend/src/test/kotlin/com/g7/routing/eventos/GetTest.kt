package com.g7.routing.eventos

import com.g7.BaseMongoTest
import com.g7.evento.Evento
import com.g7.evento.EventoResponseDto
import com.g7.repo.EventoRepo
import com.g7.repo.UsuarioRepo
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetTest: BaseMongoTest() {
    lateinit var evento1: Evento
    lateinit var evento2: Evento

    override fun populateTestData() {
        val user = UsuarioRepo.save(dataset.usuarios[0])
        evento1 = EventoRepo.save(user.id, dataset.eventos[0])
        evento2 = EventoRepo.save(user.id, dataset.eventos[1])
    }

    @Test
    fun getEventosNoParams() = withTestApp {
        client.get("/eventos").apply {
            assertEquals(HttpStatusCode.OK, status)
            val fetched = Json.decodeFromString<List<EventoResponseDto>>(bodyAsText())
            assertEquals(2, fetched.size, "Should return both eventos when no params")
            val titles = fetched.map { it.titulo }
            assertTrue(titles.containsAll(listOf(evento1.titulo, evento2.titulo)))
        }
    }

    @Test
    fun filterByCategory() = withTestApp {
        client.get("/eventos?category=CONCIERTO").apply {
            assertEquals(HttpStatusCode.OK, status)
            val fetched = Json.decodeFromString<List<EventoResponseDto>>(bodyAsText())
            assertEquals(1, fetched.size, "Only evento1 has category CONCIERTO")
            assertEquals(evento1.titulo, fetched[0].titulo)
        }
    }

    @Test
    fun filterByMinPrice() = withTestApp {
        client.get("/eventos?minPrice=1000").apply {
            assertEquals(HttpStatusCode.OK, status)
            val fetched = Json.decodeFromString<List<EventoResponseDto>>(bodyAsText())
            assertEquals(1, fetched.size, "Only evento1 should be returned for minPrice=1000")
            assertEquals(evento1.titulo, fetched[0].titulo)
        }
    }

    @Test
    fun paginationLimitAndPage() = withTestApp {
        client.get("/eventos?limit=1&page=2").apply {
            assertEquals(HttpStatusCode.OK, status)
            val fetched = Json.decodeFromString<List<EventoResponseDto>>(bodyAsText())
            assertEquals(1, fetched.size, "Should return exactly one evento for limit=1&page=2")
            assertEquals(evento2.titulo, fetched[0].titulo)
        }
    }

    @Test
    fun filterByKeyword() = withTestApp {
        client.get("/eventos?keywords=Rock,show").apply {
            assertEquals(HttpStatusCode.OK, status)
            val fetched = Json.decodeFromString<List<EventoResponseDto>>(bodyAsText())
            assertEquals(1, fetched.size, "Keywords ['Rock', 'show'] should match evento1")
            assertEquals(evento1.titulo, fetched[0].titulo)
        }
    }

}