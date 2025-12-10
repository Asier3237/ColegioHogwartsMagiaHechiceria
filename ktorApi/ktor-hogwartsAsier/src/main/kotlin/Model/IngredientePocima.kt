package com.example.Model

import kotlinx.serialization.Serializable

@Serializable
data class IngredientePocima(
    val ingredienteId: Int,
    val cantidad: Int
)