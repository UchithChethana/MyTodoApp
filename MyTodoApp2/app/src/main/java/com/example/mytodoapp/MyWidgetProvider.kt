package com.example.mytodoapp

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews

class MyWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // Update all widgets
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_layout)

            // Fetch notes from the database
            val db = TaskDatabaseHelper(context)
            val notes = db.getAllTasks()

            // Display the first note in the widget
            if (notes.isNotEmpty()) {
                views.setTextViewText(R.id.widget_title, notes[0].title)
                views.setTextViewText(R.id.widget_content, notes[0].content)
            } else {
                views.setTextViewText(R.id.widget_title, "No Notes")
                views.setTextViewText(R.id.widget_content, "Add a note to see it here.")
            }

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}