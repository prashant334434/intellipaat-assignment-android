package com.example.learning.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LoginValidationTest {

    @Test
    fun `valid email and password returns no errors`() {
        val errors = validateLogin("alex.johnson@skillforge.io", "password123")
        assertTrue(errors.isValid)
        assertNull(errors.email)
        assertNull(errors.password)
    }

    @Test
    fun `empty email reports required error`() {
        val errors = validateLogin("", "password123")
        assertFalse(errors.isValid)
        assertEquals("Email is required", errors.email)
        assertNull(errors.password)
    }

    @Test
    fun `blank whitespace email reports required error`() {
        val errors = validateLogin("   ", "password123")
        assertFalse(errors.isValid)
        assertEquals("Email is required", errors.email)
    }

    @Test
    fun `malformed email without domain reports invalid error`() {
        val errors = validateLogin("invalid-email", "password123")
        assertFalse(errors.isValid)
        assertEquals("Enter a valid email address", errors.email)
    }

    @Test
    fun `empty password reports required error`() {
        val errors = validateLogin("alex@example.com", "")
        assertFalse(errors.isValid)
        assertEquals("Password is required", errors.password)
    }

    @Test
    fun `short password under 6 chars reports minimum length error`() {
        val errors = validateLogin("alex@example.com", "12345")
        assertFalse(errors.isValid)
        assertEquals("Password must be at least 6 characters", errors.password)
    }

    @Test
    fun `both invalid email and short password report errors simultaneously`() {
        val errors = validateLogin("bad", "12")
        assertFalse(errors.isValid)
        assertNotNull(errors.email)
        assertNotNull(errors.password)
    }
}
