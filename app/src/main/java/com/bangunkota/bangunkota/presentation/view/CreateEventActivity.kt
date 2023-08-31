package com.bangunkota.bangunkota.presentation.view

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bangunkota.bangunkota.R
import com.bangunkota.bangunkota.data.repository.implementatios.EventRepositoryImpl
import com.bangunkota.bangunkota.databinding.ActivityCreateEventBinding
import com.bangunkota.bangunkota.domain.entity.Event
import com.bangunkota.bangunkota.domain.usecase.EventUseCase
import com.bangunkota.bangunkota.presentation.presenter.viewmodel.EventViewModel
import com.bangunkota.bangunkota.presentation.presenter.viewmodelfactory.EventViewModelFactory
import com.bangunkota.bangunkota.presentation.view.main.MainActivity
import com.bangunkota.bangunkota.utils.RandomTitleGenerator
import com.bangunkota.bangunkota.utils.UniqueIdGenerator
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.TextInputLayout.END_ICON_CUSTOM
import com.google.android.material.textfield.TextInputLayout.END_ICON_NONE
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CreateEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateEventBinding
    private lateinit var eventViewModel: EventViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val eventRepository = EventRepositoryImpl()
        val eventUseCase = EventUseCase(eventRepository)
        val viewModelFactory = EventViewModelFactory(eventUseCase)
        eventViewModel = ViewModelProvider(this, viewModelFactory)[EventViewModel::class.java]
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStart() {
        super.onStart()

        topAppBarBehaviour()
        setFormEvent()

        binding.btnCreateEvent.setOnClickListener {
            exampleStoreDataToFireStore()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    private fun setFormEvent() {
        binding.eventName.outlinedTextFieldEvent.hint = "Event Name"
        binding.eventLocation.outlinedTextFieldEvent.hint = "Location"
        binding.eventLocation.outlinedTextFieldEvent.endIconMode = END_ICON_CUSTOM
        binding.eventLocation.outlinedTextFieldEvent.endIconDrawable =
            ResourcesCompat.getDrawable(resources, R.drawable.marker__1_, null)
        binding.eventLocation.outlinedTextFieldEvent.setEndIconOnClickListener {
//            val placeAutoComplete = Place
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
                val formattedDate =
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectDate)
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

    private fun exampleStoreDataToFireStore() {
        val event = Event(
            id = UniqueIdGenerator.generateUniqueId(),
            title = RandomTitleGenerator.generateRandomTitle(),
            address = "Kecamatan Bekasi Selatan, Indonesia",
            image = "https://i.pinimg.com/564x/0a/ad/42/0aad421488bbc7befa490bad2ac6ef8f.jpg"
        )

        lifecycleScope.launch {
            val result = eventViewModel.insertEvent(event)
            result.onSuccess {
                if (result.isSuccess) {
                    Toast.makeText(
                        this@CreateEventActivity,
                        "Success Store Data ${event.id}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@CreateEventActivity,
                        "Gagal Store Data ${event.id}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.onFailure {
                Toast.makeText(
                    this@CreateEventActivity,
                    "Failure Store Data kesalahan ${it.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}