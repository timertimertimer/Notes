package com.example.notes.model

data class Note(
    val id: Int? = null,
    val content: String,
    val timestamp: Long,
    val priority: Int
)