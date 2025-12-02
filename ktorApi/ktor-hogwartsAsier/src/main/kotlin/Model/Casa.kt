package com.example.Model

import kotlinx.serialization.Serializable

@Serializable
data class Casa(
    val id: Int,
    val nombre: String,
    val puntos: Int
)
