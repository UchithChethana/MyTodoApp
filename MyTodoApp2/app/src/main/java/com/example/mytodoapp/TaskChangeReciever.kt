package com.example.mytodoapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.appwidget.AppWidgetManager
import android.content.ComponentName

class TaskChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, MyWidgetProvider::class.java)

        // Get all widget IDs and update them
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_content)

        // Update the widget
        for (appWidgetId in appWidgetIds) {
            MyWidgetProvider.updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }


}