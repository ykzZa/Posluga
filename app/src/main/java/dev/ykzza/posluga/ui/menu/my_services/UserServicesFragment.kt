package dev.ykzza.posluga.ui.menu.my_services

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
import dev.ykzza.posluga.databinding.FragmentUserServicesBinding
import dev.ykzza.posluga.ui.home.services.ServicesAdapter
import dev.ykzza.posluga.util.UiState
import dev.ykzza.posluga.util.hideView
import dev.ykzza.posluga.util.showToast
import dev.ykzza.posluga.util.showView

@AndroidEntryPoint
class UserServicesFragment : Fragment(), MyServicesAdapter.OnItemClickListener,
    ServicesAdapter.OnItemClickListener {

    private var _binding: FragmentUserServicesBinding? = null
    private val binding: FragmentUserServicesBinding
        get() = _binding ?: throw RuntimeException("FragmentUserServicesBinding can't be null")

    private lateinit var viewModel: UserServicesViewModel
    private val args: UserServicesFragmentArgs by navArgs()

    private val firebaseAuth: FirebaseAuth
        get() = FirebaseAuth.getInstance()

    private val recyclerViewMyServicesAdapter by lazy {
        MyServicesAdapter(this)
    }

    private val recyclerViewUserServicesAdapter by lazy {
        ServicesAdapter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserServicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[UserServicesViewModel::class.java]
        if (firebaseAuth.currentUser == null) {
            binding.apply {
                recyclerViewServices.hideView()
                errorImageView.showView()
                errorTextView.text = "You need to login!"
                errorTextView.showView()
            }
        } else {
            setupRecyclerView()
            observeViewModel()
            viewModel.getUserServices(
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
        viewModel.userServices.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiState.Success -> {
                    binding.apply {
                        progressBar.hideView()
                        recyclerViewServices.showView()
                        if (uiState.data.isEmpty()) {
                            noDataTextView.showView()
                            noDataImageView.showView()
                        }
                    }
                    recyclerViewMyServicesAdapter.submitList(uiState.data)
                    recyclerViewUserServicesAdapter.submitList(uiState.data)
                }

                is UiState.Loading -> {
                    hideUi()
                    binding.apply {
                        progressBar.showView()
                    }
                }

                else -> {
                    binding.apply {
                        recyclerViewServices.hideView()
                        errorImageView.showView()
                        errorTextView.showView()
                    }
                }
            }
        }
        viewModel.serviceDeleted.observe(viewLifecycleOwner) { uiState ->
            when(uiState) {
                is UiState.Success -> {
                    showToast("Service deleted!")
                    viewModel.getUserServices(
                        args.userId
                    )
                }
                is UiState.Loading -> {

                }
                else -> {
                    showToast("Failed to delete service!")
                }
            }
        }
    }

    private fun hideUi() {
        binding.apply {
            recyclerViewServices.hideView()
            errorImageView.hideView()
            errorTextView.hideView()
            noDataImageView.hideView()
            noDataTextView.hideView()
        }
    }

    private fun setupRecyclerView() {
        if (firebaseAuth.uid != args.userId) {
            binding.recyclerViewServices.adapter = recyclerViewUserServicesAdapter
        } else {
            binding.recyclerViewServices.adapter = recyclerViewMyServicesAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onServiceClick(serviceId: String) {
        val action = UserServicesFragmentDirections.actionUserServicesFragmentToServiceFragment(
            serviceId
        )
        findNavController().navigate(action)
    }

    override fun onItemClick(serviceId: String) {
        val action = UserServicesFragmentDirections.actionUserServicesFragmentToServiceFragment(
            serviceId
        )
        findNavController().navigate(action)
    }

    override fun onEditClick(serviceId: String) {

    }

    override fun onDeleteClick(serviceId: String) {
        viewModel.deleteService(
            serviceId
        )
    }
}