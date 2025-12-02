package com.example.hogwartsasiermartinez

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hogwartsasiermartinez.Adapters.CasasAdapter
import com.example.hogwartsasiermartinez.Helpers.DragHelper
import com.example.hogwartsasiermartinez.databinding.ActivityRegistroBinding
import com.example.hogwartsasiermartinez.model.Usuario
import com.example.hogwartsasiermartinez.viewModel.CasasViewModel
import com.example.hogwartsasiermartinez.viewModel.UsuarioViewModel

class activity_registro : AppCompatActivity() {

    lateinit var binding: ActivityRegistroBinding

    private val viewModel: UsuarioViewModel by viewModels()
    private val casasViewModel: CasasViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val adapter = CasasAdapter(mutableListOf())
        binding.recyclerCasas.adapter = adapter
        binding.recyclerCasas.layoutManager = LinearLayoutManager(this)

        val touchHelper = ItemTouchHelper(DragHelper(adapter))
        touchHelper.attachToRecyclerView(binding.recyclerCasas)

        casasViewModel.casasLiveData.observe(this) { casas ->
            Log.d("Registro", "Casas recibidas: ${casas.size}")
            adapter.casas.clear()
            adapter.casas.addAll(casas)
            adapter.notifyDataSetChanged()
        }


        casasViewModel.cargarCasas()

        binding.btRegistro.setOnClickListener {
            val nombre = binding.etNombreReg.text.toString()
            val passwd = binding.etPasswdReg.text.toString()
            val ordenFinalIds = adapter.getOrdenCasasId()

            viewModel.selectHouse(ordenFinalIds)
            viewModel.nombreAux = nombre
            viewModel.passwdAux = passwd
        }

        viewModel.casaSeleccionadaId.observe(this) { casaId ->
            if (casaId != null && casaId > 0) {
                val usuario = Usuario(
                    nombre = viewModel.nombreAux.toString(),
                    password = viewModel.passwdAux.toString(),
                    experiencia = 0,
                    nivel = 1,
                    casa_id = casaId
                )
                viewModel.addUser(usuario)

                val intentVSombrero = Intent(this, activity_sombrero::class.java)
                intentVSombrero.putExtra("casaId", casaId)
                startActivity(intentVSombrero)
            }
        }
    }
}
