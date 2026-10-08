package com.example.learning.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Brand Color Palette - Modern Indigo & Electric Cyan
val IndigoPrimary = Color(0xFF4F46E5)
val IndigoPrimaryDark = Color(0xFF818CF8)
val IndigoPrimaryContainer = Color(0xFFEEF2FF)
val IndigoPrimaryContainerDark = Color(0xFF312E81)

val CyanSecondary = Color(0xFF0284C7)
val CyanSecondaryDark = Color(0xFF38BDF8)
val CyanSecondaryContainer = Color(0xFFE0F2FE)
val CyanSecondaryContainerDark = Color(0xFF075985)

val EmeraldSuccess = Color(0xFF10B981)
val EmeraldSuccessDark = Color(0xFF34D399)
val EmeraldSuccessContainer = Color(0xFFD1FAE5)
val EmeraldSuccessContainerDark = Color(0xFF064E3B)

val AmberWarning = Color(0xFFF59E0B)
val AmberWarningDark = Color(0xFFFBBF24)
val AmberWarningContainer = Color(0xFFFEF3C7)
val AmberWarningContainerDark = Color(0xFF78350F)

val RoseError = Color(0xFFEF4444)
val RoseErrorDark = Color(0xFFF87171)
val RoseErrorContainer = Color(0xFFFEE2E2)
val RoseErrorContainerDark = Color(0xFF7F1D1D)

// Neutrals - Clean Slate Spectrum
val Slate50 = Color(0xFFF8FAFC)
val Slate100 = Color(0xFFF1F5F9)
val Slate200 = Color(0xFFE2E8F0)
val Slate300 = Color(0xFFCBD5E1)
val Slate400 = Color(0xFF94A3B8)
val Slate500 = Color(0xFF64748B)
val Slate600 = Color(0xFF475569)
val Slate700 = Color(0xFF334155)
val Slate800 = Color(0xFF1E293B)
val Slate900 = Color(0xFF0F172A)
val Slate950 = Color(0xFF080C15)

// Category Tag Colors
val PurpleCategoryBg = Color(0xFFF3E8FF)
val PurpleCategoryText = Color(0xFF7E22CE)
val BlueCategoryBg = Color(0xFFDBEAFE)
val BlueCategoryText = Color(0xFF1D4ED8)
val EmeraldCategoryBg = Color(0xFFD1FAE5)
val EmeraldCategoryText = Color(0xFF047857)
val OrangeCategoryBg = Color(0xFFFFEDD5)
val OrangeCategoryText = Color(0xFFC2410C)

// Gradients
val PrimaryGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF4F46E5), Color(0xFF06B6D4))
)

val HeroCardGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF4338CA), Color(0xFF6366F1), Color(0xFF0EA5E9))
)

val SuccessGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF10B981), Color(0xFF059669))
)

val OfflineGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFFD97706), Color(0xFFF59E0B))
)
