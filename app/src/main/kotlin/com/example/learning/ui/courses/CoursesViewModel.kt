package com.example.learning.ui.courses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learning.data.CourseRepository
import com.example.learning.data.RefreshResult
import com.example.learning.data.remote.MockCourseApi
import com.example.learning.domain.Course
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class CourseFilter {
    ALL,
    IN_PROGRESS,
    COMPLETED,
}

class CoursesViewModel(
    private val repo: CourseRepository,
    private val mockApi: MockCourseApi? = null,
) : ViewModel() {

    data class UiState(
        val courses: List<Course> = emptyList(),
        val filteredCourses: List<Course> = emptyList(),
        val isLoading: Boolean = false,
        val isRefreshing: Boolean = false,
        val offline: Boolean = false,
        val loadFailed: Boolean = false,
        val isSimulatedOffline: Boolean = false,
        val searchQuery: String = "",
        val filter: CourseFilter = CourseFilter.ALL,
        val totalCoursesCount: Int = 0,
        val completedLessonsCount: Int = 0,
        val totalLessonsCount: Int = 0,
        val overallProgressPercent: Int = 0,
    )

    private val refreshing = MutableStateFlow(false)
    private val lastResult = MutableStateFlow<RefreshResult?>(null)
    private val searchQuery = MutableStateFlow("")
    private val currentFilter = MutableStateFlow(CourseFilter.ALL)
    private val simulatedOffline = MutableStateFlow(mockApi?.failing ?: false)

    private val dataFlow: Flow<Triple<List<Course>, Boolean, RefreshResult?>> =
        combine(repo.observeCourses(), refreshing, lastResult) { courses, isRef, res ->
            Triple(courses, isRef, res)
        }

    private val filterFlow: Flow<Triple<String, CourseFilter, Boolean>> =
        combine(searchQuery, currentFilter, simulatedOffline) { query, filter, isSimOffline ->
            Triple(query, filter, isSimOffline)
        }

    val state: StateFlow<UiState> = combine(dataFlow, filterFlow) { (courses, isRefreshing, result), (query, filter, isSimOffline) ->
        val filtered = courses.filter { course ->
            val matchesQuery = query.isBlank() ||
                course.title.contains(query, ignoreCase = true) ||
                course.instructor.contains(query, ignoreCase = true)

            val matchesFilter = when (filter) {
                CourseFilter.ALL -> true
                CourseFilter.IN_PROGRESS -> course.progressPercent in 1..99
                CourseFilter.COMPLETED -> course.progressPercent == 100
            }

            matchesQuery && matchesFilter
        }

        val totalLessons = courses.sumOf { it.lessonCount }
        val completedLessons = courses.sumOf { it.completedCount }
        val overallProgress = if (totalLessons > 0) {
            ((completedLessons * 100.0) / totalLessons).toInt()
        } else 0

        UiState(
            courses = courses,
            filteredCourses = filtered,
            isLoading = isRefreshing && courses.isEmpty(),
            isRefreshing = isRefreshing && courses.isNotEmpty(),
            offline = result is RefreshResult.Offline,
            loadFailed = result is RefreshResult.Failed && courses.isEmpty(),
            isSimulatedOffline = isSimOffline,
            searchQuery = query,
            filter = filter,
            totalCoursesCount = courses.size,
            completedLessonsCount = completedLessons,
            totalLessonsCount = totalLessons,
            overallProgressPercent = overallProgress,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState(isLoading = true))

    init {
        refresh()
    }

    fun onSearchQueryChange(newQuery: String) {
        searchQuery.value = newQuery
    }

    fun onFilterSelect(filter: CourseFilter) {
        currentFilter.value = filter
    }

    fun toggleOfflineSimulation() {
        mockApi?.let { api ->
            val newMode = api.toggleOffline()
            simulatedOffline.value = newMode
            refresh()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            refreshing.value = true
            lastResult.value = repo.refreshCourses()
            refreshing.value = false
        }
    }
}
