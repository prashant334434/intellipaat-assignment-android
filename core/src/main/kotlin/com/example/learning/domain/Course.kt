package com.example.learning.domain

import kotlin.math.roundToInt

data class Lesson(
    val id: Int,
    val title: String,
    val completed: Boolean = false,
)

/** Progress is derived from lesson state, so it can never drift from the lessons. */
data class Course(
    val id: Int,
    val title: String,
    val instructor: String,
    val lessons: List<Lesson>,
) {
    val lessonCount: Int get() = lessons.size
    val completedCount: Int get() = lessons.count { it.completed }
    val progressPercent: Int get() = percentOf(completedCount, lessonCount)
    val isCompleted: Boolean get() = lessonCount > 0 && completedCount == lessonCount

    fun markLessonCompleted(lessonId: Int): Course =
        copy(lessons = lessons.map { if (it.id == lessonId) it.copy(completed = true) else it })

    fun toggleLessonCompleted(lessonId: Int): Course =
        copy(lessons = lessons.map { if (it.id == lessonId) it.copy(completed = !it.completed) else it })
}

fun percentOf(done: Int, total: Int): Int =
    if (total == 0) 0 else (done * 100.0 / total).roundToInt()
