# Domain Models

This directory contains the core domain models for the TrueLevel application.

## Models

### LevelMeasurement
- Core measurement data model representing a level measurement
- Contains angle measurements, timestamp, accuracy, and level status
- Provides factory methods for creating measurements with calculated status

### LevelStatus
- Enumeration for level status based on angle thresholds
- LEVEL: Within ±1 degree
- CLOSE: 1-5 degrees from level  
- NOT_LEVEL: >5 degrees from level

### SensorData
- Raw sensor data from accelerometer and gyroscope
- Includes timestamp and utility methods for data validation
- Supports optional gyroscope data

### CalibrationData
- Calibration data for sensor offset correction
- Includes device orientation context
- Provides factory methods for creating calibration data

### SensorAccuracy
- Enumeration for sensor accuracy levels
- UNRELIABLE, LOW, MEDIUM, HIGH

### MeasurementMode
- Available measurement modes
- DIGITAL_INCLINOMETER, BUBBLE_LEVEL, ANGLE_DISPLAY

### DeviceOrientation
- Device orientation for calibration context
- PORTRAIT, LANDSCAPE_LEFT, LANDSCAPE_RIGHT, PORTRAIT_UPSIDE_DOWN

All models are serializable using kotlinx-serialization and use kotlinx-datetime for timestamp handling.