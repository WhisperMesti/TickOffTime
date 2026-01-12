package com.example.tickofftime

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tickofftime.data.*

//enum class with main possible destinations
enum class Mains(@StringRes val title: Int) {
    ListOfTasks(title = R.string.list_of_tasks),
    Calendar(title = R.string.calendar),
    ToDoToday(title = R.string.to_do_today),
    ToDoThisWeek(title = R.string.to_do_this_week),
    CreatingCategory(title = R.string.creating_category),
    CreatingTask(title = R.string.creating_task),
    Searching(title = R.string.search_button)
}

@Composable
fun NavigationHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Mains.ToDoToday.name) {
        composable(route=Mains.ListOfTasks.name) { //List of tasks
            ListOfTasks(
                navController = navController,
                navigateEdit = { navController.navigate("${CategoryEditDestination.route}/$it") },
                navigateEditForTask = { navController.navigate("${TaskEditDestination.route}/$it") },
                onDone = {navController.navigate("${TaskDeleteDestination.route}/$it")}
            )
        }
        composable(route=Mains.ToDoToday.name) { //To Do Today
            ToDoToday(
                navController = navController,
                navigateDetails = { navController.navigate("${TaskDetailsDestination.route}/$it") },
                onDone = {navController.navigate("${TaskDeleteDestination.route}/$it")}
            )
        }
        composable(route=Mains.ToDoThisWeek.name) { //To Do This Week
            ToDoThisWeek(
                navController = navController,
                navigateDetails = { navController.navigate("${TaskDetailsDestination.route}/$it") }
            )
        }
        composable(route=Mains.Calendar.name) { //Calendar
            MyCalendar(navController = navController)
        }
        composable(route=Mains.CreatingCategory.name) { //Creating a category
            CreatingCategoryMenu(navController = navController)
        }
        composable(route=Mains.CreatingTask.name) { //Creating a task
            CreatingTaskMenu(navController = navController)
        }
        composable(route=Mains.Searching.name) { //Searching
            Searching(
                navController = navController,
                navigateDetails = { navController.navigate("${TaskEditDestination.route}/$it") }
            )
        }
        composable( //Editing Category
            route = CategoryEditDestination.routeWithArgs,
            arguments = listOf(navArgument(CategoryEditDestination.categoryIdArg) {
                type = NavType.IntType
            })
        ) {
            EditingCategoryMenu(navController = navController)
        }
        composable( //Editing Task
            route = TaskEditDestination.routeWithArgs,
            arguments = listOf(navArgument(TaskEditDestination.taskIdArg) {
                type = NavType.IntType
            })
        ) {
            EditingTaskMenu(navController = navController)
        }
        composable( //Task details
            route = TaskDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(TaskDetailsDestination.taskIdArg) {
                type = NavType.IntType
            })
        ) {
            TaskDetails(navController = navController)
        }
        composable( //Deleting Taks
            route = TaskDeleteDestination.routeWithArgs,
            arguments = listOf(navArgument(TaskDeleteDestination.taskIdArg) {
                type = NavType.IntType
            }),
        ) {
            DeletingTask(navController = navController)
        }
    }
}