package com.example.hogwartsasiermartinez.Api

import com.example.hogwartsasiermartinez.model.Asignatura
import com.example.hogwartsasiermartinez.model.Casa
import com.example.hogwartsasiermartinez.model.Usuario
import com.example.hogwartsasiermartinez.model.UsuarioLogeado
import com.example.hogwartsasiermartinez.model.UsuarioLogin
import retrofit2.Response
import retrofit2.http.*

interface UserAPI {

    @GET("usuario/listado")
    suspend fun getUsuarios(): Response<List<Usuario>>

    @GET("usuario/{id}")
    suspend fun getUsuario(@Path("id") id: Int): Response<Usuario>

    @POST("usuario/login")
    suspend fun login(@Body datos: UsuarioLogin): Response<UsuarioLogeado>

    @POST("usuario/registrar")
    suspend fun addUsuario(@Body usuario: Usuario): Response<Boolean>

    @POST("usuario/selectHouse")
    suspend fun selectHouse(@Body preferencias: List<Int>): Response<Int>

    @PUT("usuario/modificar/{id}")
    suspend fun updateUsuario(@Path("id") id: Int, @Body usuario: Usuario): Response<Boolean>

    @DELETE("usuario/borrar/{id}")
    suspend fun deleteUsuario(@Path("id") id: Int?): Response<Boolean>

    @GET("casas/listado")
    suspend fun getCasas(): Response<List<Casa>>

    @GET("asignaturas/listado")
    suspend fun getAsignaturas(): Response<List<Asignatura>>

    @GET("usuario/profesores")
    suspend fun getProfesores(): Response<List<Usuario>>

    @POST("usuario/asignaturas/{asignaturaId}/profesor/{profesorId}")
    suspend fun asignarProfesor(
        @Path("asignaturaId") asignaturaId: Int,
        @Path("profesorId") profesorId: Int
    ): Response<Unit>

}


