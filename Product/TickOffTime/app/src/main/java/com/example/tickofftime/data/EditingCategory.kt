package com.example.tickofftime.data

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tickofftime.*
import com.example.tickofftime.R
import com.example.tickofftime.categorydatabase.*
import com.example.tickofftime.taskdatabase.TaskEditViewModel
import kotlinx.coroutines.launch

//object with navigation parameters
object CategoryEditDestination : NavigationDestination {
    override val route = "category_edit"
    override val titleRes = R.string.editing_category
    const val categoryIdArg = "categoryId"
    val routeWithArgs = "$route/{$categoryIdArg}"
}

//navigate destination initialization
interface NavigationDestination {
    val route: String
    val titleRes: Int
}

//the main body of the Editing Category menu
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun EditingCategoryMenu(
    navController: NavController,
    viewModel: CategoryEditViewModel = viewModel(factory = AppViewModelProvider.Factory),
    entryViewModel: CategoryEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    homeViewModel: CategoryHomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    taskViewModelEdit: TaskEditViewModel = viewModel(factory = AppViewModelProvider.Factory),
    taskViewModel: TaskHomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    //all important variables and values
    var expanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }
    var deleteConfirmationRequiredSecond by rememberSaveable { mutableStateOf(false) }
    val taskHomeUiState by taskViewModel.taskHomeUiState.collectAsState()
    val categoryHomeUiState by homeViewModel.categoryHomeUiState.collectAsState()
    val tasksOfCategory = taskHomeUiState.taskList.filter {it.category == viewModel.categoryUiState.name}
    val fundament = CategoryUiState(name = "Uncategorised")
    val yesTasks = taskHomeUiState.taskList.any {it.category == viewModel.categoryUiState.name}

    Scaffold(
        bottomBar ={ CategoryGoBackBar( //the bar at the bottom of the screen
            GoBack = { navController.navigate(Mains.ListOfTasks.name) },
            SaveIt = {
                coroutineScope.launch {
                    navController.navigate(Mains.ListOfTasks.name)
                    viewModel.updateCategory()
                }
            },
            itemUiState = viewModel.categoryUiState
        ) },
    ) {
        Column(modifier = Modifier.fillMaxWidth().animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField( //name text field
                value = viewModel.categoryUiState.name,
                onValueChange = {
                    coroutineScope.launch {
                        for (task in tasksOfCategory) {
                            taskViewModelEdit.updateSpecificTask(task.copy(category = viewModel.categoryUiState.name))
                        }
                    }
                    viewModel.updateCategoryUiState(viewModel.categoryUiState.copy(name = it))
                },
                label = { Text(stringResource(R.string.enter_name_category))},
                modifier = Modifier.fillMaxWidth(),
                enabled = true,
                singleLine = true
            )
            ChoosingCategoryColour { expanded = !expanded } //expanding the category colours
            if (expanded) {ShowingListOfColours(viewModel.categoryUiState,viewModel::updateCategoryUiState)}
            Button(
                onClick = { deleteConfirmationRequired = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.delete))
            }
            if (deleteConfirmationRequired) {
                DeleteConfirmationDialog( //warning notification
                    onDeleteConfirm = {
                         if (!yesTasks) { //checking if a category is empty
                             coroutineScope.launch {
                                 viewModel.deleteCategory()
                                 navController.navigate(Mains.ListOfTasks.name)
                                 deleteConfirmationRequiredSecond = false
                                 deleteConfirmationRequired = false
                             }
                         }
                        else {deleteConfirmationRequiredSecond = true}
                    },
                    onDeleteCancel = { deleteConfirmationRequired = false
                        deleteConfirmationRequiredSecond = false },
                    text = { Text(stringResource(R.string.delete_question)) }
                )
                if (deleteConfirmationRequiredSecond) {
                    DeleteConfirmationDialog( //second warning notification
                        onDeleteConfirm = {
                            deleteConfirmationRequiredSecond = false
                            deleteConfirmationRequired = false
                            coroutineScope.launch {
                                viewModel.deleteCategory()
                                navController.navigate(Mains.ListOfTasks.name)
                                for (task in tasksOfCategory) {
                                    taskViewModelEdit.deleteSpecificTask(task)
                                }
                            }
                        },
                        onDeleteCancel = {
                            deleteConfirmationRequiredSecond = false
                            deleteConfirmationRequired = false
                            coroutineScope.launch {
                                viewModel.deleteCategory()
                                //initialization of Uncategorised category if it does not exist
                                if (!categoryHomeUiState.categoryList.any {it.name == "Uncategorised"} or (viewModel.categoryUiState.name == "Uncategorised")) {entryViewModel.saveSpecificCategory(fundament)}
                                navController.navigate(Mains.ListOfTasks.name)
                                //updating all tasks assigned to a category
                                for (task in tasksOfCategory) {
                                    taskViewModelEdit.updateSpecificTask(task.copy(category = "Uncategorised"))
                                }
                            }
                        },
                        text = { Text(stringResource(R.string.delete_tasks_and_category)) }
                    )
                }
            }
        }
    }
}
//the operation and design of the second warning notification
@Composable
fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier,
    text: @Composable() (() -> Unit)?
) {
    AlertDialog(
        onDismissRequest = { /* Do nothing */ },
        title = { Text(stringResource(R.string.attention)) },
        text = text,
        modifier = modifier.padding(16.dp),
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(text = stringResource(R.string.no))
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(text = stringResource(R.string.yes))
            }
        }
    )
}
