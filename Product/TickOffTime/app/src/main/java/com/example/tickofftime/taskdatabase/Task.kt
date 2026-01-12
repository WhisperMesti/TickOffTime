package com.example.tickofftime.taskdatabase

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tickofftime.R
import java.time.DayOfWeek

//initialization of task
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val description: String,
    val category: String, //change it later to classes
    val type: String, //change it later to classes
    val firstDate: String,
    val secondDate: String,
    val daysRepeated: Int,
)

//object with possible types
object TypesTasks {
    const val singleTask = "Single task"
    const val untilTask = "Until task"
     const val repeatTask = "Repeat task"
}

//array with possible types
val typesTasksArray = arrayOf(
    TypesTasks.singleTask,
    TypesTasks.untilTask,
    TypesTasks.repeatTask
)

//array with additional information displayed while choosing until and repeat tasks
val repeatTaskAdditional = arrayOf(
    "Repeat every",
    "Until"
)

//array with abbreviation of weekdays' names
val weekDaysShortNames = arrayOf(
    R.string.monday,
    R.string.tuesday,
    R.string.wednesday,
    R.string.thursday,
    R.string.friday,
    R.string.saturday,
    R.string.sunday
)

//array with long weekdays' names
val weekDaysNames = arrayOf(
    DayOfWeek.MONDAY,
    DayOfWeek.TUESDAY,
    DayOfWeek.WEDNESDAY,
    DayOfWeek.THURSDAY,
    DayOfWeek.FRIDAY,
    DayOfWeek.SATURDAY,
    DayOfWeek.SUNDAY
)