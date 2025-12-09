package com.example.hogwartsasiermartinez

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hogwartsasiermartinez.Adapters.UsuarioAdapter
import com.example.hogwartsasiermartinez.databinding.FragmentFragmentoUsuariosBinding
import com.example.hogwartsasiermartinez.viewModel.UsuarioViewModel

class FragmentoUsuarios : Fragment() {

    private lateinit var binding: FragmentFragmentoUsuariosBinding
    private val viewModel: UsuarioViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentFragmentoUsuariosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = UsuarioAdapter()
        binding.recyclerUsuarios.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerUsuarios.adapter = adapter

        adapter.onUserLongClick = { usuario ->
            viewModel.deleteUser(usuario.id)
            Toast.makeText(requireContext(), "Usuario ${usuario.nombre} eliminado", Toast.LENGTH_SHORT).show()
            viewModel.getUsers()
        }

        viewModel.usuarios.observe(viewLifecycleOwner) { usuarios ->
            Log.d("Usuarios", "Lista recibida: ${usuarios.size}")
            adapter.submitList(usuarios ?: emptyList())
        }

        viewModel.getUsers()
    }
}
