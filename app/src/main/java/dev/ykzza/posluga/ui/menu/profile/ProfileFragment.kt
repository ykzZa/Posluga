package dev.ykzza.posluga.ui.menu.profile

import android.os.Bundle
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
import dev.ykzza.posluga.util.showToast
import dev.ykzza.posluga.util.showView

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding: FragmentProfileBinding
        get() = _binding ?: throw RuntimeException("FragmentProfileBinding is null")

    private lateinit var viewModel: UserViewModel
    private val firebaseAuth: FirebaseAuth
        get() = FirebaseAuth.getInstance()

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
        setupUi()
        observeViewModel()
        viewModel.getStatistic(
            args.userId
        )
        viewModel.getUserData(
            args.userId
        )
    }

    private fun setupUi() {
        if(args.userId != firebaseAuth.uid) {
            binding.buttonChat.showView()
            binding.buttonEdit.hideView()
        } else {
            binding.buttonChat.hideView()
            binding.buttonEdit.showView()
        }
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
            textViewReviewsCount.setOnClickListener {
                val action = ProfileFragmentDirections.actionProfileFragmentToReviewsFragment(
                    args.userId
                )
                findNavController().navigate(action)
            }
            textViewServicesCount.setOnClickListener {
                val action = ProfileFragmentDirections.actionProfileFragmentToUserServicesFragment(
                    args.userId
                )
                findNavController().navigate(action)
            }
            textViewProjectsCount.setOnClickListener {
                val action = ProfileFragmentDirections.actionProfileFragmentToUserProjectsFragment(
                    args.userId
                )
                findNavController().navigate(action)
            }
            buttonChat.setOnClickListener {
                val action = ProfileFragmentDirections.actionProfileFragmentToOpenedChatFragment(
                    null, args.userId
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
        viewModel.projectStats.observe(viewLifecycleOwner) { uiState ->
            when(uiState) {
                is UiState.Error -> {
                    binding.apply {
                        statisticContainer.hideView()
                        showToast(uiState.errorMessage)
                    }
                }
                is UiState.Loading -> {

                }
                is UiState.Success -> {
                    binding.apply {
                        statisticContainer.showView()
                        textViewProjectsCount.text = "${uiState.data}\nprojects"
                    }
                }
            }
        }
        viewModel.serviceStats.observe(viewLifecycleOwner) { uiState ->
            when(uiState) {
                is UiState.Error -> {
                    binding.apply {
                        statisticContainer.hideView()
                        showToast(uiState.errorMessage)
                    }
                }
                is UiState.Loading -> {

                }
                is UiState.Success -> {
                    binding.apply {
                        statisticContainer.showView()
                        textViewServicesCount.text = "${uiState.data}\nservices"
                    }
                }
            }
        }
        viewModel.reviewsStats.observe(viewLifecycleOwner) { uiState ->
            when(uiState) {
                is UiState.Error -> {
                    binding.apply {
                        statisticContainer.hideView()
                        showToast(uiState.errorMessage)
                    }
                }
                is UiState.Loading -> {

                }
                is UiState.Success -> {
                    binding.apply {
                        statisticContainer.showView()
                        textViewReviewsCount.text = "${uiState.data}\nreviews"
                    }
                }
            }
        }
    }

    private fun hideUi() {
        binding.apply {
            buttonEdit.hideView()
            imageViewProfilePic.hideView()
            statisticContainer.hideView()
            textViewDetails.hideView()
            imageViewPhone.hideView()
            imageViewTelegram.hideView()
            imageViewInstagram.hideView()
            textViewInstagramInfo.hideView()
            textViewPhoneInfo.hideView()
            buttonChat.hideView()
            textViewTelegramInfo.hideView()
        }
    }

    private fun showUi() {
        binding.apply {
            imageViewProfilePic.showView()
            textViewDetails.showView()
            statisticContainer.showView()
            imageViewPhone.showView()
            imageViewTelegram.showView()
            imageViewInstagram.showView()
            textViewTelegramInfo.showView()
            textViewInstagramInfo.showView()
            textViewPhoneInfo.showView()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}