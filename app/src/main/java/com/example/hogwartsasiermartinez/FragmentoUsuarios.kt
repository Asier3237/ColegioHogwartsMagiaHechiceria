package com.example.hogwartsasiermartinez

import android.os.Bundle
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
import com.example.hogwartsasiermartinez.Adapters.UsuarioAdapter
import com.example.hogwartsasiermartinez.Auxiliar.Sesion
import com.example.hogwartsasiermartinez.databinding.FragmentFragmentoUsuariosBinding
import com.example.hogwartsasiermartinez.model.Usuario
import com.example.hogwartsasiermartinez.viewModel.UsuarioViewModel

class FragmentoUsuarios : Fragment() {

    private var _binding: FragmentFragmentoUsuariosBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UsuarioViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFragmentoUsuariosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Configurar el Adapter y el RecyclerView
        val adapter = UsuarioAdapter()
        binding.recyclerUsuarios.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerUsuarios.adapter = adapter

        // 2. Definir la acción para el CLIC NORMAL (Editar Rol)
        adapter.onUserClick = { usuario ->
            // --- CAMBIO SUTIL PERO IMPORTANTE ---
            // Usamos 'let' para asegurarnos de que el ID del usuario no es nulo
            usuario.id?.let { usuarioId ->
                if (Sesion.rolActivo == "admin" && Sesion.usuarioId != usuarioId) {
                    mostrarDialogoCambiarRol(usuario)
                } else if (Sesion.rolActivo != "admin") {
                    Toast.makeText(requireContext(), "No tienes permisos para esta acción", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "No puedes cambiar tu propio rol", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 3. Definir la acción para el CLIC LARGO (Borrar Usuario)
        adapter.onUserLongClick = { usuario ->
            // --- CAMBIO SUTIL PERO IMPORTANTE ---
            usuario.id?.let { usuarioId ->
                if (Sesion.rolActivo == "admin") {
                    // Aquí podrías mostrar un diálogo de confirmación antes de borrar
                    viewModel.deleteUser(usuarioId)
                    Toast.makeText(requireContext(), "Usuario ${usuario.nombre} eliminado", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 4. Observar los datos y errores del ViewModel
        setupObservers(adapter)
    }

    private fun setupObservers(adapter: UsuarioAdapter) {
        // Observador para la lista de usuarios
        viewModel.usuarios.observe(viewLifecycleOwner) { usuarios ->
            adapter.submitList(usuarios)
        }

        // Observador para la confirmación del cambio de rol
        viewModel.actualizacionExitosa.observe(viewLifecycleOwner) { fueExitoso ->
            if (fueExitoso == true) {
                Toast.makeText(requireContext(), "Rol actualizado correctamente", Toast.LENGTH_SHORT).show()
                viewModel.onActualizacionCompletada() // Limpia el estado
            }
        }

        // Observador para cualquier error
        viewModel.error.observe(viewLifecycleOwner) { mensajeError ->
            if (!mensajeError.isNullOrBlank()) {
                Toast.makeText(requireContext(), mensajeError, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun mostrarDialogoCambiarRol(usuario: Usuario) {
        val rolesDisponibles = viewModel.roles.value
        if (rolesDisponibles.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Cargando roles, inténtalo de nuevo en un segundo", Toast.LENGTH_SHORT).show()
            return
        }

        // Crea un Spinner (lista desplegable) para el diálogo
        val spinner = Spinner(requireContext()).apply {
            adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, rolesDisponibles)
            setPadding(50, 50, 50, 50) // Espaciado para que se vea bien
        }

        // Muestra el diálogo
        AlertDialog.Builder(requireContext())
            .setTitle("Cambiar rol de ${usuario.nombre}")
            .setView(spinner)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevoRol = spinner.selectedItem as String
                // --- CAMBIO SUTIL PERO IMPORTANTE ---
                // Nos aseguramos de que el ID no es nulo antes de llamar al ViewModel
                usuario.id?.let { usuarioId ->
                    viewModel.cambiarRol(usuarioId, nuevoRol)
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Evita fugas de memoria
    }
}
