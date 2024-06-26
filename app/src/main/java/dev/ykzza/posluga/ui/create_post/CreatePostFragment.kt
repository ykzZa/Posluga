package dev.ykzza.posluga.ui.create_post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import dev.ykzza.posluga.R
import dev.ykzza.posluga.databinding.FragmentCreatePostBinding
import dev.ykzza.posluga.util.showToast
import javax.inject.Inject

@AndroidEntryPoint
class CreatePostFragment : Fragment() {

    private var _binding: FragmentCreatePostBinding? = null
    private val binding: FragmentCreatePostBinding
        get() = _binding ?: throw RuntimeException("FragmentCreatePostBinding is null")

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.apply {
            buttonBack.setOnClickListener {
                findNavController().popBackStack()
            }
            buttonCreateProject.setOnClickListener {
                if(firebaseAuth.currentUser != null) {
                    findNavController().navigate(R.id.action_createPostFragment_to_createProjectFragment)
                } else {
                    showToast("You need to login!")
                }
            }
            buttonCreateService.setOnClickListener {
                if(firebaseAuth.currentUser != null) {
                    findNavController().navigate(R.id.action_createPostFragment_to_createServiceFragment)
                } else {
                    showToast("You need to login!")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}