package com.g7.evento

import com.g7.repo.UsuarioRepo
import com.g7.serializable.LocalDateTimeSerializer
import com.g7.serializable.ObjectIdSerializer
import com.g7.usuario.UsuarioResponseDto
import com.g7.usuario.toResponseDto
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import java.time.LocalDateTime

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
)