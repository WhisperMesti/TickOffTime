package com.example.tickofftime.data

import androidx.compose.material.icons.filled.*
import com.example.tickofftime.taskdatabase.TaskUiState
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tickofftime.R
import com.example.tickofftime.taskdatabase.TypesTasks
import com.example.tickofftime.taskdatabase.weekDaysShortNames
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*
import kotlin.math.pow

//the main body of the PickUpADate menu-option
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PickUpADate(
    taskUiState: TaskUiState,
    onValueChange: (TaskUiState) -> Unit,
    goBack: () -> Unit,
    typeTask: String
) {
    var sinceEnabled by remember { mutableStateOf(true) }
    var toEnabled by remember { mutableStateOf(true) }
    val firstDate = if (taskUiState.firstDate != "") LocalDate.parse(taskUiState.firstDate) else LocalDate.parse("1000-01-01")
    val secondDate = if (taskUiState.secondDate != "") LocalDate.parse(taskUiState.secondDate) else LocalDate.parse("3000-12-31")

    //all values of dates
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) }
    val endMonth = remember { currentMonth.plusMonths(100) }
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val coroutineScope = rememberCoroutineScope()

    //initialization what SAVE button does
    var saveIt = {}
    if (typeTask == TypesTasks.singleTask){
        saveIt = {
            onValueChange(taskUiState.copy(firstDate = selectedDate.toString()))
            onValueChange(taskUiState.copy(secondDate = selectedDate.toString()))
        }
    }
    else {
        if ((sinceEnabled == true) and (toEnabled == false)) {
            saveIt = { onValueChange(taskUiState.copy(firstDate = selectedDate.toString())) }
        } else if ((sinceEnabled == false) and (toEnabled == true)) {
            saveIt = { onValueChange(taskUiState.copy(secondDate = selectedDate.toString())) }
        }
    }

    var thisMonth by remember { mutableStateOf<YearMonth>(currentMonth) }

    //Calendar state initialization
    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek,
    )

    Scaffold(
        bottomBar = {
            TaskGoBackBar(
                GoBack = { goBack() },
                SaveIt = {
                    saveIt()
                    sinceEnabled = true
                    toEnabled = true
                    selectedDate = null
                         },
                enabled = selectedDate.toString() != "null",
            )
        },
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
            HorizontalCalendar( //a calendar
                state = state,
                dayContent = { day ->
                    Box(Modifier.padding(10.dp)) {
                        Day(
                            day, isSelected = selectedDate == day.date,
                            { day ->
                                selectedDate = if (selectedDate == day.date) null else day.date
                            },
                            enabled = (((day.date.isAfter(firstDate.minusDays(1))) or (!toEnabled)) and ((day.date.isBefore(secondDate.plusDays(1))) or (!sinceEnabled)) and (day.date.isAfter(LocalDate.now().minusDays(1))))
                         )
                    }
                },
                monthHeader = {
                    DaysOfWeekTitle(daysOfWeek = daysOfWeek())
                }
            )
            if (typeTask != TypesTasks.singleTask) SinceToBar(
                sinceEnabled = sinceEnabled,
                sinceDo = {
                    toEnabled=false
                    selectedDate = null
                          },
                toEnabled = toEnabled,
                toDo = {
                    sinceEnabled=false
                    selectedDate = null
                },
            )
            Text(text = if (selectedDate != null) selectedDate.toString() else "")
        }
    }
}

//the operation and design of the bar at the bottom of the calendar
@Composable
fun SinceToBar(sinceEnabled: Boolean, sinceDo: () -> Unit, toDo: () -> Unit, toEnabled: Boolean) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .size(50.dp)
        .background(color = MaterialTheme.colors.secondary),
    ) {
        Button(
            onClick = sinceDo,
            enabled = sinceEnabled,
           modifier = Modifier.weight(1f).border(
               width = 1.dp,
               color = MaterialTheme.colors.onSurface,
           )
        ) {
            Text(
                text = stringResource(R.string.since),
                color = MaterialTheme.colors.onPrimary,
                style = MaterialTheme.typography.h2,
                fontSize = 25.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    //.weight(1f)
            )
        }
        Button(
            onClick = toDo,
            enabled = toEnabled,
            modifier = Modifier.weight(1f).border(
                width = 1.dp,
                color = MaterialTheme.colors.onSurface,
            )
        ) {
            Text(
                text = stringResource(R.string.to),
                color = MaterialTheme.colors.onPrimary,
                style = MaterialTheme.typography.h2,
                fontSize = 25.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    //.weight(1f)
            )
        }
    }
}

//the operation and design of the row displayed while choosing repeated days
@Composable
fun ChoosingDays(taskUiState: TaskUiState, onValueChange: (TaskUiState) -> Unit,) {

    var chosenDays by remember {mutableStateOf(taskUiState.daysRepeated)}

    Row(modifier = Modifier
        .fillMaxWidth()
        //.background(color = MaterialTheme.colors.secondary),
    ) {
        for (i in 1..7) {
            Row(modifier = Modifier
                .weight(1f)
                .height(50.dp)
                .border(width = 1.dp, color = MaterialTheme.colors.onSurface,)
                .background(color = if (i.toString() in chosenDays.toString()) MaterialTheme.colors.primary else MaterialTheme.colors.secondary)
                .clickable {if (i.toString() in chosenDays.toString()) {chosenDays += (8-i)*10.0.pow(7-i).toInt()} else {chosenDays -= (8-i)*10.0.pow(7-i).toInt()}},
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(weekDaysShortNames[i - 1]),
                    color = MaterialTheme.colors.onPrimary,
                    style = MaterialTheme.typography.h2,
                    fontSize = 25.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
        }
    }
    onValueChange(taskUiState.copy(daysRepeated = chosenDays))
}
