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
import com.example.hogwartsasiermartinez.Adapters.HechizosAdapter
import com.example.hogwartsasiermartinez.Auxiliar.Sesion
import com.example.hogwartsasiermartinez.databinding.FragmentFragmentoHechizosBinding
import com.example.hogwartsasiermartinez.model.Hechizo

class FragmentoHechizos : Fragment() {

    private var _binding: FragmentFragmentoHechizosBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FragmentoHechizosViewModel by viewModels()

    // esta función solo infla el layout
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFragmentoHechizosBinding.inflate(inflater, container, false)
        return binding.root
    }

    // cuando la vista ya está creada, aquí es donde se pone todo
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = HechizosAdapter(
            // la acción para el clic normal: si es un alumno, le dejamos aprender el hechizo
            onHechizoClick = { hechizo ->
                if (Sesion.rolActivo == "alumno") {
                    mostrarDialogoAprender(hechizo)
                }
            },
            // la acción para el clic largo: si es admin, le dejamos borrarlo
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

        // la acción para el botón flotante de añadir
        binding.fabAnadirHechizo.setOnClickListener {
            mostrarDialogoCrear()
        }

        // le pido al viewmodel que cargue la lista de hechizos al empezar
        viewModel.cargarHechizos()
    }

    //decide si se ve el botón de añadir o no
    private fun configurarVisibilidadPorRol() {
        if (Sesion.rolActivo == "admin" || Sesion.rolActivo == "profesor") {
            binding.fabAnadirHechizo.visibility = View.VISIBLE
        } else {
            binding.fabAnadirHechizo.visibility = View.GONE
        }
    }

    // aquí configuramos los observers que reaccionan a los datos del viewmodel
    private fun setupObservers(adapter: HechizosAdapter) {
        // cuando llega la lista de hechizos, la metemos en el adapter
        viewModel.hechizos.observe(viewLifecycleOwner) { listaHechizos ->
            adapter.submitList(listaHechizos)
        }

        // cuando llega una señal de que algo ha ido bien, muestro un toast
        viewModel.operacionExitosa.observe(viewLifecycleOwner) { mensaje ->
            mensaje?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.onOperacionCompletada()
            }
        }

        // si el viewmodel manda un error, lo muestro
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.onOperacionCompletada()
            }
        }
    }

    // función para el diálogo de crear un hechizo nuevo
    private fun mostrarDialogoCrear() {
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
                // al pulsar 'crear', pillo los datos y llamo al viewmodel
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

    // el diálogo para confirmar si un alumno quiere aprender un hechizo
    private fun mostrarDialogoAprender(hechizo: Hechizo) {
        AlertDialog.Builder(requireContext())
            .setTitle("Aprender Hechizo")
            .setMessage("¿Quieres aprender '${hechizo.nombre}'?\n\n${hechizo.descripcion}\n\nRecompensa: ${hechizo.experiencia} EXP")
            .setPositiveButton("Sí, aprender") { _, _ ->
                // al pulsar 'sí', pillo el id del alumno de la sesión y llamo al viewmodel
                Sesion.usuarioId?.let { alumnoId ->
                    viewModel.alumnoAprendeHechizo(alumnoId, hechizo.id)
                }
            }
            .setNegativeButton("No", null)
            .create()
            .show()
    }

    // el diálogo para confirmar que se quiere borrar un hechizo
    private fun mostrarDialogoBorrar(hechizo: Hechizo) {
        AlertDialog.Builder(requireContext())
            .setTitle("Borrar Hechizo")
            .setMessage("¿Estás seguro de que quieres borrar el hechizo '${hechizo.nombre}'? Esta acción no se puede deshacer.")
            .setPositiveButton("Sí, borrar") { _, _ ->
                // al pulsar 'sí', llamo al viewmodel para que lo borre
                viewModel.borrarHechizo(hechizo.id)
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }


    // esto es importante para limpiar el binding y no pete
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}