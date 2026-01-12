package com.example.tickofftime.data

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tickofftime.AppViewModelProvider
import com.example.tickofftime.CategoryHomeViewModel
import com.example.tickofftime.Mains
import com.example.tickofftime.R
import com.example.tickofftime.taskdatabase.TaskEditViewModel
import kotlinx.coroutines.launch

//object with navigation parameters
object TaskEditDestination : NavigationDestination {
    override val route = "task_edit"
    override val titleRes = R.string.editing_task
    const val taskIdArg = "categoryId"
    val routeWithArgs = "$route/{$taskIdArg}"
}

//the main body of the Editing Task menu
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun EditingTaskMenu(
    navController: NavController,
    viewModel: TaskEditViewModel = viewModel(factory = AppViewModelProvider.Factory),
    categoryViewModel: CategoryHomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    //all variables responsibe for displaying options
    var expandedCategory by remember { mutableStateOf(false) }
    var expandedTypes by remember { mutableStateOf(false) }
    var expandedAdditional by remember { mutableStateOf(false) }
    var expandedDatePicker by remember { mutableStateOf(false) }
    var expandedWeek by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val categoryHomeUiState by categoryViewModel.categoryHomeUiState.collectAsState()
    var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }

    if (!expandedDatePicker) {
        Scaffold(
            bottomBar = {
                TaskGoBackBar( //the bar at the bottom of the screen
                    GoBack = { navController.popBackStack() },
                    SaveIt = {
                        coroutineScope.launch {
                            viewModel.updateTask()
                            navController.popBackStack()
                        }
                    },
                    enabled = viewModel.taskUiState.actionEnabled,
                )
            },
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                EnteringTaskName( //text field
                    taskUiState = viewModel.taskUiState,
                    onValueChange = viewModel::updateTaskUiState
                )
                ChoosingExpandingOption( //expanding categories option
                    { expandedCategory = !expandedCategory },
                    stringResource(id = R.string.choose_category)
                )
                if (expandedCategory) { //expanding categories
                    ShowingListOfCategoriesOption(
                        categoryHomeUiState.categoryList,
                        viewModel.taskUiState,
                        viewModel::updateTaskUiState
                    )
                }
                ChoosingExpandingOption( //expanding types option
                    { expandedTypes = !expandedTypes },
                    stringResource(id = R.string.choose_type)
                )
                if (expandedTypes) {
                    ShowingListOfTypesOption( //expanding types
                        taskUiState = viewModel.taskUiState,
                        onValueChange = viewModel::updateTaskUiState,
                        onClick = { expandedAdditional = !expandedAdditional },
                        onClickSingle = {
                            expandedDatePicker = !expandedDatePicker
                            expandedAdditional = false
                        }
                    )
                }
                if (expandedAdditional and expandedTypes) {
                    RepeatedAdditionalOptions( //expanding additonal options for repeat and until tasks
                        onClickFirst = {expandedWeek= !expandedWeek},
                        onClickSecond = { expandedDatePicker = !expandedDatePicker },
                    )
                }
                if (expandedWeek and expandedTypes and expandedAdditional){ // displaying a row with short weekdays' names
                    ChoosingDays(taskUiState = viewModel.taskUiState, onValueChange = viewModel::updateTaskUiState)
                }
                EnteringTaskDescription( // task description field
                    taskUiState = viewModel.taskUiState,
                    onValueChange = viewModel::updateTaskUiState
                )
                Button( //delete button
                    onClick = { deleteConfirmationRequired = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.delete))
                }
                if (deleteConfirmationRequired) { //warning notification
                    DeleteConfirmationDialog(
                        onDeleteConfirm = {
                            deleteConfirmationRequired = false
                            coroutineScope.launch {
                                viewModel.deleteTask()
                                navController.navigate(Mains.ListOfTasks.name)
                            }
                        },
                        onDeleteCancel = { deleteConfirmationRequired = false },
                        text = { Text(stringResource(R.string.delete_question)) }
                    )
                }
            }
        }
    }
    else {
        PickUpADate( //displaying a date picker/calendar
            taskUiState = viewModel.taskUiState,
            onValueChange = viewModel::updateTaskUiState,
            goBack = {expandedDatePicker = !expandedDatePicker},
            typeTask = viewModel.taskUiState.type
        )
    }
}