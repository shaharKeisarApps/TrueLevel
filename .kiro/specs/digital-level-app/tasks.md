# Implementation Plan

- [x] 1. Set up project structure and core domain models
  - Create clean architecture folder structure following KMP best practices
  - Define core domain models for measurements, sensor data, and calibration
  - Implement basic data classes with proper serialization support
  - _Requirements: 6.1, 6.2_

- [-] 2. Implement sensor abstraction layer foundation
  - [x] 2.1 Create cross-platform sensor interfaces and data models
    - Define SensorManager interface with platform-agnostic methods
    - Create SensorData and related data classes for sensor readings
    - Implement sensor accuracy and status enumerations
    - _Requirements: 6.1, 6.2, 6.3_

  - [-] 2.2 Implement Android sensor platform integration
    - Create Android-specific SensorManager implementation using Android Sensor API
    - Implement accelerometer and gyroscope data collection
    - Add sensor availability detection and permission handling
    - Write unit tests for Android sensor integration
    - _Requirements: 6.1, 6.4, 6.5_

  - [ ] 2.3 Implement iOS sensor platform integration
    - Create iOS-specific SensorManager implementation using Core Motion
    - Implement device motion data collection and processing
    - Add iOS-specific sensor availability and permission handling
    - Write unit tests for iOS sensor integration
    - _Requirements: 6.1, 6.4, 6.5_

- [ ] 3. Create measurement processing and calculation engine
  - [ ] 3.1 Implement angle calculation algorithms
    - Create functions to convert accelerometer data to pitch/roll angles
    - Implement low-pass filtering for sensor noise reduction
    - Add calibration offset application to raw measurements
    - Write comprehensive unit tests for angle calculations
    - _Requirements: 1.1, 1.2, 8.2_

  - [ ] 3.2 Implement level status determination logic
    - Create LevelStatus calculation based on angle thresholds
    - Implement color-coded status indicators (green/yellow/red)
    - Add precision formatting for angle display (0.1-degree accuracy)
    - Write unit tests for level status logic
    - _Requirements: 1.3, 1.4, 1.5_

- [ ] 4. Build MVI architecture foundation
  - [ ] 4.1 Create UI state management classes
    - Implement LevelUiState data class with all required properties
    - Create LevelIntent sealed class for user actions
    - Define state transformation functions for reactive updates
    - Write unit tests for state management logic
    - _Requirements: 5.1, 5.2_

  - [ ] 4.2 Implement ViewModel with sensor integration
    - Create LevelViewModel extending platform-appropriate base class
    - Integrate sensor data flow with UI state updates
    - Implement intent handling for user actions
    - Add proper lifecycle management for sensor operations
    - Write unit tests for ViewModel behavior
    - _Requirements: 1.2, 5.1, 9.3_

- [ ] 5. Create core UI components and theme system
  - [ ] 5.1 Implement Material Design 3 theme and color system
    - Create LevelColors object with professional color palette
    - Implement dark/light theme support with high contrast options
    - Define typography scale for measurement display hierarchy
    - Add accessibility-compliant color combinations
    - _Requirements: 7.1, 7.2, 7.3, 7.4_

  - [ ] 5.2 Build digital inclinometer display component
    - Create DigitalInclinometerDisplay composable with large angle text
    - Implement color-coded level status indicators
    - Add secondary angle display for Y-axis measurements
    - Include hold status visual indication
    - Write Compose UI tests for component behavior
    - _Requirements: 1.1, 1.3, 1.4, 1.5, 4.2_

  - [ ] 5.3 Build bubble level display component
    - Create BubbleLevelDisplay composable with realistic vial interface
    - Implement AnimatedBubble component with smooth movement
    - Add level markers and center zone indicators
    - Create LevelVial background component with traditional styling
    - Write Compose UI tests for bubble animation
    - _Requirements: 2.1, 2.2, 2.3, 2.4_

- [ ] 6. Implement measurement mode switching and navigation
  - [ ] 6.1 Create mode switching UI controls
    - Implement horizontal swipe gesture detection for mode changes
    - Create mode indicator tabs or buttons for manual switching
    - Add smooth transition animations between modes
    - Implement mode preference persistence
    - _Requirements: 5.3, 5.4, 5.5_

  - [ ] 6.2 Build main measurement screen with mode integration
    - Create MainMeasurementScreen composable integrating all modes
    - Implement seamless mode switching without measurement interruption
    - Add proper state preservation during mode changes
    - Include error handling and loading states
    - Write integration tests for mode switching
    - _Requirements: 5.1, 5.2, 5.5_

- [ ] 7. Implement hold/freeze functionality
  - [ ] 7.1 Create hold mechanism in ViewModel
    - Implement hold state management in LevelViewModel
    - Add gesture detection for tap-and-hold and double-tap
    - Create hold activation/deactivation logic
    - Ensure frozen values persist without drift
    - Write unit tests for hold functionality
    - _Requirements: 4.1, 4.3, 4.4, 4.5_

  - [ ] 7.2 Add hold visual indicators to UI components
    - Update DigitalInclinometerDisplay with hold status indication
    - Add hold indicator to BubbleLevelDisplay
    - Implement visual feedback for hold activation
    - Create hold button for manual activation
    - Write UI tests for hold visual states
    - _Requirements: 4.2, 4.3_

