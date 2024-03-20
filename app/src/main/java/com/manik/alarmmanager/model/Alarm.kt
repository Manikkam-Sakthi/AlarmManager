package com.manik.alarmmanager.model

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.manik.alarmmanager.service.MyAlarmReceiver
import com.manik.alarmmanager.utils.Util
import java.util.*

data class Alarm(
    var alarmId: Int = 0,
    var hour: Int = 0,
    var minute: Int = 0,
    var time: String = "",
    var started: Boolean = false,
    var recurring: Boolean = false,
    var monday: Boolean = false,
    var tuesday: Boolean = false,
    var wednesday: Boolean = false,
    var thursday: Boolean = false,
    var friday: Boolean = false,
    var saturday: Boolean = false,
    var sunday: Boolean = false
) {
    @SuppressLint("MissingPermission")
    fun schedule(context: Context, showAlert: Boolean = false) {

        recurring = sunday || monday || tuesday || wednesday || thursday || friday || saturday

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, MyAlarmReceiver::class.java)
        intent.putExtra(ALARM_ID, alarmId)
        intent.putExtra(RECURRING, recurring)
        intent.putExtra(MONDAY, monday)
        intent.putExtra(TUESDAY, tuesday)
        intent.putExtra(WEDNESDAY, wednesday)
        intent.putExtra(THURSDAY, thursday)
        intent.putExtra(FRIDAY, friday)
        intent.putExtra(SATURDAY, saturday)
        intent.putExtra(SUNDAY, sunday)

        val alarmPendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, 0)

        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // if alarm time has already passed, increment day by 1
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1)
        }

        time = Util.convertTimeMillisToDateTime(calendar.timeInMillis)

        if (!recurring) {
            if (showAlert) {
                var toastText: String? = null
                try {
                    toastText =
                        "One Time Alarm scheduled at ${Util.convertTimeMillisToDateTime(calendar.timeInMillis)}"
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                Toast.makeText(context, toastText, Toast.LENGTH_LONG).show()
            }
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                alarmPendingIntent
            )
        } else {
            if (showAlert) {
                val toastText = String.format(
                    "Recurring Alarm scheduled at %02d:%02d",
                    hour,
                    minute,
                    alarmId
                )
                Toast.makeText(context, toastText, Toast.LENGTH_LONG).show()
            }
            val runDaily = (24 * 60 * 60 * 1000).toLong()
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                runDaily,
                alarmPendingIntent
            )
        }

        started = true

        // Log.e("Alarm", "Data: ${toString()}")

    }

    fun cancel(context: Context, showAlert: Boolean = false) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MyAlarmReceiver::class.java)
        val alarmPendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, 0)
        alarmManager.cancel(alarmPendingIntent)
        started = false
        if (showAlert) {
            val toastText =
                String.format("Alarm cancelled for %02d:%02d with id %d", hour, minute, alarmId)
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val MONDAY = "MONDAY"
        const val TUESDAY = "TUESDAY"
        const val WEDNESDAY = "WEDNESDAY"
        const val THURSDAY = "THURSDAY"
        const val FRIDAY = "FRIDAY"
        const val SATURDAY = "SATURDAY"
        const val SUNDAY = "SUNDAY"
        const val RECURRING = "RECURRING"
        const val ALARM_ID = "ALARM_ID"
    }
}