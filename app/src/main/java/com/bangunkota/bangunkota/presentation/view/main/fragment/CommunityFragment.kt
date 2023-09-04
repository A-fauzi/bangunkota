package com.bangunkota.bangunkota.presentation.view.main.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bangunkota.bangunkota.R
import com.bangunkota.bangunkota.data.datasource.remote.firebase.FireStoreManager
import com.bangunkota.bangunkota.data.repository.implementatios.ExampleRepositoryFireStoreImpl
import com.bangunkota.bangunkota.databinding.BottomSheetMorePostBinding
import com.bangunkota.bangunkota.databinding.FragmentCommunityBinding
import com.bangunkota.bangunkota.databinding.ItemCommunityPostBinding
import com.bangunkota.bangunkota.domain.entity.User
import com.bangunkota.bangunkota.domain.entity.community_post.CommunityPost
import com.bangunkota.bangunkota.domain.usecase.ExampleUseCase
import com.bangunkota.bangunkota.presentation.adapter.AdapterPagingList
import com.bangunkota.bangunkota.presentation.presenter.viewmodel.CommunityViewModel
import com.bangunkota.bangunkota.presentation.presenter.viewmodel.UserViewModel
import com.bangunkota.bangunkota.presentation.presenter.viewmodelfactory.CommunityViewModelFactory
import com.bangunkota.bangunkota.presentation.presenter.viewmodelfactory.UserViewModelFactory
import com.bangunkota.bangunkota.utils.MessageHandler
import com.bangunkota.bangunkota.utils.UniqueIdGenerator
import com.bangunkota.bangunkota.utils.UserPreferencesManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
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
     * RECYCLERVIEW POSTING
     */
    private lateinit var recyclerviewPost: RecyclerView

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

        setTopAppBar()
        onClickViews()
        setUpViewModels()
        setUpRecyclerView()

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

        lifecycleScope.launch {
            adapterPagingList.loadStateFlow.collectLatest { loadStates ->
                val isLoading = loadStates.refresh is LoadState.Loading
                val isLoadingAppend = loadStates.append is LoadState.Loading

                if (isLoading) {
                    binding.rvCommunityPost.visibility = View.GONE
                    binding.shimmerPost.visibility = View.VISIBLE
                } else {
                    binding.rvCommunityPost.visibility = View.VISIBLE
                    binding.shimmerPost.visibility = View.GONE
                }
            }
        }

