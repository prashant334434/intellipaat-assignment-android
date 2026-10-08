package com.example.learning.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CourseProgressTest {

    @Test
    fun `percentOf correctly handles zero lessons`() {
        assertEquals(0, percentOf(done = 0, total = 0))
        assertEquals(0, percentOf(done = 5, total = 0))
    }

    @Test
    fun `percentOf calculates accurate rounded percentages`() {
        assertEquals(65, percentOf(done = 13, total = 20))
        assertEquals(25, percentOf(done = 7, total = 28))
        assertEquals(100, percentOf(done = 16, total = 16))
        assertEquals(50, percentOf(done = 1, total = 2))
    }

    @Test
    fun `course isCompleted is true only when all lessons are finished`() {
        val course = Course(
            id = 1,
            title = "Generative AI",
            instructor = "Sarah Williams",
            lessons = listOf(
                Lesson(1, "LLMs", completed = true),
                Lesson(2, "Transformers", completed = false),
            ),
        )

        assertFalse(course.isCompleted)
        assertEquals(50, course.progressPercent)

        val completedCourse = course.markLessonCompleted(2)
        assertTrue(completedCourse.isCompleted)
        assertEquals(100, completedCourse.progressPercent)
        assertEquals(2, completedCourse.completedCount)
    }

    @Test
    fun `course with empty lessons reports 0 progress and not completed`() {
        val emptyCourse = Course(
            id = 99,
            title = "Empty",
            instructor = "None",
            lessons = emptyList(),
        )

        assertEquals(0, emptyCourse.lessonCount)
        assertEquals(0, emptyCourse.completedCount)
        assertEquals(0, emptyCourse.progressPercent)
        assertFalse(emptyCourse.isCompleted)
    }
}
