package com.walidvoyages.app

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

object DuaNotifications {
    const val PREFS = "walid_user_settings"
    const val MORNING = "morning_enabled"
    const val EVENING = "evening_enabled"
    const val CHANNEL = "duas_reminders_v1"

    fun permitted(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL, "أدعية وأذكار وليد للأسفار", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "تذكيرات محلية اختيارية بأدعية الصباح والمساء"
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun applySchedule(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        schedule(context, "walid_dua_morning", prefs.getBoolean(MORNING, false), 9)
        schedule(context, "walid_dua_evening", prefs.getBoolean(EVENING, false), 20)
    }

    private fun schedule(context: Context, name: String, enabled: Boolean, hour: Int) {
        val manager = WorkManager.getInstance(context)
        if (!enabled) { manager.cancelUniqueWork(name); return }
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (!after(now)) add(Calendar.DAY_OF_YEAR, 1)
        }
        val delay = (target.timeInMillis - now.timeInMillis).coerceAtLeast(1000L)
        val request = PeriodicWorkRequestBuilder<DuaReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag(name)
            .build()
        manager.enqueueUniquePeriodicWork(name, ExistingPeriodicWorkPolicy.UPDATE, request)
    }

    fun sendTest(context: Context) {
        createChannel(context)
        post(context, "تذكير من وليد للأسفار", "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ.", 700)
    }

    fun post(context: Context, title: String, message: String, id: Int) {
        if (!permitted(context)) return
        createChannel(context)
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("tab", 3)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pending = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(context, CHANNEL)
            .setSmallIcon(R.drawable.ic_stat_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setAutoCancel(true)
            .setContentIntent(pending)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        try { NotificationManagerCompat.from(context).notify(id, notification) }
        catch (_: SecurityException) { /* user declined permission */ }
    }
}

class DuaReminderWorker(context: Context, params: WorkerParameters): Worker(context, params) {
    override fun doWork(): Result {
        val current = Calendar.getInstance()
        val chosen = AppContent.duas[(current.get(Calendar.DAY_OF_YEAR) + current.get(Calendar.YEAR)) % AppContent.duas.size]
        val partOfDay = if (current.get(Calendar.HOUR_OF_DAY) < 15) "دعاء اليوم 🌤" else "ذكر المساء 🌙"
        DuaNotifications.post(applicationContext, "$partOfDay • ${chosen.title}", chosen.text, if (current.get(Calendar.HOUR_OF_DAY) < 15) 901 else 902)
        return Result.success()
    }
}
