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
import com.bangunkota.bangunkota.data.datasource.local.AppDatabase
import com.bangunkota.bangunkota.data.datasource.local.CommunityPostDao
import com.bangunkota.bangunkota.data.repository.abstractions.CommunityRepository
import com.bangunkota.bangunkota.data.repository.implementatios.CommunityRepositoryImpl
import com.bangunkota.bangunkota.databinding.FragmentCommunityBinding
import com.bangunkota.bangunkota.databinding.ItemCommunityPostBinding
import com.bangunkota.bangunkota.domain.entity.CommunityPost
import com.bangunkota.bangunkota.domain.usecase.CommunityUseCase
import com.bangunkota.bangunkota.presentation.adapter.AdapterPagingList
import com.bangunkota.bangunkota.presentation.presenter.viewmodel.CommunityViewModel
import com.bangunkota.bangunkota.presentation.presenter.viewmodel.UserViewModel
import com.bangunkota.bangunkota.presentation.presenter.viewmodelfactory.CommunityViewModelFactory
import com.bangunkota.bangunkota.presentation.presenter.viewmodelfactory.UserViewModelFactory
import com.bangunkota.bangunkota.utils.UniqueIdGenerator
import com.bangunkota.bangunkota.utils.UserPreferencesManager
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
    private lateinit var communityPostDao: CommunityPostDao

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
        adapterPagingList = AdapterPagingList(requireActivity(), { binding, post ->
            binding.tvTextPost.text = post.text
        }, ItemCommunityPostBinding::inflate)
        userPreferencesManager = UserPreferencesManager(requireActivity())
        userViewModelFactory = UserViewModelFactory(userPreferencesManager)
        userViewModel = ViewModelProvider(this, userViewModelFactory)[UserViewModel::class.java]
        firestore = FirebaseFirestore.getInstance()
        communityPostDao = AppDatabase.getInstance(requireActivity()).communityPostDao()
        repository = CommunityRepositoryImpl(firestore, communityPostDao)
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
            val textPost = binding.etPostText.text.toString()
            lifecycleScope.launch {
                userViewModel.userId.observe(viewLifecycleOwner) { uid ->
                    val data = CommunityPost(
                        id = UniqueIdGenerator.generateUniqueId(),
                        uid = uid,
                        text = textPost
                    )

                    lifecycleScope.launch {
                        val result = communityViewModel.insertPost(data)
                        result.onSuccess {
                            if (result.isSuccess) {
                                adapterPagingList.refresh()
                                Toast.makeText(requireActivity(), "Success Posting", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(requireActivity(), "Gagal Posting", Toast.LENGTH_SHORT).show()
                            }
                        }.onFailure {
                            Toast.makeText(requireActivity(), "Error Posting ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        }

        binding.fabUpScroll.setOnClickListener {
            binding.nestedScrollView.smoothScrollTo(0,0)
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