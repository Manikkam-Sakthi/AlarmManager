package com.manik.alarmmanager.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.manik.alarmmanager.databinding.RowAlarmBinding
import com.manik.alarmmanager.model.Alarm

class AlarmListAdapter(private val listener: OnAlarmChangeListener) :
    ListAdapter<Alarm, AlarmListAdapter.ItemViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, listener)
    }

    class ItemViewHolder private constructor(private val binding: RowAlarmBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(alarm: Alarm, listener: OnAlarmChangeListener) {
            with(binding) {
                tvId.text = "ID: ${alarm.alarmId}"
                switchActive.isChecked = alarm.started
                tvTime.text = alarm.time
                tvScheduleDay.text = if (alarm.recurring) "Repeat" else "Once"

                if (alarm.sunday) chipSunday.chipBackgroundColor =
                    ColorStateList.valueOf(Color.parseColor("#FFBB86FC"))
                if (alarm.monday) chipMonday.chipBackgroundColor =
                    ColorStateList.valueOf(Color.parseColor("#FFBB86FC"))
                if (alarm.tuesday) chipTuesday.chipBackgroundColor =
                    ColorStateList.valueOf(Color.parseColor("#FFBB86FC"))
                if (alarm.wednesday) chipWednesday.chipBackgroundColor =
                    ColorStateList.valueOf(Color.parseColor("#FFBB86FC"))
                if (alarm.thursday) chipThursday.chipBackgroundColor =
                    ColorStateList.valueOf(Color.parseColor("#FFBB86FC"))
                if (alarm.friday) chipFriday.chipBackgroundColor =
                    ColorStateList.valueOf(Color.parseColor("#FFBB86FC"))
                if (alarm.saturday) chipSaturday.chipBackgroundColor =
                    ColorStateList.valueOf(Color.parseColor("#FFBB86FC"))

                chipSunday.setOnClickListener {
                    alarm.sunday = alarm.sunday.not()
                    listener.onChange(alarm, absoluteAdapterPosition)
                }

                chipMonday.setOnClickListener {
                    alarm.monday = alarm.monday.not()
                    listener.onChange(alarm, absoluteAdapterPosition)
                }

                chipTuesday.setOnClickListener {
                    alarm.tuesday = alarm.tuesday.not()
                    listener.onChange(alarm, absoluteAdapterPosition)
                }

                chipWednesday.setOnClickListener {
                    alarm.wednesday = alarm.wednesday.not()
                    listener.onChange(alarm, absoluteAdapterPosition)
                }

                chipThursday.setOnClickListener {
                    alarm.thursday = alarm.thursday.not()
                    listener.onChange(alarm, absoluteAdapterPosition)
                }

                chipFriday.setOnClickListener {
                    alarm.friday = alarm.friday.not()
                    listener.onChange(alarm, absoluteAdapterPosition)
                }

                chipSaturday.setOnClickListener {
                    alarm.saturday = alarm.saturday.not()
                    listener.onChange(alarm, absoluteAdapterPosition)
                }

                switchActive.setOnCheckedChangeListener { compoundButton, b ->
                    listener.onActiveChange(alarm, absoluteAdapterPosition)
                }
                ivRemove.setOnClickListener { listener.onRemoved(alarm) }
            }
        }

        companion object {
            fun from(parent: ViewGroup): ItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RowAlarmBinding.inflate(layoutInflater, parent, false)
                return ItemViewHolder(binding)
            }
        }
    }
}

class TaskDiffCallback : DiffUtil.ItemCallback<Alarm>() {
    override fun areItemsTheSame(
        oldItem: Alarm,
        newItem: Alarm
    ): Boolean {
        return oldItem.alarmId == newItem.alarmId
    }

    override fun areContentsTheSame(
        oldItem: Alarm,
        newItem: Alarm
    ): Boolean {
        return oldItem == newItem
    }
}

interface OnAlarmChangeListener {
    fun onChange(alarm: Alarm, index: Int)
    fun onActiveChange(alarm: Alarm, index: Int)
    fun onRemoved(alarm: Alarm)
}