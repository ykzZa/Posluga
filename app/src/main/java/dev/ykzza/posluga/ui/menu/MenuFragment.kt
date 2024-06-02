package dev.ykzza.posluga.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import dev.ykzza.posluga.R
import dev.ykzza.posluga.databinding.FragmentMenuBinding
import dev.ykzza.posluga.ui.auth.AuthViewModel
import dev.ykzza.posluga.util.makeViewGone
import dev.ykzza.posluga.util.showView

@AndroidEntryPoint
class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding: FragmentMenuBinding
        get() = _binding ?: throw RuntimeException("FragmentMenuBinding is null")

    private val firebaseAuth: FirebaseAuth
        get() = FirebaseAuth.getInstance()

    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
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
                val action = MenuFragmentDirections.actionMenuFragmentToProfileFragment(
                    firebaseAuth.uid ?: ""
                )
                findNavController().navigate(action)
            }
            buttonServices.setOnClickListener {
                findNavController().navigate(R.id.action_menuFragment_to_myServicesFragment)
            }
            buttonCreatedProjects.setOnClickListener {
                findNavController().navigate(R.id.action_menuFragment_to_myProjectsFragment)
            }
            buttonSettings.setOnClickListener {
                findNavController().navigate(R.id.action_menuFragment_to_settingsFragment)
            }
            buttonAbout.setOnClickListener {
                findNavController().navigate(R.id.action_menuFragment_to_aboutFragment)
            }
            buttonSignIn.setOnClickListener {
                findNavController().navigate(R.id.action_menuFragment_to_loginFragment)
            }
            buttonLogout.setOnClickListener {
                viewModel.signOut {
                    updateUiForGuest()
                }
            }
        }
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