package com.example.tickofftime.categorydatabase

//UiState initialization
data class CategoryUiState(
    val id: Int = 0,
    val name: String = "",
    val colour: Int = 0,
    val actionEnabled: Boolean = false
)

//converts UiState into a category
fun CategoryUiState.toCategory(): Category = Category(
    id = id,
    name = name,
    colour = colour
)

//converts a category into UiState
fun Category.toCategoryUiState(actionEnabled: Boolean = false): CategoryUiState = CategoryUiState(
    id = id,
    name = name,
    colour = colour,
    actionEnabled = actionEnabled
)

//checking validation of UiState
fun CategoryUiState.isValid() : Boolean {
    return name.isNotBlank() && colour.toString().isNotBlank()
}


