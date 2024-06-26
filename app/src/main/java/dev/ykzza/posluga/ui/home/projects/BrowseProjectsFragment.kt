package dev.ykzza.posluga.ui.home.projects

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.ykzza.posluga.databinding.FragmentBrowseProjectsBinding
import dev.ykzza.posluga.ui.home.HomeFragmentDirections
import dev.ykzza.posluga.ui.home.search_dialog.SearchSettingsDialogFragment
import dev.ykzza.posluga.util.UiState
import dev.ykzza.posluga.util.hideView
import dev.ykzza.posluga.util.showView

@AndroidEntryPoint
class BrowseProjectsFragment : Fragment(),
    ProjectsAdapter.OnItemClickListener,
    SearchSettingsDialogFragment.SearchDialogListener {

    private var _binding: FragmentBrowseProjectsBinding? = null
    private val binding: FragmentBrowseProjectsBinding
        get() = _binding ?: throw RuntimeException("FragmentBrowseProjectsBinding can't be null")

    private lateinit var viewModel: BrowseProjectsViewModel

    private val recyclerViewAdapter by lazy {
        ProjectsAdapter(
            this,
            requireContext()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBrowseProjectsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[BrowseProjectsViewModel::class.java]
        binding.recyclerViewProjects.adapter = recyclerViewAdapter
        observeViewModel()
        setOnClickListeners()
        viewModel.loadProjects()
    }

    private fun setOnClickListeners() {
        binding.floatingButtonSearch.setOnClickListener {
            val dialog = SearchSettingsDialogFragment(this)
            dialog.show(parentFragmentManager, "SETTINGS_SEARCH_DIALOG")
        }
    }

    private fun observeViewModel() {
        viewModel.projectsLoaded.observe(viewLifecycleOwner) { uiState ->
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
                        recyclerViewProjects.hideView()
                        errorImageView.showView()
                        errorTextView.showView()
                    }
                }
            }
        }
    }

    private fun hideUi() {
        binding.apply {
            recyclerViewProjects.hideView()
            floatingButtonSearch.hideView()
            errorImageView.hideView()
            errorTextView.hideView()
            noDataImageView.hideView()
            noDataTextView.hideView()
        }
    }

    private fun showUi() {
        binding.apply {
            recyclerViewProjects.showView()
            floatingButtonSearch.showView()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onItemClick(projectId: String) {
        val action = HomeFragmentDirections.actionHomeFragmentToProjectFragment(
            projectId
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
        viewModel.loadProjects(
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

        fun newInstance() = BrowseProjectsFragment()
    }

}