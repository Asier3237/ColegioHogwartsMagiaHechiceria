package com.example.hogwartsasiermartinez

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.hogwartsasiermartinez.viewModel.PerfilViewModel

class FragmentoPerfil : Fragment() {

    private val viewModel: PerfilViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_fragmento_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvNombre = view.findViewById<TextView>(R.id.tvNombre)
        val tvNivel = view.findViewById<TextView>(R.id.tvNivel)
        val tvExperiencia = view.findViewById<TextView>(R.id.tvExperiencia)
        val tvCasa = view.findViewById<TextView>(R.id.tvCasa)
        val imgCasa = view.findViewById<ImageView>(R.id.imgCasa)

        val usuarioId = arguments?.getInt("usuarioId") ?: -1
        viewModel.cargarPerfil(usuarioId)

        viewModel.usuario.observe(viewLifecycleOwner) { usuario ->
            tvNombre.text = usuario.nombre
            tvNivel.text = "Nivel: ${usuario.nivel ?: 0}"
            tvExperiencia.text = "Experiencia: ${usuario.experiencia ?: 0}"

            // Versión básica: if/else en vez de mapas/extensiones
            if (usuario.casa_id == 1) {
                tvCasa.text = "Casa: Gryffindor"
                imgCasa.setImageResource(R.drawable.gryffindor)
            } else if (usuario.casa_id == 2) {
                tvCasa.text = "Casa: Slytherin"
                imgCasa.setImageResource(R.drawable.slytherin)
            } else if (usuario.casa_id == 3) {
                tvCasa.text = "Casa: Ravenclaw"
                imgCasa.setImageResource(R.drawable.ravenclaw)
            } else if (usuario.casa_id == 4) {
                tvCasa.text = "Casa: Hufflepuff"
                imgCasa.setImageResource(R.drawable.hufflepuff)
            } else {
                tvCasa.text = "Casa: Sin casa"
                imgCasa.setImageResource(R.drawable.logohogwarts)
            }
        }
    }
}


