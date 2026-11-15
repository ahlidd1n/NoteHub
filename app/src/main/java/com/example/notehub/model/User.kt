package com.example.notehub.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.notehub.ViewModel.Important


@Entity
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var title: String = "Nothing was written",
    var description: String,
    val isComplete: Boolean,
    val important: Important = Important.LOW
)