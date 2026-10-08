package com.example.learning.data

import com.example.learning.domain.LoginFormErrors
import com.example.learning.domain.validateLogin
import kotlinx.coroutines.CancellationException

sealed interface LoginResult {
    data object Success : LoginResult
    data class Invalid(val errors: LoginFormErrors) : LoginResult
    data object Rejected : LoginResult
    data object NetworkError : LoginResult
}

class AuthRepository(
    private val api: AuthApi,
    private val tokens: TokenStore,
) {
    suspend fun login(email: String, password: String): LoginResult {
        val errors = validateLogin(email, password)
        if (!errors.isValid) return LoginResult.Invalid(errors)

        return try {
            tokens.save(api.login(email.trim(), password))
            LoginResult.Success
        } catch (e: CancellationException) {
            throw e
        } catch (e: InvalidCredentialsException) {
            LoginResult.Rejected
        } catch (e: Exception) {
            LoginResult.NetworkError
        }
    }
}
