package com.example.hogwartsasiermartinez

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.hogwartsasiermartinez.Auxiliar.Sesion
import com.example.hogwartsasiermartinez.databinding.ActivityProfesorBinding

class ProfesorActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfesorBinding
    private var usuarioId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfesorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //aplico el color de la casa
        if (Sesion.colorCasa.isNotEmpty()) {
            try {
                val color = Color.parseColor(Sesion.colorCasa)
                binding.toolbar.setBackgroundColor(color)
                window.statusBarColor = color
                val headerView = binding.navView.getHeaderView(0)
                headerView.setBackgroundColor(color)
            } catch (e: IllegalArgumentException) {

            }
        }

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

        val menu = binding.navView.menu
        menu.findItem(R.id.nav_usuarios).isVisible = false
        menu.findItem(R.id.nav_asignaturas).isVisible = false
        menu.findItem(R.id.nav_pociones).isVisible = false
        menu.findItem(R.id.nav_hechizos).isVisible = false

        if (savedInstanceState == null) {
            replaceFragment(FragmentoAsignaturas())
            binding.bottomNavView.selectedItemId = R.id.nav_asignaturas
        }

        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_ranking -> {
                    replaceFragment(FragmentoRankingCasas())
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        binding.bottomNavView.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_asignaturas -> FragmentoAsignaturas()
                R.id.nav_pociones -> FragmentoPociones()
                R.id.nav_hechizos -> FragmentoHechizos()
                else -> null
            }
            if (fragment != null) {
                replaceFragment(fragment)
                true
            } else {
                false
            }
        }

        val imgPerfil = binding.toolbar.findViewById<ImageView>(R.id.imgPerfil)
        imgPerfil.setOnClickListener {
            val fragment = FragmentoPerfil()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }

        val headerView = binding.navView.getHeaderView(0)
        val tvRol = headerView.findViewById<TextView>(R.id.tvRol)
        tvRol.text = "Profesor"
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
