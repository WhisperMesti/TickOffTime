package com.example.tickofftime.data

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tickofftime.AppViewModelProvider
import com.example.tickofftime.CategoryHomeViewModel
import com.example.tickofftime.Mains
import com.example.tickofftime.R
import com.example.tickofftime.categorydatabase.Category
import com.example.tickofftime.categorydatabase.CategoryEntryViewModel
import com.example.tickofftime.categorydatabase.CategoryUiState
import com.example.tickofftime.taskdatabase.*
import kotlinx.coroutines.launch
import java.util.*

//the main body of the Creating Task menu
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun CreatingTaskMenu(
    navController: NavController,
    viewModel:  TaskEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    entryViewModel: CategoryEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    categoryViewModel: CategoryHomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    //all variables responsibe for displaying options
    var expandedCategory by remember { mutableStateOf(false) }
    var expandedTypes by remember { mutableStateOf(false) }
    var expandedAdditional by remember { mutableStateOf(false) }
    var expandedDatePicker by remember { mutableStateOf(false) }
    var expandedWeek by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val categoryHomeUiState by categoryViewModel.categoryHomeUiState.collectAsState()
    val fundament = CategoryUiState(name = "Uncategorised")

    if (!expandedDatePicker) {
        Scaffold(
            bottomBar = {
                TaskGoBackBar( //the bar at the bottom of the screen
                    GoBack = { navController.navigate(Mains.ListOfTasks.name) },
                    SaveIt = {
                        coroutineScope.launch {
                            if (!categoryHomeUiState.categoryList.any { it.name == "Uncategorised" }) {
                                entryViewModel.saveSpecificCategory(fundament)
                            }
                            viewModel.saveTask()
                            navController.navigate(Mains.ListOfTasks.name)
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
                        onClick = { expandedAdditional = true },
                        onClickSingle = {
                            expandedDatePicker = !expandedDatePicker
                            expandedAdditional = false
                        }
                    )
                }
                if (expandedAdditional and expandedTypes) {
                    RepeatedAdditionalOptions( //expanding additonal options for repeat and until tasks
                        onClickFirst = { expandedWeek= !expandedWeek },
                        onClickSecond = { expandedDatePicker = !expandedDatePicker }
                    )
                }
                if (expandedWeek and expandedTypes and expandedAdditional){ // displaying a row with short weekdays' names
                    ChoosingDays(taskUiState = viewModel.taskUiState, onValueChange = viewModel::updateTaskUiState)
                }
                EnteringTaskDescription( // task description field
                    taskUiState = viewModel.taskUiState,
                    onValueChange = viewModel::updateTaskUiState
                )
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

//the operation and design of the name text field
@Composable
fun EnteringTaskName(
    taskUiState: TaskUiState,
    onValueChange: (TaskUiState) -> Unit ={},
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = taskUiState.name,
        onValueChange = { onValueChange(taskUiState.copy(name = it)) },
        label = { Text(stringResource(R.string.enter_name_task))},
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        singleLine = true
    )
}

//the operation and design of description text field
@Composable
fun EnteringTaskDescription(
    taskUiState: TaskUiState,
    onValueChange: (TaskUiState) -> Unit ={},
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = taskUiState.description,
        onValueChange = { onValueChange(taskUiState.copy(description = it)) },
        label = { Text(stringResource(R.string.enter_description_task))},
        modifier = Modifier
            .fillMaxWidth()
            .width(20.dp),
        enabled = enabled,
        singleLine = false
    )
}

//the operation and design of the expanding option
@Composable
fun ChoosingExpandingOption(
    onClick: () -> Unit,
    title: String
) {
    Row(
        Modifier
            .fillMaxWidth()
            .size(50.dp)
            .background(color = MaterialTheme.colors.surface),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onClick ) {
            Image(
                imageVector = Icons.Filled.Add,
                contentDescription = title,
                modifier = Modifier
                    .size(50.dp)
                    .background(color = MaterialTheme.colors.primary, RoundedCornerShape(7.dp))
                    .fillMaxHeight()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colors.onSurface,
                    ),

                )
        }
        Text(
            text = title,
            color = MaterialTheme.colors.onPrimary,
            style = MaterialTheme.typography.body1,
            fontSize = 22.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(5.dp)
        )
    }
}

//the operation and design of the list of categories
@Composable
fun ShowingListOfCategoriesOption(
    itemsList: List<Category>,
    taskUiState: TaskUiState,
    onValueChange: (TaskUiState) -> Unit
) {
    //information when there are no categories
    if (itemsList.isEmpty() or ((itemsList.size == 1) and (itemsList.any {it.name == "Uncategorised"}))) {
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.no_category),
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center
            )
        }
    }
    else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items = itemsList, key = {it.id}) { category ->
                if (category.name != "Uncategorised") CategoryBarDesign(presentCategory = category, taskUiState = taskUiState, onValueChange = onValueChange)
            }
        }
    }
}

