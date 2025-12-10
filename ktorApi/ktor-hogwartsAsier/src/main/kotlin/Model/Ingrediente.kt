package com.example.Model

import kotlinx.serialization.Serializable

@Serializable
data class Ingrediente(
    val id: Int,
    val nombre: String,
    val analgesia: Int,
    val curativo: Int,
    val desinflamatorio: Int,
    val sanacion: Int
)
