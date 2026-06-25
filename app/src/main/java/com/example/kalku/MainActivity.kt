package com.example.kalku

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.kalku.databinding.ActivityMainBinding
import com.example.kalku.history.HistoryFragment
import com.example.kalku.home.HomeFragment
import com.example.kalku.login.LoginActivity
import com.example.kalku.product.ProductFragment
import com.example.kalku.profile.ProfileFragment
import com.example.kalku.utils.SessionManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!SessionManager(this).isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                left = systemBars.left,
                top = systemBars.top,
                right = systemBars.right,
                bottom = systemBars.bottom
            )
            insets
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            showFragment(item.itemId)
            true
        }

        if (savedInstanceState == null) {
            showFragment(R.id.nav_home)
            binding.bottomNavigation.menu.findItem(R.id.nav_home).isChecked = true
        }
    }

    fun openTab(menuItemId: Int) {
        binding.bottomNavigation.selectedItemId = menuItemId
    }

    private fun showFragment(itemId: Int) {
        val fragment: Fragment = when (itemId) {
            R.id.nav_products -> ProductFragment()
            R.id.nav_history -> HistoryFragment()
            R.id.nav_profile -> ProfileFragment()
            else -> HomeFragment()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
