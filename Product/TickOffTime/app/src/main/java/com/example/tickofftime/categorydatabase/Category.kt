package com.example.tickofftime.categorydatabase

import android.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tickofftime.ui.theme.Orange
import com.example.tickofftime.ui.theme.Purple

//initialization of category
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val colour: Int,
)

//object with possible Category colours
object CategoryColours {
    const val red = Color.RED
    const val yellow = Color.YELLOW
    const val green = Color.GREEN
    const val blue = Color.BLUE
    const val pink = Color.MAGENTA
}

//array with possible Category colours
val categoryColoursArray = arrayOf(
    CategoryColours.red,
    CategoryColours.yellow,
    CategoryColours.green,
    CategoryColours.blue,
    CategoryColours.pink
)
