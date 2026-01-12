package com.example.tickofftime.taskdatabase

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tickofftime.data.TaskEditDestination
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

//class used while editing a task
class TaskEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val tasksRepository: TasksRepository
) : ViewModel() {

    var taskUiState by mutableStateOf(TaskUiState())
        private set

    private val taskId: Int = checkNotNull(savedStateHandle[TaskEditDestination.taskIdArg])

    init {
        viewModelScope.launch {
            taskUiState = tasksRepository.getTasksStream(taskId)
                .filterNotNull()
                .first()
                .toTaskUiState(actionEnabled = true)
        }
    }

    fun updateTaskUiState(newTaskUiState: TaskUiState) {
        taskUiState = newTaskUiState.copy( actionEnabled = newTaskUiState.isValid())
    }

    suspend fun updateTask() {
        if (taskUiState.isValid()) {
            tasksRepository.updateTask(taskUiState.toTask())
        }
    }
    suspend fun updateSpecificTask(task:Task) {
        tasksRepository.updateTask(task)
    }
    suspend fun deleteTask() {
        tasksRepository.deleteTask(taskUiState.toTask())
    }
    suspend fun deleteSpecificTask(task:Task) {
        tasksRepository.deleteTask(task)
    }
}