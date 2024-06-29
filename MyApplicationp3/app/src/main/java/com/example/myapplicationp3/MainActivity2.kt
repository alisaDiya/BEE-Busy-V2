package com.example.myapplicationp3

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.DropBoxManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity2 : AppCompatActivity() {
    lateinit var btn30days: Button
    lateinit var btnSelect: Button
    lateinit var txtStartDate: TextView

    // Firebase
    lateinit var database: DatabaseReference
    val storage = FirebaseStorage.getInstance()
    // Get UUID
    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseUser = firebaseAuth.currentUser

    var startDate: Date? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        txtStartDate = findViewById(R.id.tvStartDate)

        btn30days = findViewById(R.id.btn30Days)
        btnSelect = findViewById(R.id.btnSelect)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().reference

        txtStartDate.setOnClickListener { displayDatePicker(startDateListener) }

        btnSelect.setOnClickListener {
            if (txtStartDate.text.isNotEmpty()) {
                dateOnly()
            } else {
                txtStartDate.error = "Please Select Date"
            }
        }

        btn30days.setOnClickListener { thirtyDayGraph() }
    }

    private fun dateOnly() {
        val dateGiven = txtStartDate.text.toString()
        if (dateGiven.isNotEmpty()) {
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Building...")
            progressDialog.show()

            val userId = firebaseAuth.currentUser?.uid
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            var dayCounter = 0f

            val userQuery = database.child("items")
                .orderByChild("userId")
                .equalTo(userId)

            userQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dateQuery = snapshot.ref.orderByChild("startDateString").equalTo(dateGiven)
                    dateQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dateSnapshot: DataSnapshot) {
                            val totalTimesForDate = mutableSetOf<String>()
                            val minGoalsList = mutableListOf<Entry>()
                            val maxGoalsList = mutableListOf<Entry>()

                            for (entrySnapshot in dateSnapshot.children) {
                                val entry = entrySnapshot.getValue(TaskModel::class.java)
                                if (entry != null && !entry.totalTimeString.isNullOrEmpty()) {
                                    totalTimesForDate.add(entry.totalTimeString!!)
                                    // Get MinGoal
                                    entry.minGoal?.let { minGoal ->
                                        minGoalsList.add(
                                            Entry(
                                                dayCounter,
                                                minGoal.toFloat()
                                            )
                                        )
                                    }
                                    // Get MaxGoal
                                    entry.maxGoal?.let { maxGoal ->
                                        maxGoalsList.add(
                                            Entry(
                                                dayCounter,
                                                maxGoal.toFloat()
                                            )
                                        )
                                    }
                                    dayCounter += 1f
                                }
                            }

                            if (totalTimesForDate.isNotEmpty()) {
                                val graphEntries = mutableListOf<Entry>()
                                var newDayCounter = 0f

                                for (totalTime in totalTimesForDate) {
                                    val (hours, minutes) = totalTime.split(":").map { it.toFloatOrNull() ?: 0f }
                                    val totalHours = hours + (minutes / 60)
                                    graphEntries.add(Entry(newDayCounter, totalHours))
                                    newDayCounter += 1f
                                }

                                val lineDataSet1 = LineDataSet(graphEntries, "Total Hours")
                                val lineDataSet2 = LineDataSet(minGoalsList, "Min Goal")
                                lineDataSet2.color = Color.GREEN
                                val lineDataSet3 = LineDataSet(maxGoalsList, "Max Goal")
                                lineDataSet3.color = Color.RED

                                val lineData = LineData(lineDataSet1, lineDataSet2, lineDataSet3)
                                val chart = findViewById<LineChart>(R.id.lineChart)
                                chart.data = lineData
                                chart.invalidate()
                                progressDialog.dismiss()
                            } else {
                                Toast.makeText(
                                    this@MainActivity2,
                                    "No data available",
                                    Toast.LENGTH_SHORT
                                ).show()
                                progressDialog.dismiss()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle database error
                            progressDialog.dismiss()
                        }
                    })
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                    progressDialog.dismiss()
                }
            })
        } else {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
        }
    }

    private fun thirtyDayGraph() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Building...")
        progressDialog.show()

        val totalTimeList = mutableListOf<String>()
        val minGoalsList = mutableListOf<Entry>()
        val maxGoalsList = mutableListOf<Entry>()

        val userId = firebaseAuth.currentUser?.uid
        var dayCounter = 0f

        val query = database.child("items")
            .orderByChild("userId")
            .equalTo(userId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (entrySnapshot in snapshot.children) {
                    val entry = entrySnapshot.getValue(TaskModel::class.java)
                    if (entry != null) {
                        totalTimeList.add(entry.totalTimeString ?: "")
                        // Get MinGoal
                        entry.minGoal?.let { minGoal ->
                            minGoalsList.add(Entry(dayCounter, minGoal.toFloat()))
                        }
                        // Get MaxGoal
                        entry.maxGoal?.let { maxGoal ->
                            maxGoalsList.add(Entry(dayCounter, maxGoal.toFloat()))
                        }
                        dayCounter += 1f
                    }
                }

                if (totalTimeList.isNotEmpty()) {
                    val graphEntries = mutableListOf<Entry>()
                    var newDayCounter = 0f
                    for (totalTime in totalTimeList.takeLast(30)) {
                        val (hours, minutes) = totalTime.split(":").map { it.toFloatOrNull() ?: 0f }
                        val totalHours = hours + (minutes / 60)
                        graphEntries.add(Entry(newDayCounter, totalHours))
                        newDayCounter += 1f
                    }

                    val lineDataSet1 = LineDataSet(graphEntries, "Total Hours")
                    lineDataSet1.color = Color.BLUE
                    val lineDataSet2 = LineDataSet(minGoalsList.takeLast(30), "Min Goal")
                    lineDataSet2.color = Color.GREEN
                    val lineDataSet3 = LineDataSet(maxGoalsList.takeLast(30), "Max Goal")
                    lineDataSet3.color = Color.RED

                    val lineData = LineData(lineDataSet1, lineDataSet2, lineDataSet3)
                    val chart = findViewById<LineChart>(R.id.lineChart)
                    chart.data = lineData
                    chart.invalidate()
                    progressDialog.dismiss()
                } else {
                    Toast.makeText(
                        this@MainActivity2,
                        "No data available",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressDialog.dismiss()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
                progressDialog.dismiss()
            }
        })
    }

    // Date picker listener for the start date
    private val startDateListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        startDate = calendar.time
        updateStartDateText()
    }

    // Update the text field with the selected start date
    private fun updateStartDateText() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        txtStartDate.text = dateFormat.format(startDate)
    }

    private fun displayDatePicker(dateSetListener: DatePickerDialog.OnDateSetListener) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, dateSetListener, year, month, day).show()
    }
}