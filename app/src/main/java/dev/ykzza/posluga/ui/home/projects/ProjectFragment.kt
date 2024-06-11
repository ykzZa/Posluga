package dev.ykzza.posluga.ui.home.projects

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
import dev.ykzza.posluga.databinding.FragmentProjectBinding
import dev.ykzza.posluga.ui.home.SliderAdapter
import dev.ykzza.posluga.ui.menu.profile.UserViewModel
import dev.ykzza.posluga.util.UiState
import dev.ykzza.posluga.util.convertTimestampToFormattedDateTime
import dev.ykzza.posluga.util.getStringArrayEnglish
import dev.ykzza.posluga.util.getSystemLanguage
import dev.ykzza.posluga.util.hideView
import dev.ykzza.posluga.util.makeViewGone
import dev.ykzza.posluga.util.showToast
import dev.ykzza.posluga.util.showView
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class ProjectFragment : Fragment() {

    private var _binding: FragmentProjectBinding? = null
    private val binding: FragmentProjectBinding
        get() = _binding ?: throw RuntimeException("FragmentProjectBinding can't be null")

    private val args: ProjectFragmentArgs by navArgs()

    private lateinit var projectViewModel: ProjectViewModel
    private lateinit var userViewModel: UserViewModel

    private lateinit var userId: String

    @Inject
    @Named("cities")
    lateinit var cities: Array<Int>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        projectViewModel = ViewModelProvider(this)[ProjectViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        projectViewModel.getProject(
            args.projectId
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
                val action = ProjectFragmentDirections.actionProjectFragmentToProfileFragment(
                    userId
                )
                findNavController().navigate(action)
            }
        }
    }

    private fun observeViewModel() {
        projectViewModel.project.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
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
                        if (uiState.data.images.isEmpty()) {
                            viewPager.makeViewGone()
                        } else {
                            setupImageSlider(uiState.data.images)
                        }
                        textViewTitle.text = uiState.data.title
                        if (uiState.data.price == 0) {
                            textViewPrice.text = getString(R.string.tradeble_price)
                        } else {
                            if(getSystemLanguage() == "en") {
                                textViewPrice.text = "${uiState.data.price} hrn"
                            } else {
                                textViewPrice.text = "${uiState.data.price} грн"
                            }
                        }
                        textViewDescription.text = uiState.data.description
                        textViewDate.text = convertTimestampToFormattedDateTime(
                            uiState.data.date.seconds
                        )
                        if(getSystemLanguage() == "en") {
                            textViewGeo.text = "${uiState.data.state} oblast', ${uiState.data.city}"
                        } else {
                            val stateIndex =
                                getStringArrayEnglish(
                                    requireContext(),
                                    R.array.states
                                ).indexOf(uiState.data.state)
                            val state = requireContext().resources.getStringArray(R.array.states)[stateIndex]
                            val citiesArray = cities[stateIndex]
                            val cityIndex =
                                getStringArrayEnglish(
                                    requireContext(),
                                    citiesArray
                                ).indexOf(uiState.data.city)
                            val city = requireContext().resources.getStringArray(citiesArray)[cityIndex]
                            textViewGeo.text = "${state} область, ${city}"
                        }
                    }
                }
            }
        }
        userViewModel.user.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiState.Error -> {
                    showToast(uiState.errorMessage)
                }

                is UiState.Loading -> {
                }

                is UiState.Success -> {
                    binding.apply {
                        userId = uiState.data.id
                        authorName.text = uiState.data.nickname
                        Glide.with(this@ProjectFragment)
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