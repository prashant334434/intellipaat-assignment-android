package com.example.learning.data

import com.example.learning.domain.Course
import com.example.learning.domain.Lesson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CourseRepositoryTest {

    private val python = Course(
        id = 1,
        title = "Python Programming",
        instructor = "John Smith",
        lessons = listOf(
            Lesson(1, "Introduction"),
            Lesson(2, "Variables"),
            Lesson(3, "Functions"),
        ),
    )

    @Test
    fun `refresh keeps lessons completed on this device and updates progress`() = runBlocking {
        val repo = CourseRepository(FakeCourseApi(listOf(python)), MemoryCourseCache())
        repo.refreshCourses()
        repo.completeLesson(courseId = 1, lessonId = 2)

        repo.refreshCourses() // server still reports nothing completed

        val course = repo.observeCourse(1).first()!!
        assertEquals(1, course.completedCount)
        assertEquals(33, course.progressPercent)
    }

    @Test
    fun `failed refresh falls back to cached courses`() = runBlocking {
        val api = FakeCourseApi(listOf(python))
        val repo = CourseRepository(api, MemoryCourseCache())
        repo.refreshCourses()

        api.failure = IOException("offline")
        val result = repo.refreshCourses()

        assertIs<RefreshResult.Offline>(result)
        assertEquals(listOf(python), repo.observeCourses().first())
    }

    @Test
    fun `failed refresh with empty cache reports failure`() = runBlocking {
        val repo = CourseRepository(FakeCourseApi(failure = IOException("offline")), MemoryCourseCache())
        assertIs<RefreshResult.Failed>(repo.refreshCourses())
    }

    @Test
    fun `toggleLesson completes and un-completes lesson reactively`() = runBlocking {
        val repo = CourseRepository(FakeCourseApi(listOf(python)), MemoryCourseCache())
        repo.refreshCourses()

        // Toggle on
        repo.toggleLesson(courseId = 1, lessonId = 1)
        val afterOn = repo.observeCourse(1).first()!!
        assertTrue(afterOn.lessons.first { it.id == 1 }.completed)
        assertEquals(33, afterOn.progressPercent)

        // Toggle off
        repo.toggleLesson(courseId = 1, lessonId = 1)
        val afterOff = repo.observeCourse(1).first()!!
        assertEquals(0, afterOff.completedCount)
        assertEquals(0, afterOff.progressPercent)
    }

    @Test
    fun `observeCourse returns null for nonexistent course`() = runBlocking {
        val repo = CourseRepository(FakeCourseApi(listOf(python)), MemoryCourseCache())
        repo.refreshCourses()
        assertNull(repo.observeCourse(999).first())
    }

    private class FakeCourseApi(
        private val courses: List<Course> = emptyList(),
        var failure: Throwable? = null,
    ) : CourseApi {
        override suspend fun fetchCourses(): List<Course> {
            failure?.let { throw it }
            return courses
        }
    }

    private class MemoryCourseCache : CourseCache {
        private val state = MutableStateFlow<List<Course>>(emptyList())
        override fun observe(): Flow<List<Course>> = state
        override suspend fun write(courses: List<Course>) {
            state.value = courses
        }
    }
}
