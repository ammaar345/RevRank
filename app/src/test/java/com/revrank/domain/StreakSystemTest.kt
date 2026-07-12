package com.revrank.domain

import com.revrank.domain.model.StreakSystem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class StreakSystemTest {

    private val today = LocalDate.of(2026, 7, 11)

    @Test
    fun `first ever trip starts streak at one`() {
        val r = StreakSystem.updateStreak(null, 0, today, hasFreeze = false, isPro = false)
        assertEquals(1, r.newStreak)
        assertFalse(r.freezeConsumed)
    }

    @Test
    fun `same day trip does not change streak`() {
        val r = StreakSystem.updateStreak(today, 5, today, hasFreeze = false, isPro = false)
        assertEquals(5, r.newStreak)
    }

    @Test
    fun `consecutive day extends streak`() {
        val r = StreakSystem.updateStreak(today.minusDays(1), 5, today, hasFreeze = false, isPro = false)
        assertEquals(6, r.newStreak)
    }

    @Test
    fun `one missed day with freeze preserves streak and consumes freeze`() {
        val r = StreakSystem.updateStreak(today.minusDays(2), 5, today, hasFreeze = true, isPro = false)
        assertEquals(5, r.newStreak)
        assertTrue(r.freezeConsumed)
    }

    @Test
    fun `pro user always has freeze`() {
        val r = StreakSystem.updateStreak(today.minusDays(2), 5, today, hasFreeze = false, isPro = true)
        assertEquals(5, r.newStreak)
        assertTrue(r.freezeConsumed)
    }

    @Test
    fun `missed day without freeze breaks streak`() {
        val r = StreakSystem.updateStreak(today.minusDays(2), 5, today, hasFreeze = false, isPro = false)
        assertEquals(1, r.newStreak)
    }

    @Test
    fun `long gap breaks streak`() {
        val r = StreakSystem.updateStreak(today.minusDays(10), 20, today, hasFreeze = true, isPro = true)
        assertEquals(1, r.newStreak)
    }
}
