package com.example.hogwartsasiermartinez.Api

import com.example.hogwartsasiermartinez.model.Usuario
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface UserAPI {

    @GET("usuario/listado")
    suspend fun getUsuarios(): Response<List<Usuario>>

    @GET("usuario/{id}")
    suspend fun getUsuario(@Path("id") id: Int): Response<Usuario>

    @POST("usuario/registrar")
    suspend fun addUsuario(@Body usuario: Usuario): Response<Boolean>

    @PUT("usuario/modificar/{id}")
    suspend fun updateUsuario(@Path("id") id: Int, @Body usuario: Usuario): Response<Boolean>

    @DELETE("usuario/borrar/{id}")
    suspend fun deleteUsuario(@Path("id") id: Int): Response<Boolean>
}

