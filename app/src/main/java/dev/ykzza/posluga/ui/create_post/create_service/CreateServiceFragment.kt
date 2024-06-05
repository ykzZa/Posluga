package dev.ykzza.posluga.ui.create_post.create_service

import android.app.Activity.RESULT_OK
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
import dev.ykzza.posluga.databinding.FragmentCreateServiceBinding
import dev.ykzza.posluga.ui.create_post.ImagesAdapter
import dev.ykzza.posluga.util.UiState
import dev.ykzza.posluga.util.hideView
import dev.ykzza.posluga.util.showToast
import dev.ykzza.posluga.util.showView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class CreateServiceFragment : Fragment(), ImagesAdapter.OnItemClickListener {

    private var _binding: FragmentCreateServiceBinding? = null
    private val binding: FragmentCreateServiceBinding
        get() = _binding ?: throw RuntimeException("FragmentCreateServiceBinding is null")

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var viewModel: ServiceViewModel

    private val recyclerViewAdapter by lazy {
        ImagesAdapter(this)
    }

    private val categories by lazy {
        resources.getStringArray(R.array.categories)
    }
    private val subCategories = listOf(
        R.array.tutoring_subcategory,
        R.array.building_works_subcategory,
        R.array.equipment_repair_subcategory,
        R.array.handyman_subcategory,
        R.array.cleaning_subcategory,
        R.array.other_subcategory
    )
    private val states by lazy {
        resources.getStringArray(R.array.states)
    }
    private val cities = listOf(
        R.array.avtonomna_respublika_krym_cities,
        R.array.vinnytska_cities,
        R.array.volynska_cities,
        R.array.dnipropetrovska_cities,
        R.array.donetska_cities,
        R.array.zhytomyrska_cities,
        R.array.zakarpatska_cities,
        R.array.zaporizka_cities,
        R.array.ivano_frankivska_cities,
        R.array.kyivska_cities,
        R.array.kirovohradska_cities,
        R.array.luhanska_cities,
        R.array.lvivska_cities,
        R.array.mykolaivska_cities,
        R.array.odeska_cities,
        R.array.poltavska_cities,
        R.array.rivnenska_cities,
        R.array.sumska_cities,
        R.array.ternopilska_cities,
        R.array.kharkivska_cities,
        R.array.khersonska_cities,
        R.array.khmelnytska_cities,
        R.array.cherkaska_cities,
        R.array.chernivetska_cities,
        R.array.chernihivska_cities
    )

    private val categoryAdapter by lazy {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.categories,
            android.R.layout.simple_dropdown_item_1line
        )
    }
    private lateinit var subCategoryAdapter: ArrayAdapter<CharSequence>

    private val statesAdapter by lazy {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.states,
            android.R.layout.simple_dropdown_item_1line
        )
    }
    private lateinit var citiesAdapter: ArrayAdapter<CharSequence>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateServiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[ServiceViewModel::class.java]
        binding.recyclerViewImages.adapter = recyclerViewAdapter
        setOnClickListeners()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.imagesUriList.observe(viewLifecycleOwner) {
            recyclerViewAdapter.submitList(it)
        }
        viewModel.imagesUploaded.observe(viewLifecycleOwner) { uiState ->
            when(uiState) {
                is UiState.Success -> {
                    binding.apply {
                        viewModel.postService(
                            editTextServiceTitle.text.toString(),
                            editTextServiceDescription.text.toString(),
                            firebaseAuth.uid!!,
                            LocalDateTime.now().
                            format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                            editTextServicePrice.text.toString(),
                            uiState.data
                        )
                    }
                }
                else -> {
                    showToast("Failed to upload images, try again")
                    binding.apply {
                        progressBar.hideView()
                        buttonCreateService.setText(R.string.button_create_service_text)
                    }
                }
            }
        }
        viewModel.servicePosted.observe(viewLifecycleOwner) { uiState ->
            when(uiState) {
                is UiState.Error -> {
                    binding.apply {
                        buttonCreateService.setText(R.string.button_create_service_text)
                    }
                    showToast(uiState.errorMessage)
                }
                is UiState.Loading -> {
                    binding.apply {
                        buttonCreateService.text = ""
                        progressBar.showView()
                    }
                }
                is UiState.Success -> {
                    binding.apply {
                        progressBar.hideView()
                        buttonCreateService.setText(R.string.button_create_service_text)
                    }
                    findNavController().popBackStack()
                    showToast("Service has been posted")
                }
            }
        }
    }

    private fun setOnClickListeners() {
        binding.apply {
            buttonBack.setOnClickListener {
                findNavController().popBackStack()
            }
            buttonCreateService.setOnClickListener {
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
        if (requestCode == PICK_IMAGES_REQUEST && resultCode == RESULT_OK) {
            val imageUris = mutableListOf<Uri>()
            if (data?.clipData != null) {
                val count = data.clipData!!.itemCount
                if(count > 5) {
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
            autoCompleteTextCategory.setOnItemClickListener {
                    _,
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
            binding.autoCompleteTextSubcategory.setOnItemClickListener {
                    _,
                    _,
                    i,
                    _ ->
                viewModel.setSubCategory(resources.getStringArray(subCategories[chosenCategoryIndex])[i])
            }
            autoCompleteTextState.setAdapter(statesAdapter)
            autoCompleteTextState.setOnItemClickListener {
                    _,
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
            autoCompleteTextCity.setOnItemClickListener {
                    _,
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