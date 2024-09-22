package com.example.mytodoapp

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mytodoapp.R
import com.example.mytodoapp.databinding.ActivityMainBinding
import java.util.*
import java.util.Locale

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: TaskDatabaseHelper
    private lateinit var tasksAdapter: tasksAdapter
    private var tasks: List<Task> = emptyList()
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // Initialize TextToSpeech
        tts = TextToSpeech(this, this)
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported")
                }
            } else {
                Log.e("TTS", "Initialization failed")
            }
        }
        // Initialize database and RecyclerView adapter
        db = TaskDatabaseHelper(this)
        tasks = db.getAllTasks()  // Load initial notes
        tasksAdapter = tasksAdapter(tasks, this)

        binding.tasksRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.tasksRecyclerView.adapter = tasksAdapter

        // Add button to open new activity to add a note
        binding.addButton.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale("en", "US")) // American English

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language not supported")
            }
        } else {
            Log.e("TTS", "Initialization failed")
        }
    }

    override fun onResume() {
        super.onResume()
        // Fetch updated notes from the database when the activity resumes
        tasks = db.getAllTasks()
        Log.d("MainActivity", "Tasks: $tasks") // Log to check fetched notes
        tasksAdapter.refreshData(tasks)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchView = menu.findItem(R.id.action_search)?.actionView as? SearchView
        searchView?.queryHint = "Search tasks"
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterTasks(newText ?: "")
                return true
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_ascending -> {
                sortTasks(SortOrder.ASCENDING)
                true
            }
            R.id.action_sort_descending -> {
                sortTasks(SortOrder.DESCENDING)
                true
            }
            R.id.action_sort_title -> {
                sortTasks(SortOrder.TITLE)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun readTaskContent(taskContent: String) {
        if (::tts.isInitialized) {
            if (taskContent.isNotEmpty()) {
                tts.stop()
                tts.speak(taskContent, TextToSpeech.QUEUE_FLUSH, null, null)
            } else {
                Toast.makeText(this, "Task content is empty", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Text-to-Speech not initialized", Toast.LENGTH_SHORT).show()
        }
    }

    private fun filterTasks(query: String) {
        val filteredTasks = tasks.filter {
            it.title.contains(query, ignoreCase = true) || it.content.contains(query, ignoreCase = true)
        }
        tasksAdapter.refreshData(filteredTasks)
    }

    private fun sortTasks(order: SortOrder) {
        val sortedTasks = when (order) {
            SortOrder.ASCENDING -> tasks.sortedBy { it.title }
            SortOrder.DESCENDING -> tasks.sortedByDescending { it.title }
            SortOrder.TITLE -> tasks.sortedBy { it.title }
        }
        tasksAdapter.refreshData(sortedTasks)
    }



    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }

    enum class SortOrder {
        ASCENDING,
        DESCENDING,
        TITLE
    }



}

