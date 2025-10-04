package com.g7.application.middleware

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS

fun Application.installCors() {
    install(CORS) {
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Delete)


        allowMethod(HttpMethod.Options) // important for preflight

        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)

        allowCredentials = true

        // For dev
        allowHost("localhost:5173", schemes = listOf("http"))
        //For prod
        allowHost("localhost:8081", schemes = listOf("http"))

        // In prod, you can add your real frontend domain:
        // allowHost("app.mydomain.com", schemes = listOf("https"))
    }
}