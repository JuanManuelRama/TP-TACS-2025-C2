package com.g7.application.middleware

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.request.path
import org.slf4j.event.Level

fun Application.installLogging() {
    install(CallLogging) {
        level = Level.INFO
        filter { it.request.path().startsWith("/") }
    }
}