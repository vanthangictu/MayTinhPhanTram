package com.example.calculator

import org.junit.Assert.*
import org.junit.Test

class PercentCalculatorTest {

    @Test
    fun ofPercent() {
        assertEquals("30,00", calculate(PercentMode.OF, 200.0, 15.0)?.main)
    }

    @Test
    fun increase() {
        val r = calculate(PercentMode.INCREASE, 100.0, 20.0)
        assertEquals("120,00", r?.main)
        assertEquals(1, r?.sign)
    }

    @Test
    fun decrease() {
        val r = calculate(PercentMode.DECREASE, 100.0, 20.0)
        assertEquals("80,00", r?.main)
        assertEquals(-1, r?.sign)
    }

    @Test
    fun percentChange() {
        assertEquals("50,00%", calculate(PercentMode.CHANGE, 50.0, 75.0)?.main)
    }

    @Test
    fun partOf() {
        assertEquals("12,50%", calculate(PercentMode.PART, 25.0, 200.0)?.main)
    }

    @Test
    fun roundingToInteger() {
        assertEquals("13%", calculate(PercentMode.PART, 25.0, 200.0, 0)?.main)
    }

    @Test
    fun divideByZeroReturnsNull() {
        assertNull(calculate(PercentMode.PART, 10.0, 0.0))
        assertNull(calculate(PercentMode.CHANGE, 0.0, 10.0))
    }
}
