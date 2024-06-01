package dev.ykzza.posluga.ui.menu.profile

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
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import dev.ykzza.posluga.R
import dev.ykzza.posluga.databinding.FragmentProfileBinding
import dev.ykzza.posluga.util.UiState
import dev.ykzza.posluga.util.hideView
import dev.ykzza.posluga.util.makeViewGone
import dev.ykzza.posluga.util.showToast
import dev.ykzza.posluga.util.showView
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding: FragmentProfileBinding
        get() = _binding ?: throw RuntimeException("FragmentProfileBinding is null")

    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    private lateinit var viewModel: UserViewModel

    private val args: ProfileFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[UserViewModel::class.java]
        setOnClickListeners()
        hideUi()
        observeViewModel()
        viewModel.getUserData(
            args.userId
        )
    }

    private fun setOnClickListeners() {
        binding.apply {
            buttonBack.setOnClickListener {
                findNavController().popBackStack()
            }
            buttonEdit.setOnClickListener {
                val action = ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment(
                    args.userId
                )
                findNavController().navigate(action)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.user.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiState.Error -> {
                    showToast(uiState.errorMessage)
                    findNavController().popBackStack()
                }

                is UiState.Loading -> {
                    binding.progressBar.showView()
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

                        textViewName.text = uiState.data.nickname
                        textViewPhone.text = uiState.data.phoneNumber
                        textViewTelegram.text = uiState.data.telegram
                        textViewInstagram.text = uiState.data.instagram
                    }
                    showUi()
                }
            }
        }
    }

    private fun hideUi() {
        binding.apply {
            buttonEdit.hideView()
            imageViewProfilePic.hideView()
            textViewDetails.hideView()
            imageViewPhone.hideView()
            imageViewTelegram.hideView()
            imageViewInstagram.hideView()
            textViewInstagramInfo.hideView()
            textViewPhoneInfo.hideView()
            textViewTelegramInfo.hideView()
        }
    }

    private fun showUi() {
        binding.apply {
            imageViewProfilePic.showView()
            textViewDetails.showView()
            imageViewPhone.showView()
            imageViewTelegram.showView()
            imageViewInstagram.showView()
            textViewTelegramInfo.showView()
            textViewInstagramInfo.showView()
            textViewPhoneInfo.showView()
        }
        updateEditButtonVisibility()
    }


    private fun updateEditButtonVisibility() {
        val currentUserId = firebaseAuth.currentUser?.uid
        val userId = args.userId
        if (currentUserId != userId) {
            binding.buttonEdit.makeViewGone()
        } else {
            binding.buttonEdit.showView()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}