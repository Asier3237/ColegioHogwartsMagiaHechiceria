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

    // esta función solo infla el layout
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFragmentoPocionesBinding.inflate(inflater, container, false)
        return binding.root
    }

    // cuando la vista ya está creada, aquí es donde se pone todo
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // preparamos el adapter y le decimos qué hacer cuando se pulsa un item
        val adapter = PocionesAdapter(
            // clic normal: si eres profe, puedes validar una poción pendiente
            onPocionClick = { pocion ->
                if (Sesion.rolActivo == "profesor" && pocion.validada == 0) {
                    mostrarDialogoValidar(pocion)
                }
            },
            // clic largo: si eres admin, puedes borrarla
            onPocionLongClick = { pocion ->
                if (Sesion.rolActivo == "admin") {
                    mostrarDialogoBorrar(pocion)
                }
            }
        )

        binding.recyclerPociones.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerPociones.adapter = adapter

        // pongo visible o no el botón flotante según el rol
        configurarVisibilidadPorRol()
        setupObservers(adapter)

        // la acción para el botón flotante, que abre la pantalla de crear pociones
        binding.fabCrearPocion.setOnClickListener {
            val intent = Intent(requireActivity(), CrearPocionActivity::class.java)
            startActivity(intent)
        }
    }

    // esto se llama siempre que volvemos a esta pantalla
    override fun onResume() {
        super.onResume()
        // recargo la lista de pociones para que siempre esté actualizada
        viewModel.cargarPociones(Sesion.rolActivo ?: "", Sesion.usuarioId ?: 0)
    }

    // esta función decide si se ve el botón de crear o no
    private fun configurarVisibilidadPorRol() {
        binding.fabCrearPocion.visibility = if (Sesion.rolActivo == "alumno") View.VISIBLE else View.GONE
    }

    // aquí configuramos los observers que reaccionan a los datos del viewmodel
    private fun setupObservers(adapter: PocionesAdapter) {
        // cuando llega la lista de pociones se mete en el adapter
        viewModel.pociones.observe(viewLifecycleOwner) { pociones ->
            adapter.submitList(pociones)
        }

        // cuando llega una señal de que algo ha ido bien, muestro un toast y recargo la lista
        viewModel.operacionExitosa.observe(viewLifecycleOwner) { mensaje ->
            mensaje?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                // recargamos la lista para ver los cambios al instante
                viewModel.cargarPociones(Sesion.rolActivo ?: "", Sesion.usuarioId ?: 0)
                viewModel.onOperacionCompletada()
            }
        }

        // si el viewmodel manda un error, lo muestro
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.onOperacionCompletada()
            }
        }
    }

    // diálogo para que el profesor valide o rechace una poción
    private fun mostrarDialogoValidar(pocion: Pocima) {
        val esBuena = if (pocion.tipo == "buena") "Buena" else "Mala"
        AlertDialog.Builder(requireContext())
            .setTitle("Validar Poción: ${pocion.nombre}")
            .setMessage("Esta poción ha sido evaluada como: $esBuena.\n\n¿Qué quieres hacer?")
            .setPositiveButton("Validar") { _, _ ->
                viewModel.validarPocion(pocion.id, true) // true para validar
            }
            .setNegativeButton("Rechazar") { _, _ ->
                viewModel.validarPocion(pocion.id, false) // false para rechazar
            }
            .setNeutralButton("Cancelar", null)
            .create()
            .show()
    }

    // el diálogo para confirmar que el admin quiere borrar una poción
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

    // esto es importante para limpiar el binding y que no pete
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}