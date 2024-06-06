package dev.ykzza.posluga.ui.home.search_dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import dev.ykzza.posluga.R
import dev.ykzza.posluga.databinding.BottomSheetBinding
import dev.ykzza.posluga.util.showToast
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class SearchSettingsDialogFragment(
    private val listener: SearchDialogListener
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetBinding? = null
    private val binding: BottomSheetBinding
        get() = _binding ?: throw RuntimeException("BottomSheetBinding can't be null")

    private lateinit var viewModel: SearchDialogViewModel

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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetBinding.bind(inflater.inflate(R.layout.bottom_sheet, container))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[SearchDialogViewModel::class.java]
        setOnClickListeners()
        prepareAutoCompleteTexts()
    }

    private fun setOnClickListeners() {
        binding.apply {
            buttonSearch.setOnClickListener {
                if (validatePrices(
                        editTextMinPrice.text.toString().toIntOrNull(),
                        editTextMaxPrice.text.toString().toIntOrNull(),
                    )
                ) {
                    val searchQuery = if (searchView.query.toString().isBlank()) {
                        null
                    } else {
                        searchView.query.toString()
                    }
                    listener.onSearchClick(
                        searchQuery,
                        checkboxAdvancedSearch.isActivated,
                        viewModel.category.value,
                        viewModel.subCategory.value,
                        viewModel.state.value,
                        viewModel.city.value,
                        editTextMinPrice.text.toString().toIntOrNull(),
                        editTextMaxPrice.text.toString().toIntOrNull()
                    )
                    dismiss()
                } else {
                    showToast("Max price must be higher the min price")
                }
            }
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
                viewModel.subCategory.value =
                    resources.getStringArray(subCategories[chosenCategoryIndex])[i]
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
                viewModel.city.value = resources.getStringArray(cities[chosenStateIndex])[i]
            }
        }
    }

    private fun validatePrices(
        minPrice: Int?,
        maxPrice: Int?
    ): Boolean {
        if (minPrice == null || maxPrice == null) {
            return true
        }
        return (maxPrice >= minPrice)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface SearchDialogListener {

        fun onSearchClick(
            searchRequest: String?,
            descriptionSearch: Boolean,
            category: String?,
            subCategory: String?,
            state: String?,
            city: String?,
            minPrice: Int?,
            maxPrice: Int?
        )
    }
}