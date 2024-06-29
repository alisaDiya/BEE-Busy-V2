package com.example.myapplicationp3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import java.text.SimpleDateFormat
import java.util.Date

class goalss : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var btnSave: Button
    lateinit var btnClear: Button
    lateinit var etMinHours: EditText
    lateinit var etMaxHours: EditText
    lateinit var tvgoals: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goalss)

        auth = FirebaseAuth.getInstance()
        btnClear = findViewById(R.id.btnClear)
        btnSave = findViewById(R.id.btnSave)
        etMinHours = findViewById(R.id.etMinHours)
        tvgoals = findViewById(R.id.tvgoals)
        etMaxHours = findViewById(R.id.etMaxHours)

        btnSave.setOnClickListener {
            val minHours = etMinHours.text.toString().toIntOrNull()
            val maxHours = etMaxHours.text.toString().toIntOrNull()

            if (minHours != null && maxHours != null) {
                displayGoals(minHours, maxHours)
            } else {
                Toast.makeText(this, "Please input some values", Toast.LENGTH_SHORT).show()
            }
        }

        btnClear.setOnClickListener {
            clearGoals()
        }
    }

    private fun displayGoals(minHours: Int, maxHours: Int) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = dateFormat.format(Date())
        tvgoals.text = "Your goal for $currentDate:\nMinimum Hours: $minHours\nMaximum Hours: $maxHours"
    }

    private fun clearGoals() {
        tvgoals.text = ""
    }
}