package com.example.hogwartsasiermartinez.model

import kotlinx.serialization.Serializable

@Serializable
data class Asignatura(
    val id : Int? = null,
    val nombre : String
)
