package com.example.trainer.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.trainer.R
import com.example.trainer.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra("TYPE") ?: "WORKOUT"

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(context)

                var title = ""
                var text = ""
                var notificationId = 1001

                when (type) {
                    "WORKOUT" -> {
                        val calendar = Calendar.getInstance()
                        val javaDay = calendar.get(Calendar.DAY_OF_WEEK)
                        val appDay = if (javaDay == Calendar.SUNDAY) 6 else javaDay - 2

                        val scheduleItem = db.workoutDao().getScheduleForDay(appDay)

                        if (scheduleItem != null && !scheduleItem.workoutName.isNullOrEmpty()) {
                            title = "Dzisiejszy trening: ${scheduleItem.workoutName} 💪"
                            text = "Plan jest gotowy. Czekamy na Ciebie!"
                            notificationId = 100
                        } else {
                            return@launch
                        }
                    }
                    "BREAKFAST" -> {
                        title = "Czas na śniadanie! 🍳"
                        text = "Rozpocznij dzień dobrze – zapisz swoje śniadanie."
                        notificationId = 201
                    }
                    "LUNCH" -> {
                        title = "Czas na lunch 🥗"
                        text = "Nie zapomnij o jedzeniu, Twój organizm potrzebuje energii."
                        notificationId = 202
                    }
                    "DINNER" -> {
                        title = "Kolacja czeka 🍽️"
                        text = "Białko dla regeneracji mięśni. Smacznego!"
                        notificationId = 203
                    }
                    "WEIGHT" -> {
                        title = "Kontrola wagi ⚖️"
                        text = "Czas się zważyć i zapisać swoje postępy z danego tygodnia."
                        notificationId = 300
                    }
                }

                showNotification(context, title, text, notificationId)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun showNotification(context: Context, title: String, text: String, id: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, "workout_channel")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(id, notification)
    }
}