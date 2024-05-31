package dev.ykzza.posluga.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import dev.ykzza.posluga.R
import dev.ykzza.posluga.databinding.FragmentLoginBinding
import dev.ykzza.posluga.util.UiState
import dev.ykzza.posluga.util.hideView
import dev.ykzza.posluga.util.showToast
import dev.ykzza.posluga.util.showView

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding
        get() = _binding ?: throw RuntimeException("FragmentLoginBinding is null")

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        observeViewModel()
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.apply {
            buttonSignIn.setOnClickListener {
                viewModel.signIn(
                    editTextEmail.text.toString(),
                    editTextPassword.text.toString()
                )
            }
            buttonGoogleSignIn.setOnClickListener {
                signInWithGoogle()
            }
            textViewPasswordRecovery.setOnClickListener {
                findNavController().navigate(
                    R.id.action_loginFragment_to_passwordRecoveryFragment
                )
            }
            textViewSignUp.setOnClickListener {
                findNavController().navigate(
                    R.id.action_loginFragment_to_registerFragment
                )
            }
            buttonBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)!!
            viewModel.signInWithGoogle(account)
        } catch (e: ApiException) {
            showToast(e.localizedMessage ?: "Oops, something went wrong")
        }
    }

    private fun observeViewModel() {
        viewModel.signIn.observe(viewLifecycleOwner) { uiState ->
            when(uiState) {
                is UiState.Error -> {
                    binding.apply {
                        progressBar.hideView()
                        buttonSignIn.text = getString(R.string.sign_in_text)
                        showToast(uiState.errorMessage)
                    }
                }
                is UiState.Loading -> {
                    binding.apply {
                        buttonSignIn.text = ""
                        progressBar.showView()
                    }
                }
                is UiState.Success -> {
                    binding.apply {
                        progressBar.hideView()
                        buttonSignIn.text = getString(R.string.sign_in_text)
                        showToast(uiState.data)
                        findNavController().popBackStack()
                    }
                }
            }
        }
        viewModel.signInWithGoogle.observe(viewLifecycleOwner) { uiState ->
            when(uiState) {
                is UiState.Error -> {
                    binding.apply {
                        progressBar.hideView()
                        buttonGoogleSignIn.text = getString(R.string.sign_in_text)
                        showToast(uiState.errorMessage)
                    }
                }
                is UiState.Loading -> {
                    binding.apply {
                        buttonGoogleSignIn.text = ""
                        progressBarGoogle.showView()
                    }
                }
                is UiState.Success -> {
                    binding.apply {
                        progressBarGoogle.hideView()
                        buttonGoogleSignIn.text = getString(R.string.sign_in_text)
                        showToast(uiState.data)
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    companion object {
        const val RC_SIGN_IN = 123
    }
}