package dev.ykzza.posluga.ui.create_post.create_project

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import dev.ykzza.posluga.R
import dev.ykzza.posluga.databinding.FragmentCreateProjectBinding
import dev.ykzza.posluga.ui.create_post.ImagesAdapter
import dev.ykzza.posluga.util.UiState
import dev.ykzza.posluga.util.hideView
import dev.ykzza.posluga.util.showToast
import dev.ykzza.posluga.util.showView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class CreateProjectFragment : Fragment(), ImagesAdapter.OnItemClickListener {

    private var _binding: FragmentCreateProjectBinding? = null
    private val binding: FragmentCreateProjectBinding
        get() = _binding ?: throw RuntimeException("FragmentCreateProjectBinding is null")

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var viewModel: ProjectViewModel

    private val recyclerViewAdapter by lazy {
        ImagesAdapter(this)
    }

    @Inject
    @Named("categories")
    lateinit var categories: Array<String>

    @Inject
    @Named("subCategories")
    lateinit var subCategories: Array<Int>

    @Inject
    @Named("states")
    lateinit var states: Array<String>

    @Inject
    @Named("cities")
    lateinit var cities: Array<Int>

    @Inject
    @Named("categoryAdapter")
    lateinit var categoryAdapter: ArrayAdapter<CharSequence>
    private lateinit var subCategoryAdapter: ArrayAdapter<CharSequence>

    @Inject
    @Named("statesAdapter")
    lateinit var statesAdapter: ArrayAdapter<CharSequence>
    private lateinit var citiesAdapter: ArrayAdapter<CharSequence>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateProjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[ProjectViewModel::class.java]
        binding.recyclerViewImages.adapter = recyclerViewAdapter
        setOnClickListeners()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.imagesUriList.observe(viewLifecycleOwner) {
            recyclerViewAdapter.submitList(it)
        }
        viewModel.imagesUploaded.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiState.Success -> {
                    binding.apply {
                        viewModel.postProject(
                            editTextProjectTitle.text.toString(),
                            editTextProjectDescription.text.toString(),
                            firebaseAuth.uid!!,
                            LocalDateTime.now()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                            editTextProjectPrice.text.toString(),
                            uiState.data
                        )
                    }
                }

                else -> {
                    showToast("Failed to upload images, try again")
                    binding.apply {
                        progressBar.hideView()
                        buttonCreateProject.setText(R.string.button_create_project_text)
                    }
                }
            }
        }
        viewModel.projectPosted.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiState.Error -> {
                    binding.apply {
                        buttonCreateProject.setText(R.string.button_create_project_text)
                    }
                    showToast(uiState.errorMessage)
                }

                is UiState.Loading -> {
                    binding.apply {
                        buttonCreateProject.text = ""
                        progressBar.showView()
                    }
                }

                is UiState.Success -> {
                    binding.apply {
                        progressBar.hideView()
                        buttonCreateProject.setText(R.string.button_create_project_text)
                    }
                    findNavController().popBackStack()
                    showToast("Project has been posted")
                }
            }
        }
    }

    private fun setOnClickListeners() {
        binding.apply {
            buttonBack.setOnClickListener {
                findNavController().popBackStack()
            }
            buttonCreateProject.setOnClickListener {
                viewModel.uploadImages(
                    firebaseAuth.uid ?: throw RuntimeException("User is not logged")
                )
            }
            textViewImages.setOnClickListener {
                pickImagesFromGallery()
            }
        }
        prepareAutoCompleteTexts()
    }

    private fun pickImagesFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PICK_IMAGES_REQUEST)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGES_REQUEST && resultCode == Activity.RESULT_OK) {
            val imageUris = mutableListOf<Uri>()
            if (data?.clipData != null) {
                val count = data.clipData!!.itemCount
                if (count > 5) {
                    showToast("You can select up to 5 images only")
                    return
                }
                for (i in 0 until count) {
                    imageUris.add(data.clipData!!.getItemAt(i).uri)
                }
            } else if (data?.data != null) {
                data.data?.let { imageUris.add(it) }
            }
            viewModel.setImagesUri(imageUris)
        }
    }


    private fun prepareAutoCompleteTexts() {
        binding.apply {
            var chosenCategoryIndex = 0
            var chosenStateIndex = 0
            autoCompleteTextCategory.setAdapter(categoryAdapter)
            autoCompleteTextCategory.setOnItemClickListener { _,
                                                              _,
                                                              i,
                                                              _ ->

                viewModel.setCategory(categories[i])
                autoCompleteTextSubcategory.setText(getString(R.string.drop_down_menu_subcategory_text))
                subCategoryAdapter = ArrayAdapter.createFromResource(
                    requireContext(),
                    subCategories[i],
                    android.R.layout.simple_dropdown_item_1line
                )
                chosenCategoryIndex = i
                autoCompleteTextSubcategory.setAdapter(subCategoryAdapter)
            }
            binding.autoCompleteTextSubcategory.setOnItemClickListener { _,
                                                                         _,
                                                                         i,
                                                                         _ ->
                viewModel.setSubCategory(resources.getStringArray(subCategories[chosenCategoryIndex])[i])
            }
            autoCompleteTextState.setAdapter(statesAdapter)
            autoCompleteTextState.setOnItemClickListener { _,
                                                           _,
                                                           i,
                                                           _ ->

                viewModel.setState(states[i])
                autoCompleteTextCity.setText(getString(R.string.drop_down_menu_city_text))
                citiesAdapter = ArrayAdapter.createFromResource(
                    requireContext(),
                    cities[i],
                    android.R.layout.simple_dropdown_item_1line
                )
                chosenStateIndex = i
                autoCompleteTextCity.setAdapter(citiesAdapter)
            }
            autoCompleteTextCity.setOnItemClickListener { _,
                                                          _,
                                                          i,
                                                          _ ->
                viewModel.setCity(resources.getStringArray(cities[chosenStateIndex])[i])
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val PICK_IMAGES_REQUEST = 121
    }

    override fun onCancelClick(image: Uri) {
        viewModel.removeImage(image)
    }
}