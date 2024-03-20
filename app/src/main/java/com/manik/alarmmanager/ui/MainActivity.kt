package com.manik.alarmmanager.ui

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import com.manik.alarmmanager.databinding.ActivityMainBinding
import com.manik.alarmmanager.model.Alarm
import com.manik.alarmmanager.utils.AlarmReceiver
import com.manik.alarmmanager.utils.Util
import java.util.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val alarmList = arrayListOf<Alarm>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUp()
    }

    private fun setUp() {

        binding.fabNew.setOnClickListener {
            showTimeDialog()
        }
        setAdapter()

        AlarmReceiver.onUpdate.observe(this) {
            if (it) setAdapter()
        }
    }


    private fun showTimeDialog() {
        val calendar: Calendar = Calendar.getInstance()
        val dialog = TimePickerDialog(
            this,
            { timePicker, hourOfDay, minute ->
                scheduleAlarm(timePicker)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false
        )
        dialog.show()
    }

    private fun scheduleAlarm(timePicker: TimePicker) {
        val alarmId = Random.nextInt(1, 1000)

        val alarm = Alarm(
            alarmId,
            Util.getTimePickerHour(timePicker),
            Util.getTimePickerMinute(timePicker),
            started = true,
            recurring = false,
            monday = false,
            tuesday = false,
            wednesday = false,
            thursday = false,
            friday = false,
            saturday = false,
            sunday = false
        )

        // Schedule alarm
        alarm.schedule(this, true)

        // Save alarm
        Util.saveAlarm(this, alarm)

        setAdapter()
    }

    private var selectItemPosition = -1

    private fun setAdapter() {
        val adapter = AlarmListAdapter(object : OnAlarmChangeListener {
            override fun onChange(alarm: Alarm, index: Int) {
                selectItemPosition = index
                alarm.cancel(this@MainActivity, false)
                alarm.schedule(this@MainActivity)
                updateItem(alarm)
            }

            override fun onActiveChange(alarm: Alarm, index: Int) {
                selectItemPosition = index
                if (alarm.started) {
                    alarm.cancel(this@MainActivity, false)
                    updateItem(alarm)
                } else {
                    alarm.schedule(this@MainActivity, true)
                    updateItem(alarm)
                }
            }

            override fun onRemoved(alarm: Alarm) {
                alarm.cancel(this@MainActivity, false)
                Util.removeAlarm(this@MainActivity, alarm)
                setAdapter()
            }
        })
        binding.rvList.adapter = adapter
        (binding.rvList.adapter as AlarmListAdapter).submitList(getList())
        if (selectItemPosition > -1) {
            binding.rvList.scrollToPosition(selectItemPosition)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateAdapter() {
        val list = getList()
        (binding.rvList.adapter as AlarmListAdapter).submitList(list)
        if (list.isNotEmpty()) {
            (binding.rvList.adapter as AlarmListAdapter).notifyDataSetChanged()
        }
    }

    private fun getList(): List<Alarm> {
        val list = Util.getAlarmList(this)
        alarmList.removeAll(alarmList)
        alarmList.addAll(list)
        return list
    }

    private fun updateItem(alarm: Alarm) {
        Util.updateAlarm(this, alarm)
        setAdapter()
    }

    private fun showLog(msg: String) {
        Log.e(TAG, msg)
    }

    companion object {
        const val TAG = "MainActivity"
    }
}