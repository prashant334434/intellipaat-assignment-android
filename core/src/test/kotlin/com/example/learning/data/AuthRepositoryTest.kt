package com.example.learning.data

import kotlinx.coroutines.runBlocking
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class AuthRepositoryTest {

    @Test
    fun `successful login saves token and returns success`() = runBlocking {
        val tokens = FakeTokenStore()
        val repo = AuthRepository(FakeAuthApi(), tokens)

        val result = repo.login("alex@skillforge.io", "password123")
        assertIs<LoginResult.Success>(result)
        assertEquals("token-123", tokens.current?.token)
        assertEquals("alex@skillforge.io", tokens.current?.email)
    }

    @Test
    fun `invalid email format returns Invalid with errors without calling API`() = runBlocking {
        val tokens = FakeTokenStore()
        val repo = AuthRepository(FakeAuthApi(), tokens)

        val result = repo.login("invalid-email", "password123")
        assertIs<LoginResult.Invalid>(result)
        assertNull(tokens.current)
    }

    @Test
    fun `rejected credentials returns Rejected`() = runBlocking {
        val tokens = FakeTokenStore()
        val repo = AuthRepository(FakeAuthApi(reject = true), tokens)

        val result = repo.login("alex@skillforge.io", "wrongpassword")
        assertIs<LoginResult.Rejected>(result)
        assertNull(tokens.current)
    }

    @Test
    fun `network error during auth returns NetworkError`() = runBlocking {
        val tokens = FakeTokenStore()
        val repo = AuthRepository(FakeAuthApi(failure = IOException("No internet")), tokens)

        val result = repo.login("alex@skillforge.io", "password123")
        assertIs<LoginResult.NetworkError>(result)
        assertNull(tokens.current)
    }

    private class FakeAuthApi(
        val reject: Boolean = false,
        val failure: Throwable? = null,
    ) : AuthApi {
        override suspend fun login(email: String, password: String): Session {
            failure?.let { throw it }
            if (reject) throw InvalidCredentialsException()
            return Session(token = "token-123", email = email)
        }
    }

    private class FakeTokenStore : TokenStore {
        override var current: Session? = null
        override fun save(session: Session) {
            current = session
        }
    }
}
