package com.example.tickofftime.taskdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//task database initialization
@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class TaskDatabase: RoomDatabase() {
    abstract fun taskDao(): TaskDao
    companion object {
        @Volatile
        private var Instance: TaskDatabase? = null

        fun getDatabase(context: Context): TaskDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, TaskDatabase::class.java, "task_database")
                    .build()
                    .also {Instance = it}
            }
        }
    }
}