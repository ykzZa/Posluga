package dev.ykzza.posluga.ui.home.services

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.ykzza.posluga.databinding.FragmentBrowseServicesBinding
import dev.ykzza.posluga.ui.home.HomeFragmentDirections
import dev.ykzza.posluga.ui.home.search_dialog.SearchSettingsDialogFragment
import dev.ykzza.posluga.util.UiState
import dev.ykzza.posluga.util.hideView
import dev.ykzza.posluga.util.showView

@AndroidEntryPoint
class BrowseServicesFragment : Fragment(),
    ServicesAdapter.OnItemClickListener,
    SearchSettingsDialogFragment.SearchDialogListener {

    private var _binding: FragmentBrowseServicesBinding? = null
    private val binding: FragmentBrowseServicesBinding
        get() = _binding ?: throw RuntimeException("FragmentBrowseServicesBinding can't be null")

    private lateinit var viewModel: BrowseServicesViewModel

    private val recyclerViewAdapter by lazy {
        ServicesAdapter(
            this,
            requireContext()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBrowseServicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[BrowseServicesViewModel::class.java]
        binding.recyclerViewServices.adapter = recyclerViewAdapter
        observeViewModel()
        setOnClickListeners()
        viewModel.loadServices()
    }

    private fun setOnClickListeners() {
        binding.floatingButtonSearch.setOnClickListener {
            val dialog = SearchSettingsDialogFragment(this)
            dialog.show(parentFragmentManager, "SETTINGS_SEARCH_DIALOG")
        }
    }

    private fun observeViewModel() {
        viewModel.servicesLoaded.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiState.Loading -> {
                    hideUi()
                    binding.apply {
                        progressBar.showView()
                    }
                }

                is UiState.Success -> {
                    binding.apply {
                        progressBar.hideView()
                    }
                    if (uiState.data.isEmpty()) {
                        binding.apply {
                            noDataTextView.showView()
                            noDataImageView.showView()
                        }
                    }
                    recyclerViewAdapter.submitList(uiState.data)
                    showUi()
                }

                else -> {
                    showUi()
                    binding.apply {
                        recyclerViewServices.hideView()
                        errorImageView.showView()
                        errorTextView.showView()
                    }
                }
            }
        }
    }

    private fun hideUi() {
        binding.apply {
            recyclerViewServices.hideView()
            floatingButtonSearch.hideView()
            errorImageView.hideView()
            errorTextView.hideView()
            noDataImageView.hideView()
            noDataTextView.hideView()
        }
    }

    private fun showUi() {
        binding.apply {
            recyclerViewServices.showView()
            floatingButtonSearch.showView()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onItemClick(serviceId: String) {
        val action = HomeFragmentDirections.actionHomeFragmentToServiceFragment(
            serviceId
        )
        findNavController().navigate(action)
    }

    override fun onSearchClick(
        searchRequest: String?,
        descriptionSearch: Boolean,
        category: String?,
        subCategory: String?,
        state: String?,
        city: String?,
        minPrice: Int?,
        maxPrice: Int?
    ) {
        viewModel.loadServices(
            searchQuery = searchRequest,
            descriptionSearch = descriptionSearch,
            minPrice = minPrice,
            maxPrice = maxPrice,
            category = category,
            subCategory = subCategory,
            state = state,
            city = city
        )
    }

    companion object {

        fun newInstance() = BrowseServicesFragment()
    }
}