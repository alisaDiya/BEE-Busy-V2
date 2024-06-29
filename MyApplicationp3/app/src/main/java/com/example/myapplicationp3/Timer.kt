package com.example.myapplicationp3

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import android.widget.TimePicker
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class Timer : AppCompatActivity() {
    lateinit var spinner: Spinner
    lateinit var capButton: Button
    lateinit var edName: EditText
    lateinit var edDesc: EditText
    lateinit var startDateBtn: Button
    lateinit var startTimeBtn: Button
    lateinit var endDateBtn: Button
    lateinit var endTimeBtn: Button
    lateinit var pic: Button

    lateinit var btnAddCat: Button
    lateinit var editTextPersonName: EditText
    lateinit var array: ArrayList<String>
    lateinit var adapter: ArrayAdapter<String>

    lateinit var database: DatabaseReference
    lateinit var btnRead: Button
    lateinit var edMinGoal: EditText
    lateinit var edMaxGoal: EditText

    // globals
    var startDate: Date? = null
    var startTime: Date? = null
    var endDate: Date? = null
    var endTime: Date? = null

    val storage = FirebaseStorage.getInstance()
    // get UUID
    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseUser = firebaseAuth.currentUser

    val storageRef: StorageReference = storage.reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        edName = findViewById(R.id.edName)
        editTextPersonName = findViewById(R.id.editTextTextPersonName2)
        edDesc = findViewById(R.id.edDesc)
        spinner = findViewById(R.id.spinner)
        startDateBtn = findViewById(R.id.startDateBtn)
        startTimeBtn = findViewById(R.id.startTimeBtn)
        endDateBtn = findViewById(R.id.endDateBtn)
        endTimeBtn = findViewById(R.id.endTimeBtn)
        capButton = findViewById(R.id.capButton)
        pic = findViewById(R.id.pic)

        btnAddCat = findViewById(R.id.btnaddcat)
        btnRead = findViewById(R.id.btnRead)
        edMinGoal = findViewById(R.id.edMinGoal)
        edMaxGoal = findViewById(R.id.edMaxGoal)

        database = FirebaseDatabase.getInstance().reference

        // populate the spinner
        array = ArrayList()
        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, array)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Add category to spinner
        btnAddCat.setOnClickListener {
            val newCategory = editTextPersonName.text.toString()
            if (newCategory.isNotEmpty() && !array.contains(newCategory)) {
                array.add(newCategory)
                adapter.notifyDataSetChanged()
            }
        }

        startDateBtn.setOnClickListener { showDate(startDateListener) }
        endDateBtn.setOnClickListener { showDate(endDateListener) }
        startTimeBtn.setOnClickListener { showTimePicker(startTimeListener) }
        endTimeBtn.setOnClickListener { showTimePicker(endTimeListener) }

        capButton.setOnClickListener {
            val selectedItem = spinner.selectedItem as String
            val taskName = edName.text.toString()
            val taskDesc = edDesc.text.toString()
            val taskCategory = editTextPersonName.text.toString()
            val minGoal = edMinGoal.text.toString()
            val maxGoal = edMaxGoal.text.toString()

            if (taskName.isEmpty()) {
                edName.error = "Please enter a name"
                return@setOnClickListener
            }
            if (taskDesc.isEmpty()) {
                edDesc.error = "Please enter a description"
                return@setOnClickListener
            }
            if (minGoal.isEmpty()) {
                edMinGoal.error = "Cannot be empty"
                return@setOnClickListener
            }
            if (maxGoal.isEmpty()) {
                edMaxGoal.error = "Cannot be empty"
                return@setOnClickListener
            }
            if (taskCategory.isEmpty()) {
                editTextPersonName.error = "Please enter a category"
                return@setOnClickListener
            }

            saveToFirebase(selectedItem, taskName, taskDesc, taskCategory, minGoal, maxGoal)
        }

        pic.setOnClickListener {
            val intent = Intent(this, camera::class.java)
            startActivity(intent)
        }

        // calling the view records method
        btnRead.setOnClickListener {
            fetchAndDisplay()
        }
    }

    // method to pick the date
    fun showDate(dateSetListener: DatePickerDialog.OnDateSetListener) {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        // Create a DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            this,
            dateSetListener, year, month, day
        )

        // Show the DatePickerDialog
        datePickerDialog.show()
    }

    fun showTimePicker(timeSetListener: TimePickerDialog.OnTimeSetListener) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(
            this,
            timeSetListener, hour, minute, true
        )
        timePickerDialog.show()
    }

    // format --> fb --> date --> date util
    val startDateListener =
        DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, month, day)
            startDate = selectedCalendar.time
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val selectedDateString = dateFormat.format(startDate!!)
            startDateBtn.text = selectedDateString
        }

    val startTimeListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
        val selectedCalendar = Calendar.getInstance()
        selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        selectedCalendar.set(Calendar.MINUTE, minute)
        startTime = selectedCalendar.time
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val selectedTimeString = timeFormat.format(startTime!!)
        startTimeBtn.text = selectedTimeString
    }

    // end date
    val endDateListener =
        DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, month, day)
            endDate = selectedCalendar.time
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val selectedDateString = dateFormat.format(endDate!!)
            endDateBtn.text = selectedDateString
        }

    // end time listener
    val endTimeListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
        val selectedCalendar = Calendar.getInstance()
        selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        selectedCalendar.set(Calendar.MINUTE, minute)
        endTime = selectedCalendar.time
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val selectedTimeString = timeFormat.format(endTime!!)
        endTimeBtn.text = selectedTimeString
    }

    // method for firebase
    fun saveToFirebase(item: String, taskName: String, taskDesc: String, taskCategory: String, minGoal: String, maxGoal: String) {
        // formats
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        // fetch the value from the local btns text
        val startDateString = startDateBtn.text.toString()
        val startTimeString = startTimeBtn.text.toString()
        val endDateString = endDateBtn.text.toString()
        val endTimeString = endTimeBtn.text.toString()
        // parse values for firebase
        val startDate = dateFormat.parse(startDateString)
        val startTime = timeFormat.parse(startTimeString)
        val endDate = dateFormat.parse(endDateString)
        val endTime = timeFormat.parse(endTimeString)

        // cals
        val totalTimeInMillis = endDate.time - startDate.time + endTime.time - startTime.time
        val totalMinutes = totalTimeInMillis / (1000 * 60)
        val totalHours = totalMinutes / 60
        val minutesRemaining = totalMinutes % 60
        val totalTimeString = String.format(Locale.getDefault(), "%02d:%02d", totalHours, minutesRemaining)

        val key = database.child("items").push().key
        if (key != null) {
            val task = TaskModel(
                taskName, taskDesc, taskCategory, startDateString, startTimeString, endDateString, endTimeString, totalTimeString, minGoal, maxGoal
            )
            database.child("items").child(key).setValue(task)
                .addOnSuccessListener {
                    Toast.makeText(this, "Timesheet entry saved to database", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { err ->
                    Toast.makeText(this, "Error: ${err.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // method to view items from the db
    fun fetchAndDisplay() {
        database.child("items").get()
            .addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    val records = ArrayList<String>()
                    dataSnapshot.children.forEach { snapshot ->
                        val task = snapshot.getValue(TaskModel::class.java)
                        task?.let {
                            records.add(
                                "Name: ${it.taskName} \n" +
                                        "Desc: ${it.taskDesc}\n" +
                                        "Category: ${it.taskCategory}\n" +
                                        "Start Date: ${it.startDateString}\n" +
                                        "Start Time: ${it.startTimeString}\n" +
                                        "End Date: ${it.endDateString}\n" +
                                        "End Time: ${it.endTimeString}\n" +
                                        "Min Time: ${it.minGoal}\n" +
                                        "Max Time: ${it.maxGoal}\n" +
                                        "Total Hours Worked: ${it.totalTimeString}\n"
                            )
                        }
                    }
                    displayDialog(records)
                } else {
                    Toast.makeText(this, "No records found", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to fetch the data", Toast.LENGTH_SHORT).show()
            }
    }

    fun displayDialog(records: ArrayList<String>) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Database records")
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, records)
        builder.setAdapter(arrayAdapter, null)
        builder.setPositiveButton("Okay", null)
        builder.show()
    }
}

data class TaskModel(
    var taskName: String? = null,
    var taskDesc: String? = null,
    var taskCategory: String? = null,
    var startDateString: String? = null,
    var startTimeString: String? = null,
    var endDateString: String? = null,
    var endTimeString: String? = null,
    var totalTimeString: String? = null,
    var minGoal: String? = null,
    var maxGoal: String? = null,
    var userId: String? = null
) {

}