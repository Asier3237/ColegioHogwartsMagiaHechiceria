package com.example.hogwartsasiermartinez

import android.os.Bundle
import android.text.InputType
import com.example.hogwartsasiermartinez.viewModel.FragmentoHechizosViewModel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hogwartsasiermartinez.Adapters.HechizosAdapter // Necesitarás este Adapter
import com.example.hogwartsasiermartinez.Auxiliar.Sesion
import com.example.hogwartsasiermartinez.databinding.FragmentFragmentoHechizosBinding
import com.example.hogwartsasiermartinez.model.Hechizo

class FragmentoHechizos : Fragment() {

    private var _binding: FragmentFragmentoHechizosBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FragmentoHechizosViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFragmentoHechizosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = HechizosAdapter(
            // Acción para el CLIC NORMAL (Aprender Hechizo para alumnos)
            onHechizoClick = { hechizo ->
                if (Sesion.rolActivo == "alumno") {
                    mostrarDialogoAprender(hechizo)
                }
            },
            // Acción para el CLIC LARGO (Borrar Hechizo para admin)
            onHechizoLongClick = { hechizo ->
                if (Sesion.rolActivo == "admin") {
                    mostrarDialogoBorrar(hechizo)
                }
            }
        )

        binding.recyclerHechizos.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerHechizos.adapter = adapter

        configurarVisibilidadPorRol()
        setupObservers(adapter)

        binding.fabAnadirHechizo.setOnClickListener {
            mostrarDialogoCrear()
        }

        // --- ¡¡¡AQUÍ ESTÁ LA LÍNEA QUE FALTA!!! ---
        // Hacemos la llamada explícita para asegurarnos de que los datos se piden.
        viewModel.cargarHechizos()
    }

    private fun configurarVisibilidadPorRol() {
        // El botón flotante solo es visible para admin y profesor
        if (Sesion.rolActivo == "admin" || Sesion.rolActivo == "profesor") {
            binding.fabAnadirHechizo.visibility = View.VISIBLE
        } else {
            binding.fabAnadirHechizo.visibility = View.GONE
        }
    }

    private fun setupObservers(adapter: HechizosAdapter) {
        viewModel.hechizos.observe(viewLifecycleOwner) { listaHechizos ->
            adapter.submitList(listaHechizos)
        }

        viewModel.operacionExitosa.observe(viewLifecycleOwner) { mensaje ->
            mensaje?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.onOperacionCompletada()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.onOperacionCompletada()
            }
        }
    }

    private fun mostrarDialogoCrear() {
        // Creamos un layout con varios campos de texto
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 50, 50, 50)
        }
        val inputNombre = EditText(requireContext()).apply { hint = "Nombre del hechizo" }
        val inputDescripcion = EditText(requireContext()).apply { hint = "Descripción" }
        val inputExperiencia = EditText(requireContext()).apply {
            hint = "Experiencia (ej: 10)"
            inputType = InputType.TYPE_CLASS_NUMBER
        }
        layout.addView(inputNombre)
        layout.addView(inputDescripcion)
        layout.addView(inputExperiencia)

        AlertDialog.Builder(requireContext())
            .setTitle("Crear Nuevo Hechizo")
            .setView(layout)
            .setPositiveButton("Crear") { _, _ ->
                val nombre = inputNombre.text.toString()
                val descripcion = inputDescripcion.text.toString()
                val experiencia = inputExperiencia.text.toString().toIntOrNull() ?: 0

                if (nombre.isNotBlank()) {
                    viewModel.crearHechizo(nombre, descripcion, experiencia)
                } else {
                    Toast.makeText(requireContext(), "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }

    private fun mostrarDialogoAprender(hechizo: Hechizo) {
        AlertDialog.Builder(requireContext())
            .setTitle("Aprender Hechizo")
            .setMessage("¿Quieres aprender '${hechizo.nombre}'?\n\n${hechizo.descripcion}\n\nRecompensa: ${hechizo.experiencia} EXP")
            .setPositiveButton("Sí, aprender") { _, _ ->
                // --- ¡CORRECCIÓN AQUÍ! ---
                // Cambiamos 'usuarioId' por 'idUsuarioActivo' para que coincida con tu objeto Sesion
                Sesion.usuarioId?.let { alumnoId ->
                    // El ID del hechizo no debería ser nulo si viene de la BD
                    viewModel.alumnoAprendeHechizo(alumnoId, hechizo.id)
                }
            }
            .setNegativeButton("No", null)
            .create()
            .show()
    }

    // Pega esta nueva función dentro de tu clase FragmentoHechizos.kt

    private fun mostrarDialogoBorrar(hechizo: Hechizo) {
        AlertDialog.Builder(requireContext())
            .setTitle("Borrar Hechizo")
            .setMessage("¿Estás seguro de que quieres borrar el hechizo '${hechizo.nombre}'? Esta acción no se puede deshacer.")
            .setPositiveButton("Sí, borrar") { _, _ ->
                // El ID del hechizo no debería ser nulo si viene de la BD
                viewModel.borrarHechizo(hechizo.id)
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
