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

    // listas para guardar los ingredientes que hay y los que vamos añadiendo a la receta
    private var listaIngredientesDisponibles = listOf<Ingrediente>()
    private var ingredientesSeleccionados = mutableListOf<IngredientePocima>()
    private var nombresIngredientesAnadidos = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearPocionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // preparamos los observers y los listeners al crear la pantalla
        setupObservers()
        setupListeners()
    }

    // configuramos los observers que reaccionan a los datos del viewmodel
    private fun setupObservers() {
        // cuando llega la lista de ingredientes de la api, la ponemos en el spinner
        viewModel.ingredientes.observe(this) { ingredientes ->
            listaIngredientesDisponibles = ingredientes
            val nombresIngredientes = ingredientes.map { it.nombre }
            val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, nombresIngredientes)
            binding.spinnerIngredientes.adapter = spinnerAdapter
        }

        viewModel.operacionExitosa.observe(this) { mensaje ->
            mensaje?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.onOperacionCompletada()
                finish()
            }
        }

        // si el viewmodel nos manda un error, lo mostramos en un toast
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.onOperacionCompletada()
            }
        }
    }

    // configuramos las acciones de los botones
    private fun setupListeners() {
        binding.btnAnadirIngrediente.setOnClickListener {
            anadirIngredienteALaLista()
        }

        binding.btnCrearPocion.setOnClickListener {
            crearPocion()
        }
    }

    // esta función añade un ingrediente a la lista que ve el usuario
    private fun anadirIngredienteALaLista() {
        if (listaIngredientesDisponibles.isEmpty()) return

        val posicionSeleccionada = binding.spinnerIngredientes.selectedItemPosition
        val ingredienteSeleccionado = listaIngredientesDisponibles[posicionSeleccionada]
        val cantidad = binding.etCantidad.text.toString().toIntOrNull() ?: 1

        // añadimos el ingrediente a la lista de datos y la de nombres para mostrar
        ingredientesSeleccionados.add(IngredientePocima(ingredienteSeleccionado.id, cantidad))
        nombresIngredientesAnadidos.add("${ingredienteSeleccionado.nombre} (x$cantidad)")

        binding.tvIngredientesAnadidos.visibility = android.view.View.VISIBLE
        binding.tvIngredientesAnadidos.text = "Ingredientes añadidos:\n" + nombresIngredientesAnadidos.joinToString("\n")
    }

    private fun crearPocion() {
        val nombre = binding.etNombrePocion.text.toString()
        val resumen = binding.etResumenPocion.text.toString()
        val creadorId = Sesion.usuarioId

        // comprobamos que todo esté relleno
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

        viewModel.crearPocion(nombre, resumen, creadorId, ingredientesSeleccionados)
    }
}