//        adapterPagingList.addLoadStateListener { combinedLoadStates ->
//            val isLoading = combinedLoadStates.refresh is LoadState.Loading
//
//            if (isLoading) {
//                binding.rvCommunityPost.visibility = View.GONE
//                binding.shimmerPost.visibility = View.VISIBLE
//            } else {
//                binding.rvCommunityPost.visibility = View.VISIBLE
//                binding.shimmerPost.visibility = View.GONE
//            }
//        }

    }

    /**
     * INITIALIZING OBJECT
     */
    private fun initialsObject() {

        // INIT
        auth = FirebaseAuth.getInstance()
        message = MessageHandler(requireActivity())
        user = auth.currentUser


        // USER CONFIG
        userPreferencesManager = UserPreferencesManager(requireActivity())
        val fireStoreManager = FireStoreManager<User>("users")
        val userRepository = ExampleRepositoryFireStoreImpl(fireStoreManager)
        val userUseCase = ExampleUseCase(userRepository)
        userViewModelFactory = UserViewModelFactory(userPreferencesManager, userUseCase)
        userViewModel = ViewModelProvider(this, userViewModelFactory)[UserViewModel::class.java]


        // COMMUNITY CONFIG
        val firestoreManager = FireStoreManager<CommunityPost>("community_posts")
        val postRepository = ExampleRepositoryFireStoreImpl(firestoreManager)
        val postUseCase = ExampleUseCase(postRepository)
        val postViewModelFactory = CommunityViewModelFactory(postUseCase)
        communityViewModel = ViewModelProvider(requireActivity(), postViewModelFactory)[CommunityViewModel::class.java]

        // COMMUNITY SETUP ADAPTER
        adapterPagingList = AdapterPagingList(
            { binding, post ->

                // EDITTEXT POST
                binding.tvTextPost.text = post.text

                // GET USER BY ID IN LIST
                userViewModel.getDocumentUserById(post.uid.toString())
                    .addOnSuccessListener { userSnapshot ->
                        // WHEN SUCCESS GET DATA USERS
                        // CEK USER IF EXISTS
                        if (userSnapshot.exists()) {

                            userViewModel.userData.observe(viewLifecycleOwner) { currentUser ->
                                if (currentUser.id.toString() != post.uid) {
                                    binding.btnMorePost.visibility = View.GONE
                                }
                            }

                            // GET VALUE AND SET TO ITEM POSTING COMMUNITY
                            binding.btnMorePost.setOnClickListener {
                                // Handle button more post
                                // BottomSheet
                                val dialog = BottomSheetDialog(requireActivity())
                                val sheetBinding = BottomSheetMorePostBinding.inflate(layoutInflater)
                                val view = sheetBinding.root
                                dialog.setContentView(view)

                                val progressBar = sheetBinding.progressbar
                                val layoutItem = sheetBinding.layoutItem
                                sheetBinding.btnDelete.setOnClickListener {
                                    deleteEventPost(progressBar, layoutItem, dialog, post)
                                }
                                dialog.show()
                            }


                            binding.itemNameUser.text = userSnapshot.getString("name")
                            binding.itemEmailUser.text = userSnapshot.getString("email")
                            Glide.with(requireActivity())
                                .load(userSnapshot.getString("photoUrl"))
                                .placeholder(R.drawable.img_placeholder)
                                .error(R.drawable.img_placeholder)
                                .into(binding.itemIvProfile)

                            var isLoved = false
                            binding.btnLove.setOnClickListener {
                                val postId = post.id
                                val currentUserId = user?.uid.toString()

                                if (isLoved) {
                                    binding.btnLove.setImageResource(R.drawable.heart_outline)
                                    message.toastMsg("Batal Menyukai")

                                    // Handle deleted data like in document
                                } else {
                                    binding.btnLove.setImageResource(R.drawable.heart_filled)
                                    message.toastMsg("Kamu Menyukai ini")

//                                insertDataLikePost(postId)
                                }

                                isLoved = !isLoved
                            }


                        } else {
                            message.toastMsg("user not exists")
                        }
                    }
                    .addOnFailureListener {
                        // HANDLE IF FAILURE ON GET DOCUMENT USERS
                        message.toastMsg("Error ${it.message}")
                    }
            },
            ItemCommunityPostBinding::inflate
        )

    }

    private fun deleteEventPost(
        progressBar: ProgressBar,
        layoutItem: LinearLayout,
        dialog: BottomSheetDialog,
        post: CommunityPost
    ) {
        progressBar.visibility = View.VISIBLE
        layoutItem.visibility = View.GONE

        dialog.setCancelable(false)

        communityViewModel.deletePost(post.id)
            .addOnCompleteListener { delete ->
                if (delete.isSuccessful) {
                    adapterPagingList.refresh()
                    dialog.dismiss()
                } else {
                    message.toastMsg("tidak dapat menghapus")
                    dialog.dismiss()
                }
            }
            .addOnFailureListener { excep ->
                message.toastMsg("Error menghapus ${excep.message}")
                dialog.dismiss()
            }
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

        userViewModel.userData.observe(viewLifecycleOwner) {

            // TEXTFIELD FALSE
            binding.outlineTextfieldProductSpec.isEnabled = false

            // GET TEXT IN EDITTEXT POST
            val textPost = binding.etPostText.text.toString()

            // SET DATA POSTING
            val data = CommunityPost(
                id = UniqueIdGenerator.generateUniqueId(),
                uid = it.id,
                text = textPost,
                created_at = Timestamp.now()
            )

            // INSERT DATA POSTING
            lifecycleScope.launch {
                val result = communityViewModel.insertPost(data, data.id)
                result.addOnCompleteListener {
                    if (result.isSuccessful) {
                        adapterPagingList.refresh()
                        binding.outlineTextfieldProductSpec.isEnabled = true
                        binding.etPostText.text?.clear()
                    } else {
                        binding.outlineTextfieldProductSpec.isEnabled = true
                        message.toastMsg("Gagal Posting")
                    }
                }.addOnFailureListener {
                    binding.outlineTextfieldProductSpec.isEnabled = true
                    message.toastMsg("Error Posting ${it.message}")
                }
            }

        }


    }

//    private fun insertDataLikePost(postId: String) {
//        val data = UserLikePost(
//            id = UniqueIdGenerator.generateUniqueId(),
//            postId = postId,
//            userId = user?.uid.toString(),
//            createdAt = Timestamp.now()
//        )
//        lifecycleScope.launch {
//            val result = communityViewModel.insertLikePost(data)
//            result.onSuccess {
//                if (result.isSuccess) {
//                    message.toastMsg("Post success your like")
//                } else {
//                    message.toastMsg("Gagal Menyukai")
//                }
//            }.onFailure {
//                message.toastMsg("Error Posting ${it.message}")
//            }
//        }
//    }

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