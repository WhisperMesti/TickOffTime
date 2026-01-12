package com.example.tickofftime.data

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tickofftime.AppViewModelProvider
import com.example.tickofftime.Mains
import com.example.tickofftime.taskdatabase.TaskEditViewModel

//the main body of the Task Details menu that presents a task's non-changeable details
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun TaskDetails(
    navController: NavController,
    viewModel: TaskEditViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    Scaffold(
        bottomBar = {
            TaskGoBackBar(
                GoBack = { navController.popBackStack() },
                SaveIt = {},
                enabled = true,
                isDetailed = true
            ) },
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            EnteringTaskName(
                taskUiState = viewModel.taskUiState,
                onValueChange = viewModel::updateTaskUiState,
                enabled = false
            )
            EnteringTaskDescription(
                taskUiState = viewModel.taskUiState,
                onValueChange = viewModel::updateTaskUiState,
                enabled = false
            )
        }
    }
}