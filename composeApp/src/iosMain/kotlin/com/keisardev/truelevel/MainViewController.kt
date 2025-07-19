package com.keisardev.truelevel

import androidx.compose.ui.window.ComposeUIViewController
import com.keisardev.truelevel.di.PlatformModule

fun MainViewController() = ComposeUIViewController { 
    // Initialize platform-specific dependencies
    PlatformModule.initialize()
    
    App() 
}