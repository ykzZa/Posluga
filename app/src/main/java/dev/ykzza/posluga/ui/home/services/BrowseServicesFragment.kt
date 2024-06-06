package dev.ykzza.posluga.ui.home.services

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import dev.ykzza.posluga.databinding.FragmentBrowseServicesBinding
import dev.ykzza.posluga.ui.home.SearchSettingsDialogFragment
import dev.ykzza.posluga.util.UiState
import dev.ykzza.posluga.util.hideView
import dev.ykzza.posluga.util.showView

@AndroidEntryPoint
class BrowseServicesFragment : Fragment(),
    ServicesAdapter.OnItemClickListener,
    SearchSettingsDialogFragment.SearchDialogListener
{

    private var _binding: FragmentBrowseServicesBinding? = null
    private val binding: FragmentBrowseServicesBinding
        get() = _binding ?: throw RuntimeException("FragmentBrowseServicesBinding can't be null")

    private lateinit var viewModel: BrowseServicesViewModel

    private val recyclerViewAdapter by lazy {
        ServicesAdapter(
            this
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
                    recyclerViewAdapter.submitList(uiState.data)
                    showUi()
                }
                is UiState.Error -> {
                    showUi()
                    binding.apply {
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