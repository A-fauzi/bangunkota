package com.bangunkota.bangunkota.presentation.view.main.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bangunkota.bangunkota.R
import com.bangunkota.bangunkota.data.repository.abstractions.CommunityRepository
import com.bangunkota.bangunkota.data.repository.implementatios.CommunityRepositoryImpl
import com.bangunkota.bangunkota.databinding.FragmentCommunityBinding
import com.bangunkota.bangunkota.databinding.ItemCommunityPostBinding
import com.bangunkota.bangunkota.domain.entity.CommunityPost
import com.bangunkota.bangunkota.domain.entity.User
import com.bangunkota.bangunkota.domain.usecase.CommunityUseCase
import com.bangunkota.bangunkota.presentation.adapter.AdapterPagingList
import com.bangunkota.bangunkota.presentation.presenter.viewmodel.CommunityViewModel
import com.bangunkota.bangunkota.presentation.presenter.viewmodel.UserViewModel
import com.bangunkota.bangunkota.presentation.presenter.viewmodelfactory.CommunityViewModelFactory
import com.bangunkota.bangunkota.presentation.presenter.viewmodelfactory.UserViewModelFactory
import com.bangunkota.bangunkota.utils.UniqueIdGenerator
import com.bangunkota.bangunkota.utils.UserPreferencesManager
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.internal.notifyAll

class CommunityFragment : Fragment() {

    companion object {
        const val TAG = "CommunityFragment"
    }

    private lateinit var binding: FragmentCommunityBinding
    private lateinit var adapterPagingList: AdapterPagingList<CommunityPost, ItemCommunityPostBinding>
    private lateinit var communityViewModel: CommunityViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var userPreferencesManager: UserPreferencesManager
    private lateinit var userViewModelFactory: UserViewModelFactory
    private lateinit var communityViewModelFactory: CommunityViewModelFactory
    private lateinit var useCase: CommunityUseCase
    private lateinit var repository: CommunityRepository
    private lateinit var recyclerviewPost: RecyclerView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null

//    private lateinit var communityPostDao: CommunityPostDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCommunityBinding.inflate(layoutInflater, container, false)

        initialsObject()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Dapatkan instance Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Dapatkan informasi pengguna yang saat ini masuk
        user = auth.currentUser

        // Dapatkan ID pengguna
        val userID = user?.uid

        // Dapatkan instance Firebase Firestore
        val db = FirebaseFirestore.getInstance()

        // Buat referensi ke dokumen pengguna di Firestore
        val userRef = db.collection("users").document(userID.toString())

