package io.github.kkoshin.muse.designsystem.theme

import androidx.compose.ui.graphics.Color

/**
 * Muse Design System — Color tokens.
 *
 * Two-layer structure:
 *  1. Palette  — raw hex values used only inside this file.
 *  2. Semantic — the tokens consumed by components & business code.
 */
object MuseColor {

    // ── Palette (internal reference only) ──────────────────────────────
    private val Blue50  = Color(0xFFE3F2FD)
    private val Blue100 = Color(0xFFBBDEFB)
    private val Blue200 = Color(0xFF90CAF9)
    private val Blue300 = Color(0xFF64B5F6)
    private val Blue400 = Color(0xFF42A5F5)
    private val Blue500 = Color(0xFF5D9CED) // current primary
    private val Blue600 = Color(0xFF4A8CD8)
    private val Blue700 = Color(0xFF3A7CC4)
    private val Blue800 = Color(0xFF2A6CB0)
    private val Blue900 = Color(0xFF1A5C9C)

    private val Neutral0   = Color(0xFF000000)
    private val Neutral10  = Color(0xFF1C1C1E)
    private val Neutral20  = Color(0xFF2C2C2E)
    private val Neutral50  = Color(0xFF48484A)
    private val Neutral80  = Color(0xFF8E8E93)
    private val Neutral90  = Color(0xFFAEAEB2)
    private val Neutral95  = Color(0xFFD1D1D6)
    private val Neutral99  = Color(0xFFF5F5F7)
    private val Neutral100 = Color(0xFFFFFFFF)

    private val Red500 = Color(0xFFFF3B30)

    // ── Semantic — Light scheme ────────────────────────────────────────
    val primary           = Blue500
    val onPrimary         = Neutral100
    val primaryContainer  = Blue100
    val onPrimaryContainer = Blue900

    val secondary         = Blue300
    val onSecondary       = Neutral100
    val secondaryContainer = Blue50
    val onSecondaryContainer = Blue800

    val background        = Neutral99
    val onBackground      = Neutral10
    val surface           = Neutral100
    val onSurface         = Neutral10
    val surfaceVariant    = Neutral95
    val onSurfaceVariant  = Neutral50

    val error             = Red500
    val onError           = Neutral100

    val outline           = Neutral90
    val outlineVariant    = Neutral95

    // ── Semantic — Dark scheme ─────────────────────────────────────────
    val darkPrimary           = Blue300
    val darkOnPrimary         = Neutral10
    val darkPrimaryContainer  = Blue700
    val darkOnPrimaryContainer = Blue100

    val darkSecondary         = Blue200
    val darkOnSecondary       = Neutral10
    val darkSecondaryContainer = Blue800
    val darkOnSecondaryContainer = Blue50

    val darkBackground        = Neutral10
    val darkOnBackground      = Neutral99
    val darkSurface           = Neutral20
    val darkOnSurface         = Neutral99
    val darkSurfaceVariant    = Neutral50
    val darkOnSurfaceVariant  = Neutral90

    val darkError             = Color(0xFFFF6961)
    val darkOnError           = Neutral10

    val darkOutline           = Neutral80
    val darkOutlineVariant    = Neutral50

    // ── Extended semantic tokens ───────────────────────────────────────
    /** Replaces `onSurface.copy(alpha = 0.5f)` scattered across the codebase. */
    val secondaryText        = Neutral80
    val darkSecondaryText    = Neutral90

    /** Replaces `onSurface.copy(alpha = 0.12f)` used for dividers. */
    val divider              = Neutral95
    val darkDivider          = Neutral50

    /** Replaces `primary.copy(alpha = 0.1f)` used for highlight backgrounds. */
    val highlightBg          = Color(0x1A5D9CED)
    val darkHighlightBg      = Color(0x1A64B5F6)

    /** Replaces `onBackground.copy(alpha = 0.5f)` used for disabled states. */
    val disabledText         = Neutral90
    val darkDisabledText     = Neutral50

    /** Replaces `Color.Black.copy(alpha = 0.4f)` used for scrim / overlay. */
    val scrim                = Color(0x66000000)
    val darkScrim            = Color(0x99000000)
}
