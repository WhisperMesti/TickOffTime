package com.example.tickofftime.categorydatabase

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tickofftime.data.CategoryEditDestination
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

//class used while editing a category
class CategoryEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val categoriesRepository: CategoriesRepository
) : ViewModel() {

    var categoryUiState by mutableStateOf(CategoryUiState())
        private set

    private val categoryId: Int = checkNotNull(savedStateHandle[CategoryEditDestination.categoryIdArg])

    init {
        viewModelScope.launch {
            categoryUiState = categoriesRepository.getCategoriesStream(categoryId)
                .filterNotNull()
                .first()
                .toCategoryUiState(actionEnabled = true)
        }
    }

    fun updateCategoryUiState(newCategoryUiState: CategoryUiState) {
        categoryUiState = newCategoryUiState.copy( actionEnabled = newCategoryUiState.isValid())
    }

    suspend fun updateCategory() {
        if (categoryUiState.isValid()) {
            categoriesRepository.updateCategory(categoryUiState.toCategory())
        }
    }
    suspend fun deleteCategory() {
        categoriesRepository.deleteCategory(categoryUiState.toCategory())
    }
}