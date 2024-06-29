package com.example.myapplicationp3

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationp3.databinding.ActivityLandingBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.Random

class landing : AppCompatActivity() {
    private val quotes = listOf(
        "Believe you can and you're halfway there.",
        "Your limitation—it’s only your imagination.",
        "Push yourself, because no one else is going to do it for you.",
        "Success doesn’t just find you. You have to go out and get it.",
        "The harder you work for something, the greater you’ll feel when you achieve it.",
        "Dream bigger. Do bigger.",
        "Don’t stop when you’re tired. Stop when you’re done.",
        "Wake up with determination. Go to bed with satisfaction.",
        "Do something today that your future self will thank you for.",
        "Little things make big days."
    )

    lateinit var quoteTextView: TextView


    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityLandingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        quoteTextView = findViewById(R.id.quoteTextView)
       // Initialize after setContentView

        setSupportActionBar(binding.appBarLanding.toolbar)

        binding.appBarLanding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_landing)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_Timer -> {
                    // Go to that screen
                    val intent = Intent(this@landing, Timer::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_pomodoro -> {
                    // Go to that screen
                    val intent = Intent(this@landing, pomodoro::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_goal -> {
                    // Go to that screen
                    val intent = Intent(this@landing, goalss::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_graphgoal -> {
                    // Go to that screen
                    val intent = Intent(this@landing, MainActivity2::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_aboutscreen1 -> {
                    // Go to that screen
                    val intent = Intent(this@landing, aboutscreen::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_logout -> {
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    if (currentUser != null) {
                        FirebaseAuth.getInstance().signOut()
                        Toast.makeText(this, "You are logged out", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@landing, register::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "You are still logged in", Toast.LENGTH_SHORT).show()
                    }
                    true
                }

                else -> false
            }
        }

        runnable = Runnable {
            val randomQuote = quotes[Random().nextInt(quotes.size)]
            quoteTextView.text = randomQuote
            handler.postDelayed(runnable, 120000) // 120000 milliseconds = 2 minutes
        }

        handler.post(runnable)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.landing, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_landing)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }
}
