package com.example.hogwartsasiermartinez

import android.content.Intent
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

    // esta función solo infla el layout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFragmentoUsuariosBinding.inflate(inflater, container, false)
        return binding.root
    }

    // cuando la vista ya está creada, aquí es donde se pone todo
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // preparo el adapter para la lista
        val adapter = UsuarioAdapter()
        binding.recyclerUsuarios.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerUsuarios.adapter = adapter

        // le digo al adapter qué hacer con el clic normal (editar rol)
        adapter.onUserClick = { usuario ->
            usuario.id?.let { usuarioId ->
                // solo un admin puede cambiar el rol, y no a sí mismo
                if (Sesion.rolActivo == "admin" && Sesion.usuarioId != usuarioId) {
                    mostrarDialogoCambiarRol(usuario)
                } else if (Sesion.rolActivo != "admin") {
                    Toast.makeText(requireContext(), "No tienes permisos para esta acción", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "No puedes cambiar tu propio rol", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // le digo al adapter qué hacer con el clic largo (borrar usuario)
        adapter.onUserLongClick = { usuario ->
            usuario.id?.let { usuarioId ->
                if (Sesion.rolActivo == "admin") {
                    viewModel.deleteUser(usuarioId)
                    Toast.makeText(requireContext(), "Usuario ${usuario.nombre} eliminado", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // si el usuario es admin, se mestra el botón de añadir y se pone la acción
        if (Sesion.rolActivo == "admin") {
            binding.fabAnadirUsuario.visibility = View.VISIBLE
            binding.fabAnadirUsuario.setOnClickListener {
                val intent = Intent(requireActivity(), AdminCrearUsuarioActivity::class.java)
                startActivity(intent)
            }
        } else {
            binding.fabAnadirUsuario.visibility = View.GONE
        }

        setupObservers(adapter)
    }

    // aquí configuro los observers que reaccionan a los datos del viewmodel
    private fun setupObservers(adapter: UsuarioAdapter) {
        // cuando llega la lista de usuarios, la metemos en el adapter
        viewModel.usuarios.observe(viewLifecycleOwner) { usuarios ->
            adapter.submitList(usuarios)
        }

        // cuando llega la señal de que el rol se ha cambiado, muestro un toast
        viewModel.actualizacionExitosa.observe(viewLifecycleOwner) { fueExitoso ->
            if (fueExitoso == true) {
                Toast.makeText(requireContext(), "Rol actualizado correctamente", Toast.LENGTH_SHORT).show()
                viewModel.onActualizacionCompletada() // limpio el estado para que no se repita
            }
        }

        // si el viewmodel manda un error, lo muestro
        viewModel.error.observe(viewLifecycleOwner) { mensajeError ->
            if (!mensajeError.isNullOrBlank()) {
                Toast.makeText(requireContext(), mensajeError, Toast.LENGTH_LONG).show()
            }
        }
    }

    // función para el diálogo de cambiar el rol
    private fun mostrarDialogoCambiarRol(usuario: Usuario) {
        val rolesDisponibles = viewModel.roles.value
        // si los roles aún no han llegado, aviso al usuario
        if (rolesDisponibles.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Cargando roles, inténtalo de nuevo en un segundo", Toast.LENGTH_SHORT).show()
            return
        }

        // creo un spinner con los roles disponibles
        val spinner = Spinner(requireContext()).apply {
            adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, rolesDisponibles)
            setPadding(50, 50, 50, 50)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Cambiar rol de ${usuario.nombre}")
            .setView(spinner)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevoRol = spinner.selectedItem as String
                // al pulsar 'guardar', pillo el rol seleccionado y llamo al viewmodel
                usuario.id?.let { usuarioId ->
                    viewModel.cambiarRol(usuarioId, nuevoRol)
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }

    override fun onResume() {
        super.onResume()
        // recargo la lista para que siempre esté actualizada
        viewModel.getUsers()
    }

    // esto es importante para limpiar el binding y que no pete
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
