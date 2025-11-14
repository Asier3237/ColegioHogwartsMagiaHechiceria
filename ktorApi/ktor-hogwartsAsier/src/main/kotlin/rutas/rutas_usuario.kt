package routes

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import Model.Usuario
import Model.UsuarioLogin
import DAO.UsuarioDaoImpl
import io.ktor.http.HttpStatusCode

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
            call.respond(exito)
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

    }
}
