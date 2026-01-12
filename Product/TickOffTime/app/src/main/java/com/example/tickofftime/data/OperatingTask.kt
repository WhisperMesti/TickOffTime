package com.example.tickofftime.data

import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tickofftime.AppViewModelProvider
import com.example.tickofftime.R
import com.example.tickofftime.taskdatabase.TaskEditViewModel
import com.example.tickofftime.taskdatabase.TypesTasks
import com.example.tickofftime.taskdatabase.toTask
import com.example.tickofftime.taskdatabase.weekDaysNames
import kotlinx.coroutines.launch
import java.time.LocalDate

//object with navigation parameters
object TaskDeleteDestination : NavigationDestination {
    override val route = "task_operate"
    override val titleRes = R.string.editing_task
    const val taskIdArg = "categoryId"
    val routeWithArgs = "$route/{$taskIdArg}"
}

//function with no Ui that allows to delete a task by clicking "Done"
@Composable
fun  DeletingTask(
    navController: NavController,
    viewModel: TaskEditViewModel = viewModel(factory = AppViewModelProvider.Factory),
){
    val coroutineScope = rememberCoroutineScope()
    var deleteConfirmationRequired by rememberSaveable { mutableStateOf(true) }

    if (deleteConfirmationRequired) {
        DeleteConfirmationDialog(
            onDeleteConfirm = {
                deleteConfirmationRequired = false
                coroutineScope.launch {
                    if (viewModel.taskUiState.type != TypesTasks.repeatTask) viewModel.deleteTask()
                    else if (LocalDate.now().isBefore(LocalDate.parse(viewModel.taskUiState.secondDate))) {
                        var nextPossibleDay = LocalDate.now().plusDays(1)
                        while ((nextPossibleDay !in LocalDate.parse(viewModel.taskUiState.firstDate)..LocalDate.parse(viewModel.taskUiState.secondDate)) or ((weekDaysNames.indexOf(nextPossibleDay.dayOfWeek) +1).toString() !in viewModel.taskUiState.daysRepeated.toString())) {
                            nextPossibleDay = nextPossibleDay.plusDays(1)
                        }
                        viewModel.updateSpecificTask(viewModel.taskUiState.copy(firstDate = nextPossibleDay.toString()).toTask())
                    }
                    else viewModel.deleteTask()
                    navController.popBackStack()
                }
                              },
            onDeleteCancel = {
                deleteConfirmationRequired = false
                navController.popBackStack()
                             },
            text = { Text(stringResource(R.string.delete_question)) }
        )
    }
}