package com.example.hogwartsasiermartinez

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hogwartsasiermartinez.viewModel.FragmentoHechizosAdminViewModel
import com.example.hogwartsasiermartinez.viewModel.FragmentoHechizosAlumnosViewModel
import com.example.hogwartsasiermartinez.viewModel.FragmentoHechizosProfesViewModel
import com.example.hogwartsasiermartinez.viewModel.FragmentoPocionesAdminViewModel
import com.example.hogwartsasiermartinez.viewModel.FragmentoPocionesAlumnosViewModel
import com.example.hogwartsasiermartinez.viewModel.FragmentoPocionesProfesViewModel

class FragmentoPociones : Fragment() {

    private val viewModelAdmin: FragmentoPocionesAdminViewModel by viewModels()
    private val viewModelAlumnos: FragmentoPocionesAlumnosViewModel by viewModels()
    private val viewModelProfes: FragmentoPocionesProfesViewModel by viewModels()

    companion object {
        fun newInstance() = FragmentoPociones()
    }

    private val viewModel: FragmentoPocionesAdminViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_fragmento_pociones, container, false)
    }
}