package com.example.hogwartsasiermartinez

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.hogwartsasiermartinez.Auxiliar.Sesion
import com.example.hogwartsasiermartinez.databinding.FragmentFragmentoPerfilBinding
import com.example.hogwartsasiermartinez.viewModel.PerfilViewModel

class FragmentoPerfil : Fragment() {

    private lateinit var binding: FragmentFragmentoPerfilBinding
    private val viewModel: PerfilViewModel by viewModels()

    // esta función solo infla el layout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFragmentoPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    // cuando la vista ya está creada, aquí es donde se pone todo
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // pillo el id del usuario que ha iniciado sesión
        val usuarioId = Sesion.usuarioId
        viewModel.cargarPerfil(usuarioId)

        viewModel.usuario.observe(viewLifecycleOwner) { usuario ->
            // cuando llegan los datos, los pinto en los textviews
            binding.tvNombre.text = usuario.nombre
            binding.tvNivel.text = "Nivel: ${usuario.nivel ?: 0}"
            binding.tvExperiencia.text = "Experiencia: ${usuario.experiencia ?: 0}"

            // según el id de la casa, pongo el nombre y la foto que toca
            if (usuario.casa_id == 1) {
                binding.tvCasa.text = "Casa: Gryffindor"
                binding.imgCasa.setImageResource(R.drawable.gryffindor)
            } else if (usuario.casa_id == 2) {
                binding.tvCasa.text = "Casa: Slytherin"
                binding.imgCasa.setImageResource(R.drawable.slytherin)
            } else if (usuario.casa_id == 3) {
                binding.tvCasa.text = "Casa: Ravenclaw"
                binding.imgCasa.setImageResource(R.drawable.ravenclaw)
            } else if (usuario.casa_id == 4) {
                binding.tvCasa.text = "Casa: Hufflepuff"
                binding.imgCasa.setImageResource(R.drawable.hufflepuff)
            } else {
                binding.tvCasa.text = "Casa: Sin casa"
                binding.imgCasa.setImageResource(R.drawable.logohogwarts)
            }
        }

        binding.btnCerrarSesion.setOnClickListener {
            // creo un intent para volver a la pantalla de login
            val intentVMain = Intent(activity, MainActivity::class.java)
            startActivity(intentVMain)
            // y muestro un mensaje para que el usuario sepa que ha salido
            Toast.makeText(requireContext(), "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()
        }

    }
}