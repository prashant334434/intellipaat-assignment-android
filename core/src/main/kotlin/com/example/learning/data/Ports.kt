package com.example.learning.data

import com.example.learning.domain.Course
import kotlinx.coroutines.flow.Flow

/** Remote source of course content. The server owns course structure. */
interface CourseApi {
    suspend fun fetchCourses(): List<Course>
}

/** Local persistence. Reads are reactive so the UI always reflects the stored state. */
interface CourseCache {
    fun observe(): Flow<List<Course>>
    suspend fun write(courses: List<Course>)
}

data class Session(val token: String, val email: String)

interface AuthApi {
    /** Throws [InvalidCredentialsException] for bad credentials, any other exception for transport errors. */
    suspend fun login(email: String, password: String): Session
}

class InvalidCredentialsException : Exception("Invalid email or password")

interface TokenStore {
    val current: Session?
    fun save(session: Session)
}
