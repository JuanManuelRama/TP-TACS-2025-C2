package com.g7.routing.eventos

import io.ktor.server.application.*

data class EventoParams(
    val keywords: List<String>? = null,
    val category: String? = null,
    val maxPrice: Double? = null,
    val minPrice: Double? = null,
    val limit: Int = 10,
    val page: Int = 1
)

fun ApplicationCall.parseEventoParams(): EventoParams {
    val category = parameters["category"]
    val keywords = request.queryParameters["keywords"]?.split(",")
    val maxPrice = request.queryParameters["maxPrice"]?.toDoubleOrNull()
    val minPrice = request.queryParameters["minPrice"]?.toDoubleOrNull()

    var limit = request.queryParameters["limit"]?.toIntOrNull() ?: 10

    if(limit == 0 || limit > 20) {
        limit = 10
    }

    var page = request.queryParameters["page"]?.toIntOrNull() ?: 1
    if (page == 0) {
        page = 1
    }

    return EventoParams(keywords, category, maxPrice, minPrice, limit, page)
}