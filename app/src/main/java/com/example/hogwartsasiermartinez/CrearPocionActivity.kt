package com.example.hogwartsasiermartinez

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.Model.Ingrediente
import com.example.Model.IngredientePocima
import com.example.hogwartsasiermartinez.Auxiliar.Sesion
import com.example.hogwartsasiermartinez.databinding.ActivityCrearPocionBinding
import com.example.hogwartsasiermartinez.viewModel.FragmentoPocionesViewModel

class CrearPocionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrearPocionBinding
    private val viewModel: FragmentoPocionesViewModel by viewModels()

    // Guardaremos los ingredientes disponibles y los que el usuario añade
    private var listaIngredientesDisponibles = listOf<Ingrediente>()
    private var ingredientesSeleccionados = mutableListOf<IngredientePocima>()
    private var nombresIngredientesAnadidos = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearPocionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        // Observador para la lista de ingredientes que viene de la API
        viewModel.ingredientes.observe(this) { ingredientes ->
            listaIngredientesDisponibles = ingredientes
            val nombresIngredientes = ingredientes.map { it.nombre }
            val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, nombresIngredientes)
            binding.spinnerIngredientes.adapter = spinnerAdapter
        }

        // Observador para la confirmación de la creación de la poción
        viewModel.operacionExitosa.observe(this) { mensaje ->
            mensaje?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.onOperacionCompletada()
                finish() // Cierra la actividad y vuelve a la lista de pociones
            }
        }

        // Observador para cualquier error
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.onOperacionCompletada()
            }
        }
    }

    private fun setupListeners() {
        // Lógica del botón "Añadir" ingrediente
        binding.btnAnadirIngrediente.setOnClickListener {
            anadirIngredienteALaLista()
        }

        // Lógica del botón final "Crear Poción"
        binding.btnCrearPocion.setOnClickListener {
            crearPocion()
        }
    }

    private fun anadirIngredienteALaLista() {
        if (listaIngredientesDisponibles.isEmpty()) return

        val posicionSeleccionada = binding.spinnerIngredientes.selectedItemPosition
        val ingredienteSeleccionado = listaIngredientesDisponibles[posicionSeleccionada]
        val cantidad = binding.etCantidad.text.toString().toIntOrNull() ?: 1

        // Añadimos el ingrediente a nuestra lista de "receta"
        ingredientesSeleccionados.add(IngredientePocima(ingredienteSeleccionado.id, cantidad))
        nombresIngredientesAnadidos.add("${ingredienteSeleccionado.nombre} (x$cantidad)")

        // Actualizamos el TextView para que el usuario vea lo que ha añadido
        binding.tvIngredientesAnadidos.visibility = android.view.View.VISIBLE
        binding.tvIngredientesAnadidos.text = "Ingredientes añadidos:\n" + nombresIngredientesAnadidos.joinToString("\n")
    }

    private fun crearPocion() {
        val nombre = binding.etNombrePocion.text.toString()
        val resumen = binding.etResumenPocion.text.toString()
        val creadorId = Sesion.usuarioId

        if (nombre.isBlank()) {
            Toast.makeText(this, "El nombre de la poción no puede estar vacío", Toast.LENGTH_SHORT).show()
            return
        }
        if (ingredientesSeleccionados.isEmpty()) {
            Toast.makeText(this, "Debes añadir al menos un ingrediente", Toast.LENGTH_SHORT).show()
            return
        }
        if (creadorId == null) {
            Toast.makeText(this, "Error: No se ha podido identificar al usuario", Toast.LENGTH_SHORT).show()
            return
        }

        // Llamamos al ViewModel para que envíe la poción al servidor
        viewModel.crearPocion(nombre, resumen, creadorId, ingredientesSeleccionados)
    }
}
