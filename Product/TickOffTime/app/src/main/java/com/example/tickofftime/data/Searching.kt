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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tickofftime.AppViewModelProvider
import com.example.tickofftime.CategoryHomeViewModel
import com.example.tickofftime.Mains
import com.example.tickofftime.R
import com.example.tickofftime.categorydatabase.Category
import com.example.tickofftime.taskdatabase.TaskUiState
import com.example.tickofftime.taskdatabase.TypesTasks
import com.example.tickofftime.taskdatabase.typesTasksArray
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

//the main body of the Searching menu
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "MutableCollectionMutableState")
@Composable
fun Searching(
    navController: NavController,
    categoryViewModel: CategoryHomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateDetails: (Int) -> Unit,
) {
    val categoryHomeUiState by categoryViewModel.categoryHomeUiState.collectAsState()

    //all variables that save the chosen requirements
    var name by remember { mutableStateOf("") }
    var categories by remember { mutableStateOf(listOf<String>()) }
    var types by remember { mutableStateOf(listOf<String>()) }
    var firstDate by remember { mutableStateOf(LocalDate.parse("1000-01-01")) }
    var secondDate by remember { mutableStateOf(LocalDate.parse("3000-12-31")) }

    //all variables responsible for expanding options
    var expandedCategory by remember { mutableStateOf(false) }
    var expandedTypes by remember { mutableStateOf(false) }
    var expandedDate by remember { mutableStateOf(false) }
    var showResults by remember { mutableStateOf(false) }

    if (!expandedDate and !showResults) {
        Scaffold(
        topBar = { SearchButton(onClick = {showResults = true})},
        floatingActionButton = {
            FloatingActionButton({}) {
                HoldButton(
                    onClick = { navController.popBackStack() },
                    onLongClick = {},
                    imageVector = Icons.Default.KeyboardArrowLeft,
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) {
            Column(
                modifier = Modifier.fillMaxWidth().animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField( //name text field
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.enter_name_task)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = true,
                    singleLine = true
                )
                ChoosingExpandingOption( //expanding categories option
                    { expandedCategory = !expandedCategory },
                    stringResource(id = R.string.choose_category)
                )
                if (expandedCategory) { //expanding categories
                    //showing information when there are no categories
                    if (categoryHomeUiState.categoryList.isEmpty() or ((categoryHomeUiState.categoryList.size == 1) and (categoryHomeUiState.categoryList.any {it.name == "Uncategorised"}))) {
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
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(3.dp)) { //showing categories
                            items(items = categoryHomeUiState.categoryList, key = {it.id}) { category ->
                                if (category.name != "Uncategorised") CategoryShown(
                                    presentCategory = category,
                                    onClick = { categories = if (!categories.contains(category.name)) {
                                        categories.plus(category.name)
                                    } else {
                                        categories.minus(category.name)
                                    } },
                                    included = categories.contains(category.name)
                                )
                            }
                        }
                    }
                }
                ChoosingExpandingOption( //expanding types option
                    { expandedTypes = !expandedTypes },
                    stringResource(id = R.string.choose_type)
                )
                if (expandedTypes) { //expanding types
                    for (type in typesTasksArray) {
                        TypeShown(
                            presentType = type,
                            onClick = { types = if (!types.contains(type)) {
                                types.plus(type)
                            } else {
                                types.minus(type)
                            } },
                            included = types.contains(type))
                    }
                }
                ChoosingExpandingOption( //expanding calendar option
                    { expandedDate = !expandedDate },
                    stringResource(id = R.string.date)
                )
            }
        }
    }
    else if (expandedDate and !showResults){
        val (var1, var2) = pickUpADateSearching { expandedDate = !expandedDate } //saving obtained dates
        firstDate = var1
        secondDate = var2
    }
    else if (!expandedDate and showResults) { //proceeding to the results
        ShowingSearchedResults(
            onBack = {showResults = false},
            name = name,
            categories = categories,
            types = types,
            firstDate = firstDate,
            secondDate = secondDate,
            navigateDetails = {navigateDetails(it.id)}
        )
    }
}

//the operation and design of a single category view
@Composable
fun CategoryShown(presentCategory: Category, onClick: () -> Unit, included: Boolean) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(start = 50.dp)
            .background(color = MaterialTheme.colors.surface),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            imageVector = Icons.Filled.Add,
            contentDescription = "category_choosing",
            modifier = Modifier
                .size(50.dp)
                .background(color = Color(presentCategory.colour), shape = RoundedCornerShape(7.dp))
                .fillMaxHeight()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colors.onSurface,
                ),
            )
        Text(
            text = presentCategory.name,
            color = MaterialTheme.colors.onPrimary,
            style = MaterialTheme.typography.body1,
            fontSize = 22.sp,
            textAlign = TextAlign.Center,
            fontWeight = if (included) FontWeight.Bold else null,
            modifier = Modifier
                .padding(5.dp)
        )
    }
}

//the operation and design of a single type view
@Composable
fun TypeShown(presentType: String, onClick: () -> Unit, included: Boolean) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(start = 50.dp)
            .background(color = MaterialTheme.colors.surface),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            imageVector = Icons.Filled.Add,
            contentDescription = "types_choosing",
            modifier = Modifier
                .size(50.dp)
                .background(color = MaterialTheme.colors.primary, RoundedCornerShape(7.dp))
                .fillMaxHeight()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colors.onSurface,
                ),
            )
        Text(
            text = presentType,
            color = MaterialTheme.colors.onPrimary,
            style = MaterialTheme.typography.body1,
            fontSize = 22.sp,
            textAlign = TextAlign.Center,
            fontWeight = if (included) FontWeight.Bold else null,
            modifier = Modifier
                .padding(5.dp)
        )
        }
    }

//the operation and design of the whole calendar/date picker
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun pickUpADateSearching(
    goBack: () -> Unit,
): Pair<LocalDate, LocalDate> {

    var sinceEnabled by remember { mutableStateOf(true) }
    var toEnabled by remember { mutableStateOf(true) }
    var isFinished by remember { mutableStateOf(false) }
    var firstDate by remember { mutableStateOf(LocalDate.parse("1000-01-01")) }
    var secondDate by remember { mutableStateOf(LocalDate.parse("3000-12-31")) }
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) } // Adjust as needed
    val endMonth = remember { currentMonth.plusMonths(100) } // Adjust as needed
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() } // Available from the library
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val coroutineScope = rememberCoroutineScope()

    var saveIt = {}
    if ((sinceEnabled == true) and (toEnabled == false)) {
        saveIt = { firstDate = selectedDate }
    } else if ((sinceEnabled == false) and (toEnabled == true)) {
        saveIt = { secondDate = selectedDate }
    }

    var thisMonth by remember { mutableStateOf<YearMonth>(currentMonth) }

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek,
    )

    Scaffold(
        bottomBar = {
            TaskGoBackBar(
                GoBack = { isFinished = true},
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
            HorizontalCalendar(
                state = state,
                dayContent = { day ->
                    Box(Modifier.padding(10.dp)) {
                        Day(
                            day, isSelected = selectedDate == day.date,
                            { day ->
                                selectedDate = if (selectedDate == day.date) null else day.date
                            },
                            enabled = (((day.date.isAfter(firstDate.minusDays(1))) or (!toEnabled)) and ((day.date.isBefore(secondDate.plusDays(1))) or (!sinceEnabled)))
                        )
                    }
                },
                monthHeader = {
                    DaysOfWeekTitle(daysOfWeek = daysOfWeek())
                }
            )
            SinceToBar(
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
    if (isFinished) {
        goBack()
    }
    return Pair(firstDate,secondDate)
}