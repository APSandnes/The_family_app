package com.example.mainactivity.workers

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.LocalDate

@RunWith(JUnit4::class)
class NotificationWorkerUtilsTest {
    // Fixed "today" used for all deterministic tests
    private val today = LocalDate.of(2024, 6, 25)

    // ── daysUntilRecurring ────────────────────────────────────────────────────

    @Test
    fun daysUntilRecurring_dateIsToday_returns0() {
        val result = daysUntilRecurring("1990-06-25", today)
        assertEquals(0, result)
    }

    @Test
    fun daysUntilRecurring_dateIsTomorrow_returns1() {
        val result = daysUntilRecurring("1990-06-26", today)
        assertEquals(1, result)
    }

    @Test
    fun daysUntilRecurring_dateWasYesterday_returnsAbout364or365() {
        // June 24 was yesterday → next recurring is June 24, 2025.
        // 2025 is not a leap year so 365 days from June 25 2024.
        // Days between June 25 2024 and June 24 2025 = 364.
        val result = daysUntilRecurring("1990-06-24", today)
        assertNotNull(result)
        // 364 days for a non-leap transition (Jun 25 2024 → Jun 24 2025)
        assertEquals(364, result)
    }

    @Test
    fun daysUntilRecurring_birthdayEarlierInYear_returnsCorrectDays() {
        // Jan 1: next occurrence is Jan 1, 2025.
        // Days from June 25 2024 to Jan 1 2025:
        //   June: 5 remaining, July: 31, Aug: 31, Sep: 30, Oct: 31, Nov: 30, Dec: 31, Jan 1 = 190
        val result = daysUntilRecurring("1985-01-01", today)
        assertEquals(190, result)
    }

    @Test
    fun daysUntilRecurring_birthdayLaterInYear_returnsCorrectDays() {
        // Dec 31: still in 2024, after today June 25.
        // Days from June 25 to Dec 31 2024:
        //   June: 5, July: 31, Aug: 31, Sep: 30, Oct: 31, Nov: 30, Dec: 31 = 189
        val result = daysUntilRecurring("2000-12-31", today)
        assertEquals(189, result)
    }

    @Test
    fun daysUntilRecurring_invalidDateString_returnsNull() {
        assertNull(daysUntilRecurring("not-a-date", today))
    }

    @Test
    fun daysUntilRecurring_emptyString_returnsNull() {
        assertNull(daysUntilRecurring("", today))
    }

    @Test
    fun daysUntilRecurring_feb29InLeapYear_beforeFeb29_returnsCorrectDays() {
        // Use today = 2024-02-01 so Feb 29 2024 is still in the future (2024 is leap)
        val leapToday = LocalDate.of(2024, 2, 1)
        val result = daysUntilRecurring("2000-02-29", leapToday)
        assertNotNull(result)
        // Days from Feb 1 to Feb 29 2024 = 28
        assertEquals(28, result)
    }

    // ── daysUntilOneTime ──────────────────────────────────────────────────────

    @Test
    fun daysUntilOneTime_dateIsToday_returns0() {
        val result = daysUntilOneTime("2024-06-25", today)
        assertEquals(0, result)
    }

    @Test
    fun daysUntilOneTime_dateIsTomorrow_returns1() {
        val result = daysUntilOneTime("2024-06-26", today)
        assertEquals(1, result)
    }

    @Test
    fun daysUntilOneTime_dateWasYesterday_returnsNull() {
        // Past dates → null (no future occurrence for one-time events)
        val result = daysUntilOneTime("2024-06-24", today)
        assertNull(result)
    }

    @Test
    fun daysUntilOneTime_dateFarFuture_returnsPositiveDays() {
        val result = daysUntilOneTime("2025-01-01", today)
        assertNotNull(result)
        assertTrue("Expected > 0 days", result!! > 0)
    }

    @Test
    fun daysUntilOneTime_dateFarPast_returnsNull() {
        val result = daysUntilOneTime("2020-01-01", today)
        assertNull(result)
    }

    @Test
    fun daysUntilOneTime_invalidDateString_returnsNull() {
        assertNull(daysUntilOneTime("not-a-date", today))
    }

    @Test
    fun daysUntilOneTime_emptyString_returnsNull() {
        assertNull(daysUntilOneTime("", today))
    }

    @Test
    fun daysUntilOneTime_specificFutureDate_returnsExactDays() {
        // From 2024-06-25 to 2024-07-25 = 30 days
        val result = daysUntilOneTime("2024-07-25", today)
        assertEquals(30, result)
    }
}
