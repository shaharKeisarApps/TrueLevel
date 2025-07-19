package com.keisardev.truelevel.domain.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class AngleUtilsTest {
    
    @Test
    fun testPitchCalculation() {
        // Test level position (0 degrees)
        val pitch = AngleUtils.calculatePitch(0f, 0f, 9.8f)
        assertEquals(0.0, pitch, 0.1)
        
        // Test 45 degree tilt
        val pitch45 = AngleUtils.calculatePitch(9.8f, 0f, 9.8f)
        assertEquals(45.0, pitch45, 1.0)
    }
    
    @Test
    fun testRollCalculation() {
        // Test level position (0 degrees)
        val roll = AngleUtils.calculateRoll(0f, 0f, 9.8f)
        assertEquals(0.0, roll, 0.1)
        
        // Test 45 degree tilt
        val roll45 = AngleUtils.calculateRoll(0f, 9.8f, 9.8f)
        assertEquals(45.0, roll45, 1.0)
    }
    
    @Test
    fun testCalibrationApplication() {
        val angle = 5.0
        val offset = 2.0
        val calibrated = AngleUtils.applyCalibration(angle, offset)
        assertEquals(3.0, calibrated)
    }
    
    @Test
    fun testAngleNormalization() {
        assertEquals(10.0, AngleUtils.normalizeAngle(10.0))
        assertEquals(-170.0, AngleUtils.normalizeAngle(190.0))
        assertEquals(170.0, AngleUtils.normalizeAngle(-190.0))
        assertEquals(0.0, AngleUtils.normalizeAngle(360.0))
    }
    
    @Test
    fun testAngleFormatting() {
        assertEquals("5.0", AngleUtils.formatAngle(5.0, 1))
        assertEquals("5.12", AngleUtils.formatAngle(5.123, 2))
        assertEquals("5", AngleUtils.formatAngle(5.0, 0))
    }
    
    @Test
    fun testIsLevel() {
        assertTrue(AngleUtils.isLevel(0.5))
        assertTrue(AngleUtils.isLevel(-0.8))
        assertFalse(AngleUtils.isLevel(1.5))
        assertFalse(AngleUtils.isLevel(-2.0))
    }
    
    @Test
    fun testMaxAngle() {
        assertEquals(3.0, AngleUtils.maxAngle(2.0, 3.0))
        assertEquals(3.0, AngleUtils.maxAngle(-3.0, 2.0))
        assertEquals(5.0, AngleUtils.maxAngle(-5.0, -2.0))
    }
    
    @Test
    fun testTiltMagnitudeCalculation() {
        // Test Pythagorean theorem for tilt magnitude
        assertEquals(5.0, AngleUtils.calculateTiltMagnitude(3.0, 4.0), 0.01)
        assertEquals(0.0, AngleUtils.calculateTiltMagnitude(0.0, 0.0), 0.01)
        assertEquals(7.07, AngleUtils.calculateTiltMagnitude(5.0, 5.0), 0.01)
    }
    
    @Test
    fun testDegreesToRadians() {
        assertEquals(0.0, AngleUtils.degreesToRadians(0.0), 0.01)
        assertEquals(kotlin.math.PI / 2, AngleUtils.degreesToRadians(90.0), 0.01)
        assertEquals(kotlin.math.PI, AngleUtils.degreesToRadians(180.0), 0.01)
    }
    
    @Test
    fun testRadiansToDegrees() {
        assertEquals(0.0, AngleUtils.radiansToDegrees(0.0), 0.01)
        assertEquals(90.0, AngleUtils.radiansToDegrees(kotlin.math.PI / 2), 0.01)
        assertEquals(180.0, AngleUtils.radiansToDegrees(kotlin.math.PI), 0.01)
    }
    
    @Test
    fun testLowPassFilter() {
        // Test filter behavior
        val current = 10.0
        val previous = 0.0
        val alpha = 0.8
        
        val filtered = AngleUtils.lowPassFilter(current, previous, alpha)
        assertEquals(2.0, filtered, 0.01) // 0.8 * 0 + 0.2 * 10 = 2.0
        
        // Test with different alpha values
        assertEquals(5.0, AngleUtils.lowPassFilter(current, previous, 0.5), 0.01)
        assertEquals(1.0, AngleUtils.lowPassFilter(current, previous, 0.9), 0.01)
    }
    
    @Test
    fun testAnglesEqual() {
        assertTrue(AngleUtils.anglesEqual(5.0, 5.05, 0.1))
        assertTrue(AngleUtils.anglesEqual(5.0, 4.95, 0.1))
        assertFalse(AngleUtils.anglesEqual(5.0, 5.2, 0.1))
        assertFalse(AngleUtils.anglesEqual(5.0, 4.8, 0.1))
        
        // Test with custom tolerance
        assertTrue(AngleUtils.anglesEqual(5.0, 5.5, 0.6))
        assertFalse(AngleUtils.anglesEqual(5.0, 5.5, 0.4))
    }
}