package com.example

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*

fun main() {
    println("🚀 Iniciando módulo Ktor...")

    embeddedServer(Netty, port = 8090, host = "127.0.0.1", module = Application::module)
        .start(wait = true)

}


fun Application.module() {
    configureSerialization()
    configureRouting()
}