        // Lakukan pengecekan apakah dokumen pengguna sudah ada di Firestore
        userRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (!document.exists()) {
                    // Dokumen pengguna belum ada, maka Anda bisa memasukkan datanya ke Firestore
                    val userData = User(
                        user?.uid,
                        user?.displayName,
                        user?.email,
                        user?.photoUrl.toString(),
                        Timestamp.now().toDate(),
                        null,
                        null
                    )

                    // Masukkan data pengguna ke Firestore
                    userRef.set(userData)
                        .addOnSuccessListener {
                            // Data pengguna berhasil dimasukkan ke Firestore
                            Toast.makeText(
                                requireActivity(),
                                "Data Pengguna berhasil di simpan",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener { exception ->
                            // Penanganan kesalahan jika gagal memasukkan data
                            Toast.makeText(
                                requireActivity(),
                                "Data Pengguna gagal di simpan",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    // Jika pengguna sudah ada di database
                }
            } else {
                // Pengguna belum masuk, Anda harus menangani kasus ini sesuai dengan kebutuhan Anda
                Toast.makeText(
                    requireActivity(),
                    "Kesalahan mengambil dokument pengguna, pengguna belum masuk",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        setTopAppBar()
        onClickViews()
        setUpViewModels()
        setUpRecyclerView()
        //        communityViewModel.fetchDataAndSaveToRoom()

    }

    private fun setUpViewModels() {
        lifecycleScope.launch {
            communityViewModel.getPosts.collectLatest { pagingData ->
                adapterPagingList.submitData(pagingData)
            }
        }
    }

    private fun setUpRecyclerView() {
        recyclerviewPost = binding.rvCommunityPost
        recyclerviewPost.apply {
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            adapter = adapterPagingList
        }
    }

    private fun initialsObject() {

        // Misalnya, Anda memiliki koleksi 'users' dalam Firestore
        val userRef = FirebaseFirestore.getInstance().collection("users")

        adapterPagingList = AdapterPagingList(requireActivity(), { binding, post ->

            binding.tvTextPost.text = post.text

            // Dapatkan informasi pengguna berdasarkan UID
            userRef.document(post.uid.toString()).get()
                .addOnSuccessListener { userSnapshot ->
                    if (userSnapshot.exists()) {
                        // Tampilkan nama pengguna dalam TextView atau tempat lain yang sesuai
                        binding.itemNameUser.text = userSnapshot.getString("name")
                        binding.itemEmailUser.text = userSnapshot.getString("email")
                        Glide.with(requireActivity())
                            .load(userSnapshot.getString("photoUrl"))
                            .placeholder(R.drawable.img_placeholder)
                            .error(R.drawable.example_profile)
                            .into(binding.itemIvProfile)
                    }
                }
                .addOnFailureListener { exception ->
                    // Penanganan kesalahan jika gagal mengambil data pengguna
                }


        }, ItemCommunityPostBinding::inflate)
        userPreferencesManager = UserPreferencesManager(requireActivity())
        userViewModelFactory = UserViewModelFactory(userPreferencesManager)
        userViewModel = ViewModelProvider(this, userViewModelFactory)[UserViewModel::class.java]
        firestore = FirebaseFirestore.getInstance()
//        communityPostDao = AppDatabase.getInstance(requireActivity()).communityPostDao()
        repository = CommunityRepositoryImpl(firestore)
        useCase = CommunityUseCase(repository)
        communityViewModelFactory = CommunityViewModelFactory(useCase)
        communityViewModel = ViewModelProvider(
            requireActivity(),
            communityViewModelFactory
        )[CommunityViewModel::class.java]
    }

    @SuppressLint("SuspiciousIndentation")
    private fun onClickViews() {

        binding.outlineTextfieldProductSpec.setEndIconOnClickListener {

            binding.outlineTextfieldProductSpec.isEnabled = false

            val textPost = binding.etPostText.text.toString()
            val data = CommunityPost(
                id = UniqueIdGenerator.generateUniqueId(),
                uid = user?.uid,
                text = textPost,
                create_at = Timestamp.now()
            )

            lifecycleScope.launch {
                val result = communityViewModel.insertPost(data)
                result.onSuccess {
                    if (result.isSuccess) {
                        adapterPagingList.refresh()
                        binding.outlineTextfieldProductSpec.isEnabled = true
                        binding.etPostText.text?.clear()
                    } else {
                        binding.outlineTextfieldProductSpec.isEnabled = true
                        Toast.makeText(
                            requireActivity(),
                            "Gagal Posting",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }.onFailure {
                    binding.outlineTextfieldProductSpec.isEnabled = true
                    Toast.makeText(
                        requireActivity(),
                        "Error Posting ${it.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }

        binding.fabUpScroll.setOnClickListener {
            binding.nestedScrollView.smoothScrollTo(0, 0)
        }
    }

    private fun setTopAppBar() {
        binding.appBarLayout.topAppBar.title = "Community"
        binding.appBarLayout.topAppBar.menu.findItem(R.id.account).isVisible = false
        binding.appBarLayout.topAppBar.navigationIcon =
            ResourcesCompat.getDrawable(resources, R.drawable.angle_left, null)
        binding.appBarLayout.topAppBar.setNavigationOnClickListener {
            Toast.makeText(requireActivity(), "Menu Clicked", Toast.LENGTH_SHORT).show()
        }
    }
}