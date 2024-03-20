package com.manik.alarmmanager.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.widget.TimePicker
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.manik.alarmmanager.model.Alarm
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

object Util {

    fun getTimePickerHour(tp: TimePicker): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tp.hour
        } else {
            tp.currentHour
        }
    }

    fun getTimePickerMinute(tp: TimePicker): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tp.minute
        } else {
            tp.currentMinute
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun convertTimeMillisToDateTime(millis: Long): String {
        val formatter = SimpleDateFormat("hh:mm a")
        return formatter.format(Date(millis))
    }

    /*
    * Prefs Methods
    * */

    fun getAlarmList(context: Context): List<Alarm> {
        val config = BaseConfig.newInstance(context)
        val list = arrayListOf<Alarm>()
        if (config.alarmDetails.isNotEmpty()) {
            val jsonArray = JSONArray(config.alarmDetails)
            for (i in 0 until jsonArray.length()) {
                val jObj = JSONObject(jsonArray[i].toString())
                list.add(getAlarmItem(jObj))
            }
        }
        return list
    }

    fun getAlarmById(context: Context, id: Int): Alarm? {
        val list = getAlarmList(context)
        list.forEach {
            if (it.alarmId == id)
                return it
        }
        return null
    }

    private fun getAlarmItem(jObj: JSONObject): Alarm {
        return Alarm(
            jObj.getInt("alarmId"),
            jObj.getInt("hour"),
            jObj.getInt("minute"),
            jObj.getString("time"),
            jObj.getBoolean("started"),
            jObj.getBoolean("recurring"),
            jObj.getBoolean("monday"),
            jObj.getBoolean("tuesday"),
            jObj.getBoolean("wednesday"),
            jObj.getBoolean("thursday"),
            jObj.getBoolean("friday"),
            jObj.getBoolean("saturday"),
            jObj.getBoolean("sunday")
        )
    }

    fun saveAlarm(context: Context, alarm: Alarm) {
        val config = BaseConfig.newInstance(context)
        val alarmDetail = Gson().toJson(alarm)

        var alarmListArray = JSONArray()
        if (config.alarmDetails.isNotEmpty())
            alarmListArray = JSONArray(config.alarmDetails)

        alarmListArray.put(alarmDetail)
        config.alarmDetails = alarmListArray.toString()
    }

    fun updateAlarm(context: Context, alarm: Alarm) {
        val config = BaseConfig.newInstance(context)
        val alarmArray = JSONArray(config.alarmDetails)
        val updateArray = JSONArray()

        for (i in 0 until alarmArray.length()) {
            val jObj = JSONObject(alarmArray[i].toString())
            if (jObj.getInt("alarmId") == alarm.alarmId) {
                updateArray.put(Gson().toJson(alarm))
            } else updateArray.put(Gson().toJson(getAlarmItem(jObj)))
        }

        config.alarmDetails = updateArray.toString()
    }

    fun removeAlarm(context: Context, alarm: Alarm) {
        val config = BaseConfig.newInstance(context)
        val alarmArray = JSONArray(config.alarmDetails)
        val updateArray = JSONArray()

        for (i in 0 until alarmArray.length()) {
            val jObj = JSONObject(alarmArray[i].toString())
            if (jObj.getInt("alarmId") != alarm.alarmId)
                updateArray.put(Gson().toJson(getAlarmItem(jObj)))
        }

        if (updateArray.length() > 0)
            config.alarmDetails = updateArray.toString()
        else config.alarmDetails = ""
    }

    fun rescheduleAlarms(context: Context) {
        val list = getAlarmList(context)
        list.forEach {
            if (it.started)
                it.schedule(context)
        }
    }
}

object AlarmReceiver {
    val onUpdate: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
}