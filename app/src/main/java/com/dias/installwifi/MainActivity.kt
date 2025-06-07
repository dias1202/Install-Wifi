package com.dias.installwifi

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.dias.installwifi.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var isGrid = false

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(binding.topAppBar)

        val navView: BottomNavigationView = binding.navView

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController

        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_edit_profile -> {
                    binding.apply {
                        navView.visibility = View.GONE
                        topAppBar.navigationIcon = getDrawable(R.drawable.ic_back)
                        topAppBar.setNavigationOnClickListener {
                            navController.navigateUp()
                        }
                    }
                }

                else -> {
                    binding.apply {
                        navView.visibility = View.VISIBLE
                        topAppBar.navigationIcon = null
                    }
                }
            }
            binding.topAppBar.menu.findItem(R.id.action_toggle_layout)?.isVisible =
                destination.id == R.id.navigation_package
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)
        menu?.findItem(R.id.action_toggle_layout)?.isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_toggle_layout -> {
                isGrid = !isGrid

                item.setIcon(if (isGrid) R.drawable.ic_list_32dp else R.drawable.ic_grid_32dp)

                val navHostFragment =
                    supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
                navHostFragment.childFragmentManager.setFragmentResult(
                    "toggle_layout",
                    Bundle().apply { putBoolean("isGrid", isGrid) }
                )

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}