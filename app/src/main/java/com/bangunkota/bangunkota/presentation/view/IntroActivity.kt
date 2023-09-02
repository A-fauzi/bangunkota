package com.bangunkota.bangunkota.presentation.view

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import com.bangunkota.bangunkota.R
import com.bangunkota.bangunkota.data.repository.implementatios.AuthRepositoryImpl
import com.bangunkota.bangunkota.domain.usecase.SignInUseCase
import com.bangunkota.bangunkota.presentation.presenter.viewmodel.SignInViewModel
import com.bangunkota.bangunkota.presentation.presenter.viewmodel.UserViewModel
import com.bangunkota.bangunkota.presentation.presenter.viewmodelfactory.SignInViewModelFactory
import com.bangunkota.bangunkota.presentation.view.main.MainActivity
import com.cuneytayyildiz.onboarder.OnboarderActivity
import com.cuneytayyildiz.onboarder.OnboarderPagerAdapter
import com.cuneytayyildiz.onboarder.model.*
import com.cuneytayyildiz.onboarder.utils.OnboarderPageChangeListener
import com.cuneytayyildiz.onboarder.utils.color
import com.google.firebase.auth.FirebaseAuth

class IntroActivity : OnboarderActivity(), OnboarderPageChangeListener {
    private lateinit var viewModel: SignInViewModel
    private lateinit var userViewModel: UserViewModel

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        setOnboarderPageChangeListener(this)

        val pages: MutableList<OnboarderPage> = createOnBoarderPages()

        initOnboardingPages(pages)

        val firebaseAuth = FirebaseAuth.getInstance()
        val authRepository = AuthRepositoryImpl(firebaseAuth)
        val signInUseCase = SignInUseCase(authRepository)
        val signInViewModelFactory = SignInViewModelFactory(signInUseCase)
        viewModel = ViewModelProvider(this, signInViewModelFactory)[SignInViewModel::class.java]

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStart() {
        super.onStart()

        viewModel.isUserSignedIn.observe(this) { isLogged ->
            if (isLogged) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
//                Toast.makeText(this, "Halo ✌!", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.checkCurrentUser()
    }

    override fun onSkipButtonPressed() {
        super.onSkipButtonPressed()
//        Toast.makeText(this, "Skip button was pressed!", Toast.LENGTH_SHORT).show()
    }

    override fun onFinishButtonPressed() {
        // implement your logic, save induction has done to sharedPrefs
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }

    override fun onPageChanged(position: Int) {
        Log.d(javaClass.simpleName, "onPageChanged: $position")
    }

    private fun createOnBoarderPages(): MutableList<OnboarderPage> {
        return mutableListOf(
            onboarderPage {
                setOnBoardPage(R.drawable.nature, "Nature", "Nature Explorers adalah aplikasi komunitas yang menghubungkan pecinta alam dari seluruh dunia. Di sini, Anda dapat berbagi foto-foto alam indah, pengalaman mendaki, camping, atau berbagi tips tentang berkebun dan konservasi lingkungan. Bergabunglah dengan diskusi tentang keindahan alam, perubahan iklim, dan upaya pelestarian, sambil terhubung dengan orang-orang yang memiliki cinta yang sama terhadap alam.")
            },
            onboarderPage {
                setOnBoardPage(R.drawable.hand_sewing, "Hand", "Crafty Creators adalah aplikasi yang menghubungkan pencinta karya tangan dan kerajinan tangan. Di sini, Anda dapat membagikan proyek-proyek kreatif Anda, mendapatkan inspirasi untuk kreasi baru, dan berinteraksi dengan komunitas yang bersemangat tentang karya tangan. Sertai tantangan berdasarkan tema tertentu, berbicaralah tentang teknik dan materi terbaru, dan jalin persahabatan dengan sesama pencipta karya tangan.")
            },
            onboarderPage {
                setOnBoardPage(R.drawable.online_popularity, "Online", "SkillShare Hub adalah aplikasi komunitas yang memungkinkan pengguna untuk berbagi pengetahuan dan keterampilan secara online. Dalam komunitas ini, Anda dapat mengakses kursus dan tutorial yang dibuat oleh anggota komunitas, memberikan ulasan, atau bahkan membuat konten pendidikan Anda sendiri. Temukan ratusan topik yang berkisar dari seni digital hingga pengembangan web, dan tingkatkan keterampilan Anda dengan akses ke sumber daya pembelajaran yang beragam.")
            }
        )
    }

    private fun OnboarderPage.setOnBoardPage(image: Int, title: String, desc: String) {
        backgroundColor = color(R.color.purple_500)
        image {
            imageResId = image
        }
        title {
            text = title
            textColor = color(R.color.white)
        }

        description {
            text = desc
            textColor = color(R.color.white)
            multilineCentered = true
        }
    }


}