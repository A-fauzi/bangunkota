package com.bangunkota.bangunkota.presentation.view.main.fragment

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bangunkota.bangunkota.R
import com.bangunkota.bangunkota.databinding.FragmentHomeBinding
import com.bangunkota.bangunkota.presentation.presenter.viewmodel.UserViewModel
import com.bangunkota.bangunkota.presentation.presenter.viewmodelfactory.UserViewModelFactory
import com.bangunkota.bangunkota.utils.UserPreferencesManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        val userPreferencesManager = UserPreferencesManager(requireActivity())
        val userViewModelFactory = UserViewModelFactory(userPreferencesManager)
        userViewModel = ViewModelProvider(this, userViewModelFactory)[UserViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel.userName.observe(viewLifecycleOwner){
            binding.topAppBar.title = it
        }

        userViewModel.userPhoto.observe(viewLifecycleOwner) {
            Glide.with(requireContext())
                .asBitmap()
                .load(it)
                .apply(RequestOptions.circleCropTransform())
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        // Konversi Bitmap menjadi Drawable
                        val iconDrawable = BitmapDrawable(resources, resource)
                        binding.topAppBar.menu.findItem(R.id.account).icon = iconDrawable
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // Handle jika gambar gagal dimuat atau dihapus
                        Toast.makeText(requireActivity(), "Glide Gagal Upload Image Profile", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        binding.topAppBar.setNavigationOnClickListener {
            Toast.makeText(requireActivity(), "Menu Clicked", Toast.LENGTH_SHORT).show()
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.account -> {
                    Toast.makeText(requireActivity(), "User Clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }
}