package com.juanantbuit.weatherproject.framework.ui.searchList

import com.juanantbuit.weatherproject.domain.models.ItemModel

sealed class SearchListUIState {
    data object Loading : SearchListUIState()
    data object NotFound : SearchListUIState()
    data class Success(val searchValues: ItemModel) : SearchListUIState()
    data class Error(val msg: String) : SearchListUIState()
}