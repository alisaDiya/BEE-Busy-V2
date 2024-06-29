package com.example.myapplicationp3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView

class MainActivity : AppCompatActivity() {
    lateinit var imageView2 : ImageView


    //timer delay -- splash
    //android rule -- 5 secs ie 5000ms
    val delay : Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageView2 = findViewById(R.id.imageView2)


        //handler to loop the splashy
        Handler(Looper.getMainLooper()).postDelayed({
            //start next activity
            val intent = Intent(this@MainActivity,
                aboutscreen::class.java)
            startActivity(intent)
            finish()
        },delay)
       }
}