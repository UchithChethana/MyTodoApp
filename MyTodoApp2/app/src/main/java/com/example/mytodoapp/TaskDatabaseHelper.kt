
package com.example.mytodoapp

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.text.format.DateUtils
import java.util.*

// Database connection

class TaskDatabaseHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "tasksapp.db"
        private const val DATABASE_VERSION = 2
        private const val TABLE_NAME = "alltasks"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_CONTENT = "content"
        private const val COLUMN_CREATED_AT = "created_at"
        private const val COLUMN_UPDATED_AT = "updated_at"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, 
                $COLUMN_TITLE TEXT, 
                $COLUMN_CONTENT TEXT, 
                $COLUMN_CREATED_AT INTEGER,
                $COLUMN_UPDATED_AT INTEGER
            )
        """
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db?.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_CREATED_AT INTEGER")
            db?.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_UPDATED_AT INTEGER")
        }
    }

    fun insertTask(task: Task) {
        val db = writableDatabase
        val currentTime = System.currentTimeMillis()
        val values = ContentValues().apply {
            put(COLUMN_TITLE, task.title)
            put(COLUMN_CONTENT, task.content)
            put(COLUMN_CREATED_AT, currentTime)
            put(COLUMN_UPDATED_AT, currentTime)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()

        // Send broadcast to update widget
        val intent = Intent(context, TaskChangeReceiver::class.java)
        context.sendBroadcast(intent)
    }

    fun getAllTasks(): List<Task> {
        val tasksList = mutableListOf<Task>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
            val createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT))
            val updatedAt = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_UPDATED_AT))

            val task = Task(id, title, content, createdAt, updatedAt)
            tasksList.add(task)
        }

        cursor.close()
        db.close()
        return tasksList
    }

    fun updateTask(task: Task) {
        val db = writableDatabase
        val currentTime = System.currentTimeMillis()
        val values = ContentValues().apply {
            put(COLUMN_TITLE, task.title)
            put(COLUMN_CONTENT, task.content)
            put(COLUMN_UPDATED_AT, currentTime)
        }
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(task.id.toString())
        db.update(TABLE_NAME, values, whereClause, whereArgs)
        db.close()

        // Send broadcast to update widget
        val intent = Intent(context, TaskChangeReceiver::class.java)
        context.sendBroadcast(intent)
    }

    fun deleteTask(taskId: Int) {
        val db = writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(taskId.toString())
        db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()

        // Send broadcast to update widget
        val intent = Intent(context, TaskChangeReceiver::class.java)
        context.sendBroadcast(intent)
    }

    fun getTaskById(taskId: Int): Task? {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = $taskId"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()

        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
        val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
        val createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT))
        val updatedAt = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_UPDATED_AT))

        cursor.close()
        db.close()
        return Task(id, title, content, createdAt, updatedAt)
    }

    // Helper function to get time ago
    fun getTimeAgo(time: Long): String {
        return DateUtils.getRelativeTimeSpanString(time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS).toString()
    }
}