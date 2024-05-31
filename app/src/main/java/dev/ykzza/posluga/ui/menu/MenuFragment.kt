package dev.ykzza.posluga.ui.menu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import dev.ykzza.posluga.R
import dev.ykzza.posluga.databinding.FragmentMenuBinding
import dev.ykzza.posluga.util.makeViewGone
import dev.ykzza.posluga.util.showView

@AndroidEntryPoint
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setOnClickListeners()
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            updateUiForUser()
        } else {
            updateUiForGuest()
        }
    }

    private fun setOnClickListeners() {
        binding.apply {
            buttonProfile.setOnClickListener {
                onProfileButtonClick()
            }
            buttonServices.setOnClickListener {
                onServicesButtonClick()
            }
            buttonCreatedProjects.setOnClickListener {
                onProjectsButtonClick()
            }
            buttonSettings.setOnClickListener {
                onSettingsButtonClick()
            }
            buttonAbout.setOnClickListener {
                onAboutButtonClick()
            }
            buttonSignIn.setOnClickListener {
                onSignInButtonClick()
            }
            buttonLogout.setOnClickListener {
                onLogOutButtonClick()
            }
        }
    }

    private fun onProfileButtonClick() {
        findNavController().navigate(R.id.action_menuFragment_to_profileFragment)
    }

    private fun onServicesButtonClick() {
        findNavController().navigate(R.id.action_menuFragment_to_myServicesFragment)
    }

    private fun onProjectsButtonClick() {
        findNavController().navigate(R.id.action_menuFragment_to_myProjectsFragment)
    }

    private fun onSettingsButtonClick() {
        findNavController().navigate(R.id.action_menuFragment_to_settingsFragment)
    }

    private fun onAboutButtonClick() {
        findNavController().navigate(R.id.action_menuFragment_to_aboutFragment)
    }

    private fun onSignInButtonClick() {
        findNavController().navigate(R.id.action_menuFragment_to_loginFragment)
    }

    private fun onLogOutButtonClick() {
        firebaseAuth.signOut()
        updateUiForGuest()
    }

    private fun updateUiForUser() {
        binding.apply {
            buttonSignIn.makeViewGone()
            buttonProfile.showView()
            dividerProfile.showView()
            buttonServices.showView()
            dividerServices.showView()
            buttonCreatedProjects.showView()
            dividerCreatedProjects.showView()
            buttonLogout.showView()
        }
    }

    private fun updateUiForGuest() {
        binding.apply {
            buttonSignIn.showView()
            buttonProfile.makeViewGone()
            dividerProfile.makeViewGone()
            buttonServices.makeViewGone()
            dividerServices.makeViewGone()
            buttonCreatedProjects.makeViewGone()
            dividerCreatedProjects.makeViewGone()
            buttonLogout.makeViewGone()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}