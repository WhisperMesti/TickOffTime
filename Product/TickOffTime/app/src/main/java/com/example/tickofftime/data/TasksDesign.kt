package com.example.tickofftime.data

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tickofftime.categorydatabase.Category
import com.example.tickofftime.taskdatabase.Task
import com.example.tickofftime.taskdatabase.TypesTasks
import com.example.tickofftime.taskdatabase.weekDaysNames
import com.example.tickofftime.taskdatabase.weekDaysShortNames
import java.time.DayOfWeek

//the operation and design of tasks presented in To Do Today menu
@Composable
fun TodayTaskDesign(categoryColor: Int, task: Task, navigate: (Task) -> Unit, overdue: Boolean = false, comingDeadline: Boolean = false, done: (Task) -> Unit) {
    Card(modifier = Modifier
        .padding(8.dp)
        .border(
            width = 1.dp,
            color = MaterialTheme.colors.onSurface,
            shape = AbsoluteRoundedCornerShape(4.dp)
        )
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .size(50.dp)
                .background(color = MaterialTheme.colors.surface),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(imageVector = Icons.Filled.Done,
                contentDescription = "figure",
                modifier = Modifier
                    .size(50.dp)
                    .background(color = Color(categoryColor), RoundedCornerShape(7.dp))
                    .fillMaxHeight()
                    .clickable { done(task) }
                    .border(
                        width = 0.dp,
                        color = MaterialTheme.colors.onSurface,
                        shape = AbsoluteRoundedCornerShape(7.dp)
                    )
                )
            Row(modifier = Modifier
                .fillMaxWidth()
                .clickable { navigate(task) }
            ) {
                Text(
                    text = task.name, //max = 27
                    color = if (overdue) Color.Red else MaterialTheme.colors.onPrimary,
                    style = MaterialTheme.typography.body1,
                    fontSize = 22.sp,
                    fontWeight = if (comingDeadline) FontWeight.Bold else null,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(5.dp)
                )
            }
        }
    }
}

//the operation and design of tasks presented in To Do This Week menu
@Composable
fun ThisWeekTaskDesign(
    categoryColor: Int,
    task: Task,
    navigate: (Task) -> Unit,
    overdue: Boolean = false,
    comingDeadline: Boolean = false,
    currentDay: DayOfWeek
) {
    Card(modifier = Modifier
        .padding(8.dp)
        .border(
            width = 1.dp,
            color = MaterialTheme.colors.onSurface,
            //shape = AbsoluteRoundedCornerShape(4.dp)
        )
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .size(50.dp)
                .background(color = MaterialTheme.colors.surface),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(color = Color(categoryColor), RoundedCornerShape(4.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = stringResource(weekDaysShortNames[weekDaysNames.indexOf(currentDay)]),
                    color = MaterialTheme.colors.onPrimary,
                    style = MaterialTheme.typography.body1,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                )
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .clickable { navigate(task) }) {
                Text(
                    text = task.name, //max = 27
                    color = if (overdue) Color.Red else MaterialTheme.colors.onPrimary,
                    style = MaterialTheme.typography.body1,
                    fontSize = 22.sp,
                    fontWeight = if (comingDeadline) FontWeight.Bold else null,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(5.dp)
                )
            }
        }
    }
}

//the operation and design of categories presented in List Of Tasks menu
@Composable
fun CategoryDesign(
    category: Category,
    navigateEdit: (Category) -> Unit,
    itemList: List<Task>,
    navigateEditForTask: (Task) -> Unit,
    done: (Task) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }

    Column ( verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Row(
            Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colors.onSurface
                )
                .background(color = MaterialTheme.colors.surface),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(imageVector = if (expanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowRight,
                contentDescription = "arrow",
                modifier = Modifier
                    .size(50.dp)
                    .background(color = Color(category.colour))
                    .fillMaxHeight()
                    .clickable { expanded = !expanded }
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colors.onSurface
                    )
            )
            Row(Modifier
                .clickable { if (category.name != "Uncategorised") navigateEdit(category) }
                .fillMaxWidth()
            ) {
                Text(
                    text = category.name, //max = 27
                    color = MaterialTheme.colors.onPrimary,
                    style = MaterialTheme.typography.body1,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(5.dp)
                )
            }
        }
        if (expanded) ListOfTasksDisplay(itemsList = itemList, currentCategory = category, navigateEdit = navigateEditForTask, done = done)
    }
}

//the operation and design of tasks presented in List Of Tasks menu
@Composable
fun ListTaskDesign(task: Task, navigateEdit: (Task) -> Unit = {}, done: (Task) -> Unit = {}) { //

    val colors = when(task.type) {
        TypesTasks.singleTask -> Color.Cyan
        TypesTasks.untilTask -> Color.Magenta
        TypesTasks.repeatTask -> Color.Yellow
        else -> Color.Transparent
    }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 50.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colors.onSurface
            )
            .background(color = MaterialTheme.colors.surface),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(imageVector = Icons.Filled.Done,
            contentDescription = task.name,
            modifier = Modifier
                .size(50.dp)
                .background(color = colors) //change it when I have types of tasks
                .fillMaxHeight()
                .clickable { done(task) }
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colors.onSurface
                )
            )
            Row(modifier = Modifier
                .clickable { navigateEdit(task) }
                .fillMaxWidth()
            ) {
                Text(
                    text = task.name, //max = 27
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

//the operation and design of tasks presented in Calendar menu
@Composable
fun CalendarTaskDesign(categoryColor: Int, task: Task, comingDeadline: Boolean = false, overdue: Boolean = false) {
    Card(modifier = Modifier
        .padding(8.dp)
        .border(
            width = 1.dp,
            color = MaterialTheme.colors.onSurface,
            shape = AbsoluteRoundedCornerShape(4.dp)
        )
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .size(50.dp)
                .background(color = MaterialTheme.colors.surface),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(imageVector = Icons.Filled.Done,
                contentDescription = "figure",
                modifier = Modifier
                    .size(50.dp)
                    .background(color = Color(categoryColor), RoundedCornerShape(7.dp))
                    .fillMaxHeight()
                    .border(
                        width = 0.dp,
                        color = MaterialTheme.colors.onSurface,
                        shape = AbsoluteRoundedCornerShape(7.dp)
                    )
            )
            Text(
                text = task.name, //max = 27
                color = if (overdue) Color.Red else MaterialTheme.colors.onPrimary,
                style = MaterialTheme.typography.body1,
                fontSize = 22.sp,
                fontWeight = if (comingDeadline) FontWeight.Bold else null,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(5.dp)
            )
        }
    }
}
