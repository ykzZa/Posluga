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
import dev.ykzza.posluga.data.entities.User
import dev.ykzza.posluga.databinding.FragmentRegisterBinding
import dev.ykzza.posluga.util.UiState
import dev.ykzza.posluga.util.hideView
import dev.ykzza.posluga.util.showToast
import dev.ykzza.posluga.util.showView

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding: FragmentRegisterBinding
        get() = _binding ?: throw RuntimeException("FragmentRegisterBinding is null")

    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        observeViewModel()
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.apply {
            buttonSignUp.setOnClickListener {
                viewModel.signUp(
                    getUserData(),
                    editTextEmail.text.toString(),
                    editTextPassword.text.toString()
                )
            }
            buttonBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun getUserData(): User = User(
        "",
        binding.editTextName.text.toString(),
        "",
        "",
        "",
        ""
    )

    private fun observeViewModel() {
        viewModel.signUp.observe(viewLifecycleOwner) { uiState ->
            when(uiState) {
                is UiState.Error -> {
                    binding.apply {
                        buttonSignUp.text = getString(R.string.sign_up_text)
                        progressBar.hideView()
                        showToast(uiState.errorMessage)
                    }
                }
                is UiState.Loading -> {
                    binding.apply {
                        buttonSignUp.text = ""
                        progressBar.showView()
                    }
                }
                is UiState.Success -> {
                    binding.apply {
                        buttonSignUp.text = getString(R.string.sign_up_text)
                        progressBar.hideView()
                        showToast(uiState.data)
                        findNavController().popBackStack(R.id.menuFragment, false)
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