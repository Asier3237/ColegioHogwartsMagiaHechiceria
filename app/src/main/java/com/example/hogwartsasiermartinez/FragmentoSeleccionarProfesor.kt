package com.example.hogwartsasiermartinez

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hogwartsasiermartinez.Adapters.ProfesoresAdapter
import com.example.hogwartsasiermartinez.Auxiliar.Sesion
import com.example.hogwartsasiermartinez.model.Usuario
import com.example.hogwartsasiermartinez.viewModel.ProfesoresViewModel

class FragmentoSeleccionarProfesor : Fragment() {

    // el id de la asignatura a la que le vamos a poner un profe
    private var asignaturaId: Int = -1
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProfesoresAdapter
    private val viewModel: ProfesoresViewModel by viewModels()

    // antes de que se cree la vista, pillo el id de la asignatura que guardé en la sesión
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        asignaturaId = Sesion.asignaturaId
    }

    // esta función solo infla el layout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_fragmento_seleccionar_profesor, container, false)
    }

    // cuando la vista ya está creada, aquí monto todo
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // conecto el recyclerview y le digo cómo mostrar la lista
        recyclerView = view.findViewById(R.id.recyclerProfesores)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        // creo el adapter y le paso la acción que hará cuando se pulse en un profe
        adapter = ProfesoresAdapter(emptyList()) { profesor ->
            // cuando se pulsa, llamo al viewmodel para que asigne ese profe a la asignatura
            profesor.id?.let { id ->
                viewModel.asignarProfesor(id, asignaturaId)
            }
        }
        recyclerView.adapter = adapter

        viewModel.profesoresLiveData.observe(viewLifecycleOwner) { lista ->
            // cuando llega la lista, se la paso al adapter para que la pinte
            adapter.updateData(lista)
        }

        // le pido al viewmodel que empiece a cargar la lista de profes
        viewModel.cargarProfesores()
    }
}