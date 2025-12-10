package com.example.hogwartsasiermartinez

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hogwartsasiermartinez.Adapters.AsignaturasAdapter
import com.example.hogwartsasiermartinez.Auxiliar.Sesion
import com.example.hogwartsasiermartinez.viewModel.FragmentoAsignaturasViewModel

class FragmentoAsignaturas : Fragment() {

    private val viewModel: FragmentoAsignaturasViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AsignaturasAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Log para saber que el fragmento se está creando
        Log.d("FragmentoAsignaturas", "onCreateView - Creando la vista del fragmento.")
        return inflater.inflate(R.layout.fragment_fragmento_asignaturas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("FragmentoAsignaturas", "onViewCreated - La vista ha sido creada.")

        setupRecyclerView()
        observeViewModel()

        Log.d("FragmentoAsignaturas", "Iniciando la carga de datos desde el ViewModel.")
        viewModel.cargarAsignaturas()
        viewModel.cargarProfesores()
    }

    private fun setupRecyclerView() {
        recyclerView = requireView().findViewById(R.id.recyclerAsignaturas)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = AsignaturasAdapter(emptyList()) { asignatura ->
            // Log para confirmar que el click en el adapter funciona
            Log.d("FragmentoAsignaturas", "Clic detectado en el Adapter para la asignatura: ${asignatura.nombre}")

            if (Sesion.rolActivo == "admin") {
                Log.d("FragmentoAsignaturas", "El usuario es ADMIN. Procediendo a mostrar diálogo.")
                asignatura.id?.let { idAsignatura ->
                    mostrarDialogoSeleccionarProfesor(idAsignatura)
                }
            } else {
                Log.d("FragmentoAsignaturas", "El usuario con rol '${Sesion.rolActivo}' no tiene permisos.")
            }
        }
        recyclerView.adapter = adapter
        Log.d("FragmentoAsignaturas", "RecyclerView y Adapter configurados.")
    }

    private fun observeViewModel() {
        viewModel.asignaturasLiveData.observe(viewLifecycleOwner) { listaAsignaturas ->
            Log.d("FragmentoAsignaturas", "Observer: Se recibieron ${listaAsignaturas.size} asignaturas.")
            adapter.updateData(listaAsignaturas)
        }

        viewModel.errorLiveData.observe(viewLifecycleOwner) { error ->
            Log.e("FragmentoAsignaturas", "Observer: Error recibido - $error")
            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
        }

        // --- OBSERVER CORREGIDO Y MÁS SEGURO ---
        viewModel.asignacionExitosa.observe(viewLifecycleOwner) { fueExitoso ->
            // Comprobamos explícitamente que sea 'true' para evitar problemas con 'null'
            if (fueExitoso == true) {
                Log.d("FragmentoAsignaturas", "Observer: Asignación exitosa confirmada.")
                Toast.makeText(requireContext(), "Profesor asignado correctamente", Toast.LENGTH_SHORT).show()
                viewModel.onAsignacionCompletada() // Resetea el estado para evitar múltiples toasts
            }
        }
        Log.d("FragmentoAsignaturas", "Todos los observers del ViewModel han sido configurados.")
    }

    private fun mostrarDialogoSeleccionarProfesor(idAsignatura: Int) {
        // Log para confirmar que esta función se está ejecutando
        Log.d("FragmentoAsignaturas", "Función 'mostrarDialogoSeleccionarProfesor' ejecutada para asignatura ID: $idAsignatura")

        val profesores = viewModel.profesoresLiveData.value
        if (profesores.isNullOrEmpty()) {
            Log.w("FragmentoAsignaturas", "No hay profesores disponibles en LiveData para mostrar en el diálogo.")
            Toast.makeText(requireContext(), "No hay profesores disponibles para asignar.", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("FragmentoAsignaturas", "Mostrando diálogo con ${profesores.size} profesores.")
        val nombresProfesores = profesores.map { it.nombre }
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, nombresProfesores)

        val spinnerProfesores = Spinner(requireContext()).apply {
            adapter = spinnerAdapter
            setPadding(40, 40, 40, 40)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Asignar Profesor")
            .setMessage("Selecciona un profesor para esta asignatura:")
            .setView(spinnerProfesores)
            .setPositiveButton("Asignar") { dialog, _ ->
                val profesorSeleccionado = profesores[spinnerProfesores.selectedItemPosition]
                profesorSeleccionado.id?.let { idProfesor ->
                    Log.d("FragmentoAsignaturas", "Botón 'Asignar' pulsado. Asignando profesor ID: $idProfesor a asignatura ID: $idAsignatura")
                    viewModel.asignarProfesorAAsignatura(idAsignatura, idProfesor)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }
}
