package com.example.tickofftime.categorydatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//category database initialization
@Database(entities = [Category::class], version = 1, exportSchema = false) //tworzenie bazy
abstract class CategoryDatabase: RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    companion object {
        @Volatile
        private var Instance: CategoryDatabase? = null

        fun getDatabase(context: Context): CategoryDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, CategoryDatabase::class.java, "category_database")
                    .build()
                    .also { Instance = it}
            }
        }
    }
}