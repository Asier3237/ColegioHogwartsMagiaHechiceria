package com.example.hogwartsasiermartinez

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hogwartsasiermartinez.Auxiliar.Sesion
import com.example.hogwartsasiermartinez.databinding.ActivityAlumnosBinding
import com.example.hogwartsasiermartinez.databinding.ActivitySombreroBinding
import com.google.android.material.navigation.NavigationView

class AlumnosActivity : AppCompatActivity() {

    lateinit var binding: ActivityAlumnosBinding
    private var usuarioId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAlumnosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            0,
            0
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        usuarioId = Sesion.usuarioId

        val navView = findViewById<NavigationView>(R.id.navView)
        val menu = navView.menu

        menu.findItem(R.id.nav_usuarios).isVisible = false
        menu.findItem(R.id.nav_ranking).isVisible = false

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, FragmentoAsignaturas())
                .commit()
        }

        binding.navView.setNavigationItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_pociones -> FragmentoPociones()
                R.id.nav_hechizos -> FragmentoHechizos()
                R.id.nav_asignaturas -> FragmentoAsignaturas()
                else -> null
            }

            fragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, it)
                    .commit()
            }

            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        val imgPerfil = binding.toolbar.findViewById<ImageView>(R.id.imgPerfil)
        imgPerfil.setOnClickListener {
            val fragment = FragmentoPerfil().apply {
                usuarioId = Sesion.usuarioId
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }

        val headerView = binding.navView.getHeaderView(0)
        val tvRol = headerView.findViewById<TextView>(R.id.tvRol)
        tvRol.text = "Alumno"

    }
}