package routes

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import Model.Usuario
import Model.UsuarioLogin
import DAO.UsuarioDaoImpl
import com.example.DAO.AsignaturaDaoImpl
import com.example.DAO.CasaDaoImpl
import com.example.Service.HouseService
import io.ktor.http.HttpStatusCode
import java.security.Provider

fun Route.rutas_usuario() {

    route("/usuario") {

        get("/listado") {
            val usuarios = UsuarioDaoImpl.obtenerTodos()
            call.respond(usuarios)
        }

        get("/buscar") {
            val nombre = call.request.queryParameters["nombre"]
            if (nombre != null) {
                val resultado = UsuarioDaoImpl.buscarPorNombre(nombre)
                call.respond(resultado)
            } else {
                call.respond(emptyList<Usuario>())
            }
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id != null) {
                val usuario = UsuarioDaoImpl.obtenerPorId(id)
                if (usuario != null) {
                    call.respond(usuario)
                } else {
                    call.respondText("Usuario no encontrado", status = io.ktor.http.HttpStatusCode.NotFound)
                }
            }
        }

        post("/registrar") {
            val nuevo = call.receive<Usuario>()
            val exito = UsuarioDaoImpl.registrar(nuevo)

            if (exito) {
                call.respond(HttpStatusCode.Created, true)
            } else {
                call.respond(HttpStatusCode.Conflict, false)
            }
        }

        put("/modificar/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            val actualizado = call.receive<Usuario>()
            val exito = if (id != null) UsuarioDaoImpl.actualizar(id, actualizado) else false
            call.respond(exito)
        }

        delete("/borrar/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            val exito = if (id != null) UsuarioDaoImpl.borrar(id) else false
            call.respond(exito)
        }

        post("/login") {
            val datos = call.receive<UsuarioLogin>()
            val usuario = UsuarioDaoImpl.login(datos.nombre, datos.pwd)
            if (usuario != null) {
                call.respond(usuario)
            } else {
                call.respondText("Credenciales inválidas", status = HttpStatusCode.Unauthorized)
            }
        }

        post("/selectHouse") {
            val preferencias = call.receive<List<Int>>()
            val casaId = HouseService().selectHouse(preferencias)
            println("Casa asignada: $casaId")
            call.respond(casaId)
        }

        get("/profesores"){
            try {
                val profesores = UsuarioDaoImpl.listadoProfesores()
                call.respond(profesores)
            }catch (e: Exception){
                println("Error al obtener profesores: ${e.message}")
                call.respond(HttpStatusCode.InternalServerError, "Error interno: ${e.message}")
            }
        }

        // En tu archivo de rutas de Ktor, dentro del bloque routing { ... }

// --- CÓDIGO FINAL Y CORRECTO PARA LA RUTA ---
        post("/asignaturas/{asignaturaId}/profesor/{profesorId}") {
            // Extraemos los IDs de la URL
            val asignaturaId = call.parameters["asignaturaId"]?.toIntOrNull()
            val profesorId = call.parameters["profesorId"]?.toIntOrNull()

            // Comprobamos que los IDs son válidos
            if (asignaturaId == null || profesorId == null) {
                call.respond(HttpStatusCode.BadRequest, "Los IDs deben ser números enteros.")
                return@post
            }

            // Llamamos a la nueva función del DAO
            val exito = UsuarioDaoImpl.asignarProfesorAAsignatura(asignaturaId, profesorId)

            if (exito) {
                // ¡Éxito! El DAO confirmó que la operación en la BD funcionó.
                call.respond(HttpStatusCode.OK, "Profesor asignado correctamente.")
            } else {
                // El DAO devolvió false, indicando un error en la base de datos.
                call.respond(HttpStatusCode.InternalServerError, "No se pudo completar la asignación en la base de datos.")
            }
        }


    }

    route("/casas"){

        get("/listado") {
            try {
                val casas = CasaDaoImpl.obtenerTodas()
                println("Casas obtenidas: $casas")
                call.respond(casas)
            } catch (e: Exception) {
                println("Error al obtener casas: ${e.message}")
                call.respond(HttpStatusCode.InternalServerError, "Error interno: ${e.message}")
            }
        }


    }

    route("/asignaturas"){
        get("/listado"){
            try {
                val asignaturas = AsignaturaDaoImpl.obtenerTodas()
                call.respond(asignaturas)
            }catch (e: Exception){
                print("Error al obtener las asignaturas: ${e.message}")
                call.respond(HttpStatusCode.InternalServerError, "Error interno: ${e.message}")
            }
        }
    }

}


