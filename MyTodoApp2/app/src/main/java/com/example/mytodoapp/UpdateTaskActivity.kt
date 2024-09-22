package com.example.mytodoapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
//import com.example.memoire.databinding.ActivityUpdateBinding
import com.example.mytodoapp.databinding.ActivityUpdateBinding

class UpdateTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateBinding
    private lateinit var db: TaskDatabaseHelper
    private var taskId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = TaskDatabaseHelper(this)
        taskId = intent.getIntExtra("task_id", -1)
        if (taskId == -1) {
            finish()
            return
        }

        val task = db.getTaskById(taskId)



        binding.updateSaveButton.setOnClickListener {
            val newTitle = binding.updateTitleEditText.text.toString()
            val newContent = binding.updateContentEditText.text.toString()

            // Retrieve the existing note from the database
            val existingTask = db.getTaskById(taskId)

            if (existingTask != null) {
                val updatedTask = Task(
                    id = taskId,
                    title = newTitle,
                    content = newContent,
                    createdAt = existingTask.createdAt,  // Keep the original creation time
                    updatedAt = System.currentTimeMillis() // Set updatedAt to the current time
                )
                db.updateTask(updatedTask)
                finish()
                Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show()
            }
        }
    }
}