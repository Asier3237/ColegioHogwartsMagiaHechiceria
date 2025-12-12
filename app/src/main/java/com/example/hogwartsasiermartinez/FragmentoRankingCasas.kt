package com.example.hogwartsasiermartinez

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hogwartsasiermartinez.Adapters.RankingCasasAdapter
import com.example.hogwartsasiermartinez.databinding.FragmentFragmentoRankingCasasBinding
import com.example.hogwartsasiermartinez.viewModel.CasasViewModel

class FragmentoRankingCasas : Fragment() {

    private var _binding: FragmentFragmentoRankingCasasBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CasasViewModel by viewModels()

    // esta función solo infla el layout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFragmentoRankingCasasBinding.inflate(inflater, container, false)
        return binding.root
    }

    // cuando la vista ya está creada, aquí es donde configuro todo
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // preparo el adapter, que es solo para mostrar
        val adapter = RankingCasasAdapter()

        // configuro el recyclerview
        binding.recyclerRanking.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerRanking.adapter = adapter

        viewModel.rankingLiveData.observe(viewLifecycleOwner) { listaDeCasas ->
            // cuando llega la lista, se la paso al adapter y él solo se encarga de pintarla
            adapter.submitList(listaDeCasas)
        }
    }

    override fun onResume() {
        super.onResume()
        // cada vez que volvemos, le pido que recargue el ranking por si ha cambiado
        viewModel.cargarRanking()
    }

    // esto es importante para limpiar el binding y que no pete
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}