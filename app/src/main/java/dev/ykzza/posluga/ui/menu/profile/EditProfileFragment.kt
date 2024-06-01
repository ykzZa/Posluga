package dev.ykzza.posluga.ui.menu.profile

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import dev.ykzza.posluga.R
import dev.ykzza.posluga.data.entities.User
import dev.ykzza.posluga.databinding.FragmentEditProfileBinding
import dev.ykzza.posluga.ui.auth.AuthViewModel
import dev.ykzza.posluga.util.UiState
import dev.ykzza.posluga.util.hideView
import dev.ykzza.posluga.util.showToast
import dev.ykzza.posluga.util.showView

@AndroidEntryPoint
class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding: FragmentEditProfileBinding
        get() = _binding ?: throw RuntimeException("FragmentEditProfileBinding is null")

    private val args: EditProfileFragmentArgs by navArgs()

    private lateinit var userViewModel: UserViewModel
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        observeViewModel()
        setOnClickListeners()
        userViewModel.getUserData(
            args.userId
        )
    }

    private fun setOnClickListeners() {
        binding.apply {
            buttonBack.setOnClickListener {
                findNavController().popBackStack()
            }
            buttonUpdateData.setOnClickListener {
                binding.apply {
                    userViewModel.updateUser(
                        args.userId,
                        hashMapOf(
                            "nickname" to editTextName.text.toString(),
                            "phoneNumber" to editTextPhoneNumber.text.toString(),
                            "instagram" to editTextInstagram.text.toString(),
                            "telegram" to editTextTelegram.text.toString()
                        )
                    )
                }
            }
            buttonRemoveProfilePicture.setOnClickListener {
                userViewModel.removeProfilePicture(args.userId)
            }
            imageViewProfilePic.setOnClickListener {
                pickImageFromGallery()
            }
        }
    }


    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val imageUri: Uri? = data.data
            if (imageUri != null) {
                userViewModel.saveImageUri(imageUri)
            }
        }
    }

    private fun observeViewModel() {
        userViewModel.user.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiState.Error -> {
                    showToast(uiState.errorMessage)
                    findNavController().popBackStack()
                }

                is UiState.Loading -> {
                    hideUi()
                    binding.apply {
                        progressBar.showView()
                    }
                }

                is UiState.Success -> {
                    binding.apply {
                        progressBar.hideView()
                        Glide
                            .with(requireContext())
                            .load(uiState.data.photoUrl)
                            .placeholder(R.drawable.ic_profile)
                            .circleCrop()
                            .into(imageViewProfilePic)

                        editTextName.setText(uiState.data.nickname)
                        editTextPhoneNumber.setText(uiState.data.phoneNumber)
                        editTextInstagram.setText(uiState.data.instagram)
                        editTextTelegram.setText(uiState.data.telegram)
                    }
                    showUi()
                }
            }
        }
        userViewModel.imageUri.observe(viewLifecycleOwner) {
            binding.apply {
                Glide
                    .with(requireContext())
                    .load(it)
                    .placeholder(R.drawable.ic_profile)
                    .circleCrop()
                    .into(imageViewProfilePic)
            }
        }
        userViewModel.dataUpdated.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiState.Error -> {
                    binding.apply {
                        progressBarUpdate.hideView()
                        showToast(uiState.errorMessage)
                    }
                }
                is UiState.Loading -> {
                    binding.apply {
                        progressBarUpdate.showView()
                        buttonUpdateData.text = ""
                    }
                }
                is UiState.Success -> {
                    binding.apply {
                        buttonUpdateData.text = getString(R.string.update_profile_text)
                        progressBarUpdate.hideView()
                        showToast("Data updated successfully")
                        findNavController().popBackStack()
                    }
                }
            }
        }
        userViewModel.pictureDeleted.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiState.Error -> {
                    binding.apply {
                        progressBarRemove.hideView()
                        buttonRemoveProfilePicture.text = getString(R.string.button_remove_photo_text)
                        showToast(uiState.errorMessage)
                    }
                }

                is UiState.Loading -> {
                    binding.apply {
                        buttonRemoveProfilePicture.text = ""
                        progressBarRemove.showView()
                    }
                }

                is UiState.Success -> {
                    binding.apply {
                        progressBarRemove.hideView()
                        buttonRemoveProfilePicture.text = getString(R.string.button_remove_photo_text)
                        Glide
                            .with(requireContext())
                            .load(R.drawable.ic_profile)
                            .placeholder(R.drawable.ic_profile)
                            .into(imageViewProfilePic)
                        showToast(uiState.data)
                    }
                }
            }
        }
    }

    private fun hideUi() {
        binding.apply {
            scrollViewContainer.hideView()
            buttonUpdateData.hideView()
        }
    }

    private fun showUi() {
        binding.apply {
            scrollViewContainer.showView()
            buttonUpdateData.showView()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val PICK_IMAGE_REQUEST = 122
    }
}