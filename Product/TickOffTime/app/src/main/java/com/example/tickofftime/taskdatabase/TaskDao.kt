package com.example.tickofftime.taskdatabase
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

//interface with all main functions that connect with the database
@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun create(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("SELECT * from tasks WHERE id= :id")
    fun getTask(id:Int): Flow<Task>

    @Query("SELECT * from tasks WHERE category= :category")
    fun getTaskByCategory(category:String): Flow<Task>

    @Query("SELECT * from tasks ORDER BY name ASC")
    fun getAllTasks(): Flow<List<Task>>
}

