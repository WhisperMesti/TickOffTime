/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.tickofftime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import com.example.tickofftime.categorydatabase.CategoriesRepository
import com.example.tickofftime.categorydatabase.Category
import com.example.tickofftime.taskdatabase.Task
import com.example.tickofftime.taskdatabase.TasksRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

//initialization of the category list
class CategoryHomeViewModel(categoriesRepository: CategoriesRepository) : ViewModel() {

    val categoryHomeUiState: StateFlow<CategoryHomeUiState> =
        categoriesRepository.getAllCategoriesStream().map {CategoryHomeUiState(it)}
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = CategoryHomeUiState()
            )
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class CategoryHomeUiState(val categoryList: List<Category> = listOf())

//initialization of the task list
class TaskHomeViewModel(tasksRepository: TasksRepository) : ViewModel() {

    val taskHomeUiState: StateFlow<TaskHomeUiState> =
        tasksRepository.getAllTasksStream().map {TaskHomeUiState(it)}
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = TaskHomeUiState()
            )
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class TaskHomeUiState(val taskList: List<Task> = listOf())