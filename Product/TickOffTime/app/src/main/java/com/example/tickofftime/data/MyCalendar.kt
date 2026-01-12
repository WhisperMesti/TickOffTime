package com.example.tickofftime.data

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tickofftime.AppViewModelProvider
import com.example.tickofftime.CategoryHomeViewModel
import com.example.tickofftime.R
import com.example.tickofftime.TaskHomeViewModel
import com.example.tickofftime.taskdatabase.TypesTasks
import com.example.tickofftime.taskdatabase.weekDaysNames
import com.example.tickofftime.ui.theme.Dark
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

//the Calendar menu
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MyCalendar(
    navController: NavController,
    modifier: Modifier = Modifier,
    taskViewModel: TaskHomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    categoryViewModel: CategoryHomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {

    val taskHomeUiState by taskViewModel.taskHomeUiState.collectAsState()
    val categoryHomeUiState by categoryViewModel.categoryHomeUiState.collectAsState()

    //all values of dates
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) }
    val endMonth = remember { currentMonth.plusMonths(100) }
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var thisMonth by remember { mutableStateOf<YearMonth>(currentMonth) }
    var howManyTasks by remember { mutableStateOf(0) }

    //sorting a taskList based on the chosen day and types requirements
    var sortedList = listOf<taskTDTW>()
    if (selectedDate != null) {
        val thisDay = selectedDate ?: LocalDate.now()
        val index = weekDaysNames.indexOf(thisDay.dayOfWeek) +1
        for (task in taskHomeUiState.taskList) {
            if (task.type == TypesTasks.singleTask) {
                if ((thisDay.isAfter(LocalDate.parse(task.secondDate))) and (thisDay == LocalDate.now())) {
                    sortedList = sortedList.plus(
                        taskTDTW(
                            id = howManyTasks,
                            task = task,
                            date = LocalDate.now(),
                            isOverdue = true
                        )
                    )
                    howManyTasks++
                } else if ((thisDay == LocalDate.parse(task.firstDate))) {
                    sortedList = sortedList.plus(
                        taskTDTW(
                            id = howManyTasks,
                            task = task,
                            date = LocalDate.now()
                        )
                    )
                    howManyTasks++
                }
            } else if (task.type == TypesTasks.untilTask) {
                if ((thisDay.isAfter(LocalDate.parse(task.secondDate))) and (thisDay == LocalDate.now())) {
                    sortedList = sortedList.plus(
                        taskTDTW(
                            id = howManyTasks,
                            task = task,
                            date = LocalDate.now(),
                            isOverdue = true
                        )
                    )
                    howManyTasks++
                } else if (((thisDay == LocalDate.parse(task.secondDate).minusDays(1)) or (thisDay == LocalDate.parse(task.secondDate))) and (thisDay == LocalDate.now())) {
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
                } else if ((thisDay in LocalDate.parse(task.firstDate)..LocalDate.parse(task.secondDate)) and (index.toString() in task.daysRepeated.toString()) and (thisDay == LocalDate.now())) {
                    sortedList = sortedList.plus(
                        taskTDTW(
                            id = howManyTasks,
                            task = task,
                            date = LocalDate.now()
                        )
                    )
                    howManyTasks++
                }
            } else if (task.type == TypesTasks.repeatTask) {
                if ((thisDay in LocalDate.parse(task.firstDate)..LocalDate.parse(task.secondDate)) and (index.toString() in task.daysRepeated.toString())) {
                    sortedList = sortedList.plus(
                        taskTDTW(
                            id = howManyTasks,
                            task = task,
                            date = LocalDate.now()
                        )
                    )
                    howManyTasks++
                }
                else if ((LocalDate.now() in LocalDate.parse(task.firstDate)..LocalDate.parse(task.secondDate)) and (index.toString() !in task.daysRepeated.toString()) and (thisDay == LocalDate.now())) {
                    sortedList = sortedList.plus(taskTDTW(id = howManyTasks, task = task, date = LocalDate.now(), isOverdue = true))
                    howManyTasks++
                }
                else if ((thisDay.isAfter(LocalDate.parse(task.secondDate))) and (thisDay == LocalDate.now())) {
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
    }
    else {sortedList = sortedList.filter {it.task.type == "hdbe123%feh"}} //sending always empty list

    //Calendar state initialization
    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek,

    )

    Scaffold(
        topBar = {
            DayBar(
                onClickLeft = {
                    coroutineScope.launch {
                        thisMonth = thisMonth.previousMonth
                        state.animateScrollToMonth(thisMonth)
                    }
                },
                onClickRight = {
                    coroutineScope.launch {
                        thisMonth = thisMonth.nextMonth
                        state.animateScrollToMonth(thisMonth)
                    }
                },
                monthName = thisMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
            )
        },
        bottomBar = {
            MainBar(
                navController = navController,
                firstDestination = stringResource(id = R.string.calendar)
            )
        },
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                ))
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            HorizontalCalendar( //a calendar
                state = state,
                dayContent = { day ->
                    Box(modifier.padding(10.dp)) {
                        Day(
                            day, isSelected = selectedDate == day.date,
                            { day ->
                                selectedDate = if (selectedDate == day.date) null else day.date
                            },
                            enabled = true
                        )
                    }
                },
                monthHeader = {
                    DaysOfWeekTitle(daysOfWeek = daysOfWeek())
                }
            )
            if (sortedList.isEmpty() and (selectedDate.toString() != "null")) { //displaying "No tasks" information
                Row( verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.no_task),
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.Center
                    )
                }
            }
            else for (task in sortedList) { //displaying a sorted list
                CalendarTaskDesign(
                 categoryColor = categoryHomeUiState.categoryList.find { it.name == task.task.category }?.colour ?: 1,
                    task = task.task,
                    comingDeadline = task.isDeadline,
                    overdue = task.isOverdue
            )
        }
    }
    }
}

