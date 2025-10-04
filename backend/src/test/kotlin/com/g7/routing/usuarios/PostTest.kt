package com.g7.routing.usuarios

import com.g7.BaseMongoTest
import com.g7.repo.UsuarioRepo
import com.g7.usuario.dto.UsuarioResponseDto
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class PostTest: BaseMongoTest() {

    @Test
    fun createUser() = withTestApp {
        val user = dataset.usuarios[0]
        client.post("/usuarios") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(user))
        }.apply {
            assertEquals(HttpStatusCode.Created, status)
            val createdUser = Json.decodeFromString<UsuarioResponseDto>(bodyAsText())
            assertEquals(createdUser.username, user.username, "Returned username should match input")

            val fetchedFromDb = UsuarioRepo.getFromId(createdUser.id)
            assertEquals(user.username, fetchedFromDb.username, "DB username should match input")
            assertEquals(createdUser.id, fetchedFromDb.id, "DB id should match response id")
            assertNotEquals((user.password), fetchedFromDb.password, "Stored password should be hashed")
        }
    }

    @Test
    fun createUserConflict() = withTestApp {
        val user = dataset.usuarios[0]
        client.post("/usuarios") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(user))
        }
        client.post("/usuarios") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(user))
        }.apply {
            assertEquals(HttpStatusCode.Conflict, status)
            assertEquals(UsuarioRepo.getUsuarios().size, 1, "Only one user gets created")
        }
    }

    @Test
    fun createUserInvalidData() = withTestApp {
        client.post("/usuarios") {
            contentType(ContentType.Application.Json)
            setBody("{}") // Empty JSON
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, status)
            assertEquals(UsuarioRepo.getUsuarios().size, 0, "No user gets created")
        }
        client.post("/usuarios") {
            contentType(ContentType.Application.Json)
            setBody("{\"username\": \"onlyusername\"}") // Missing password
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, status)
            assertEquals(UsuarioRepo.getUsuarios().size, 0, "No user gets created")
        }
        client.post("/usuarios") {
            contentType(ContentType.Application.Json)
            setBody("{\"password\": \"onlypassword\"}") // Missing username
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, status)
            assertEquals(UsuarioRepo.getUsuarios().size, 0, "No user gets created")
        }
    }
}