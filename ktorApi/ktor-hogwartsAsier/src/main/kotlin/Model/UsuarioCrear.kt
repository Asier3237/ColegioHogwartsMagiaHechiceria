package com.example.Model

import kotlinx.serialization.Serializable

@Serializable
data class UsuarioCrear(
    val nombre: String,
    val password: String,
    val casaId: Int,
    val rol: String,
    val nivel: Int,
    val experiencia: Int
)
