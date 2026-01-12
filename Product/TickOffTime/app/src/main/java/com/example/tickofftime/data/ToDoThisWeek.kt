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
import androidx.compose.material.icons.filled.KeyboardArrowLeft
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
import androidx.room.PrimaryKey
import com.example.tickofftime.*
import com.example.tickofftime.R
import com.example.tickofftime.taskdatabase.*
import com.example.tickofftime.ui.theme.Dark
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

//initialization of the class that contains tasks and its extra features
data class taskTDTW(
    val id: Int,
    val task: Task,
    val date: LocalDate,
    val isOverdue: Boolean = false,
    val isDeadline: Boolean = false,
)
//the To Do This Week menu - main body
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ToDoThisWeek(
    navController: NavController,
    categoryViewModel: CategoryHomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    taskViewModel: TaskHomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateDetails: (Int) -> Unit
) {
    val taskHomeUiState by taskViewModel.taskHomeUiState.collectAsState()
    val categoryHomeUiState by categoryViewModel.categoryHomeUiState.collectAsState()
    var howManyTasks by remember { mutableStateOf(0) }
    val today = LocalDate.now()
    val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

    var sortedListSaved by remember { mutableStateOf(listOf<taskTDTW>())}

    //sorting a task list based on the currently checked day and types requirements
   LaunchedEffect(taskHomeUiState.taskList, categoryHomeUiState.categoryList) {
        var sortedList = listOf<taskTDTW>()
        for (task in taskHomeUiState.taskList) {
            for (i in 1..7) {
                if (task.type == TypesTasks.singleTask) { //single task
                    if ((LocalDate.now()
                            .isAfter(LocalDate.parse(task.secondDate))) and (LocalDate.now() == startOfWeek.plusDays(
                            i.toLong() - 1
                        ))
                    ) {
                        sortedList = sortedList.plus(
                            taskTDTW(
                                id = howManyTasks,
                                task = task,
                                date = startOfWeek.plusDays(i.toLong() - 1),
                                isOverdue = true
                            )
                        )
                        howManyTasks++
                    } else if (startOfWeek.plusDays(i.toLong() - 1) == LocalDate.parse(task.firstDate)) {
                        sortedList = sortedList.plus(
                            taskTDTW(
                                id = howManyTasks,
                                task = task,
                                date = startOfWeek.plusDays(i.toLong() - 1)
                            )
                        )
                        howManyTasks++
                    }
                } else if (task.type == TypesTasks.untilTask) { //until task
                    if ((LocalDate.now()
                            .isAfter(LocalDate.parse(task.secondDate))) and (LocalDate.now() == startOfWeek.plusDays(
                            i.toLong() - 1
                        ))
                    ) {
                        sortedList = sortedList.plus(
                            taskTDTW(
                                id = howManyTasks,
                                task = task,
                                date = startOfWeek.plusDays(i.toLong() - 1),
                                isOverdue = true
                            )
                        )
                        howManyTasks++
                    } else if (((LocalDate.now() == LocalDate.parse(task.secondDate).minusDays(1)) or (LocalDate.now() == LocalDate.parse(task.secondDate))) and (LocalDate.now() == startOfWeek.plusDays(i.toLong() - 1))
                    ) {
                        sortedList = sortedList.plus(
                            taskTDTW(
                                id = howManyTasks,
                                task = task,
                                date = startOfWeek.plusDays(i.toLong() - 1),
                                isOverdue = true,
                                isDeadline = true
                            )
                        )
                        howManyTasks++
                    } else if ((startOfWeek.plusDays(i.toLong() - 1) in LocalDate.parse(task.firstDate)..LocalDate.parse(task.secondDate)) and (i.toString() in task.daysRepeated.toString()) and (!LocalDate.now().isAfter(LocalDate.parse(task.secondDate))) and (LocalDate.now() == startOfWeek.plusDays(i.toLong() - 1))
                    ) {
                        sortedList = sortedList.plus(
                            taskTDTW(
                                id = howManyTasks,
                                task = task,
                                date = startOfWeek.plusDays(i.toLong() - 1)
                            )
                        )
                        howManyTasks++
                    }
                } else if (task.type == TypesTasks.repeatTask) { //repeat task
                    if ((startOfWeek.plusDays(i.toLong() - 1) in LocalDate.parse(task.firstDate)..LocalDate.parse(
                            task.secondDate
                        )) and (i.toString() in task.daysRepeated.toString()) and (!LocalDate.now()
                            .isAfter(LocalDate.parse(task.secondDate))) and (!LocalDate.now()
                            .isAfter(startOfWeek.plusDays(i.toLong() - 1)))
                    ) {
                        sortedList = sortedList.plus(
                            taskTDTW(
                                id = howManyTasks,
                                task = task,
                                date = startOfWeek.plusDays(i.toLong() - 1)
                            )
                        )
                        howManyTasks++
                    } else if ((LocalDate.now() in LocalDate.parse(task.firstDate)..LocalDate.parse(
                            task.secondDate
                        )) and (i.toString() !in task.daysRepeated.toString()) and (LocalDate.now() == startOfWeek.plusDays(
                            i.toLong() - 1
                        ))
                    ) {
                        sortedList = sortedList.plus(
                            taskTDTW(
                                id = howManyTasks,
                                task = task,
                                date = startOfWeek.plusDays(i.toLong() - 1),
                                isOverdue = true
                            )
                        )
                        howManyTasks++
                    } else if ((LocalDate.now()
                            .isAfter(LocalDate.parse(task.secondDate))) and (LocalDate.now() == startOfWeek.plusDays(
                            i.toLong() - 1
                        ))
                    ) {
                        sortedList = sortedList.plus(
                            taskTDTW(
                                id = howManyTasks,
                                task = task,
                                date = startOfWeek.plusDays(i.toLong() - 1),
                                isOverdue = true
                            )
                        )
                        howManyTasks++
                    }
                }
            }
        }
        sortedList = sortedList.sortedBy { it.date }
        if (sortedListSaved != sortedList) sortedListSaved = sortedList
    }

    Scaffold(
        topBar = { WeekBar(doIt = { navController.navigate(Mains.ToDoToday.name) })},
        bottomBar ={ MainBar( navController = navController,
            firstDestination = stringResource(id = R.string.to_do_this_week)
        )},
    ) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items = sortedListSaved, key = { it.id }) { item -> //displaying tasks
                ThisWeekTaskDesign(
                    categoryColor = categoryHomeUiState.categoryList.find { it.name == item.task.category }?.colour
                        ?: 1,
                    task = item.task,
                    navigate = { navigateDetails(it.id) },
                    currentDay = item.date.dayOfWeek,
                    overdue = item.isOverdue,
                    comingDeadline = item.isDeadline
                )
            }
        }
        if (howManyTasks == 0) { //presenting information in case of lack of tasks assigned to the present week
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.no_tasks_this_week),
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
fun WeekBar(doIt: () -> Unit, modifier: Modifier = Modifier) {

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
        IconButton(onClick = doIt) {
            Image(
                imageVector = Icons.Filled.KeyboardArrowLeft,
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

        Text(
            text = "This week",
            color = MaterialTheme.colors.onPrimary,
            style = MaterialTheme.typography.h2,
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .wrapContentWidth(Alignment.CenterHorizontally)
                .weight(1f)
        )
    }
}
