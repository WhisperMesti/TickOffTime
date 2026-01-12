package com.example.tickofftime.data

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tickofftime.*
import com.example.tickofftime.R
import com.example.tickofftime.categorydatabase.Category
import com.example.tickofftime.taskdatabase.Task

//the List Of Tasks menu - main body
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ListOfTasks(
    navController: NavController,
    categoryViewModel: CategoryHomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    taskViewModel: TaskHomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateEdit: (Int) -> Unit,
    navigateEditForTask: (Int) -> Unit,
    onDone: (Int) -> Unit,
) {
    val categoryHomeUiState by categoryViewModel.categoryHomeUiState.collectAsState()
    val taskHomeUiState by taskViewModel.taskHomeUiState.collectAsState()

    Scaffold(
        topBar = { SearchButton(onClick = {navController.navigate(Mains.Searching.name)},)},
        bottomBar ={ MainBar(navController = navController, firstDestination = stringResource(R.string.list_of_tasks)) },
        floatingActionButton = {
            FloatingActionButton({}) {
                HoldButton(onClick = {navController.navigate(Mains.CreatingTask.name)}, onLongClick = {navController.navigate(Mains.CreatingCategory.name)})
            }
        },
    ) {
        ListOfCategories(
            itemsList = categoryHomeUiState.categoryList,
            navigateEdit = { navigateEdit(it.id) },
            itemList = taskHomeUiState.taskList,
            navigateEditForTask = { navigateEditForTask(it.id) },
            done = { onDone(it.id) }
        )
    }
}

//the operation and design of the search button at the top of the screen
@Composable
fun SearchButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    IconButton(onClick = onClick ) {
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
                text = stringResource(R.string.search_button),
                color = MaterialTheme.colors.onPrimary,
                style = MaterialTheme.typography.h2,
                fontSize = 25.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .padding(start = 16.dp, end = 16.dp)
            )
        }
    }
}

//the operation and design of the list of categories
@Composable
fun ListOfCategories(
    itemsList: List<Category>,
    navigateEdit: (Category) -> Unit,
    itemList: List<Task>,
    navigateEditForTask: (Task) -> Unit,
    done: (Task) -> Unit
) {
    //displaying information when there are no categories
    if (itemsList.isEmpty() or ((itemsList.size == 1) and (itemsList.any {it.name == "Uncategorised"}) and (!itemList.any {it.category == "Uncategorised"}))) {
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
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.animateContentSize(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        )) {
            items(items = itemsList, key = {it.id}) { category ->
                if (category.name != "Uncategorised") CategoryDesign( //preventing from displaying Uncategorised category when it is empty
                    category = category,
                    navigateEdit = navigateEdit,
                    itemList = itemList,
                    navigateEditForTask = navigateEditForTask,
                    done = done
                )
                else {
                    if (itemList.any {it.category == "Uncategorised"}) {
                        CategoryDesign(
                            category = category,
                            navigateEdit = navigateEdit,
                            itemList = itemList,
                            navigateEditForTask = navigateEditForTask,
                            done = done
                        )
                    }
                }
            }
        }
    }
}

////the operation and design of the floating action button that allows to create tasks and categories
@Composable
fun HoldButton(
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    imageVector: ImageVector = Icons.Default.Add,
) {
    val pressGesture = Modifier.pointerInput(Unit) {
        detectTapGestures(
            onDoubleTap  = {
                onLongClick()
            },
            onTap = {
                onClick()
            }
        )
    }

    Box(
        modifier = Modifier.then(pressGesture),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = "button",
            tint = MaterialTheme.colors.onSurface,
        )
    }
}

//the operation and design of the list of tasks
@Composable
fun ListOfTasksDisplay(
    itemsList: List<Task>,
    navigateEdit: (Task) -> Unit = {},
    currentCategory: Category,
    done: (Task) -> Unit, ) {

    val filteredTasks = itemsList.filter{it.category == currentCategory.name}.sortedBy { it.firstDate }

    //presenting information when there are no tasks assigned to that category
    if (filteredTasks.isEmpty()) {
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
    else {
        for (element in filteredTasks) {
            ListTaskDesign(task = element, navigateEdit = navigateEdit, done = done) //
        }
    }
}