- [ ] 8. Create data persistence and logging system
  - [ ] 8.1 Implement measurement repository and storage
    - Create MeasurementRepository interface and implementation
    - Implement local database storage for measurement history
    - Add measurement logging with timestamp and mode information
    - Create storage space management and cleanup functionality
    - Write unit tests for repository operations
    - _Requirements: 3.1, 3.3, 3.4_

  - [ ] 8.2 Build measurement history and export features
    - Create measurement history display screen
    - Implement CSV and PDF export functionality
    - Add filtering and search capabilities for measurement history
    - Create export format selection and sharing options
    - Write integration tests for export functionality
    - _Requirements: 3.2, 3.5_

- [ ] 9. Implement calibration system
  - [ ] 9.1 Create calibration data models and storage
    - Implement CalibrationData model with offset values
    - Create SettingsRepository for calibration persistence
    - Add device orientation detection for calibration context
    - Implement calibration profile management
    - Write unit tests for calibration data handling
    - _Requirements: 8.1, 8.2, 8.4_

  - [ ] 9.2 Build calibration UI and workflow
    - Create step-by-step calibration screen with guidance
    - Implement calibration process with visual feedback
    - Add calibration validation and error handling
    - Create calibration reminder system for outdated data
    - Write UI tests for calibration workflow
    - _Requirements: 8.1, 8.3, 8.5_

- [ ] 10. Add battery optimization and performance features
  - [ ] 10.1 Implement adaptive sensor sampling
    - Create dynamic sensor polling rate adjustment
    - Implement stationary device detection for reduced updates
    - Add battery level monitoring and power-saving modes
    - Create background sensor operation management
    - Write performance tests for battery optimization
    - _Requirements: 9.1, 9.2, 9.3, 9.4_

  - [ ] 10.2 Add performance monitoring and optimization
    - Implement frame rate monitoring for smooth 60fps updates
    - Add memory usage optimization for continuous operation
    - Create sensor data buffering and processing optimization
    - Implement proper lifecycle management for all components
    - Write performance benchmarks and tests
    - _Requirements: 1.2, 9.5_

- [ ] 11. Implement comprehensive error handling
  - [ ] 11.1 Create error handling system
    - Implement LevelError sealed class with all error types
    - Add error recovery strategies for each error type
    - Create user-friendly error messages and recovery guidance
    - Implement graceful degradation for missing sensors
    - Write unit tests for error handling scenarios
    - _Requirements: 10.1, 10.2, 10.5_

  - [ ] 11.2 Add reliability and crash recovery features
    - Implement app state recovery after crashes or restarts
    - Add sensor reliability monitoring and fallback mechanisms
    - Create offline operation support with local storage
    - Implement measurement uncertainty indicators
    - Write integration tests for reliability features
    - _Requirements: 10.3, 10.4, 10.5_

- [ ] 12. Enhance accessibility and user experience
  - [ ] 12.1 Implement accessibility features
    - Add comprehensive content descriptions for screen readers
    - Implement voice announcements for level status changes
    - Create haptic feedback for level achievement
    - Add support for large text and high contrast modes
    - Write accessibility tests and validation
    - _Requirements: 7.4, 2.4_

  - [ ] 12.2 Add advanced UI/UX features
    - Implement responsive design for different screen sizes
    - Add gesture-based interactions and shortcuts
    - Create smooth animations and transitions
    - Implement professional visual polish and micro-interactions
    - Write UI tests for responsive behavior
    - _Requirements: 7.5, 5.5_

- [ ] 13. Create comprehensive testing suite
  - [ ] 13.1 Implement unit tests for all core components
    - Write unit tests for domain models and business logic
    - Create tests for sensor data processing and calculations
    - Add tests for repository implementations and data handling
    - Implement ViewModel and state management tests
    - Achieve high code coverage for critical components
    - _Requirements: All requirements validation_

  - [ ] 13.2 Add integration and UI tests
    - Create integration tests for sensor-to-UI data flow
    - Implement Compose UI tests for all components
    - Add cross-platform behavior validation tests
    - Create performance and battery usage tests
    - Write accessibility compliance tests
    - _Requirements: All requirements validation_

- [ ] 14. Final integration and polish
  - [ ] 14.1 Integrate all features and perform end-to-end testing
    - Connect all implemented components into cohesive application
    - Perform comprehensive end-to-end testing across all features
    - Validate all requirements are met with automated tests
    - Fix any integration issues and edge cases
    - _Requirements: All requirements_

  - [ ] 14.2 Apply final polish and optimization
    - Optimize app performance and memory usage
    - Polish UI animations and transitions
    - Validate professional-grade user experience
    - Prepare app for production deployment
    - Create final validation against all acceptance criteria
    - _Requirements: All requirements_