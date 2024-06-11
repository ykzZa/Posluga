package dev.ykzza.posluga.ui.menu.my_projects

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import dev.ykzza.posluga.databinding.FragmentUserProjectsBinding
import dev.ykzza.posluga.ui.home.projects.ProjectsAdapter
import dev.ykzza.posluga.util.UiState
import dev.ykzza.posluga.util.hideView
import dev.ykzza.posluga.util.showToast
import dev.ykzza.posluga.util.showView

@AndroidEntryPoint
class UserProjectsFragment : Fragment(), MyProjectsAdapter.OnItemClickListener,
    ProjectsAdapter.OnItemClickListener {

    private var _binding: FragmentUserProjectsBinding? = null
    private val binding: FragmentUserProjectsBinding
        get() = _binding ?: throw RuntimeException("FragmentUserProjectsBinding can't be null")

    private lateinit var viewModel: UserProjectsViewModel
    private val args: UserProjectsFragmentArgs by navArgs()

    private val firebaseAuth: FirebaseAuth
        get() = FirebaseAuth.getInstance()

    private val recyclerViewMyProjectsAdapter by lazy {
        MyProjectsAdapter(this)
    }

    private val recyclerViewUserProjectsAdapter by lazy {
        ProjectsAdapter(this, requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProjectsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[UserProjectsViewModel::class.java]
        if (firebaseAuth.currentUser == null) {
            binding.apply {
                recyclerViewProjects.hideView()
                errorImageView.showView()
                errorTextView.text = "You need to login!"
                errorTextView.showView()
            }
        } else {
            setupRecyclerView()
            observeViewModel()
            viewModel.getUserProjects(
                args.userId
            )
        }
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.apply {
            buttonBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.userProjects.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiState.Success -> {
                    binding.apply {
                        progressBar.hideView()
                        recyclerViewProjects.showView()
                        if (uiState.data.isEmpty()) {
                            noDataTextView.showView()
                            noDataImageView.showView()
                        }
                    }
                    recyclerViewMyProjectsAdapter.submitList(uiState.data)
                    recyclerViewUserProjectsAdapter.submitList(uiState.data)
                }

                is UiState.Loading -> {
                    hideUi()
                    binding.apply {
                        progressBar.showView()
                    }
                }

                else -> {
                    binding.apply {
                        recyclerViewProjects.hideView()
                        errorImageView.showView()
                        errorTextView.showView()
                    }
                }
            }
        }
        viewModel.projectDeleted.observe(viewLifecycleOwner) { uiState ->
            when(uiState) {
                is UiState.Success -> {
                    viewModel.getUserProjects(
                        args.userId
                    )
                }
                is UiState.Loading -> {

                }
                else -> {
                    showToast("Failed to delete project!")
                }
            }
        }
    }

    private fun hideUi() {
        binding.apply {
            recyclerViewProjects.hideView()
            errorImageView.hideView()
            errorTextView.hideView()
            noDataImageView.hideView()
            noDataTextView.hideView()
        }
    }

    private fun setupRecyclerView() {
        if (firebaseAuth.uid != args.userId) {
            binding.recyclerViewProjects.adapter = recyclerViewUserProjectsAdapter
        } else {
            binding.recyclerViewProjects.adapter = recyclerViewMyProjectsAdapter
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(projectId: String) {
        val action = UserProjectsFragmentDirections.actionUserProjectsFragmentToProjectFragment(
            projectId
        )
        findNavController().navigate(action)
    }

    override fun onProjectClick(projectId: String) {
        val action = UserProjectsFragmentDirections.actionUserProjectsFragmentToProjectFragment(
            projectId
        )
        findNavController().navigate(action)
    }

    override fun onEditClick(projectId: String) {
        val action = UserProjectsFragmentDirections.actionUserProjectsFragmentToEditProjectFragment(
            projectId
        )
        findNavController().navigate(action)
    }

    override fun onDeleteClick(projectId: String) {
        viewModel.deleteProject(
            projectId
        )
    }
}