package dev.ykzza.posluga.ui.saved_posts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import dev.ykzza.posluga.databinding.FragmentSavedPostsBinding
import dev.ykzza.posluga.ui.home.services.ServicesAdapter
import dev.ykzza.posluga.util.UiState
import dev.ykzza.posluga.util.hideView
import dev.ykzza.posluga.util.showView

@AndroidEntryPoint
class SavedPostsFragment : Fragment(), ServicesAdapter.OnItemClickListener {

    private var _binding: FragmentSavedPostsBinding? = null
    private val binding: FragmentSavedPostsBinding
        get() = _binding ?: throw RuntimeException("FragmentSavedPostsBinding can't be null")

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var viewModel: SavedPostsViewModel

    private val recyclerViewAdapter by lazy {
        ServicesAdapter(
            this, requireContext()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedPostsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[SavedPostsViewModel::class.java]
        binding.recyclerViewServices.adapter = recyclerViewAdapter
        if(firebaseAuth.currentUser != null) {
            observeViewModel()
            viewModel.getUser(
                firebaseAuth.uid!!
            )
        } else {
            binding.apply {
                recyclerViewServices.hideView()
                errorImageView.showView()
                errorTextView.text = "You need to login!"
                errorTextView.showView()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.user.observe(viewLifecycleOwner) { uiState ->
            when(uiState) {
                is UiState.Success -> {
                    viewModel.getServicesByIds(
                        uiState.data.favourites
                    )
                }
                is UiState.Loading -> {
                    binding.apply {
                        progressBar.showView()
                    }
                }
                else -> {
                    binding.apply {
                        errorImageView.showView()
                        errorTextView.showView()
                    }
                }
            }
        }
        viewModel.savedServices.observe(viewLifecycleOwner) { uiState ->
            when(uiState) {
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
                }
                is UiState.Loading -> {
                    binding.apply {
                        progressBar.showView()
                    }
                }
                else -> {
                    binding.apply {
                        errorImageView.showView()
                        errorTextView.showView()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(serviceId: String) {
        val action = SavedPostsFragmentDirections.actionSavedPostsFragmentToServiceFragment(
            serviceId
        )
        findNavController().navigate(action)
    }
}