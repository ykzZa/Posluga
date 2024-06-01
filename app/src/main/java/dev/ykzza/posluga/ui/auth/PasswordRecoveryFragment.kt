package dev.ykzza.posluga.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.ykzza.posluga.R
import dev.ykzza.posluga.databinding.FragmentPasswordRecoveryBinding
import dev.ykzza.posluga.util.UiState
import dev.ykzza.posluga.util.hideView
import dev.ykzza.posluga.util.showToast
import dev.ykzza.posluga.util.showView

@AndroidEntryPoint
class PasswordRecoveryFragment : Fragment() {

    private var _binding: FragmentPasswordRecoveryBinding? = null
    private val binding: FragmentPasswordRecoveryBinding
        get() = _binding ?: throw RuntimeException("FragmentPasswordRecoveryBinding is null")

    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPasswordRecoveryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        observeViewModel()
        binding.apply {
            buttonRecover.setOnClickListener {
                viewModel.recoverPassword(editTextEmail.text.toString())
            }
            buttonBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.recoverPassword.observe(viewLifecycleOwner) { uiState ->
            when(uiState) {
                is UiState.Error -> {
                    binding.apply {
                        buttonRecover.text = getString(R.string.recover_text)
                        progressBar.hideView()
                        showToast(uiState.errorMessage)
                    }
                }
                is UiState.Loading -> {
                    binding.apply {
                        buttonRecover.text = ""
                        progressBar.showView()
                    }
                }
                is UiState.Success -> {
                    binding.apply {
                        progressBar.hideView()
                        buttonRecover.text = getString(R.string.recover_text)
                        showToast(uiState.data)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}