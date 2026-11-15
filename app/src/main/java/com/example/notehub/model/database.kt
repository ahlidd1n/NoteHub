package com.example.notehub.model

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.notehub.NetWorking.TaskDao
import com.example.notehub.ViewModel.Converters

@TypeConverters(Converters::class)
@Database(
    entities = [Task::class],
    version = 2
)
abstract class TaskDatabase : RoomDatabase() {
    abstract val dao: TaskDao
}