package com.example.tickofftime.data

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.tickofftime.*
import com.example.tickofftime.R
import com.example.tickofftime.ui.theme.Black
import com.example.tickofftime.ui.theme.Dark
import com.example.tickofftime.ui.theme.White

//the operation and design of the bottom bar that allows navigate through the menus
@Composable
fun MainBar(navController:NavController, firstDestination: String, modifier: Modifier = Modifier) {
    var active0 = false
    var active1= false
    var active2= false

    when(firstDestination) {
        stringResource(id = R.string.list_of_tasks) -> active0=true
        stringResource(id = R.string.to_do_today) -> active1=true
        stringResource(id = R.string.to_do_this_week) -> active1=true
        stringResource(id = R.string.calendar) -> active2=true
    }

    Row(modifier = modifier
        .fillMaxWidth()
        .size(60.dp)
        .background(color = MaterialTheme.colors.secondary)
        .border(
            width = 1.dp,
            color = MaterialTheme.colors.onSurface,
            shape = AbsoluteRoundedCornerShape(4.dp)
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = modifier.weight(1f))
        MainIcon(doIt = { navController.navigate(Mains.ListOfTasks.name)},active = active0, imageVector = Icons.Filled.List, name = "list")
        Spacer(modifier = modifier.weight(1f))
        MainIcon(doIt = { navController.navigate(Mains.ToDoToday.name)},active = active1, imageVector = Icons.Filled.Star, name = "list")
        Spacer(modifier = modifier.weight(1f))
        MainIcon(doIt = { navController.navigate(Mains.Calendar.name)},active = active2, imageVector = Icons.Filled.DateRange, name = "list")
        Spacer(modifier = modifier.weight(1f))
    }
}

//the operation and design of the single element (3)
@Composable
fun MainIcon(doIt: () -> Unit, active: Boolean, imageVector: ImageVector, name: String, modifier: Modifier = Modifier) {

    var color = MaterialTheme.colors.background
    if (active) {
        if (MaterialTheme.colors.primary == Dark) {
            color = White
        } else  {
            color = MaterialTheme.colors.primary
        }
    }

    IconButton(onClick = doIt ) {
        Image(
            imageVector = imageVector,
            contentDescription = name,
            modifier = modifier
                .size(60.dp)
                .background(color = color)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colors.onSurface,
                    shape = AbsoluteRoundedCornerShape(4.dp)
                ),
            contentScale = ContentScale.Crop,
        )
    }
}

//preview of the main bar
@Preview
@Composable
fun MainBarPreview() {
    MainBar(navController = rememberNavController(), firstDestination = stringResource(id = R.string.list_of_tasks))
}
