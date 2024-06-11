package dev.ykzza.posluga.ui.home.reviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import dev.ykzza.posluga.R
import dev.ykzza.posluga.databinding.FragmentCreateReviewBinding
import dev.ykzza.posluga.util.UiState
import dev.ykzza.posluga.util.hideView
import dev.ykzza.posluga.util.showToast
import dev.ykzza.posluga.util.showView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class CreateReviewFragment : Fragment() {

    private var _binding: FragmentCreateReviewBinding? = null
    private val binding: FragmentCreateReviewBinding
        get() = _binding ?: throw RuntimeException("FragmentCreateReviewBinding can't be null")

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var viewModel: ReviewViewModel

    private val args: CreateReviewFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[ReviewViewModel::class.java]
        setOnClickListeners()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.addedReview.observe(viewLifecycleOwner) { uiState ->
            when(uiState) {
                is UiState.Success -> {
                    findNavController().popBackStack()
                }
                is UiState.Loading -> {
                    binding.apply {
                        buttonWriteReview.text = ""
                        progressBar.showView()
                    }
                }
                is UiState.Error -> {
                    binding.apply {
                        showToast(uiState.errorMessage)
                        buttonWriteReview.setText(R.string.button_write_review_text)
                        progressBar.hideView()
                    }
                }
            }
        }
    }

    private fun setOnClickListeners() {
        binding.apply {
            buttonBack.setOnClickListener {
                findNavController().popBackStack()
            }
            buttonWriteReview.setOnClickListener {
                val titleText = editTextReviewTitle.text.toString()
                val reviewText = editTextReviewDescription.text.toString()
                val rating = ratingBar.rating.toInt()

                if(titleText.isNotBlank() && rating > 0) {
                    viewModel.addReview(
                        args.userId,
                        firebaseAuth.uid ?: throw RuntimeException("Guest can't add reviews"),
                        titleText,
                        reviewText,
                        Timestamp.now(),
                        rating
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}