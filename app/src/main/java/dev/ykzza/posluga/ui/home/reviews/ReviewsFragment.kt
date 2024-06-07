package dev.ykzza.posluga.ui.home.reviews

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
import dev.ykzza.posluga.data.entities.Review
import dev.ykzza.posluga.data.entities.User
import dev.ykzza.posluga.databinding.FragmentReviewsBinding
import dev.ykzza.posluga.util.UiState
import dev.ykzza.posluga.util.hideView
import dev.ykzza.posluga.util.showView

@AndroidEntryPoint
class ReviewsFragment : Fragment(), ReviewsAdapter.OnItemClickListener {

    private var _binding: FragmentReviewsBinding? = null
    private val binding: FragmentReviewsBinding
        get() = _binding ?: throw RuntimeException("FragmentReviewsBinding can't be null")

    private lateinit var viewModel: ReviewViewModel
    private val firebaseAuth: FirebaseAuth
        get() = FirebaseAuth.getInstance()

    private val args: ReviewsFragmentArgs by navArgs()

    private val recyclerViewAdapter by lazy {
        ReviewsAdapter(
            this
        )
    }

    private lateinit var reviews: List<Review>
    private lateinit var authors: List<User>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[ReviewViewModel::class.java]
        binding.recyclerViewReviews.adapter = recyclerViewAdapter
        observeViewModel()
        setOnClickListeners()
        viewModel.getReviews(
            args.userId
        )
    }

    private fun setOnClickListeners() {
        binding.apply {
            buttonBack.setOnClickListener {
                findNavController().popBackStack()
            }
            buttonAddReview.setOnClickListener {
                val action = ReviewsFragmentDirections.actionReviewsFragmentToCreateReviewFragment(
                    args.userId
                )
                findNavController().navigate(
                    action
                )
            }
        }
    }

    private fun observeViewModel() {
        viewModel.userReviews.observe(viewLifecycleOwner) { uiState ->
            when(uiState) {
                is UiState.Loading -> {
                    hideUi()
                    binding.progressBar.showView()
                }
                is UiState.Success -> {
                    if(uiState.data.isEmpty()) {
                        showUi()
                        binding.apply {
                            progressBar.hideView()
                            noDataTextView.showView()
                            noDataImageView.showView()
                        }
                    } else {
                        reviews = uiState.data
                        viewModel.getUsers(uiState.data.map {
                            it.authorId
                        })
                    }
                }
                else -> {
                    showUi()
                    binding.apply {
                        errorImageView.showView()
                        errorTextView.showView()
                    }
                }
            }
        }
        viewModel.authors.observe(viewLifecycleOwner) { uiState ->
            when(uiState) {
                is UiState.Success -> {
                    showUi()
                    binding.progressBar.hideView()
                    if(uiState.data.isEmpty()) {
                        binding.apply {
                            errorImageView.showView()
                            errorTextView.showView()
                        }
                    } else {
                        authors = uiState.data
                        val reviewUserPairs = reviews.zip(authors)
                        recyclerViewAdapter.submitList(reviewUserPairs)
                    }
                }
                else -> {
                    showUi()
                    binding.apply {
                        errorImageView.showView()
                        errorTextView.showView()
                    }
                }
            }
        }
    }

    private fun hideUi() {
        binding.apply {
            recyclerViewReviews.hideView()
            buttonAddReview.hideView()
            errorImageView.hideView()
            errorTextView.hideView()
            noDataImageView.hideView()
            noDataTextView.hideView()
        }
    }

    private fun showUi() {
        binding.apply {
            recyclerViewReviews.showView()
            if(!checkUser()) {
                buttonAddReview.showView()
            }
        }
    }

    private fun checkUser(): Boolean =
        args.userId == firebaseAuth.uid || firebaseAuth.currentUser == null

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAuthorClick(authorId: String) {
        val action = ReviewsFragmentDirections.actionReviewsFragmentToProfileFragment(
            authorId
        )
        findNavController().navigate(action)
    }
}