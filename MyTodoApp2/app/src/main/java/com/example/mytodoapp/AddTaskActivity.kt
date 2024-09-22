package com.example.mytodoapp

import android.R
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mytodoapp.Task
import com.example.mytodoapp.databinding.ActivityAddTaskBinding

class AddTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding
    private lateinit var db: TaskDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db =TaskDatabaseHelper(this)

        // Populate category spinner
        val categories = arrayOf("Work", "Personal", "Shopping")
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)

        binding.saveButton.setOnClickListener {
            val title = binding.titleEditText.text.toString()
            val content = binding.contentEditText.text.toString()

            // Get the current timestamp for createdAt and updatedAt
            val currentTime = System.currentTimeMillis()

            // Create a new note with the current time for createdAt and updatedAt
            val task = Task(0, title, content, createdAt = currentTime, updatedAt = currentTime)

            db.insertTask(task)
            finish()
            Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show()
        }
    }
}