package com.example.hogwartsasiermartinez

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Model.Pocima
import com.example.hogwartsasiermartinez.Adapters.PocionesAdapter
import com.example.hogwartsasiermartinez.Auxiliar.Sesion
import com.example.hogwartsasiermartinez.databinding.FragmentFragmentoPocionesBinding
import com.example.hogwartsasiermartinez.viewModel.FragmentoPocionesViewModel

class FragmentoPociones : Fragment() {

    private var _binding: FragmentFragmentoPocionesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FragmentoPocionesViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFragmentoPocionesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = PocionesAdapter(
            // Clic normal: para profesores
            onPocionClick = { pocion ->
                if (Sesion.rolActivo == "profesor" && pocion.validada == 0) {
                    mostrarDialogoValidar(pocion)
                }
            },
            // Clic largo: para admins
            onPocionLongClick = { pocion ->
                if (Sesion.rolActivo == "admin") {
                    mostrarDialogoBorrar(pocion)
                }
            }
        )

        binding.recyclerPociones.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerPociones.adapter = adapter

        configurarVisibilidadPorRol()
        setupObservers(adapter)

        // El botón flotante abre la nueva activity para crear pociones
        binding.fabCrearPocion.setOnClickListener {
             val intent = Intent(requireActivity(), CrearPocionActivity::class.java)
             startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Recargamos las pociones cada vez que el fragmento se vuelve visible
        viewModel.cargarPociones(Sesion.rolActivo ?: "", Sesion.usuarioId ?: 0)
    }

    private fun configurarVisibilidadPorRol() {
        // El botón de crear solo es visible para alumnos
        binding.fabCrearPocion.visibility = if (Sesion.rolActivo == "alumno") View.VISIBLE else View.GONE
    }

    private fun setupObservers(adapter: PocionesAdapter) {
        viewModel.pociones.observe(viewLifecycleOwner) { pociones ->
            adapter.submitList(pociones)
        }

        viewModel.operacionExitosa.observe(viewLifecycleOwner) { mensaje ->
            mensaje?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                // Recargamos la lista para ver los cambios
                viewModel.cargarPociones(Sesion.rolActivo ?: "", Sesion.usuarioId ?: 0)
                viewModel.onOperacionCompletada()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.onOperacionCompletada()
            }
        }
    }

    // Diálogo para que el profesor valide o rechace
    private fun mostrarDialogoValidar(pocion: Pocima) {
        val esBuena = if (pocion.tipo == "buena") "Buena" else "Mala"
        AlertDialog.Builder(requireContext())
            .setTitle("Validar Poción: ${pocion.nombre}")
            .setMessage("Esta poción ha sido evaluada como: $esBuena.\n\n¿Qué quieres hacer?")
            .setPositiveButton("Validar") { _, _ ->
                viewModel.validarPocion(pocion.id, true) // true = validar
            }
            .setNegativeButton("Rechazar") { _, _ ->
                viewModel.validarPocion(pocion.id, false) // false = rechazar
            }
            .setNeutralButton("Cancelar", null)
            .create()
            .show()
    }

    // Diálogo para que el admin borre
    private fun mostrarDialogoBorrar(pocion: Pocima) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Poción")
            .setMessage("¿Estás seguro de que quieres eliminar '${pocion.nombre}'? Esta acción es permanente.")
            .setPositiveButton("Sí, eliminar") { _, _ ->
                viewModel.borrarPocion(pocion.id)
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
