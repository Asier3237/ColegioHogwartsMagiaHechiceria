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

    // esta función solo prepara el layout para que se pueda ver
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("FragmentoAsignaturas", "onCreateView - Creando la vista del fragmento.")
        return inflater.inflate(R.layout.fragment_fragmento_asignaturas, container, false)
    }

    // cuando la vista ya está creada, aquí es donde configuramos todo
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("FragmentoAsignaturas", "onViewCreated - La vista ha sido creada.")

        // preparamos la lista y los observers de datos
        setupRecyclerView()
        observeViewModel()

        // le decimos al viewmodel que empiece a cargar los datos
        Log.d("FragmentoAsignaturas", "Iniciando la carga de datos desde el ViewModel.")
        viewModel.cargarAsignaturas()
        viewModel.cargarProfesores()
    }

    // prepara el recyclerview y su adapter
    private fun setupRecyclerView() {
        recyclerView = requireView().findViewById(R.id.recyclerAsignaturas)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // creamos el adapter y le pasamos la acción que hará al pulsar en una asignatura
        adapter = AsignaturasAdapter(emptyList()) { asignatura ->
            Log.d("FragmentoAsignaturas", "Clic detectado en el Adapter para la asignatura: ${asignatura.nombre}")

            // solo si el usuario es admin, le dejamos asignar un profesor
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
        // cuando llegue la lista de asignaturas, la metemos en el adapter para que se vea
        viewModel.asignaturasLiveData.observe(viewLifecycleOwner) { listaAsignaturas ->
            Log.d("FragmentoAsignaturas", "Observer: Se recibieron ${listaAsignaturas.size} asignaturas.")
            adapter.updateData(listaAsignaturas)
        }

        // si llega un error, lo mostramos en un toast
        viewModel.errorLiveData.observe(viewLifecycleOwner) { error ->
            Log.e("FragmentoAsignaturas", "Observer: Error recibido - $error")
            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
        }

        // cuando nos llegue la señal de que la asignación ha ido bien, mostramos un toast
        viewModel.asignacionExitosa.observe(viewLifecycleOwner) { fueExitoso ->
            if (fueExitoso == true) {
                Log.d("FragmentoAsignaturas", "Observer: Asignación exitosa confirmada.")
                Toast.makeText(requireContext(), "Profesor asignado correctamente", Toast.LENGTH_SHORT).show()
                viewModel.onAsignacionCompletada()
            }
        }
        Log.d("FragmentoAsignaturas", "Todos los observers del ViewModel han sido configurados.")
    }

    // crea y muestra el diálogo para elegir un profesor
    private fun mostrarDialogoSeleccionarProfesor(idAsignatura: Int) {
        Log.d("FragmentoAsignaturas", "Función 'mostrarDialogoSeleccionarProfesor' ejecutada para asignatura ID: $idAsignatura")

        val profesores = viewModel.profesoresLiveData.value
        // si no hay profes para elegir, mostramos un aviso y no hacemos nada más
        if (profesores.isNullOrEmpty()) {
            Log.w("FragmentoAsignaturas", "No hay profesores disponibles en LiveData para mostrar en el diálogo.")
            Toast.makeText(requireContext(), "No hay profesores disponibles para asignar.", Toast.LENGTH_SHORT).show()
            return
        }

        // creamos un spinner para meterlo en el diálogo
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
            .setView(spinnerProfesores) // aquí metemos nuestro spinner
            .setPositiveButton("Asignar") { dialog, _ ->
                // cuando pulsan 'asignar', pillamos el profe seleccionado y llamamos al viewmodel
                val profesorSeleccionado = profesores[spinnerProfesores.selectedItemPosition]
                profesorSeleccionado.id?.let { idProfesor ->
                    Log.d("FragmentoAsignaturas", "Botón 'Asignar' pulsado. Asignando profesor ID: $idProfesor a asignatura ID: $idAsignatura")
                    viewModel.asignarProfesorAAsignatura(idAsignatura, idProfesor)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null) // el botón de cancelar cierra el diálogo
            .create()
            .show()
    }
}