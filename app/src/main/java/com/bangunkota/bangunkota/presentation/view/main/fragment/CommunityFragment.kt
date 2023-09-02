package com.bangunkota.bangunkota.presentation.view.main.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
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
import com.bangunkota.bangunkota.data.datasource.remote.firebase.FireStoreManager
import com.bangunkota.bangunkota.data.repository.abstractions.CommunityRepository
import com.bangunkota.bangunkota.data.repository.abstractions.UserRepository
import com.bangunkota.bangunkota.data.repository.implementatios.CommunityRepositoryImpl
import com.bangunkota.bangunkota.data.repository.implementatios.UserRepositoryImpl
import com.bangunkota.bangunkota.databinding.FragmentCommunityBinding
import com.bangunkota.bangunkota.databinding.ItemCommunityPostBinding
import com.bangunkota.bangunkota.domain.entity.CommunityPost
import com.bangunkota.bangunkota.domain.entity.User
import com.bangunkota.bangunkota.domain.usecase.CommunityUseCase
import com.bangunkota.bangunkota.domain.usecase.UserUseCase
import com.bangunkota.bangunkota.presentation.adapter.AdapterPagingList
import com.bangunkota.bangunkota.presentation.presenter.viewmodel.CommunityViewModel
import com.bangunkota.bangunkota.presentation.presenter.viewmodel.UserViewModel
import com.bangunkota.bangunkota.presentation.presenter.viewmodelfactory.CommunityViewModelFactory
import com.bangunkota.bangunkota.presentation.presenter.viewmodelfactory.UserViewModelFactory
import com.bangunkota.bangunkota.utils.MessageHandler
import com.bangunkota.bangunkota.utils.UniqueIdGenerator
import com.bangunkota.bangunkota.utils.UserPreferencesManager
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CommunityFragment : Fragment() {

    companion object {
        const val TAG = "CommunityFragment"
    }

    /**
     * BINDING VIEW
     */
    private lateinit var binding: FragmentCommunityBinding

    /**
     * ADAPTER PAGING
     */
    private lateinit var adapterPagingList: AdapterPagingList<CommunityPost, ItemCommunityPostBinding>

    /**
     * COMMUNITY VIEWMODEL
     */
    private lateinit var communityViewModel: CommunityViewModel

    /**
     * USER VIEWMODEL
     */
    private lateinit var userViewModel: UserViewModel

    /**
     * USER PREFERENCES
     */
    private lateinit var userPreferencesManager: UserPreferencesManager

    /**
     * USER VIEWMODEL FACTORY
     */
    private lateinit var userViewModelFactory: UserViewModelFactory

    /**
     * COMMUNITY VIEWMODEL FACTORY
     */
    private lateinit var communityViewModelFactory: CommunityViewModelFactory

    /**
     * COMMUNITY USECASE
     */
    private lateinit var communityUseCase: CommunityUseCase

    /**
     * COMMUNITY REPOSITORY
     */
    private lateinit var communityRepository: CommunityRepository

    /**
     * RECYCLERVIEW POSTING
     */
    private lateinit var recyclerviewPost: RecyclerView

    /**
     * FIRESTORE DATABASE
     */
    private lateinit var firestore: FirebaseFirestore

    /**
     * FIREBASE AUTH
     */
    private lateinit var auth: FirebaseAuth

    /**
     * FIREBASE USER
     */
    private var user: FirebaseUser? = null

    /**
     * MESSAGE TOAST
     */
    private lateinit var message: MessageHandler

    /**
     * COLLECTION REFERENCE
     */
    private lateinit var collectionReference: CollectionReference

    /**
     * USER USECASE
     */
    private lateinit var userUseCase: UserUseCase

    /**
     * USER REPOSITORY
     */
    private lateinit var userRepository: UserRepository

    /**
     * FIRESTORE MANAGER
     */
    private lateinit var fireStoreManager: FireStoreManager

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

    /**
     * SETUP VIEWMODEL
     */
    private fun setUpViewModels() {
        lifecycleScope.launch {
            communityViewModel.getPosts.collectLatest { pagingData ->
                adapterPagingList.submitData(pagingData)
            }
        }
    }

    /**
     * SETUP RECYCLERVIEW
     */
    private fun setUpRecyclerView() {
        recyclerviewPost = binding.rvCommunityPost
        recyclerviewPost.apply {
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            adapter = adapterPagingList
        }
    }

    /**
     * INITIALIZING OBJECT
     */
    private fun initialsObject() {

        // INIT
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        fireStoreManager = FireStoreManager(firestore)
        message = MessageHandler(requireActivity())

        user = auth.currentUser

        // USER CONFIG
        userPreferencesManager = UserPreferencesManager(requireActivity())
        userRepository = UserRepositoryImpl(fireStoreManager)
        userUseCase = UserUseCase(userRepository)
        userViewModelFactory = UserViewModelFactory(userPreferencesManager, userUseCase)
        userViewModel = ViewModelProvider(this, userViewModelFactory)[UserViewModel::class.java]


        // COMMUNITY CONFIG
        communityRepository = CommunityRepositoryImpl(firestore)
        communityUseCase = CommunityUseCase(communityRepository)
        communityViewModelFactory = CommunityViewModelFactory(communityUseCase)
        communityViewModel = ViewModelProvider(
            requireActivity(),
            communityViewModelFactory
        )[CommunityViewModel::class.java]

        // GET COLLECTION USERS
        collectionReference = firestore.collection("users")

        // COMMUNITY SETUP ADAPTER
        adapterPagingList = AdapterPagingList(requireActivity(), { binding, post ->

            // EDITTEXT POST
            binding.tvTextPost.text = post.text

            // GET INFORMATION USER BY POST UID
            collectionReference.document(post.uid.toString()).get()
                .addOnSuccessListener { userSnapshot ->

                    // WHEN SUCCESS GET DATA USERS
                    // CEK USER IF EXISTS
                    if (userSnapshot.exists()) {

                        // GET VALUE AND SET TO ITEM POSTING COMMUNITY
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

                    // HANDLE IF FAILURE ON GET DOCUMENT USERS
                    message.toastMsg("Error ${exception.message}")

                }


        }, ItemCommunityPostBinding::inflate)

    }

    /**
     * VIEWS ONCLICK
     */
    @SuppressLint("SuspiciousIndentation")
    private fun onClickViews() {
        // WHEN BUTTON END ICON POSTING MESSAGE ONCLICK
        binding.outlineTextfieldProductSpec.setEndIconOnClickListener { insertDataPost() }

        // button top scroll
        binding.fabUpScroll.setOnClickListener { binding.nestedScrollView.smoothScrollTo(0, 0) }
    }

    /**
     * INSERT DATA POSTING
     */
    private fun insertDataPost() {

        // TEXTFIELD FALSE
        binding.outlineTextfieldProductSpec.isEnabled = false

        // GET TEXT IN EDITTEXT POST
        val textPost = binding.etPostText.text.toString()

        // SET DATA POSTING
        val data = CommunityPost(
            id = UniqueIdGenerator.generateUniqueId(),
            uid = user?.uid,
            text = textPost,
            create_at = Timestamp.now()
        )

        // INSERT DATA POSTING
        lifecycleScope.launch {
            val result = communityViewModel.insertPost(data)
            result.onSuccess {
                if (result.isSuccess) {
                    adapterPagingList.refresh()
                    binding.outlineTextfieldProductSpec.isEnabled = true
                    binding.etPostText.text?.clear()
                } else {
                    binding.outlineTextfieldProductSpec.isEnabled = true
                    message.toastMsg("Gagal Posting")
                }
            }.onFailure {
                binding.outlineTextfieldProductSpec.isEnabled = true
                message.toastMsg("Error Posting ${it.message}")
            }
        }
    }

    /**
     * SET VIEW OR CONFIG TOPAPPBAR
     */
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