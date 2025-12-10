package com.example.Model

import kotlinx.serialization.Serializable


@Serializable
data class Hechizo(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val experiencia: Int
)



