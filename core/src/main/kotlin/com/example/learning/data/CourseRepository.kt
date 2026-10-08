package com.example.learning.data

import com.example.learning.domain.Course
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

sealed interface RefreshResult {
    /** Server data is now in the cache. */
    data object Updated : RefreshResult

    /** Refresh failed, but cached courses exist and are shown. */
    data object Offline : RefreshResult

    /** Refresh failed and there is nothing cached to show. */
    data class Failed(val cause: Throwable) : RefreshResult
}

/**
 * The cache is the single source of truth the UI observes.
 * Network refreshes write into it, and lesson completion writes into it directly,
 * so the app works the same online and offline.
 */
class CourseRepository(
    private val api: CourseApi,
    private val cache: CourseCache,
) {
    private val writeLock = Mutex()

    fun observeCourses(): Flow<List<Course>> = cache.observe()

    fun observeCourse(courseId: Int): Flow<Course?> =
        cache.observe().map { courses -> courses.firstOrNull { it.id == courseId } }

    suspend fun refreshCourses(): RefreshResult =
        try {
            val remote = api.fetchCourses()
            writeLock.withLock {
                val local = cache.observe().first().associateBy { it.id }
                cache.write(remote.map { keepLocalCompletions(it, local[it.id]) })
            }
            RefreshResult.Updated
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            if (cache.observe().first().isNotEmpty()) RefreshResult.Offline else RefreshResult.Failed(e)
        }

    suspend fun completeLesson(courseId: Int, lessonId: Int) {
        writeLock.withLock {
            val updated = cache.observe().first().map { course ->
                if (course.id == courseId) course.markLessonCompleted(lessonId) else course
            }
            cache.write(updated)
        }
    }

    suspend fun toggleLesson(courseId: Int, lessonId: Int) {
        writeLock.withLock {
            val updated = cache.observe().first().map { course ->
                if (course.id == courseId) course.toggleLessonCompleted(lessonId) else course
            }
            cache.write(updated)
        }
    }

    /**
     * The server does not store progress yet, so a refresh must not wipe
     * lessons the user completed on this device.
     */
    private fun keepLocalCompletions(remote: Course, local: Course?): Course {
        if (local == null) return remote
        val completedIds = local.lessons.filter { it.completed }.map { it.id }.toSet()
        return remote.copy(
            lessons = remote.lessons.map { lesson ->
                if (lesson.id in completedIds) lesson.copy(completed = true) else lesson
            },
        )
    }
}
