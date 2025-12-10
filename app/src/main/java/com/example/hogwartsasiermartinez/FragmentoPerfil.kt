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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFragmentoPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usuarioId = Sesion.usuarioId
        viewModel.cargarPerfil(usuarioId)

        viewModel.usuario.observe(viewLifecycleOwner) { usuario ->
            binding.tvNombre.text = usuario.nombre
            binding.tvNivel.text = "Nivel: ${usuario.nivel ?: 0}"
            binding.tvExperiencia.text = "Experiencia: ${usuario.experiencia ?: 0}"

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
            val intentVMain = Intent(activity, MainActivity::class.java)
            startActivity(intentVMain)
            Toast.makeText(requireContext(), "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()
        }

    }
}
