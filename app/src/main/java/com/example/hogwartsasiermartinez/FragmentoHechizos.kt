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

class FragmentoHechizos : Fragment() {

    private val viewModelAdmin: FragmentoHechizosAdminViewModel by viewModels()
    private val viewModelAlumnos: FragmentoHechizosAlumnosViewModel by viewModels()
    private val viewModelProfes: FragmentoHechizosProfesViewModel by viewModels()

    companion object {
        fun newInstance() = FragmentoHechizos()
    }

    private val viewModel: FragmentoHechizosAdminViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_fragmento_hechizos, container, false)
    }
}