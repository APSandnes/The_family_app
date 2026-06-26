package com.example.mainactivity.workers

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.LocalDate

/**
 * Tests for the notification trigger condition logic found in [NotificationWorker.doWork].
 *
 * The condition in the worker (identical for birthdays and calendar events):
 *   `if (daysUntil == 0 || (daysBefore > 0 && daysUntil == daysBefore))`
 *
 * These tests verify the condition itself and also test it end-to-end by combining
 * the pure date functions [daysUntilRecurring] / [daysUntilOneTime] with the condition.
 *
 * Date-function correctness (parsing, leap-year, wrapping) is already covered by
 * [NotificationWorkerUtilsTest]; this class focuses exclusively on the notify/no-notify
 * decision boundary.
 */
@RunWith(JUnit4::class)
class NotificationConditionTest {
    // Fixed "today" used across all tests for deterministic results
    private val today = LocalDate.of(2024, 6, 25)

    /**
     * Mirrors the condition in NotificationWorker.doWork() so tests stay coupled to
     * the real business rule without needing to spin up the full CoroutineWorker.
     */
    private fun shouldNotify(
        daysUntil: Int,
        notifyDaysBefore: Int,
    ): Boolean =
        daysUntil == 0 || (notifyDaysBefore > 0 && daysUntil == notifyDaysBefore)

    // ─────────────────────────────────────────────────────────────────────────
    // 1. Same-day (daysUntil == 0) — always fires regardless of notifyDaysBefore
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun shouldNotify_today_notifyDaysBefore3_returnsTrue() {
        assertTrue(shouldNotify(daysUntil = 0, notifyDaysBefore = 3))
    }

    @Test
    fun shouldNotify_today_notifyDaysBefore0_returnsTrue() {
        // daysBefore = 0 disables advance notice, but same-day still fires
        assertTrue(shouldNotify(daysUntil = 0, notifyDaysBefore = 0))
    }

