package com.example.myapplicationp3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import java.util.concurrent.TimeUnit

class pomodoro : AppCompatActivity() {
    private lateinit var timerTextView: TextView
    private lateinit var startButton: Button
    private lateinit var resetButton: Button
    private lateinit var durationSpinner: Spinner

    private lateinit var timer: CountDownTimer
    private var isTimerRunning = false
    private var selectedDuration: Long = TimeUnit.MINUTES.toMillis(25)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pomodoro)
        timerTextView = findViewById(R.id.timerTextView)
        startButton = findViewById(R.id.startButton)
        resetButton = findViewById(R.id.resetButton)
        durationSpinner = findViewById(R.id.durationSpinner)

        val durationOptions = arrayOf("25 minutes", "45 minutes", "60 minutes") // Updated options
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, durationOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        durationSpinner.adapter = adapter

        this.durationSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedDuration = when (position) {
                    0 -> TimeUnit.MINUTES.toMillis(25)
                    1 -> TimeUnit.MINUTES.toMillis(45)
                    2 -> TimeUnit.MINUTES.toMillis(60)
                    else -> TimeUnit.MINUTES.toMillis(25)
                }
                resetTimer()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case where nothing is selected if needed
            }
        })

        timer = createTimer(selectedDuration)

        startButton.setOnClickListener {
            if (isTimerRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        resetButton.setOnClickListener {
            resetTimer()
        }
    }

    private fun createTimer(timeInMillis: Long): CountDownTimer {
        return object : CountDownTimer(timeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                updateTimerText(millisUntilFinished)
            }

            override fun onFinish() {
                timerTextView.text = "00:00"
                isTimerRunning = false
            }
        }
    }

    private fun startTimer() {
        timer.start()
        startButton.text = "Pause"
        isTimerRunning = true
    }

    private fun pauseTimer() {
        timer.cancel()
        startButton.text = "Start"
        isTimerRunning = false
    }

    private fun resetTimer() {
        timer.cancel()
        timer = createTimer(selectedDuration)
        updateTimerText(selectedDuration)
        startButton.text = "Start"
        isTimerRunning = false
    }

    private fun updateTimerText(timeInMillis: Long) {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMillis) % 60
        val timeFormatted = String.format("%02d:%02d", minutes, seconds)
        timerTextView.text = timeFormatted
    }
}