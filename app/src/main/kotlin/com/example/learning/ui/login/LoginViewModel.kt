package com.example.learning.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learning.data.AuthRepository
import com.example.learning.data.LoginResult
import com.example.learning.domain.LoginFormErrors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(private val auth: AuthRepository) : ViewModel() {

    data class UiState(
        val email: String = "",
        val password: String = "",
        val errors: LoginFormErrors = LoginFormErrors(),
        val loading: Boolean = false,
        val serverError: String? = null,
        val loggedIn: Boolean = false,
        val isPasswordVisible: Boolean = false,
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    fun onEmailChange(value: String) =
        _state.update { it.copy(email = value, errors = it.errors.copy(email = null), serverError = null) }

    fun onPasswordChange(value: String) =
        _state.update { it.copy(password = value, errors = it.errors.copy(password = null), serverError = null) }

    fun togglePasswordVisibility() =
        _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }

    fun fillDemoCredentials(valid: Boolean = true) {
        if (valid) {
            _state.update {
                it.copy(
                    email = "alex.johnson@skillforge.io",
                    password = "password123",
                    errors = LoginFormErrors(),
                    serverError = null,
                )
            }
        } else {
            _state.update {
                it.copy(
                    email = "student@skillforge.io",
                    password = "wrongpass",
                    errors = LoginFormErrors(),
                    serverError = null,
                )
            }
        }
    }

    fun submit() {
        val input = _state.value
        if (input.loading) return
        _state.update { it.copy(loading = true, serverError = null) }

        viewModelScope.launch {
            val result = auth.login(input.email, input.password)
            _state.update { current ->
                when (result) {
                    LoginResult.Success -> current.copy(loading = false, loggedIn = true)
                    is LoginResult.Invalid -> current.copy(loading = false, errors = result.errors)
                    LoginResult.Rejected -> current.copy(loading = false, serverError = "Incorrect credentials. Try alex.johnson@skillforge.io / password123")
                    LoginResult.NetworkError -> current.copy(loading = false, serverError = "Unable to connect to the authentication service. Please check your network connection.")
                }
            }
        }
    }
}
