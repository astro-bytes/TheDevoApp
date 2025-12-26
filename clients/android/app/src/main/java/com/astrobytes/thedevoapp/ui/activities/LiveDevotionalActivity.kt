package com.astrobytes.thedevoapp.ui.activities

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.astrobytes.thedevoapp.ui.composables.LiveDevotionalRootView
import com.astrobytes.thedevoapp.ui.theme.TheDevoAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LiveDevotionalActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.requestWindowFeature(Window.FEATURE_NO_TITLE)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            TheDevoAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LiveDevotionalRootView(Modifier.padding(innerPadding))
                }
            }
        }
    }
}