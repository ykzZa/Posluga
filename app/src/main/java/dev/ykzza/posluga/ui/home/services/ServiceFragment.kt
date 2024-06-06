package dev.ykzza.posluga.ui.home.services

import android.os.Bundle
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
import dev.ykzza.posluga.databinding.FragmentServiceBinding
import dev.ykzza.posluga.ui.home.SliderAdapter
import dev.ykzza.posluga.ui.menu.profile.UserViewModel
import dev.ykzza.posluga.util.UiState
import dev.ykzza.posluga.util.hideView
import dev.ykzza.posluga.util.makeViewGone
import dev.ykzza.posluga.util.showToast
import dev.ykzza.posluga.util.showView

@AndroidEntryPoint
class ServiceFragment : Fragment() {

    private var _binding: FragmentServiceBinding? = null
    private val binding: FragmentServiceBinding
        get() = _binding ?: throw RuntimeException("FragmentServiceBinding can't be null")

    private val args: ServiceFragmentArgs by navArgs()

    private lateinit var serviceViewModel: ServiceViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentServiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        serviceViewModel = ViewModelProvider(this)[ServiceViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        serviceViewModel.getService(
            args.serviceId
        )
        observeViewModel()
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.apply {
            buttonBack.setOnClickListener {
                findNavController().popBackStack()
            }
            userCard.setOnClickListener {

            }
        }
    }

    private fun observeViewModel() {
        serviceViewModel.service.observe(viewLifecycleOwner) { uiState ->
            when(uiState) {
                is UiState.Error -> {
                    showToast(uiState.errorMessage)
                    findNavController().popBackStack()
                }
                is UiState.Loading -> {
                    binding.apply {
                        progressBar.showView()
                        scrollView.hideView()
                    }
                }
                is UiState.Success -> {
                    userViewModel.getUserData(
                        uiState.data.authorId
                    )
                    binding.apply {
                        progressBar.hideView()
                        scrollView.showView()
                        if(uiState.data.images.isEmpty()) {
                            buttonBack.setColorFilter(R.color.black)
                            viewPager.makeViewGone()
                        } else {
                            setupImageSlider(uiState.data.images)
                        }
                        textViewTitle.text = uiState.data.title
                        if(uiState.data.price == 0) {
                            textViewPrice.text = "Договірна"
                        } else {
                            textViewPrice.text = "${uiState.data.price} hrn"
                        }
                        textViewDescription.text = uiState.data.description
                        textViewDate.text = uiState.data.date
                        textViewGeo.text = "${uiState.data.state}, ${uiState.data.city}"
                    }
                }
            }
        }
        userViewModel.user.observe(viewLifecycleOwner) { uiState ->
            when(uiState) {
                is UiState.Error -> {
                    showToast(uiState.errorMessage)
                }
                is UiState.Loading -> {
                }
                is UiState.Success -> {
                    binding.apply {
                        authorName.text = uiState.data.nickname
                        Glide.with(this@ServiceFragment)
                            .load(uiState.data.photoUrl)
                            .placeholder(R.drawable.ic_profile_grey)
                            .into(binding.authorProfileImage)
                        userCard.showView()
                    }
                }
            }
        }
    }

    private fun setupImageSlider(imageList: List<String>) {
        val adapter = SliderAdapter(requireContext(), imageList)
        binding.viewPager.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}