package com.manik.alarmmanager.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.manik.alarmmanager.R
import com.manik.alarmmanager.model.Alarm
import com.manik.alarmmanager.ui.MainActivity
import com.manik.alarmmanager.utils.AlarmReceiver
import com.manik.alarmmanager.utils.Util
import java.util.Calendar
import kotlin.random.Random

class MyAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        if (Intent.ACTION_BOOT_COMPLETED == intent!!.action) {
            Log.e("MyAlarmReceiver","Alarm Reboot")
            // Reschedule Alarms while device is reboot
            Util.rescheduleAlarms(context!!)
        } else {
            Log.e("MyAlarmReceiver","Alarm Received")
            if (!intent.getBooleanExtra(Alarm.RECURRING, false)) {
                // Stop One time alarm
                sendNotification(context!!, intent)
                val alarm = Util.getAlarmById(context, intent.getIntExtra(Alarm.ALARM_ID, 0))
                if (alarm != null) {
                    alarm.cancel(context, false)
                    Util.updateAlarm(context, alarm)
                    AlarmReceiver.onUpdate.value = true
                }
            } else if (alarmIsToday(intent)) {
                sendNotification(context!!, intent)
            }
        }
    }

    private fun alarmIsToday(intent: Intent): Boolean {
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> {
                return intent.getBooleanExtra(Alarm.MONDAY, false)
            }
            Calendar.TUESDAY -> {
                return intent.getBooleanExtra(Alarm.TUESDAY, false)
            }
            Calendar.WEDNESDAY -> {
                return intent.getBooleanExtra(Alarm.WEDNESDAY, false)
            }
            Calendar.THURSDAY -> {
                return intent.getBooleanExtra(Alarm.THURSDAY, false)
            }
            Calendar.FRIDAY -> {
                return intent.getBooleanExtra(Alarm.FRIDAY, false)
            }
            Calendar.SATURDAY -> {
                return intent.getBooleanExtra(Alarm.SATURDAY, false)
            }
            Calendar.SUNDAY -> {
                return intent.getBooleanExtra(Alarm.SUNDAY, false)
            }
        }
        return false
    }

    private fun sendNotification(context: Context, alarmIntent: Intent) {

        val sb = StringBuilder()
        sb.append("ID: ${alarmIntent.getIntExtra(Alarm.ALARM_ID, 0)}")
        sb.append("\n")

        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = context.getString(R.string.app_name)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Alarm Received")
            .setContentText(sb.toString())
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "General",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(Random.nextInt(1, 1000), notificationBuilder.build())
    }

}