package com.example.hogwartsasiermartinez.model

import com.example.hogwartsasiermartinez.model.Usuario
import kotlinx.serialization.Serializable

@Serializable
data class UsuarioLogeado(
    val usuario: Usuario,
    val roles: List<String>,
    val colorCasa: String
)

