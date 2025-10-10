package com.g7.evento

import com.g7.exception.InvalidConstructorException
import com.g7.repo.UsuarioRepo
import com.g7.serializable.LocalDateTimeSerializer
import com.g7.serializable.ObjectIdSerializer
import com.g7.usuario.UsuarioResponseDto
import com.g7.usuario.toResponseDto
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import java.time.LocalDateTime
import kotlin.compareTo
import kotlin.text.compareTo

@Serializable
data class EventoResponseDto (
    @Serializable(with = ObjectIdSerializer::class)
    val id: ObjectId,
    var organizador: UsuarioResponseDto,
    val titulo: String,
    val descripcion: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val inicio: LocalDateTime,
    val duracion: Long,
    val cupoMaximo: Int,
    val cupoMinimo: Int? = null,
    val precio: Float,
    val categorias: List<String> = ArrayList(),
    )

fun Evento.toDto(repo: UsuarioRepo): EventoResponseDto = EventoResponseDto(
    id = this.id,
    organizador = repo.getFromId(this.organizador).toResponseDto(),
    titulo = this.titulo,
    descripcion = this.descripcion,
    inicio = this.inicio,
    duracion = this.duracion,
    cupoMaximo = this.cupoMaximo,
    cupoMinimo = this.cupoMinimo,
    precio = this.precio,
    categorias = this.categorias
)

fun Evento.toDto(usuario: UsuarioResponseDto): EventoResponseDto = EventoResponseDto(
    id = this.id,
    organizador = usuario,
    titulo = this.titulo,
    descripcion = this.descripcion,
    inicio = this.inicio,
    duracion = this.duracion,
    cupoMaximo = this.cupoMaximo,
    cupoMinimo = this.cupoMinimo,
    precio = this.precio,
    categorias = this.categorias
)

@Serializable
data class EventoInputDto(
    val titulo: String,
    val descripcion: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val inicio: LocalDateTime,
    val duracion: Long,
    val cupoMaximo: Int,
    val cupoMinimo: Int? = null,
    val precio: Float,
    val categorias: List<String> = ArrayList(),
) {
    fun validate() {
        if(titulo.length !in 2..20)
            throw InvalidConstructorException("El titulo debe tener entre 2 y 20 caracteres")
        if (descripcion.length !in 2..100)
            throw InvalidConstructorException("La descripcion debe tener entre 2 y 100 caracteres")
        if (duracion <= 0)
            throw InvalidConstructorException("La duracion no ser menor a 0")
        if (cupoMinimo != null && cupoMinimo < 0)
            throw InvalidConstructorException("El cupo minimo no puede ser negativo")
        if (cupoMinimo != null && cupoMinimo > cupoMaximo)
            throw InvalidConstructorException("El cupo minimo no puede ser mayor al cupo maximo")
        if (cupoMaximo <= 0)
            throw InvalidConstructorException("El cupo maximo no puede ser menor a 0")
        if (inicio.isBefore(LocalDateTime.now()))
            throw InvalidConstructorException("La fecha de inicio no puede ser en el pasado")
        if (precio < 0)
            throw InvalidConstructorException("El precio no puede ser negativo")
        if (categorias.any { it.length !in 2..20 })
            throw InvalidConstructorException("Las categorias deben tener entre 2 y 20 caracteres")
        if (categorias.size > 5)
            throw InvalidConstructorException("No se pueden agregar mas de 5 categorias")
    }
}