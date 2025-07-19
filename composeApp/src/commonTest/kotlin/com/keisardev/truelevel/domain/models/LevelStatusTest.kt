package com.keisardev.truelevel.domain.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class LevelStatusTest {
    
    @Test
    fun testLevelStatusFromAngles() {
        // Test LEVEL status (within ±1 degree)
        assertEquals(LevelStatus.LEVEL, LevelStatus.fromAngles(0.5, 0.8))
        assertEquals(LevelStatus.LEVEL, LevelStatus.fromAngles(-0.9, 0.3))
        assertEquals(LevelStatus.LEVEL, LevelStatus.fromAngles(1.0, 0.0))
        assertEquals(LevelStatus.LEVEL, LevelStatus.fromAngles(0.0, -1.0))
        
        // Test CLOSE status (1-5 degrees)
        assertEquals(LevelStatus.CLOSE, LevelStatus.fromAngles(1.1, 0.5))
        assertEquals(LevelStatus.CLOSE, LevelStatus.fromAngles(0.5, 3.0))
        assertEquals(LevelStatus.CLOSE, LevelStatus.fromAngles(-2.5, 1.5))
        assertEquals(LevelStatus.CLOSE, LevelStatus.fromAngles(5.0, 0.0))
        assertEquals(LevelStatus.CLOSE, LevelStatus.fromAngles(0.0, -5.0))
        
        // Test NOT_LEVEL status (>5 degrees)
        assertEquals(LevelStatus.NOT_LEVEL, LevelStatus.fromAngles(5.1, 0.0))
        assertEquals(LevelStatus.NOT_LEVEL, LevelStatus.fromAngles(0.0, 6.0))
        assertEquals(LevelStatus.NOT_LEVEL, LevelStatus.fromAngles(10.0, 2.0))
        assertEquals(LevelStatus.NOT_LEVEL, LevelStatus.fromAngles(-7.5, -3.0))
    }
    
    @Test
    fun testLevelStatusFromSingleAngle() {
        // Test LEVEL status
        assertEquals(LevelStatus.LEVEL, LevelStatus.fromAngle(0.5))
        assertEquals(LevelStatus.LEVEL, LevelStatus.fromAngle(-0.9))
        assertEquals(LevelStatus.LEVEL, LevelStatus.fromAngle(1.0))
        assertEquals(LevelStatus.LEVEL, LevelStatus.fromAngle(-1.0))
        
        // Test CLOSE status
        assertEquals(LevelStatus.CLOSE, LevelStatus.fromAngle(1.1))
        assertEquals(LevelStatus.CLOSE, LevelStatus.fromAngle(-3.0))
        assertEquals(LevelStatus.CLOSE, LevelStatus.fromAngle(5.0))
        assertEquals(LevelStatus.CLOSE, LevelStatus.fromAngle(-5.0))
        
        // Test NOT_LEVEL status
        assertEquals(LevelStatus.NOT_LEVEL, LevelStatus.fromAngle(5.1))
        assertEquals(LevelStatus.NOT_LEVEL, LevelStatus.fromAngle(-6.0))
        assertEquals(LevelStatus.NOT_LEVEL, LevelStatus.fromAngle(10.0))
        assertEquals(LevelStatus.NOT_LEVEL, LevelStatus.fromAngle(-15.0))
    }
    
    @Test
    fun testLevelStatusFromMaxAngle() {
        // Test LEVEL status
        assertEquals(LevelStatus.LEVEL, LevelStatus.fromMaxAngle(0.5))
        assertEquals(LevelStatus.LEVEL, LevelStatus.fromMaxAngle(1.0))
        
        // Test CLOSE status
        assertEquals(LevelStatus.CLOSE, LevelStatus.fromMaxAngle(1.1))
        assertEquals(LevelStatus.CLOSE, LevelStatus.fromMaxAngle(3.0))
        assertEquals(LevelStatus.CLOSE, LevelStatus.fromMaxAngle(5.0))
        
        // Test NOT_LEVEL status
        assertEquals(LevelStatus.NOT_LEVEL, LevelStatus.fromMaxAngle(5.1))
        assertEquals(LevelStatus.NOT_LEVEL, LevelStatus.fromMaxAngle(10.0))
    }
    
    @Test
    fun testLevelStatusBoundaryConditions() {
        // Test exact boundary values
        assertEquals(LevelStatus.LEVEL, LevelStatus.fromAngle(1.0))
        assertEquals(LevelStatus.CLOSE, LevelStatus.fromAngle(1.0000001)) // Just over 1 degree
        assertEquals(LevelStatus.CLOSE, LevelStatus.fromAngle(5.0))
        assertEquals(LevelStatus.NOT_LEVEL, LevelStatus.fromAngle(5.0000001)) // Just over 5 degrees
        
        // Test with dual angles at boundaries
        assertEquals(LevelStatus.LEVEL, LevelStatus.fromAngles(1.0, 0.5))
        assertEquals(LevelStatus.CLOSE, LevelStatus.fromAngles(1.0, 1.1)) // Max is 1.1
        assertEquals(LevelStatus.CLOSE, LevelStatus.fromAngles(5.0, 2.0)) // Max is 5.0
        assertEquals(LevelStatus.NOT_LEVEL, LevelStatus.fromAngles(5.1, 1.0)) // Max is 5.1
    }
    
    @Test
    fun testLevelStatusProperties() {
        // Test color hex values
        assertEquals("#4CAF50", LevelStatus.LEVEL.colorHex)
        assertEquals("#FF9800", LevelStatus.CLOSE.colorHex)
        assertEquals("#F44336", LevelStatus.NOT_LEVEL.colorHex)
        
        // Test display names
        assertEquals("Level", LevelStatus.LEVEL.displayName)
        assertEquals("Close", LevelStatus.CLOSE.displayName)
        assertEquals("Not Level", LevelStatus.NOT_LEVEL.displayName)
        
        // Test descriptions
        assertEquals("Device is level (±1°)", LevelStatus.LEVEL.description)
        assertEquals("Close to level (1-5°)", LevelStatus.CLOSE.description)
        assertEquals("Not level (>5°)", LevelStatus.NOT_LEVEL.description)
    }
    
    @Test
    fun testLevelStatusUsesMaxAngle() {
        // Verify that the status is determined by the maximum angle, not sum or average
        
        // Case where X is small but Y is large
        val status1 = LevelStatus.fromAngles(0.5, 6.0) // Max = 6.0
        assertEquals(LevelStatus.NOT_LEVEL, status1)
        
        // Case where Y is small but X is large
        val status2 = LevelStatus.fromAngles(7.0, 0.2) // Max = 7.0
        assertEquals(LevelStatus.NOT_LEVEL, status2)
        
        // Case where both are moderate but max determines CLOSE
        val status3 = LevelStatus.fromAngles(2.0, 3.0) // Max = 3.0
        assertEquals(LevelStatus.CLOSE, status3)
        
        // Case where both are small
        val status4 = LevelStatus.fromAngles(0.8, 0.9) // Max = 0.9
        assertEquals(LevelStatus.LEVEL, status4)
    }
    
    @Test
    fun testNegativeAnglesHandledCorrectly() {
        // Verify that negative angles are handled using absolute values
        assertEquals(LevelStatus.LEVEL, LevelStatus.fromAngles(-0.5, -0.8))
        assertEquals(LevelStatus.CLOSE, LevelStatus.fromAngles(-2.0, -3.0))
        assertEquals(LevelStatus.NOT_LEVEL, LevelStatus.fromAngles(-6.0, -7.0))
        
        // Mixed positive and negative
        assertEquals(LevelStatus.CLOSE, LevelStatus.fromAngles(-2.0, 3.0))
        assertEquals(LevelStatus.NOT_LEVEL, LevelStatus.fromAngles(6.0, -2.0))
    }
    
    @Test
    fun testZeroAngles() {
        // Test perfect level (zero angles)
        assertEquals(LevelStatus.LEVEL, LevelStatus.fromAngles(0.0, 0.0))
        assertEquals(LevelStatus.LEVEL, LevelStatus.fromAngle(0.0))
        assertEquals(LevelStatus.LEVEL, LevelStatus.fromMaxAngle(0.0))
    }
}