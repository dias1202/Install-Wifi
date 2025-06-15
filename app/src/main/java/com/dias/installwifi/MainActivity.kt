package com.dias.installwifi

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.dias.installwifi.data.ResultState
import com.dias.installwifi.databinding.ActivityMainBinding
import com.dias.installwifi.view.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var isGrid = false
    private var isTechnician = false
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewModel.getSession()
        observeUserSession()

        setSupportActionBar(binding.topAppBar)
        initNavigation()
    }

    private fun initNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController

        setupNavigationListeners()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setupNavigationListeners() {
        // Update UI berdasarkan destination
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

    private fun observeUserSession() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.getSessionResult.collect { result ->
                    when (result) {
                        is ResultState.Success -> {
                            isTechnician = result.data.isTechnician == true
                            setupUIBasedOnRole(isTechnician)
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    private fun setupUIBasedOnRole(isTechnician: Boolean) {
        runOnUiThread {
            try {
                // 1. Dapatkan NavController segar
                val navHostFragment = supportFragmentManager
                    .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
                val navController = navHostFragment.navController

                // 2. Inflate graph yang sesuai
                val graph = navController.navInflater.inflate(
                    if (isTechnician) R.navigation.mobile_navigation_technician
                    else R.navigation.mobile_navigation_user
                )

                // 3. Set graph baru
                navController.graph = graph

                // 4. Update bottom nav menu
                binding.navView.menu.clear()
                binding.navView.inflateMenu(
                    if (isTechnician) R.menu.bottom_nav_technician
                    else R.menu.bottom_nav_user
                )

                // 5. Setup dengan NavController
                binding.navView.setupWithNavController(navController)

                // 6. Update title
                binding.topAppBar.title = if (isTechnician) {
                    getString(R.string.app_name_technician)
                } else {
                    getString(R.string.app_name)
                }

            } catch (e: Exception) {
                navController.navigate(R.id.navigation_home) {
                    popUpTo(R.id.navigation_home) { inclusive = true }
                }
            }
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