//bar that allows navigate through the calendar
@Composable
fun DayBar(
    onClickLeft: () -> Unit,
    onClickRight: () -> Unit,
    monthName: String
) {

    val color = if (MaterialTheme.colors.primary == Dark) {
        MaterialTheme.colors.secondary
    }
    else {
        MaterialTheme.colors.primary
    }

    Row(
        modifier = Modifier
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
        Image(
            imageVector = Icons.Filled.KeyboardArrowLeft,
            contentDescription = "arrow_right",
            modifier = Modifier
                .size(60.dp)
                .clickable { onClickLeft() }
                .background(color)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colors.onSurface,
                    shape = AbsoluteRoundedCornerShape(4.dp)
                ),
            contentScale = ContentScale.Crop,
        )

        Text(
            text = monthName,
            color = MaterialTheme.colors.onPrimary,
            style = MaterialTheme.typography.h2,
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .wrapContentWidth(Alignment.CenterHorizontally)
                .weight(1f)
        )

        Image(
            imageVector = Icons.Filled.KeyboardArrowRight,
            contentDescription = "arrow_right",
            modifier = Modifier
                .size(60.dp)
                .clickable { onClickRight() }
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

//the operation and design of a single day
@Composable
fun Day(day: CalendarDay, isSelected: Boolean, onClick: (CalendarDay) -> Unit, enabled: Boolean) {

    if (day.position == DayPosition.MonthDate) Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .border(
                width = 1.dp,
                color = MaterialTheme.colors.onSurface,
                shape = CircleShape
            )
            .background(color = if (isSelected) MaterialTheme.colors.primary else if (!enabled) Color.Gray else Color.Transparent)
            .clickable(
                onClick = { onClick(day) },
                enabled = enabled
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            color =  MaterialTheme.colors.onPrimary
        )
    }
}

//bar at the top of the calendar
@Composable
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
            )
        }
    }
}

