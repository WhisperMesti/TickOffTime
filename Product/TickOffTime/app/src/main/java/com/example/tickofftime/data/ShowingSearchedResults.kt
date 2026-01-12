package com.example.tickofftime.data

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tickofftime.*
import com.example.tickofftime.R
import com.example.tickofftime.categorydatabase.Category
import com.example.tickofftime.taskdatabase.Task
import com.example.tickofftime.taskdatabase.TypesTasks
import java.time.LocalDate

//the main body of the Showing Searched Results that displays all tasks that fulfil previously saved requirements
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ShowingSearchedResults(
    categoryViewModel: CategoryHomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    taskViewModel: TaskHomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onBack: () -> Unit,
    name: String,
    categories: List<String>,
    types: List<String>,
    firstDate: LocalDate,
    secondDate: LocalDate,
    navigateDetails: (Task) -> Unit,
    ) {
    val taskHomeUiState by taskViewModel.taskHomeUiState.collectAsState()
    val categoryHomeUiState by categoryViewModel.categoryHomeUiState.collectAsState()
    var sortedList = taskHomeUiState.taskList

    if(name.isNotBlank()) sortedList = sortedList.filter { it.name == name }
    if(categories.isNotEmpty()) sortedList = sortedList.filter { categories.contains(it.category) }
    if(types.isNotEmpty()) sortedList = sortedList.filter { types.contains(it.type) }
    sortedList = sortedList.filter { (LocalDate.parse(it.firstDate) in firstDate..secondDate) || (LocalDate.parse(it.secondDate) in firstDate..secondDate) }

    Scaffold(
        topBar = { SearchButton(onClick = {})},
        floatingActionButton = {
            FloatingActionButton({}) {
                HoldButton(
                    onClick = {onBack()},
                    onLongClick = {},
                    imageVector = Icons.Default.KeyboardArrowLeft,
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        )
    {
        if (sortedList.isEmpty()) {
            Text(
                text = stringResource(R.string.no_tasks_found),
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center
            )
        } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items = sortedList, key = { it.id }) { task ->
                TodayTaskDesign(categoryColor = categoryHomeUiState.categoryList.find { it.name == task.category }?.colour
                    ?: 1,
                    task = task,
                    navigate = { navigateDetails(it) },
                    done = {})
            }
        }
    }
    }
}
