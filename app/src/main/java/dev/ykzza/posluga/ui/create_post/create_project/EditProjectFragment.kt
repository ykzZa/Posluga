package dev.ykzza.posluga.ui.create_post.create_project

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import dev.ykzza.posluga.R
import dev.ykzza.posluga.databinding.FragmentEditProjectBinding
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
class EditProjectFragment : Fragment(), ImagesAdapter.OnItemClickListener {

    private var _binding: FragmentEditProjectBinding? = null
    private val binding: FragmentEditProjectBinding
        get() = _binding ?: throw RuntimeException("FragmentEditProjectBinding can't be null")

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var viewModel: ProjectViewModel
    private val args: EditProjectFragmentArgs by navArgs()

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
        _binding = FragmentEditProjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[ProjectViewModel::class.java]
        binding.recyclerViewImages.adapter = recyclerViewAdapter
        setOnClickListeners()
        observeViewModel()
        viewModel.getProject(
            args.projectId
        )
    }

    private fun observeViewModel() {
        viewModel.project.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiState.Loading -> {
                    hideUi()
                    binding.apply {
                        progressBar.showView()
                    }
                }

                is UiState.Success -> {
                    val project = uiState.data
                    binding.apply {
                        progressBar.hideView()
                        editTextProjectTitle.setText(project.title)
                        editTextProjectDescription.setText(project.description)
                        recyclerViewAdapter.submitList(project.images.map {
                            it.toUri()
                        })
                        viewModel.setCategory(project.category)
                        val categoryIndex = categories.indexOf(project.category)
                        val stateIndex = states.indexOf(project.state)
                        prepareAutoCompleteTexts(categoryIndex, stateIndex)
                        autoCompleteTextCategory.setText(
                            project.category, false
                        )
                        viewModel.setSubCategory(project.subCategory)
                        autoCompleteTextSubcategory.setText(
                            project.subCategory, false
                        )
                        viewModel.setState(project.state)
                        autoCompleteTextState.setText(
                            project.state, false
                        )
                        viewModel.setCity(project.city)
                        autoCompleteTextCity.setText(
                            project.city, false
                        )
                        viewModel.setProjectImages(project.images)
                        if (project.price != 0) {
                            editTextProjectPrice.setText(
                                project.price.toString()
                            )
                        }
                    }
                    showUi()
                }

                else -> {
                    showToast("Oops, something went wrong")
                    findNavController().popBackStack()
                }
            }
        }
        viewModel.imagesUriList.observe(viewLifecycleOwner) {
            recyclerViewAdapter.submitList(it)
        }
        viewModel.projectImages.observe(viewLifecycleOwner) {
            if (it != null) {
                recyclerViewAdapter.submitList(it.map { str ->
                    str.toUri()
                })
            }
        }
        viewModel.imagesUploaded.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiState.Success -> {
                    binding.apply {
                        val listImages: List<String> = if(viewModel.projectImages.value != null &&
                            viewModel.projectImages.value?.isEmpty() == false
                        ) {
                            viewModel.projectImages.value!!
                        } else {
                            uiState.data
                        }
                        viewModel.postProject(
                            args.projectId,
                            editTextProjectTitle.text.toString(),
                            editTextProjectDescription.text.toString(),
                            firebaseAuth.uid!!,
                            LocalDateTime.now()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                            editTextProjectPrice.text.toString(),
                            listImages
                        )
                    }
                }

                else -> {
                    showToast("Failed to delete images, try again")
                    binding.apply {
                        progressBar.hideView()
                        buttonEditProject.setText(R.string.button_edit_project_text)
                    }
                }
            }
        }
        viewModel.projectPosted.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiState.Error -> {
                    binding.apply {
                        progressBar.hideView()
                        buttonEditProject.setText(R.string.button_edit_project_text)
                    }
                    showToast(uiState.errorMessage)
                }

                is UiState.Loading -> {
                    binding.apply {
                        buttonEditProject.text = ""
                        progressBar.showView()
                    }
                }

                is UiState.Success -> {
                    binding.apply {
                        progressBar.hideView()
                        buttonEditProject.setText(R.string.button_edit_project_text)
                    }
                    findNavController().popBackStack()
                    showToast("Project has been updated")
                }
            }
        }
    }

    private fun setOnClickListeners() {
        binding.apply {
            buttonBack.setOnClickListener {
                findNavController().popBackStack()
            }
            textViewImages.setOnClickListener {
                pickImagesFromGallery()
            }
            buttonEditProject.setOnClickListener {
                viewModel.uploadImages(
                    firebaseAuth.uid ?: throw RuntimeException("User is not logged")
                )
            }
        }
    }

    private fun hideUi() {
        binding.apply {
            textViewTitle.hideView()
            textViewSubtitle.hideView()
            scrollViewContainer.hideView()
            buttonEditProject.hideView()
        }
    }

    private fun showUi() {
        binding.apply {
            textViewTitle.showView()
            textViewSubtitle.showView()
            scrollViewContainer.showView()
            buttonEditProject.showView()
        }
    }

    private fun pickImagesFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(
            Intent.createChooser(intent, "Select Pictures"),
            IMAGE_PICKER_REQUEST
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
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


    private fun prepareAutoCompleteTexts(chCategory: Int = 0, chState: Int = 0) {
        binding.apply {
            var chosenCategoryIndex = chCategory
            var chosenStateIndex = chState
            if(chCategory != 0) {
                subCategoryAdapter = ArrayAdapter.createFromResource(
                    requireContext(),
                    subCategories[chosenCategoryIndex],
                    android.R.layout.simple_dropdown_item_1line
                )
                autoCompleteTextSubcategory.setAdapter(subCategoryAdapter)
            }
            if(chState != 0) {
                citiesAdapter = ArrayAdapter.createFromResource(
                    requireContext(),
                    cities[chosenStateIndex],
                    android.R.layout.simple_dropdown_item_1line
                )
                autoCompleteTextCity.setAdapter(citiesAdapter)
            }
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

    override fun onCancelClick(image: Uri) {
        viewModel.removeImage(image)
    }

    companion object {
        const val IMAGE_PICKER_REQUEST = 100
    }
}