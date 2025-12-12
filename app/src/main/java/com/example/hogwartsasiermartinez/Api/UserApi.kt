package com.example.hogwartsasiermartinez.Api

import com.example.Model.Ingrediente
import com.example.Model.Pocima
import com.example.Model.PocimaCrear
import com.example.Model.UsuarioCrear
import com.example.hogwartsasiermartinez.model.Asignatura
import com.example.hogwartsasiermartinez.model.Casa
import com.example.hogwartsasiermartinez.model.Hechizo
import com.example.hogwartsasiermartinez.model.HechizoUsu
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

    @GET("/usuario/roles")
    suspend fun getRoles(): Response<List<String>>

    @PUT("usuario/{id}/rol")
    suspend fun cambiarRol(
        @Path("id") idUsuario: Int?,
        @Query("nuevoRol") nuevoRol: String
    ): Response<Unit>

    // --- Endpoint para que un Admin cree un Usuario ---
    @POST("usuario/crearUsuario")
    suspend fun adminCrearUsuario(@Body usuarioData: UsuarioCrear): Response<Unit>

    // --- Endpoint para obtener el Ranking de Casas ---
    @GET("casas/ranking")
    suspend fun getRankingCasas(): Response<List<Casa>>


    // --- Endpoints para Hechizos ---

    // Devuelve la lista de todos los hechizos desde el servidor
    @GET("/hechizos/listado")
    suspend fun getHechizos(): Response<List<Hechizo>>

    // Envía un objeto Hechizo completo para ser creado en el servidor
    @POST("/hechizos/crear")
    suspend fun crearHechizo(@Body hechizo: Hechizo): Response<Unit>

    // Envía los IDs del alumno y del hechizo para registrar el aprendizaje
    @POST("/hechizos/aprender")
    suspend fun aprenderHechizo(@Body request: HechizoUsu): Response<Unit>

    // --- Endpoint para Borrar un Hechizo ---
    @DELETE("/hechizos/borrar/{id}")
    suspend fun borrarHechizo(@Path("id") hechizoId: Int): Response<Unit>

    @GET("pociones/listadoRol")
    suspend fun getPociones(
        @Query("rol") rol: String,
        @Query("usuarioId") usuarioId: Int
    ): Response<List<Pocima>>

    // 2. Obtiene la lista de todos los ingredientes (esta ya estaba bien)
    @GET("pociones/ingredientes")
    suspend fun getIngredientes(): Response<List<Ingrediente>>

    // 3. Envía una nueva poción para ser creada (ruta corregida)
    @POST("pociones/crear")
    suspend fun crearPocion(@Body pocimaData: PocimaCrear): Response<Unit>

    // 4. Actualiza el estado de una poción (esta ya estaba bien)
    @PUT("pociones/{id}/validar")
    suspend fun validarPocion(
        @Path("id") pocionId: Int,
        @Query("estado") nuevoEstado: Int // 1 para validar, 2 para rechazar
    ): Response<Unit>

    // 5. Elimina una poción (ruta corregida)
    @DELETE("pociones/borrar/{id}")
    suspend fun borrarPocion(@Path("id") pocionId: Int): Response<Unit>

}


