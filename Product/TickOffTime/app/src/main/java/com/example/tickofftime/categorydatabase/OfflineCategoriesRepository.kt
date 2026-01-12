package com.example.tickofftime.taskdatabase

import com.example.tickofftime.categorydatabase.CategoriesRepository
import com.example.tickofftime.categorydatabase.Category
import com.example.tickofftime.categorydatabase.CategoryDao
import kotlinx.coroutines.flow.Flow

class OfflineCategoriesRepository(private val categoryDao: CategoryDao) : CategoriesRepository {

    //takes all elements from the list
    override fun getAllCategoriesStream(): Flow<List<Category>> = categoryDao.getAllCategories()

    //takes one element from the list
    override fun getCategoriesStream(id: Int): Flow<Category?> = categoryDao.getCategory(id)

    //creates a category
    override suspend fun createCategory(category: Category) = categoryDao.create(category)

    //deletes a category
    override suspend fun deleteCategory(category: Category) = categoryDao.delete(category)

    //updates a category
    override suspend fun updateCategory(category: Category) = categoryDao.update(category)
}