package com.example.learning.data.local

import com.example.learning.data.CourseCache
import com.example.learning.domain.Course
import com.example.learning.domain.Lesson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class RoomCourseCache(private val dao: CourseDao) : CourseCache {

    override fun observe(): Flow<List<Course>> =
        combine(dao.observeCourses(), dao.observeLessons()) { courses, lessons ->
            val lessonsByCourse = lessons.groupBy { it.courseId }
            courses.map { c ->
                Course(
                    id = c.id,
                    title = c.title,
                    instructor = c.instructor,
                    lessons = lessonsByCourse[c.id].orEmpty().map { Lesson(it.id, it.title, it.completed) },
                )
            }
        }

    override suspend fun write(courses: List<Course>) {
        dao.replaceAll(
            courses = courses.map { CourseEntity(it.id, it.title, it.instructor) },
            lessons = courses.flatMap { c ->
                c.lessons.map { LessonEntity(c.id, it.id, it.title, it.completed) }
            },
        )
    }
}
