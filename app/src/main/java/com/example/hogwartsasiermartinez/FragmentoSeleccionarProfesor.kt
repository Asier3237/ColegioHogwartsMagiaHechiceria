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

    private var asignaturaId: Int = -1
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProfesoresAdapter
    private val viewModel: ProfesoresViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        asignaturaId = Sesion.asignaturaId
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_fragmento_seleccionar_profesor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerProfesores)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ProfesoresAdapter(emptyList()) { profesor ->
            profesor.id?.let { id ->
                viewModel.asignarProfesor(id, asignaturaId)
            }
        }
        recyclerView.adapter = adapter

        viewModel.profesoresLiveData.observe(viewLifecycleOwner) { lista ->
            adapter.updateData(lista)
        }

        viewModel.cargarProfesores()
    }
}