//the operation and design of the single category item view
@Composable
fun CategoryBarDesign(
    presentCategory: Category,
    taskUiState: TaskUiState,
    onValueChange: (TaskUiState) -> Unit,
) {
    val expectedCategory = taskUiState.category

    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 50.dp)
            .background(color = MaterialTheme.colors.surface),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {onValueChange(taskUiState.copy(category = presentCategory.name))} ) {
            Image(
                imageVector = Icons.Filled.Add,
                contentDescription = "category_choosing",
                modifier = Modifier
                    .size(50.dp)
                    .background(color = Color(presentCategory.colour), RoundedCornerShape(7.dp))
                    .fillMaxHeight()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colors.onSurface,
                    ),

                )
        }
        Text(
            text = presentCategory.name,
            color = MaterialTheme.colors.onPrimary,
            style = MaterialTheme.typography.body1,
            fontSize = 22.sp,
            textAlign = TextAlign.Center,
            fontWeight = if (expectedCategory == presentCategory.name) FontWeight.Bold else null,
            modifier = Modifier
                .padding(5.dp)
        )
    }
}

//the operation and design of the bar at the bottom of the screen
@Composable
fun TaskGoBackBar(
    GoBack: () -> Unit,
    SaveIt: () -> Unit,
    enabled: Boolean,
    isDetailed: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .size(50.dp)
            .background(color = MaterialTheme.colors.secondary)
            .border(
                width = 1.dp,
                color = MaterialTheme.colors.onSurface,
            ),
    ) {
        IconButton(onClick = GoBack) {
            Image(
                imageVector = Icons.Filled.KeyboardArrowLeft,
                contentDescription = "arrow_left",
                modifier = Modifier
                    .size(50.dp)
                    .background(MaterialTheme.colors.primary)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colors.onSurface,
                    ),
                contentScale = ContentScale.Crop,
            )
        }
        Button(
            onClick = SaveIt,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (!isDetailed) stringResource(R.string.save) else "",
                color = MaterialTheme.colors.onPrimary,
                style = MaterialTheme.typography.h2,
                fontSize = 25.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .weight(1f)
            )
        }
    }
}

//the operation and design of the list of types
@Composable
fun ShowingListOfTypesOption(
    taskUiState: TaskUiState,
    onValueChange: (TaskUiState) -> Unit,
    onClick: () -> Unit,
    onClickSingle: () -> Unit,
) {
    Column {
        for (i in 0..2) {
            TypesBarDesign(presentType = typesTasksArray[i], taskUiState, onValueChange, onClick, onClickSingle = onClickSingle)
        }
    }
}

//the operation and design of the single type view
@Composable
fun TypesBarDesign(
    presentType: String,
    taskUiState: TaskUiState,
    onValueChange: (TaskUiState) -> Unit,
    onClick: () -> Unit,
    onClickSingle: () -> Unit
) {
    val expectedType = taskUiState.type
    val coroutineScope = rememberCoroutineScope()
    val onButtonClicked = when(presentType) {
        TypesTasks.singleTask -> onClickSingle //callendar
        TypesTasks.untilTask -> onClick
        TypesTasks.repeatTask -> onClick
        else -> onClick
    }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 50.dp)
            .background(color = MaterialTheme.colors.surface),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            coroutineScope.launch {
                onButtonClicked()
                onValueChange(taskUiState.copy(type = presentType))
            }
        }
        ) {
            Image(
                imageVector = Icons.Filled.Add,
                contentDescription = "types_choosing",
                modifier = Modifier
                    .size(50.dp)
                    .background(color = MaterialTheme.colors.primary, RoundedCornerShape(7.dp))
                    .fillMaxHeight()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colors.onSurface,
                    ),

                )
        }
        Text(
            text = presentType,
            color = MaterialTheme.colors.onPrimary,
            style = MaterialTheme.typography.body1,
            fontSize = 22.sp,
            textAlign = TextAlign.Center,
            fontWeight = if (expectedType == presentType) FontWeight.Bold else null,
            modifier = Modifier
                .padding(5.dp)
        )
    }
}

//the operation and design of expanding additional options for until and repeat tasks
@Composable
fun RepeatedAdditionalOptions(
    onClickSecond: () -> Unit,
    onClickFirst: () -> Unit,
) {
    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 50.dp)
                .background(color = MaterialTheme.colors.surface),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClickSecond )
             {
                Image(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "types_choosing",
                    modifier = Modifier
                        .size(50.dp)
                        .background(color = MaterialTheme.colors.primary, RoundedCornerShape(7.dp))
                        .fillMaxHeight()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colors.onSurface,
                        ),

                    )
            }
            Text(
                text = repeatTaskAdditional[1],
                color = MaterialTheme.colors.onPrimary,
                style = MaterialTheme.typography.body1,
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(5.dp)
            )
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 50.dp)
                .background(color = MaterialTheme.colors.surface),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClickFirst) {
                Image(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "types_choosing",
                    modifier = Modifier
                        .size(50.dp)
                        .background(color = MaterialTheme.colors.primary, RoundedCornerShape(7.dp))
                        .fillMaxHeight()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colors.onSurface,
                        ),

                    )
            }
            Text(
                text = repeatTaskAdditional[0],
                color = MaterialTheme.colors.onPrimary,
                style = MaterialTheme.typography.body1,
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(5.dp)
            )
        }
    }
}



