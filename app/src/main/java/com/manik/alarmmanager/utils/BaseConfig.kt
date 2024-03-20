package com.manik.alarmmanager.utils

import android.content.Context

class BaseConfig(private val context: Context) {

    val prefs = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_KEY = "AlarmPrefs"
        private const val ALARM_DATA = "AlarmDetails"

        fun newInstance(context: Context) = BaseConfig(context)
    }

    var alarmDetails: String
        get() = prefs.getString(ALARM_DATA, "")!!
        set(alarmDetails) = prefs.edit().putString(ALARM_DATA, alarmDetails).apply()
}