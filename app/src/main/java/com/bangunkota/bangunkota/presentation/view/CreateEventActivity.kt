package com.bangunkota.bangunkota.presentation.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.bangunkota.bangunkota.R
import com.bangunkota.bangunkota.databinding.ActivityCreateEventBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.TextInputLayout.END_ICON_CUSTOM
import com.google.android.material.textfield.TextInputLayout.END_ICON_NONE
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.*

class CreateEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateEventBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        topAppBarBehaviour()
        setFormEvent()
    }

    private fun setFormEvent() {
        binding.eventName.outlinedTextFieldEvent.hint = "Event Name"
        binding.eventLocation.outlinedTextFieldEvent.hint = "Location"
        binding.eventLocation.outlinedTextFieldEvent.endIconMode = END_ICON_CUSTOM
        binding.eventLocation.outlinedTextFieldEvent.endIconDrawable =
            ResourcesCompat.getDrawable(resources, R.drawable.marker__1_, null)
        binding.eventLocation.outlinedTextFieldEvent.setEndIconOnClickListener {
            Toast.makeText(this, "Map Clicked", Toast.LENGTH_SHORT).show()
        }

        binding.eventDate.outlinedTextFieldEvent.hint = "Date"
        binding.eventDate.outlinedTextFieldEvent.endIconMode = END_ICON_CUSTOM
        binding.eventDate.outlinedTextFieldEvent.endIconDrawable =
            ResourcesCompat.getDrawable(resources, R.drawable.calendar, null)
        binding.eventDate.outlinedTextFieldEvent.setEndIconOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            datePicker.show(supportFragmentManager, "date")
            datePicker.addOnPositiveButtonClickListener {
                val selectDate = Date(it)
                val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectDate)
                binding.eventDate.outlinedTextFieldEvent.editText?.setText(formattedDate)
            }
        }

        binding.eventTime.outlinedTextFieldEvent.hint = "Time"
        binding.eventTime.outlinedTextFieldEvent.endIconMode = END_ICON_CUSTOM
        binding.eventTime.outlinedTextFieldEvent.endIconDrawable =
            ResourcesCompat.getDrawable(resources, R.drawable.clock, null)
        binding.eventTime.outlinedTextFieldEvent.setEndIconOnClickListener {
            val picker =
                MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(12)
                    .setMinute(10)
                    .setTitleText("Select Appointment time")
                    .build()
            picker.show(supportFragmentManager, "time")
            picker.addOnPositiveButtonClickListener {
                val selectedHour = picker.hour
                val selectedMinute = picker.minute
                val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)

                binding.eventTime.outlinedTextFieldEvent.editText?.setText(formattedTime)
            }
        }

    }


    private fun topAppBarBehaviour() {
        binding.appBarLayout.topAppBar.title = "Create Event"
        binding.appBarLayout.topAppBar.menu.findItem(R.id.account).isVisible = false
        binding.appBarLayout.topAppBar.navigationIcon =
            ResourcesCompat.getDrawable(resources, R.drawable.angle_left, null)

        binding.appBarLayout.topAppBar.setNavigationOnClickListener {
            Toast.makeText(this, "Menu Clicked", Toast.LENGTH_SHORT).show()
        }
    }
}