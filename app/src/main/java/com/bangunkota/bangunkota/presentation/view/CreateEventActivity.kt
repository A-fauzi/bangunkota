package com.bangunkota.bangunkota.presentation.view

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bangunkota.bangunkota.R
import com.bangunkota.bangunkota.data.repository.implementatios.EventRepositoryImpl
import com.bangunkota.bangunkota.databinding.ActivityCreateEventBinding
import com.bangunkota.bangunkota.domain.entity.CommunityEvent
import com.bangunkota.bangunkota.domain.usecase.EventUseCase
import com.bangunkota.bangunkota.presentation.presenter.viewmodel.EventViewModel
import com.bangunkota.bangunkota.presentation.presenter.viewmodel.UserViewModel
import com.bangunkota.bangunkota.presentation.presenter.viewmodelfactory.EventViewModelFactory
import com.bangunkota.bangunkota.presentation.presenter.viewmodelfactory.UserViewModelFactory
import com.bangunkota.bangunkota.presentation.view.main.MainActivity
import com.bangunkota.bangunkota.utils.UniqueIdGenerator
import com.bangunkota.bangunkota.utils.UserPreferencesManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout.END_ICON_CUSTOM
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

@RequiresApi(Build.VERSION_CODES.N)
class CreateEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateEventBinding
    private lateinit var eventViewModel: EventViewModel
    private lateinit var userViewModel: UserViewModel
    private var fillPath: Uri? = null
    private var dataDate = "null"
    private var dataTime = "null"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initObject()

    }

    private fun initObject() {
        val eventRepository = EventRepositoryImpl()
        val eventUseCase = EventUseCase(eventRepository)
        val viewModelFactory = EventViewModelFactory(eventUseCase)
        eventViewModel = ViewModelProvider(this, viewModelFactory)[EventViewModel::class.java]

        val userPreferencesManager = UserPreferencesManager(this)
        val userViewModelFactory = UserViewModelFactory(userPreferencesManager)
        userViewModel = ViewModelProvider(this, userViewModelFactory)[UserViewModel::class.java]
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStart() {
        super.onStart()

        topAppBarBehaviour()
        setFormEvent()
        onClickViews()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun onClickViews() {
        binding.cvUploadImage.setOnClickListener {
            getImageFromGalerry()
        }
        binding.btnCreateEvent.setOnClickListener {

            binding.btnCreateEvent.visibility = View.GONE
            binding.progressbar.visibility = View.VISIBLE

            uploadImageToFireStorage()
        }
        binding.eventLocation.outlinedTextFieldEvent.setEndIconOnClickListener {
//            val placeAutoComplete = Place
        }
        binding.eventDate.outlinedTextFieldEvent.setEndIconOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            datePicker.show(supportFragmentManager, "17:00")
            datePicker.addOnPositiveButtonClickListener {
                val selectDate = Date(it)
                val formattedDate =
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectDate)
                binding.eventDate.outlinedTextFieldEvent.editText?.setText(formattedDate)

                dataDate = formattedDate
            }
        }
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

                dataTime = formattedTime
            }
        }
        binding.appBarLayout.topAppBar.setNavigationOnClickListener {
            Toast.makeText(this, "Menu Clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getImageFromGalerry() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            try {
                if (data != null) {
                    fillPath = data.data
                    binding.tvSetFillPath.text = fillPath?.path.toString()
                } else {
                    Toast.makeText(this, "Data photo is null", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun uploadImageToFireStorage() {
        val firebaseStorage = FirebaseStorage.getInstance()
        val fileName = UUID.randomUUID()
        val refStorage = firebaseStorage.reference.child("/image_post_event/${fileName}.jpg")

        // Compress image
        val reduceImage: ByteArray = bytes(fillPath.toString())

        refStorage.putBytes(reduceImage).addOnSuccessListener { uploadTask ->
            uploadTask.storage.downloadUrl.addOnSuccessListener { imageUri ->
                // Get Uri Image to upload firestore
                storeDataToFireStore(imageUri.toString())

            }.addOnFailureListener { imgUriExc ->

                binding.btnCreateEvent.visibility = View.VISIBLE
                binding.progressbar.visibility = View.GONE

                Toast.makeText(this, imgUriExc.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { uploadTaskExc ->

            binding.btnCreateEvent.visibility = View.VISIBLE
            binding.progressbar.visibility = View.GONE

            Toast.makeText(this, uploadTaskExc.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun bytes(fillPath: String): ByteArray {
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, fillPath.toUri())
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    private fun setFormEvent() {

        binding.eventLocation.tvInputName.text = "Choose Location"
        binding.eventDate.tvInputName.text = "Set Date Event"
        binding.eventTime.tvInputName.text = "Set Time Event"
        binding.eventName.tvInputName.text = "Event Name"

        binding.eventLocation.editTextCreateEvent.hint = "Bekasi, Jawabarat"
        binding.eventName.editTextCreateEvent.hint = "Charity child people"
        binding.eventDate.editTextCreateEvent.hint = "2023-08-31"
        binding.eventTime.editTextCreateEvent.hint = "18:10"

        binding.eventLocation.outlinedTextFieldEvent.endIconMode = END_ICON_CUSTOM
        binding.eventDate.outlinedTextFieldEvent.endIconMode = END_ICON_CUSTOM
        binding.eventTime.outlinedTextFieldEvent.endIconMode = END_ICON_CUSTOM

        binding.eventLocation.outlinedTextFieldEvent.endIconDrawable = ResourcesCompat.getDrawable(resources, R.drawable.marker__1_, null)
        binding.eventDate.outlinedTextFieldEvent.endIconDrawable = ResourcesCompat.getDrawable(resources, R.drawable.calendar, null)
        binding.eventTime.outlinedTextFieldEvent.endIconDrawable = ResourcesCompat.getDrawable(resources, R.drawable.clock, null)

        binding.eventTime.editTextCreateEvent.isEnabled = false
        binding.eventDate.editTextCreateEvent.isEnabled = false


    }

    private fun topAppBarBehaviour() {
        binding.appBarLayout.topAppBar.title = "Create Event"
        binding.appBarLayout.topAppBar.menu.findItem(R.id.account).isVisible = false
        binding.appBarLayout.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, R.drawable.angle_left, null)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun storeDataToFireStore(imageUri: String) {

        userViewModel.userId.observe(this) { userId ->

            val event = setDataEvent(imageUri, userId)

            lifecycleScope.launch {

                val result = eventViewModel.insertEvent(event)

                result.onSuccess {
                    if (result.isSuccess) {

                        val intent = Intent(this@CreateEventActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()

                        Toast.makeText(
                            this@CreateEventActivity,
                            "Success Store Data ${event.id}",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {

                        binding.btnCreateEvent.visibility = View.VISIBLE
                        binding.progressbar.visibility = View.GONE

                        // Hapus foto sebelum nya yang berhasil di uploas ke firebase storage
                        // this code

                        Toast.makeText(
                            this@CreateEventActivity,
                            "Gagal Store Data ${event.id}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }.onFailure {

                    binding.btnCreateEvent.visibility = View.VISIBLE
                    binding.progressbar.visibility = View.GONE

                    // Hapus foto sebelum nya yang berhasil di uploas ke firebase storage
                    // this code

                    Toast.makeText(
                        this@CreateEventActivity,
                        "Failure Store Data kesalahan ${it.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
    }

    private fun setDataEvent(
        imageUri: String,
        userId: String?
    ): CommunityEvent {
        return CommunityEvent(
            id = UniqueIdGenerator.generateUniqueId(),
            title = binding.eventName.editTextCreateEvent.text.toString(),
            address = binding.eventLocation.editTextCreateEvent.text.toString(),
            image = imageUri,
            date = dataDate,
            time = dataTime,
            createdBy = userId
        )
    }

    companion object {
        const val REQUEST_CODE = 101
    }
}