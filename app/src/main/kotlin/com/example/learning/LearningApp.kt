package com.example.learning

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.learning.data.AuthRepository
import com.example.learning.data.CourseRepository
import com.example.learning.data.Session
import com.example.learning.data.TokenStore
import com.example.learning.data.local.AppDatabase
import com.example.learning.data.local.RoomCourseCache
import com.example.learning.data.remote.MockAuthApi
import com.example.learning.data.remote.MockCourseApi

class LearningApp : Application() {
    val container: AppContainer by lazy { AppContainer(this) }
}

/** Manual dependency wiring. Small enough that a DI framework would add more than it saves. */
class AppContainer(context: Context) {
    private val database: AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "learning.db").build()

    val tokenStore: TokenStore = InMemoryTokenStore()

    val authRepository = AuthRepository(MockAuthApi(), tokenStore)

    val mockCourseApi = MockCourseApi(failing = false)

    val courseRepository = CourseRepository(
        api = mockCourseApi,
        cache = RoomCourseCache(database.courseDao()),
    )
}

/** Session is kept in memory only. Production needs Keystore-backed storage. See README. */
private class InMemoryTokenStore : TokenStore {
    @Volatile
    private var session: Session? = null

    override val current: Session? get() = session
    override fun save(session: Session) {
        this.session = session
    }
}
