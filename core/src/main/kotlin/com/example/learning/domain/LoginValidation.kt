package com.example.learning.domain

data class LoginFormErrors(
    val email: String? = null,
    val password: String? = null,
) {
    val isValid: Boolean get() = email == null && password == null
}

private val EMAIL_PATTERN = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")

fun validateLogin(email: String, password: String): LoginFormErrors = LoginFormErrors(
    email = when {
        email.isBlank() -> "Email is required"
        !EMAIL_PATTERN.matches(email.trim()) -> "Enter a valid email address"
        else -> null
    },
    password = when {
        password.isEmpty() -> "Password is required"
        password.length < 6 -> "Password must be at least 6 characters"
        else -> null
    },
)
