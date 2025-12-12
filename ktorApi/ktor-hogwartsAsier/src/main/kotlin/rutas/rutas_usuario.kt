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
import com.example.DAO.HechizoDaoImpl
import com.example.DAO.PocimasDaoImpl
import com.example.Model.Hechizo
import com.example.Model.HechizoUsu
import com.example.Model.PocimaCrear
import com.example.Model.UsuarioCrear
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

        post("/asignaturas/{asignaturaId}/profesor/{profesorId}") {

            val asignaturaId = call.parameters["asignaturaId"]?.toIntOrNull()
            val profesorId = call.parameters["profesorId"]?.toIntOrNull()

            if (asignaturaId == null || profesorId == null) {
                call.respond(HttpStatusCode.BadRequest, "Los IDs deben ser números enteros.")
                return@post
            }

            val exito = UsuarioDaoImpl.asignarProfesorAAsignatura(asignaturaId, profesorId)

            if (exito) {
                call.respond(HttpStatusCode.OK, "Profesor asignado correctamente.")
            } else {
                call.respond(HttpStatusCode.InternalServerError, "No se pudo completar la asignación en la base de datos.")
            }
        }

        get("/roles") {
            val listaDeRoles = UsuarioDaoImpl.getTodosLosRoles()
            if (listaDeRoles.isNotEmpty()) {
                call.respond(listaDeRoles)
            } else {
                call.respond(HttpStatusCode.NotFound, "No se encontraron roles en la base de datos.")
            }
        }

        put("/{id}/rol") {
            val usuarioId = call.parameters["id"]?.toIntOrNull()
            val nuevoRol = call.request.queryParameters["nuevoRol"]

            if (usuarioId == null || nuevoRol.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Falta el ID del usuario o el nuevo rol.")
                return@put
            }

            val exito = UsuarioDaoImpl.cambiarRolDeUsuario(usuarioId, nuevoRol)

            if (exito) {
                call.respond(HttpStatusCode.OK, "Rol actualizado correctamente.")
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Error al actualizar el rol en la base de datos.")
            }
        }

        post("/crearUsuario") {
            try {
                val datosUsuario = call.receive<UsuarioCrear>()
                val exito = UsuarioDaoImpl.crearUsuario(datosUsuario) // Usa tu DAO
                if (exito) {
                    call.respond(HttpStatusCode.Created, "Usuario creado correctamente.")
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Error al crear el usuario.")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Datos de usuario inválidos.")
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

        get("/ranking") {
            val rankingDeCasas = CasaDaoImpl.getCasasRanking()
            call.respond(rankingDeCasas)
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

    route("/hechizos"){
        get("/listado") {
            try {
                val listaDeHechizos = HechizoDaoImpl.getTodosLosHechizos() // Usa el nombre de tu DAO
                call.respond(listaDeHechizos)
            } catch (e: Exception){
                print("Error al obtener los hechizos: ${e.message}")
                call.respond(HttpStatusCode.InternalServerError, "Error interno: ${e.message}")
            }
        }

        post("/crear") {
            try {
                val hechizoNuevo = call.receive<Hechizo>()
                if (hechizoNuevo.nombre.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "El nombre no puede estar vacío.")
                    return@post
                }
                val exito = HechizoDaoImpl.crearHechizo(hechizoNuevo.nombre, hechizoNuevo.descripcion, hechizoNuevo.experiencia) // Usa tu DAO
                if (exito) {
                    call.respond(HttpStatusCode.Created, "Hechizo creado.")
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Error al guardar el hechizo.")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Datos del hechizo inválidos.")
            }
        }

        post("/aprender") {
            try {
                val datos = call.receive<HechizoUsu>() // Recibe alumnoId y hechizoId desde la app

                val exito = HechizoDaoImpl.aprenderHechizo(datos.alumnoId, datos.hechizoId)

                if (exito) {
                    call.respond(HttpStatusCode.OK, "Operación completada.")
                } else {
                    call.respond(HttpStatusCode.NotFound, "No se pudo encontrar el usuario o el hechizo.")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Datos inválidos.")
            }
        }

        delete("/borrar/{id}") {
            val hechizoId = call.parameters["id"]?.toIntOrNull()
            if (hechizoId == null) {
                call.respond(HttpStatusCode.BadRequest, "Falta el ID del hechizo.")
                return@delete
            }

            val exito = HechizoDaoImpl.borrarHechizo(hechizoId) // Usa el nombre de tu DAO

            if (exito) {
                call.respond(HttpStatusCode.OK, "Hechizo eliminado correctamente.")
            } else {
                call.respond(HttpStatusCode.NotFound, "No se encontró el hechizo a borrar.")
            }
        }

    }

    route("/pociones"){

        get("/listadoRol") {
            val rol = call.request.queryParameters["rol"] ?: ""
            val usuarioId = call.request.queryParameters["usuarioId"]?.toIntOrNull() ?: 0
            val pociones = PocimasDaoImpl.getPociones(rol, usuarioId)
            call.respond(pociones)
        }

        get("/ingredientes") {
            val ingredientes = PocimasDaoImpl.getIngredientes()
            call.respond(ingredientes)
        }

        post("/crear") {
            try {
                val pocimaData = call.receive<PocimaCrear>()
                val exito = PocimasDaoImpl.crearPocion(pocimaData)
                if (exito) call.respond(HttpStatusCode.Created, "Poción creada.")
                else call.respond(HttpStatusCode.InternalServerError, "Error al crear la poción.")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Datos inválidos.")
            }
        }

        put("/{id}/validar") {
            val pocionId = call.parameters["id"]?.toIntOrNull()
            val nuevoEstado = call.request.queryParameters["estado"]?.toIntOrNull()
            if (pocionId == null || nuevoEstado == null || nuevoEstado !in 0..2) {
                call.respond(HttpStatusCode.BadRequest, "Faltan datos o el estado es inválido.")
                return@put
            }
            val exito = PocimasDaoImpl.validarPocion(pocionId, nuevoEstado) // Usa tu DAO
            if (exito) call.respond(HttpStatusCode.OK, "Poción actualizada.")
            else call.respond(HttpStatusCode.InternalServerError, "Error al actualizar.")
        }

        delete("/borrar/{id}") {
            val pocionId = call.parameters["id"]?.toIntOrNull()
            if (pocionId == null) {
                call.respond(HttpStatusCode.BadRequest, "Falta el ID.")
                return@delete
            }
            val exito = PocimasDaoImpl.borrarPocion(pocionId)
            if (exito) call.respond(HttpStatusCode.OK, "Poción eliminada.")
            else call.respond(HttpStatusCode.NotFound, "Poción no encontrada.")
        }

    }

}


