package com.example.tickofftime

import android.content.Context
import com.example.tickofftime.categorydatabase.CategoriesRepository
import com.example.tickofftime.categorydatabase.CategoryDatabase
import com.example.tickofftime.taskdatabase.*

interface AppContainer {
    val tasksRepository: TasksRepository
    val categoriesRepository: CategoriesRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val tasksRepository: TasksRepository by lazy {
        OfflineTasksRepository(TaskDatabase.getDatabase(context).taskDao())
     }
    override val categoriesRepository: CategoriesRepository by lazy {
        OfflineCategoriesRepository(CategoryDatabase.getDatabase(context).categoryDao())
    }
}