package com.example.tickofftime.data

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tickofftime.AppViewModelProvider
import com.example.tickofftime.Mains
import com.example.tickofftime.R
import com.example.tickofftime.categorydatabase.CategoryEntryViewModel
import com.example.tickofftime.categorydatabase.CategoryUiState
import com.example.tickofftime.categorydatabase.categoryColoursArray
import kotlinx.coroutines.launch

//the main body of the Editing Category menu
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun CreatingCategoryMenu(
    navController: NavController,
    viewModel: CategoryEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var expanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        bottomBar ={ CategoryGoBackBar( //the bar at the bottom of the screen
            GoBack = { navController.navigate(Mains.ListOfTasks.name) },
            SaveIt = {
                coroutineScope.launch {
                    viewModel.saveCategory()
                    navController.navigate(Mains.ListOfTasks.name)
                }
            },
            itemUiState = viewModel.categoryUiState
        ) },
    ) {
        Column(modifier = Modifier.fillMaxWidth().animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )), verticalArrangement = Arrangement.spacedBy(16.dp), ) {
            EnteringCategoryName( //name text field
                categoryUiState = viewModel.categoryUiState,
                onValueChange = viewModel::updateCategoryUiState
            )
            ChoosingCategoryColour { expanded = !expanded } //expanding the category colours
            if (expanded) {ShowingListOfColours(viewModel.categoryUiState,viewModel::updateCategoryUiState)}
        }
    }
}

//the operation and design of name text field
@Composable
fun EnteringCategoryName(
    categoryUiState: CategoryUiState,
    onValueChange: (CategoryUiState) -> Unit ={},
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = categoryUiState.name,
        onValueChange = { onValueChange(categoryUiState.copy(name = it)) },
        label = { Text(stringResource(R.string.enter_name_category))},
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        singleLine = true
    )
}

//the operation and design of the expanding a category colour
@Composable
fun ChoosingCategoryColour(
    onClick: () -> Unit,
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
                contentDescription = "colour_choosing",
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
            text = stringResource(id = R.string.choose_colour),
            color = MaterialTheme.colors.onPrimary,
            style = MaterialTheme.typography.body1,
            fontSize = 22.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(5.dp)
        )
    }
}

//the operation and design of the list of category colours
@Composable
fun ShowingListOfColours(
    categoryUiState: CategoryUiState,
    onValueChange: (CategoryUiState) -> Unit,
) {
    Column {
        for (i in 0..4) {
            ColourBarDesign(presentColour = categoryColoursArray[i], categoryUiState, onValueChange)
        }
    }
}

//the operation and design of a single category colour view
@Composable
fun ColourBarDesign(
    presentColour: Int,
    categoryUiState: CategoryUiState,
    onValueChange: (CategoryUiState) -> Unit,
) {
    val buttonColors = ButtonDefaults.buttonColors(
        backgroundColor = Color(presentColour),
        contentColor = Color(presentColour)
    )
    val expectedColour = categoryUiState.colour

    Row(modifier = Modifier
        .padding(start = 50.dp)
        .fillMaxWidth()
        .border(
            width = if (expectedColour == presentColour) 3.dp else 1.dp,
            color = MaterialTheme.colors.onSurface,
        )){
            Button(onClick = {onValueChange(categoryUiState.copy(colour = presentColour))},
                colors = buttonColors,
                modifier = Modifier.fillMaxWidth()
            ) {}
    }
}

//the operation and design of the bar at the bottom of the screen
@Composable
fun CategoryGoBackBar(
    GoBack: () -> Unit,
    SaveIt: () -> Unit,
    itemUiState: CategoryUiState) {
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
            enabled = itemUiState.actionEnabled,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.save),
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