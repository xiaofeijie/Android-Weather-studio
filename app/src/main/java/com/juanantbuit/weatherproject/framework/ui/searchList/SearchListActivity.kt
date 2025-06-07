package com.juanantbuit.weatherproject.framework.ui.searchList

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.search.SearchView
import com.juanantbuit.weatherproject.WeatherApplication
import com.juanantbuit.weatherproject.databinding.SearchListBinding
import com.juanantbuit.weatherproject.domain.models.SearchItemModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class SearchListActivity : AppCompatActivity() {
    private val viewModel by viewModels<SearchListViewModel>()
    private lateinit var binding: SearchListBinding
    private lateinit var searchType: String
    private val application: WeatherApplication = WeatherApplication()
    @Inject lateinit var dispatcher: CoroutineDispatcher

    @FlowPreview
    private val searchQueryFlow = MutableStateFlow("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SearchListBinding.inflate(this.layoutInflater)
        setContentView(binding.root)

        viewModel.initSearchSettings()

        val layoutManager = LinearLayoutManager(this)

        binding.citySearcherList.post {
            binding.citySearcherList.show()
        }

        searchType = intent.getStringExtra("searchType")!!

        binding.citySearcherList.addTransitionListener { _, _, newState ->
            if (newState == SearchView.TransitionState.HIDDEN) {
                finish()

                if (Build.VERSION.SDK_INT >= 34) {
                    overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0)
                } else {
                    overridePendingTransition(0, 0)
                }
            }
        }

        binding.citySearcherList.editText.addTextChangedListener(object : TextWatcher {
            @FlowPreview
            override fun afterTextChanged(editableQuery: Editable?) {
                val query = editableQuery.toString()

                if (query.isNotEmpty() || query.isNotBlank()) {
                    viewModel.setLoading()
                    searchQueryFlow.value = query
                } else {
                    viewModel.setNotFound()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                return
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                return
            }
        })

        lifecycleScope.launch @FlowPreview {
            searchQueryFlow.debounce(1100) // Time to wait for the user to stop typing
                .distinctUntilChanged() // Emit only if the value changes
                .collect { query ->
                    if (query.isNotBlank()) {
                        viewModel.getSearchItemList(query)
                    }
                }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is SearchListUIState.Error -> {
                            hideProgressBar()
                            application.showToast("Ha ocurrido un error: ${uiState.msg}")
                        }

                        SearchListUIState.Loading -> {
                            binding.recyclerSearch.visibility = View.INVISIBLE
                            showProgressBar()
                        }

                        SearchListUIState.NotFound -> {
                            binding.recyclerSearch.visibility = View.INVISIBLE
                            hideProgressBar()
                        }

                        is SearchListUIState.Success -> {
                            dispatcher = Dispatchers.Main
                            withContext(dispatcher) {
                                binding.recyclerSearch.adapter = SearchItemsAdapter(
                                    uiState.searchValues.searchItemModelsList,
                                    uiState.searchValues.cityInfoModelsList
                                ) { searchItem ->
                                    onItemSelected(searchItem)
                                }
                            }
                            binding.recyclerSearch.layoutManager = layoutManager

                            hideProgressBar()

                            binding.recyclerSearch.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun onItemSelected(searchItem: SearchItemModel) {
        when (searchType) {
            "firstSave" -> {
                viewModel.saveGeonameId("firstSaveGeoId", searchItem.geonameid)
                viewModel.saveFirstSaveName(searchItem.name)
            }

            "secondSave" -> {
                viewModel.saveGeonameId("secondSaveGeoId", searchItem.geonameid)
                viewModel.saveSecondSaveName(searchItem.name)
            }

            "thirdSave" -> {
                viewModel.saveGeonameId("thirdSaveGeoId", searchItem.geonameid)
                viewModel.saveThirdSaveName(searchItem.name)
            }

            else -> {
                viewModel.saveLastGeonameId(searchItem.geonameid)
                viewModel.saveIsFirstAppStar(false)
            }
        }
        viewModel.saveLastSavedIsCoordinated(false)
        binding.citySearcherList.hide()
    }

    private fun showProgressBar() {
        binding.progressBar2.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar2.visibility = View.INVISIBLE
    }
}
