package com.example.workhourstracker

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var drawer: DrawerLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this,drawer,toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close)
        drawer?.addDrawerListener(toggle)
        toggle.syncState()

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, DashboardFragment())
                .commit()
            navigationView.setCheckedItem(R.id.nav_dashboard)
        }
    }

    override fun onBackPressed() {
        if(drawer?.isDrawerOpen(GravityCompat.START)!!){
            drawer?.closeDrawer(GravityCompat.START)
        }else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_dashboard ->{
                toolbar?.title = "Dashboard"
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, DashboardFragment())
                    .commit()
            }
            R.id.nav_calculator -> {
                toolbar?.title = "Calculator"
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, CalculatorFragment())
                    .commit()
            }
            R.id.nav_calendar -> {
                var toolbar = findViewById<Toolbar>(R.id.toolbar)
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, CalendarFragment(toolbar))
                    .commit()
            }
            R.id.nav_settings -> {
                toolbar?.title = "Settings"
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, SettingsFragment())
                    .commit()
            }
            R.id.nav_share -> {
                Toast.makeText(this,"Share",Toast.LENGTH_SHORT).show()
            }
            R.id.nav_send -> {
                Toast.makeText(this,"Send",Toast.LENGTH_SHORT).show()
            }
        }

        drawer?.closeDrawer(GravityCompat.START)
        return true
    }

}
