package DAO

import Database.Conexion
import Model.Usuario
import java.sql.ResultSet

object UsuarioDaoImpl {

    fun obtenerTodos(): List<Usuario> {
        val lista = mutableListOf<Usuario>()
        val query = "SELECT * FROM usuario"
        val statement = Conexion.connection.prepareStatement(query)
        val result = statement.executeQuery()

        while (result.next()) {
            lista.add(result.toUsuario())
        }

        return lista
    }

    fun insertar(usuario: Usuario): Boolean {
        val query = """
            INSERT INTO usuario (nombre, password, experiencia, nivel, fk_casa_jad)
            VALUES (?, ?, ?, ?, ?)
        """.trimIndent()

        val statement = Conexion.connection.prepareStatement(query)
        statement.setString(1, usuario.nombre)
        statement.setString(2, usuario.password)
        statement.setInt(3, usuario.experiencia)
        statement.setInt(4, usuario.nivel)
        statement.setInt(5, usuario.casa_id)

        return statement.executeUpdate() > 0
    }

    private fun ResultSet.toUsuario(): Usuario = Usuario(
        id = getInt("id"),
        nombre = getString("nombre"),
        password = getString("password"),
        experiencia = getInt("experiencia"),
        nivel = getInt("nivel"),
        casa_id = getInt("casa_id")
    )

    fun obtenerPorId(id: Int): Usuario? {
        val query = "SELECT * FROM usuario WHERE id = ?"
        val statement = Conexion.connection.prepareStatement(query)
        statement.setInt(1, id)
        val result = statement.executeQuery()

        return if (result.next()) result.toUsuario() else null
    }

    fun buscarPorNombre(nombre: String): List<Usuario> {
        val lista = mutableListOf<Usuario>()
        val query = "SELECT * FROM usuario WHERE nombre LIKE ?"
        val statement = Conexion.connection.prepareStatement(query)
        statement.setString(1, "%$nombre%")
        val result = statement.executeQuery()

        while (result.next()) {
            lista.add(result.toUsuario())
        }

        return lista
    }

    fun actualizar(id: Int, usuario: Usuario): Boolean {
        val query = """
        UPDATE usuario SET 
            nombre = ?, 
            password = ?, 
            experiencia = ?, 
            nivel = ?, 
            fk_casa_jad = ?
        WHERE id = ?
    """.trimIndent()

        val statement = Conexion.connection.prepareStatement(query)
        statement.setString(1, usuario.nombre)
        statement.setString(2, usuario.password)
        statement.setInt(3, usuario.experiencia)
        statement.setInt(4, usuario.nivel)
        statement.setInt(5, usuario.casa_id)
        statement.setInt(6, id)

        return statement.executeUpdate() > 0
    }

    fun borrar(id: Int): Boolean {
        val query = "DELETE FROM usuario WHERE id = ?"
        val statement = Conexion.connection.prepareStatement(query)
        statement.setInt(1, id)
        return statement.executeUpdate() > 0
    }

    fun login(nombre: String, pwd: String): Usuario? {
        val query = "SELECT * FROM usuario WHERE nombre = ? AND pwd = ?"
        val statement = Conexion.connection.prepareStatement(query)
        statement.setString(1, nombre)
        statement.setString(2, pwd)
        val result = statement.executeQuery()

        return if (result.next()) result.toUsuario() else null
    }


}
