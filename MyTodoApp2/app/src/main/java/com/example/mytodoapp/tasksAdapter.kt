
package com.example.mytodoapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class tasksAdapter(
    private var tasksList: List<Task>,
    private val context: Context
) : RecyclerView.Adapter<tasksAdapter.TaskViewHolder>() {

    private val db: TaskDatabaseHelper = TaskDatabaseHelper(context)

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        val updateButton: ImageView = itemView.findViewById(R.id.updateButton)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int =tasksList.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasksList[position]
        val timeAgo = db.getTimeAgo(task.updatedAt)  // Get the time ago string

        holder.titleTextView.text = task.title
        holder.contentTextView.text = task.content
        holder.timeTextView.text = timeAgo  // Display the time ago string

        holder.updateButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdateTaskActivity::class.java).apply {
                putExtra("task_id", task.id)
            }
            holder.itemView.context.startActivity(intent)
        }

        holder.deleteButton.setOnClickListener {
            db.deleteTask(task.id)
            refreshData(db.getAllTasks())
            Toast.makeText(holder.itemView.context, "Task deleted", Toast.LENGTH_SHORT).show()
        }
    }

    fun refreshData(newTasks: List<Task>) {
        tasksList = newTasks
        notifyDataSetChanged()
    }
}