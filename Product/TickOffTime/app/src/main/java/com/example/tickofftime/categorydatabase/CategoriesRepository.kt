package com.example.tickofftime.categorydatabase

import kotlinx.coroutines.flow.Flow

//interface with all significant category functions
interface CategoriesRepository {

    fun getAllCategoriesStream(): Flow<List<Category>>


    fun getCategoriesStream(id: Int): Flow<Category?>


    suspend fun createCategory(task: Category)


    suspend fun deleteCategory(task: Category)


    suspend fun updateCategory(task: Category)
}
