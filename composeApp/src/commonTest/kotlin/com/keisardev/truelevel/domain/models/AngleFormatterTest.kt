package com.keisardev.truelevel.domain.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class AngleFormatterTest {
    
    @Test
    fun testFormatToTenthDegree() {
        // Test basic 0.1-degree precision formatting
        assertEquals("0.0", AngleFormatter.formatToTenthDegree(0.0))
        assertEquals("1.0", AngleFormatter.formatToTenthDegree(1.0))
        assertEquals("2.5", AngleFormatter.formatToTenthDegree(2.5))
        assertEquals("-1.5", AngleFormatter.formatToTenthDegree(-1.5))
        
        // Test that it produces reasonable output for various inputs
        val result1 = AngleFormatter.formatToTenthDegree(1.23)
        assertTrue(result1.contains("."))
        assertTrue(result1.length <= 4) // Should be like "1.2" or "-1.2"
    }
    
    @Test
    fun testFormatAngleWithDecimalPlaces() {
        val angle = 5.0
        
        assertEquals("5", AngleFormatter.formatAngle(angle, 0))
        assertEquals("5.0", AngleFormatter.formatAngle(angle, 1))
        
        // Test that formatting works for basic cases
        val result1 = AngleFormatter.formatAngle(1.5, 1)
        assertTrue(result1.contains("1.5") || result1.contains("1.5"))
        
        val result2 = AngleFormatter.formatAngle(-2.3, 1)
        assertTrue(result2.contains("-2.3") || result2.contains("-2.3"))
    }
    
    @Test
    fun testFormatWithDegreeSymbol() {
        assertEquals("0.0°", AngleFormatter.formatWithDegreeSymbol(0.0))
        assertEquals("1.5°", AngleFormatter.formatWithDegreeSymbol(1.5))
        assertEquals("-2.3°", AngleFormatter.formatWithDegreeSymbol(-2.3))
        assertEquals("10.25°", AngleFormatter.formatWithDegreeSymbol(10.25, 2))
    }
    
    @Test
    fun testFormatWithSign() {
        assertEquals("0.0°", AngleFormatter.formatWithSign(0.0))
        assertEquals("+1.5°", AngleFormatter.formatWithSign(1.5))
        assertEquals("-2.3°", AngleFormatter.formatWithSign(-2.3))
        
        // Test that sign formatting works for basic cases
        val result1 = AngleFormatter.formatWithSign(5.0)
        assertTrue(result1.startsWith("+"))
        assertTrue(result1.endsWith("°"))
        
        val result2 = AngleFormatter.formatWithSign(-3.0)
        assertTrue(result2.startsWith("-"))
        assertTrue(result2.endsWith("°"))
    }
    
    @Test
    fun testFormatDualAxis() {
        assertEquals("X: 1.5°, Y: -2.3°", AngleFormatter.formatDualAxis(1.5, -2.3))
        assertEquals("X: 0.0°, Y: 0.0°", AngleFormatter.formatDualAxis(0.0, 0.0))
        assertEquals("X: 10.25°, Y: 5.75°", AngleFormatter.formatDualAxis(10.25, 5.75, 2))
    }
    
    @Test
    fun testFormatForAccessibility() {
        assertEquals("0.0 degrees", AngleFormatter.formatForAccessibility(0.0))
        assertEquals("positive 1.0 degree", AngleFormatter.formatForAccessibility(1.0))
        assertEquals("negative 1.0 degree", AngleFormatter.formatForAccessibility(-1.0))
        assertEquals("positive 2.5 degrees", AngleFormatter.formatForAccessibility(2.5))
        assertEquals("negative 3.0 degrees", AngleFormatter.formatForAccessibility(-3.0))
        assertEquals("positive 1.25 degrees", AngleFormatter.formatForAccessibility(1.25, 2))
    }
    
    @Test
    fun testFormatStatusWithAngle() {
        assertEquals("Level (1.5°)", AngleFormatter.formatStatusWithAngle(LevelStatus.LEVEL, 1.5))
        assertEquals("Close (3.2°)", AngleFormatter.formatStatusWithAngle(LevelStatus.CLOSE, 3.2))
        assertEquals("Not Level (7.8°)", AngleFormatter.formatStatusWithAngle(LevelStatus.NOT_LEVEL, 7.8))
    }
    
    @Test
    fun testIsEffectivelyZero() {
        assertTrue(AngleFormatter.isEffectivelyZero(0.0))
        assertTrue(AngleFormatter.isEffectivelyZero(0.04))
        assertTrue(AngleFormatter.isEffectivelyZero(-0.03))
        assertFalse(AngleFormatter.isEffectivelyZero(0.06))
        assertFalse(AngleFormatter.isEffectivelyZero(-0.1))
        
        // Test with custom threshold
        assertTrue(AngleFormatter.isEffectivelyZero(0.08, 0.1))
        assertFalse(AngleFormatter.isEffectivelyZero(0.12, 0.1))
    }
    
    @Test
    fun testFormatWithZeroThreshold() {
        // Test basic zero threshold functionality
        assertEquals("0.0", AngleFormatter.formatWithZeroThreshold(0.03))
        assertEquals("0.0", AngleFormatter.formatWithZeroThreshold(-0.04))
        assertEquals("1.5", AngleFormatter.formatWithZeroThreshold(1.5))
        
        // Test that small values become zero
        val result1 = AngleFormatter.formatWithZeroThreshold(0.01)
        assertEquals("0.0", result1)
        
        // Test that larger values are preserved
        val result2 = AngleFormatter.formatWithZeroThreshold(0.1)
        assertTrue(result2.contains("0.1") || result2.contains("0.1"))
    }
    
    @Test
    fun testFormatMeasurementDisplay() {
        val measurement = LevelMeasurement.create(
            angleX = 1.5,
            angleY = -2.3,
            timestamp = 1234567890L,
            accuracy = SensorAccuracy.HIGH
        )
        
        val display = AngleFormatter.formatMeasurementDisplay(measurement)
        
        assertEquals("1.5°", display.primaryAngle)
        assertEquals("-2.3°", display.secondaryAngle)
        assertEquals("Close", display.statusText)
        assertEquals("#FF9800", display.statusColor)
        assertEquals("X: 1.5°, Y: -2.3°", display.dualAxisText)
        assertTrue(display.accessibilityText.contains("positive 1.5 degree"))
        assertTrue(display.accessibilityText.contains("negative 2.3 degrees"))
        assertTrue(display.accessibilityText.contains("Close to level"))
    }
    
    @Test
    fun testRoundingConsistency() {
        // Test that rounding is consistent across different methods
        val angle = 1.25
        
        val formatted1 = AngleFormatter.formatToTenthDegree(angle)
        val formatted2 = AngleFormatter.formatAngle(angle, 1)
        val formatted3 = AngleFormatter.formatWithDegreeSymbol(angle, 1).removeSuffix("°")
        
        assertEquals(formatted1, formatted2)
        assertEquals(formatted2, formatted3)
    }
    
    @Test
    fun testEdgeCases() {
        // Test very small numbers
        assertEquals("0.0", AngleFormatter.formatToTenthDegree(0.000001))
        assertEquals("0.0", AngleFormatter.formatToTenthDegree(-0.000001))
        
        // Test that large numbers work
        val result1 = AngleFormatter.formatToTenthDegree(999.0)
        assertTrue(result1.contains("999"))
        
        // Test that basic rounding works
        val result2 = AngleFormatter.formatToTenthDegree(1.0)
        assertEquals("1.0", result2)
    }
}