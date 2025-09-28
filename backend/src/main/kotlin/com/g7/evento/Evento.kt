package com.g7.evento

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.LocalDateTime

@Serializable
enum class Categoria {
    FIESTA,
    CONCIERTO,
}

data class Evento (
    @param:BsonId val id: ObjectId = ObjectId(),
    val organizador: ObjectId,
    val titulo: String,
    val descripcion: String,
    val inicio: LocalDateTime,
    val duracion: Long,
    val cupoMaximo: Int,
    val cupoMinimio: Int?,
    val precio: Float,
    val categorias: List<Categoria>,
    val cantInscripciones: Long = 0,
    var cantEspera: Int = 0,
    var cantEsperaExitosas: Int = 0,
    var cantEsperaCancelada: Int = 0
) {

    fun porcentajeExito(): Float? = ratio(cantEsperaExitosas, cantEspera)

    fun porcentajeCancelacion(): Float? = ratio(cantEsperaCancelada, cantEspera)

    private fun ratio(part: Int, total: Int): Float? =
        if (total == 0) null else (part.toFloat() / total.toFloat()) * 100
}
