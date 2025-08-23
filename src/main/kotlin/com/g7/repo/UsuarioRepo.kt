package com.g7.repo

import com.g7.usuario.Usuario

interface UsuarioRepo {
    fun getUsuarios(): Set<Usuario>


}