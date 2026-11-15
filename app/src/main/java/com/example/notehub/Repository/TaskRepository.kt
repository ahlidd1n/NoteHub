package com.example.notehub.Repository

import com.example.notehub.NetWorking.TaskDao
import com.example.notehub.model.Task

class TaskRepository(
    private val dao: TaskDao
) {

    val notes = dao.getAllTask()

    suspend fun insert(task: Task) {
        dao.insertTask(task)
    }

    suspend fun delete(task: Task) {
        dao.deleteTask(task)
    }

    suspend fun update(task: Task) {
        dao.updateTask(task)
    }
}