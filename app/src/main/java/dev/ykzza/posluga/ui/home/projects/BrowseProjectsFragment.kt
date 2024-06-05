package dev.ykzza.posluga.ui.home.projects

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import dev.ykzza.posluga.databinding.FragmentBrowseProjectsBinding
import dev.ykzza.posluga.util.UiState
import dev.ykzza.posluga.util.hideView
import dev.ykzza.posluga.util.showView

@AndroidEntryPoint
class BrowseProjectsFragment : Fragment(), ProjectsAdapter.OnItemClickListener {

    private var _binding: FragmentBrowseProjectsBinding? = null
    private val binding: FragmentBrowseProjectsBinding
        get() = _binding ?: throw RuntimeException("FragmentBrowseProjectsBinding can't be null")

    private lateinit var viewModel: BrowseProjectsViewModel

    private val recyclerViewAdapter by lazy {
        ProjectsAdapter(
            this
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
        viewModel.loadServices()
    }

    private fun setOnClickListeners() {

    }

    private fun observeViewModel() {
        viewModel.projectsLoaded.observe(viewLifecycleOwner) { uiState ->
            when(uiState) {
                is UiState.Error -> {
                    showUi()
                    binding.apply {
                        errorImageView.showView()
                        errorTextView.showView()
                    }
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
                    }
                    Log.d("BrowseServicesFragment", uiState.data.toString())
                    recyclerViewAdapter.submitList(uiState.data)
                    showUi()
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

    companion object {

        fun newInstance() = BrowseProjectsFragment()
    }

    override fun onItemClick(projectId: String) {
        Log.d("BrowseProjectsFragment", projectId)
    }
}