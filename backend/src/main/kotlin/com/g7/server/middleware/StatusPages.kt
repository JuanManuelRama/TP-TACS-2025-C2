package com.g7.server.middleware

import com.g7.exception.*
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.uri
import io.ktor.server.response.respond

fun Application.configureExceptions() {
    install(StatusPages) {
        exception <IllegalStateException> { call, cause ->
            call.respond(HttpStatusCode.Conflict, mapOf("error" to (cause.message ?: "Conflict error")))
        }
        exception <NoSuchElementException> { call, cause ->
            call.respond(HttpStatusCode.NotFound, mapOf("error" to (cause.message ?: "Not found")))
        }
        exception <MissingParameterException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to cause.message))
        }
        exception <InvalidCredentialsException> { call, cause ->
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to cause.message))
        }
        exception <InvalidIdException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to cause.message))
        }
        exception <IllegalAccessException> { call, cause ->
            call.respond(HttpStatusCode.Forbidden, mapOf("error" to (cause.message ?: "Forbidden")))
        }
        exception <Throwable> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "internal server error"))
            call.application.environment.log.error("Internal server error on ${call.request.uri}", cause)
        }

    }
}