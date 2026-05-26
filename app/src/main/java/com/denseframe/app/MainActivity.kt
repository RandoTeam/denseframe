package com.denseframe.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.denseframe.app.ui.DenseFrameApp
import com.denseframe.designsystem.DenseFrameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DenseFrameTheme {
                DenseFrameApp()
            }
        }
    }
}
