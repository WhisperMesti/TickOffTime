package com.example.tickofftime.taskdatabase

import kotlinx.coroutines.flow.Flow

class OfflineTasksRepository(private val taskDao: TaskDao) : TasksRepository {

    //takes all elements from the list
    override fun getAllTasksStream(): Flow<List<Task>> = taskDao.getAllTasks()

    //takes one element from the list
    override fun getTasksStream(id: Int): Flow<Task?> = taskDao.getTask(id)

    //creates a task
    override suspend fun createTask(task: Task) = taskDao.create(task)

    //deletes a task
    override suspend fun deleteTask(task: Task) = taskDao.delete(task)

    //updates a task
    override suspend fun updateTask(task: Task) = taskDao.update(task)
}