
package com.example.mytodoapp

data class Task(
    val id: Int,
    val title: String,
    val content: String,
    val createdAt: Long,  // Timestamps added
    val updatedAt: Long
)