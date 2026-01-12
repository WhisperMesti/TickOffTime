package com.example.tickofftime.data

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tickofftime.*
import com.example.tickofftime.R
import com.example.tickofftime.taskdatabase.TypesTasks
import com.example.tickofftime.taskdatabase.weekDaysNames
import com.example.tickofftime.ui.theme.Dark
import java.time.LocalDate

//object with navigation parameters
object TaskDetailsDestination : NavigationDestination {
    override val route = "task_details"
    override val titleRes = R.string.editing_task
    const val taskIdArg = "categoryId"
    val routeWithArgs = "$route/{$taskIdArg}"
}
//the To Do Today menu - main body
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ToDoToday(
    navController: NavController,
    categoryViewModel: CategoryHomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    taskViewModel: TaskHomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateDetails: (Int) -> Unit,
    onDone: (Int) -> Unit
) {
    val taskHomeUiState by taskViewModel.taskHomeUiState.collectAsState()
    val categoryHomeUiState by categoryViewModel.categoryHomeUiState.collectAsState()
    var howManyTasks by remember { mutableStateOf(0) }
    val index = weekDaysNames.indexOf(LocalDate.now().dayOfWeek) +1

    var sortedListSaved by remember { mutableStateOf(listOf<taskTDTW>())}

    //sorting a task list based on the present day and types requirements
    LaunchedEffect(taskHomeUiState.taskList, categoryHomeUiState.categoryList) {
        var sortedList = listOf<taskTDTW>()
        for (task in taskHomeUiState.taskList) {
            if (task.type == TypesTasks.singleTask) { //single task
                if ((LocalDate.now().isAfter(LocalDate.parse(task.secondDate)))) {
                    sortedList = sortedList.plus(
                        taskTDTW(
                            id = howManyTasks,
                            task = task,
                            date = LocalDate.now(),
                            isOverdue = true
                        )
                    )
                    howManyTasks++
                } else if (LocalDate.now() == LocalDate.parse(task.firstDate)) {
                    sortedList = sortedList.plus(
                        taskTDTW(
                            id = howManyTasks,
                            task = task,
                            date = LocalDate.now()
                        )
                    )
                    howManyTasks++
                }
            } else if (task.type == TypesTasks.untilTask) { //until task
                if (LocalDate.now().isAfter(LocalDate.parse(task.secondDate))) {
                    sortedList = sortedList.plus(
                        taskTDTW(
                            id = howManyTasks,
                            task = task,
                            date = LocalDate.now(),
                            isOverdue = true
                        )
                    )
                    howManyTasks++
                } else if ((LocalDate.now() == LocalDate.parse(task.secondDate).minusDays(1)) or (LocalDate.now() == LocalDate.parse(task.secondDate))) {
                    sortedList = sortedList.plus(
                        taskTDTW(
                            id = howManyTasks,
                            task = task,
                            date = LocalDate.now(),
                            isOverdue = true,
                            isDeadline = true
                        )
                    )
                    howManyTasks++
                } else if ((LocalDate.now() in LocalDate.parse(task.firstDate)..LocalDate.parse(task.secondDate)) and (index.toString() in task.daysRepeated.toString())) {
                    sortedList = sortedList.plus(
                        taskTDTW(
                            id = howManyTasks,
                            task = task,
                            date = LocalDate.now()
                        )
                    )
                    howManyTasks++
                }
            } else if (task.type == TypesTasks.repeatTask) { //repeat task
                if ((LocalDate.now() in LocalDate.parse(task.firstDate)..LocalDate.parse(task.secondDate)) and (index.toString() in task.daysRepeated.toString())) {
                    sortedList = sortedList.plus(
                        taskTDTW(
                            id = howManyTasks,
                            task = task,
                            date = LocalDate.now()
                        )
                    )
                    howManyTasks++
                } else if ((LocalDate.now() in LocalDate.parse(task.firstDate)..LocalDate.parse(task.secondDate)) and (index.toString() !in task.daysRepeated.toString())) {
                    sortedList = sortedList.plus(
                        taskTDTW(
                            id = howManyTasks,
                            task = task,
                            date = LocalDate.now(),
                            isOverdue = true
                        )
                    )
                    howManyTasks++
                } else if (LocalDate.now().isAfter(LocalDate.parse(task.secondDate))) {
                    sortedList = sortedList.plus(
                        taskTDTW(
                            id = howManyTasks,
                            task = task,
                            date = LocalDate.now(),
                            isOverdue = true
                        )
                    )
                    howManyTasks++
                }
            }
        }
        sortedList = sortedList.sortedBy { it.task.category }
        if (sortedListSaved != sortedList) sortedListSaved = sortedList
    }

    Scaffold(
        topBar = { TodayBar(doIt = {navController.navigate(Mains.ToDoThisWeek.name)})},
        bottomBar ={ MainBar( navController = navController,
            firstDestination = stringResource(R.string.to_do_today)
        )},
    ) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            items(items = sortedListSaved, key = { it.id }) { item -> //displaying tasks
                TodayTaskDesign(
                    categoryColor = categoryHomeUiState.categoryList.find { it.name == item.task.category }?.colour
                        ?: 1,
                    task = item.task,
                    navigate = { navigateDetails(it.id) },
                    done = { onDone(it.id) },
                    overdue = item.isOverdue,
                    comingDeadline = item.isDeadline
                )
            }
        }
            if (howManyTasks == 0) { //presenting information in case of lack of tasks assigned to the present day
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.no_tasks_today),
                        style = MaterialTheme.typography.body2,
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = stringResource(R.string.no_tasks_today_2),
                        style = MaterialTheme.typography.body2,
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
            }
    }
}

//the operation and design of the bar at the top of the screen
@Composable
fun TodayBar(doIt: () -> Unit, modifier: Modifier = Modifier) {

    val color = if (MaterialTheme.colors.primary == Dark) {
        MaterialTheme.colors.secondary
    }
    else {
        MaterialTheme.colors.primary
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .size(50.dp)
            .padding(vertical = 5.dp)
            .background(color = MaterialTheme.colors.primary)
            .border(
                width = 1.dp,
                color = MaterialTheme.colors.onSurface,
                shape = AbsoluteRoundedCornerShape(4.dp)
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
        ) {
        Text(
            text = LocalDate.now().dayOfWeek.toString(),
            color = MaterialTheme.colors.onPrimary,
            style = MaterialTheme.typography.h2,
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .wrapContentWidth(Alignment.CenterHorizontally)
                .weight(1f)
        )

        IconButton(onClick = doIt) {
            Image(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = "arrow_right",
                modifier = modifier
                    .size(60.dp)
                    .background(color)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colors.onSurface,
                        shape = AbsoluteRoundedCornerShape(4.dp)
                    ),
                contentScale = ContentScale.Crop,
            )
        }
    }
}