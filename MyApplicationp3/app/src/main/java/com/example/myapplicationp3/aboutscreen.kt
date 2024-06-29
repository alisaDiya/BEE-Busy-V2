package com.example.myapplicationp3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class aboutscreen : AppCompatActivity() {
    lateinit var imageView: ImageView
    lateinit var TVsolgan1: TextView
    lateinit var TVabout1: TextView
    lateinit var btnlgs: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aboutscreen)

        imageView=findViewById(R.id.imageView)
        TVsolgan1=findViewById(R.id.TVsolgan1)
        TVabout1=findViewById(R.id.TVabout1)
        btnlgs=findViewById(R.id.btnlgs)
        btnlgs.setOnClickListener()
        {
            tonext()
        }
    }
    private fun tonext()
    {
        val intent = Intent(this@aboutscreen,Login::class.java)
        startActivity(intent)
    }
}