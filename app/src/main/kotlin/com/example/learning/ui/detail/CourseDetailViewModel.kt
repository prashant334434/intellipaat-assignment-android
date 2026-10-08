package com.example.learning.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learning.data.CourseRepository
import com.example.learning.domain.Course
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CourseDetailViewModel(
    private val repo: CourseRepository,
    private val courseId: Int,
) : ViewModel() {

    val course: StateFlow<Course?> = repo.observeCourse(courseId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _showCelebration = MutableStateFlow(false)
    val showCelebration: StateFlow<Boolean> = _showCelebration.asStateFlow()

    fun completeLesson(lessonId: Int) {
        viewModelScope.launch {
            repo.completeLesson(courseId, lessonId)
        }
    }

    fun toggleLesson(lessonId: Int) {
        viewModelScope.launch {
            repo.toggleLesson(courseId, lessonId)
        }
    }

    fun dismissCelebration() {
        _showCelebration.value = false
    }
}
