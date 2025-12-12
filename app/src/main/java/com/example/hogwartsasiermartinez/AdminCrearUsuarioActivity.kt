package com.example.hogwartsasiermartinez

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.Model.UsuarioCrear
import com.example.hogwartsasiermartinez.databinding.ActivityAdminCrearUsuarioBinding
import com.example.hogwartsasiermartinez.model.Casa
import com.example.hogwartsasiermartinez.viewModel.CasasViewModel
import com.example.hogwartsasiermartinez.viewModel.UsuarioViewModel

class AdminCrearUsuarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminCrearUsuarioBinding

    private val usuarioViewModel: UsuarioViewModel by viewModels()
    private val casasViewModel: CasasViewModel by viewModels()

    private var listaCasas: List<Casa> = emptyList()
    private var listaRoles: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminCrearUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        casasViewModel.cargarCasas()

        // configuramos los observers y los listeners
        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        // observer para la lista de casas
        casasViewModel.casasLiveData.observe(this) { casas ->
            if (casas != null) {
                // cuando llega la lista, la guardamos y la ponemos en el spinner
                listaCasas = casas
                val nombresCasas = casas.map { it.nombre }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, nombresCasas)
                binding.spinnerCasa.adapter = adapter
            }
        }

        // observer para la lista de roles
        usuarioViewModel.roles.observe(this) { roles ->
            if (roles != null) {
                // hacemos lo mismo que con las casas, pero para los roles
                listaRoles = roles
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)
                binding.spinnerRol.adapter = adapter
            }
        }

        // observer para saber si el usuario se ha creado correctamente
        usuarioViewModel.creacionExitosa.observe(this) { fueExitoso ->
            if (fueExitoso == true) {
                // si se ha creado bien, mostramos un mensaje y cerramos la pantalla
                Toast.makeText(this, "Usuario creado con éxito", Toast.LENGTH_LONG).show()
                usuarioViewModel.onCreacionCompletada()
                finish()
            }
        }

        // observer para cualquier error que pueda ocurrir
        usuarioViewModel.error.observe(this) { error ->
            if (!error.isNullOrBlank()) {
                // si hay un error, se muestra y se limpia el mensaje para que no se repita
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                usuarioViewModel.onCreacionCompletada()
            }
        }
    }

    private fun setupListeners() {
        // listener para el botón de crear
        binding.btnCrear.setOnClickListener {
            // cuando se pulse, llamamos a la función que tiene toda la lógica
            crearUsuario()
        }
    }

    private fun crearUsuario() {
        val nombre = binding.etNombre.text.toString()
        val password = binding.etPassword.text.toString()

        // comprobamos que no dejen campos vacíos
        if (nombre.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Nombre y contraseña son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }
        // comprobamos que los datos de los spinners ya se hayan cargado
        if (listaCasas.isEmpty() || listaRoles.isEmpty()) {
            Toast.makeText(this, "Cargando datos, por favor espere un segundo", Toast.LENGTH_SHORT).show()
            return
        }

        // cogemos la casa y el rol que se han seleccionado en los spinners
        val casaSeleccionada = listaCasas[binding.spinnerCasa.selectedItemPosition]
        val rolSeleccionado = listaRoles[binding.spinnerRol.selectedItemPosition]

        // se decide el nivel y la experiencia que tendrá el usuario según el rol
        val (nivel, experiencia) = when (rolSeleccionado.lowercase()) {
            "alumno" -> 1 to 0
            "profesor" -> 2 to 100
            "admin" -> 5 to 500
            else -> 1 to 0 // por si acaso, le ponemos el de alumno
        }

        val datosNuevoUsuario = UsuarioCrear(
            nombre = nombre,
            password = password,
            casaId = casaSeleccionada.id,
            rol = rolSeleccionado,
            nivel = nivel,
            experiencia = experiencia
        )

        usuarioViewModel.adminCrearUsuario(datosNuevoUsuario)
    }
}