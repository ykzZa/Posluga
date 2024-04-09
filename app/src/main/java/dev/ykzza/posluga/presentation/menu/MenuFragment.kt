package dev.ykzza.posluga.presentation.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import dev.ykzza.posluga.R
import dev.ykzza.posluga.databinding.FragmentMenuBinding

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding: FragmentMenuBinding
        get() = _binding ?: throw RuntimeException("FragmentMenuBinding is null")

    private val firebaseAuth: FirebaseAuth
        get() = FirebaseAuth.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)

        binding.apply {
            loginButton.setOnClickListener {
                login()
            }
            buttonLogout.setOnClickListener {
                logout()
            }
            buttonAbout.setOnClickListener {
                findNavController().navigate(R.id.action_menuFragment_to_aboutFragment)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            updateUiForUser()
        } else {
            updateUiForGuest()
        }
    }

    private fun login() {
        findNavController().navigate(R.id.action_menuFragment_to_loginFragment)
    }

    private fun logout() {
        firebaseAuth.signOut()
        updateUiForGuest()
    }

    private fun updateUiForUser() {
        binding.apply {
            textViewAdvice.visibility = View.GONE
            loginButton.visibility = View.GONE
            buttonProfile.visibility = View.VISIBLE
            dividerProfile.visibility = View.VISIBLE
            buttonServices.visibility = View.VISIBLE
            dividerServices.visibility = View.VISIBLE
            buttonCreatedProjects.visibility = View.VISIBLE
            dividerCreatedProjects.visibility = View.VISIBLE
            buttonParticipatingProjects.visibility = View.VISIBLE
            dividerParticipatingProjects.visibility = View.VISIBLE
            buttonLogout.visibility = View.VISIBLE
        }
    }

    private fun updateUiForGuest() {
        binding.apply {
            textViewAdvice.visibility = View.VISIBLE
            loginButton.visibility = View.VISIBLE
            buttonProfile.visibility = View.GONE
            dividerProfile.visibility = View.GONE
            buttonServices.visibility = View.GONE
            dividerServices.visibility = View.GONE
            buttonCreatedProjects.visibility = View.GONE
            dividerCreatedProjects.visibility = View.GONE
            buttonParticipatingProjects.visibility = View.GONE
            dividerParticipatingProjects.visibility = View.GONE
            buttonLogout.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val RC_SIGN_IN = 123 
        private const val TAG = "MenuFragment"
    }
}