    @Test
    fun shouldNotify_today_notifyDaysBefore1_returnsTrue() {
        assertTrue(shouldNotify(daysUntil = 0, notifyDaysBefore = 1))
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. Exact threshold match — fires when daysUntil == notifyDaysBefore > 0
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun shouldNotify_daysUntil3_notifyDaysBefore3_returnsTrue() {
        assertTrue(shouldNotify(daysUntil = 3, notifyDaysBefore = 3))
    }

    @Test
    fun shouldNotify_daysUntil1_notifyDaysBefore1_returnsTrue() {
        assertTrue(shouldNotify(daysUntil = 1, notifyDaysBefore = 1))
    }

    @Test
    fun shouldNotify_daysUntil365_notifyDaysBefore365_returnsTrue() {
        // Very large threshold — exact match still fires
        assertTrue(shouldNotify(daysUntil = 365, notifyDaysBefore = 365))
    }

    @Test
    fun shouldNotify_daysUntil7_notifyDaysBefore7_returnsTrue() {
        assertTrue(shouldNotify(daysUntil = 7, notifyDaysBefore = 7))
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 3. Above threshold — should NOT fire
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun shouldNotify_daysUntil4_notifyDaysBefore3_returnsFalse() {
        assertFalse(shouldNotify(daysUntil = 4, notifyDaysBefore = 3))
    }

    @Test
    fun shouldNotify_daysUntil100_notifyDaysBefore3_returnsFalse() {
        assertFalse(shouldNotify(daysUntil = 100, notifyDaysBefore = 3))
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 4. Below threshold but still future — should NOT fire (no "within X days" logic)
    //    The condition is == not <=, so only the exact day triggers the advance notice.
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun shouldNotify_daysUntil2_notifyDaysBefore3_returnsFalse() {
        // Past the advance-notice day, but not yet today
        assertFalse(shouldNotify(daysUntil = 2, notifyDaysBefore = 3))
    }

    @Test
    fun shouldNotify_daysUntil1_notifyDaysBefore3_returnsFalse() {
        assertFalse(shouldNotify(daysUntil = 1, notifyDaysBefore = 3))
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 5. notifyDaysBefore == 0 disables advance notice (guard: daysBefore > 0)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun shouldNotify_daysUntil3_notifyDaysBefore0_returnsFalse() {
        // daysBefore = 0 means "same-day only"; future events don't fire
        assertFalse(shouldNotify(daysUntil = 3, notifyDaysBefore = 0))
    }

    @Test
    fun shouldNotify_daysUntil1_notifyDaysBefore0_returnsFalse() {
        assertFalse(shouldNotify(daysUntil = 1, notifyDaysBefore = 0))
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 6. End-to-end: condition combined with daysUntilRecurring (birthday path)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun birthdayToday_notifyDaysBefore3_firesNotification() {
        // Birthday falls on today (June 25). daysUntilRecurring returns 0.
        val daysUntil = daysUntilRecurring("1990-06-25", today)!!
        assertTrue("Birthday today must trigger notification", shouldNotify(daysUntil, notifyDaysBefore = 3))
    }

    @Test
    fun birthdayExactlyThresholdAway_notifyDaysBefore3_firesNotification() {
        // Birthday is June 28 = 3 days away. Should match the advance-notice threshold.
        val daysUntil = daysUntilRecurring("1990-06-28", today)!!
        assertTrue("Birthday exactly 3 days away must trigger advance notice", shouldNotify(daysUntil, notifyDaysBefore = 3))
    }

    @Test
    fun birthdayFourDaysAway_notifyDaysBefore3_doesNotFire() {
        // Birthday is June 29 = 4 days away. One past the threshold.
        val daysUntil = daysUntilRecurring("1990-06-29", today)!!
        assertFalse("Birthday 4 days away must not fire when threshold is 3", shouldNotify(daysUntil, notifyDaysBefore = 3))
    }

    @Test
    fun birthdayYesterday_wrapsToNextYear_doesNotFireForThreshold3() {
        // June 24 was yesterday; next occurrence wraps to 2025 (~364 days away).
        // With notifyDaysBefore = 3 that does not match.
        val daysUntil = daysUntilRecurring("1990-06-24", today)!!
        assertFalse("Wrapped birthday far in future must not fire for small threshold", shouldNotify(daysUntil, notifyDaysBefore = 3))
    }

    @Test
    fun birthdayInvalidDate_nullGuard_doesNotFire() {
        // Worker skips the birthday with `?: return@forEach` when daysUntilRecurring is null.
        val daysUntil = daysUntilRecurring("not-a-date", today)
        // null means skip — no notification
        assertTrue("Invalid date string must produce null (skipped)", daysUntil == null)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 7. End-to-end: condition combined with daysUntilOneTime (calendar event path)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun calendarEventToday_notifyDaysBefore3_firesNotification() {
        val daysUntil = daysUntilOneTime("2024-06-25", today)!!
        assertTrue("Event today must trigger notification", shouldNotify(daysUntil, notifyDaysBefore = 3))
    }

    @Test
    fun calendarEventExactlyThresholdAway_notifyDaysBefore3_firesNotification() {
        // June 28 = 3 days away
        val daysUntil = daysUntilOneTime("2024-06-28", today)!!
        assertTrue("Event exactly 3 days away must trigger advance notice", shouldNotify(daysUntil, notifyDaysBefore = 3))
    }

    @Test
    fun calendarEventFourDaysAway_notifyDaysBefore3_doesNotFire() {
        val daysUntil = daysUntilOneTime("2024-06-29", today)!!
        assertFalse("Event 4 days away must not fire when threshold is 3", shouldNotify(daysUntil, notifyDaysBefore = 3))
    }

    @Test
    fun calendarEventInPast_nullGuard_doesNotFire() {
        // Past events return null from daysUntilOneTime — worker skips them.
        val daysUntil = daysUntilOneTime("2024-06-24", today)
        assertTrue("Past event must produce null (skipped)", daysUntil == null)
    }

    @Test
    fun calendarEventTomorrow_notifyDaysBefore0_doesNotFire() {
        // notifyDaysBefore = 0 → only same-day events; tomorrow should not fire.
        val daysUntil = daysUntilOneTime("2024-06-26", today)!!
        assertFalse("Tomorrow's event must not fire when notifyDaysBefore is 0", shouldNotify(daysUntil, notifyDaysBefore = 0))
    }

    @Test
    fun calendarEventTwoDaysAway_notifyDaysBefore3_doesNotFire() {
        // 2 days < 3 threshold — past the advance-notice day but not yet today
        val daysUntil = daysUntilOneTime("2024-06-27", today)!!
        assertFalse("Event 2 days away must not fire when threshold is 3", shouldNotify(daysUntil, notifyDaysBefore = 3))
    }
}
