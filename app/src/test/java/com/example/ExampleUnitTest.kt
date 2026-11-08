package com.example

import com.example.ui.util.NumberUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testCurrencyFormatting() {
        val formatted = NumberUtils.formatIqd(25000.0)
        assertEquals("25,000 د.ع", formatted)

        val parcel100 = 100 * 250.0
        assertEquals("25,000 د.ع", NumberUtils.formatIqd(parcel100))
    }

    @Test
    fun testMonthCalculations() {
        val currentMonth = "2026-11"
        val next = NumberUtils.getNextMonthKey(currentMonth)
        val prev = NumberUtils.getPreviousMonthKey(currentMonth)

        assertEquals("2026-12", next)
        assertEquals("2026-10", prev)

        val kurdishName = NumberUtils.getKurdishMonthName(currentMonth)
        assertTrue(kurdishName.contains("تشرینی دووەم"))
        assertTrue(kurdishName.contains("2026"))
    }
}
