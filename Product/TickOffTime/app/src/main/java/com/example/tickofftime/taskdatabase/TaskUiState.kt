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

package com.example.tickofftime.taskdatabase

import java.time.LocalDate

//UiState initialization
data class TaskUiState(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val category: String = "Uncategorised",
    val type: String = "",
    val firstDate: String = LocalDate.now().toString(),
    val secondDate: String = LocalDate.now().plusDays(9999999).toString(),
    val daysRepeated: Int = 1234567,
    val actionEnabled: Boolean = false
)

//converts UiState into a task
fun TaskUiState.toTask(): Task = Task(
    id = id,
    name = name,
    description = description,
    category = category,
    type = type,
    firstDate = firstDate,
    secondDate = secondDate,
    daysRepeated = daysRepeated
)

//converts a task into UiState
fun Task.toTaskUiState(actionEnabled: Boolean = false): TaskUiState = TaskUiState(
    id = id,
    name = name,
    description = description,
    category = category,
    type = type,
    firstDate = firstDate,
    secondDate = secondDate,
    daysRepeated = daysRepeated,
    actionEnabled = actionEnabled
)

//checking validation of UiState
fun TaskUiState.isValid() : Boolean {
    return name.isNotBlank() && type.isNotBlank() && (daysRepeated != 8888888)
}


