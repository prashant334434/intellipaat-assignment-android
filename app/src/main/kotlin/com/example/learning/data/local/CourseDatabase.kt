package com.example.learning.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val instructor: String,
)

@Entity(tableName = "lessons", primaryKeys = ["courseId", "id"])
data class LessonEntity(
    val courseId: Int,
    val id: Int,
    val title: String,
    val completed: Boolean,
)

@Dao
abstract class CourseDao {
    @Query("SELECT * FROM courses ORDER BY id")
    abstract fun observeCourses(): Flow<List<CourseEntity>>

    @Query("SELECT * FROM lessons ORDER BY courseId, id")
    abstract fun observeLessons(): Flow<List<LessonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertCourses(items: List<CourseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertLessons(items: List<LessonEntity>)

    @Query("DELETE FROM courses")
    abstract suspend fun clearCourses()

    @Query("DELETE FROM lessons")
    abstract suspend fun clearLessons()

    @Transaction
    open suspend fun replaceAll(courses: List<CourseEntity>, lessons: List<LessonEntity>) {
        clearCourses()
        clearLessons()
        insertCourses(courses)
        insertLessons(lessons)
    }
}

@Database(entities = [CourseEntity::class, LessonEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
}
