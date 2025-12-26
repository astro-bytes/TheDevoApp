package com.astrobytes.thedevoapp.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.astrobytes.thedevoapp.ui.theme.TheDevoAppTheme
import com.astrobytes.thedevoapp.usecases.RecordTap
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LiveDevotionalActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.requestWindowFeature(Window.FEATURE_NO_TITLE)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            TheDevoAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TapView(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun TapView(
    modifier: Modifier = Modifier,
    model: TapViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    FullscreenDarkSystemUi()
    DoubleBackToExit()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current
            ) {
                model.onTap(context)
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Tap when you hear something you like",
            color = Color.DarkGray
        )
    }
}

@Composable
fun FullscreenDarkSystemUi() {
    val view = LocalView.current

    SideEffect {
        val window = (view.context as Activity).window

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val controller = WindowInsetsControllerCompat(window, view)
        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = false

        controller.hide(
            WindowInsetsCompat.Type.statusBars() or
                    WindowInsetsCompat.Type.navigationBars()
        )

        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

@Composable
fun DoubleBackToExit(
    exitDelayMillis: Long = 2000L
) {
    var lastBackPressTime by remember { mutableLongStateOf(0L) }
    val context = LocalContext.current

    BackHandler {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastBackPressTime < exitDelayMillis) {
            (context as Activity).finish()
        } else {
            lastBackPressTime = currentTime
            Toast
                .makeText(context, "Tap back again to exit", Toast.LENGTH_SHORT)
                .show()
        }
    }
}

@HiltViewModel
class TapViewModel @Inject constructor(
    private val recordTap: RecordTap
): ViewModel() {
    fun onTap(context: Context) {
        viewModelScope.launch {
            runCatching { recordTap.execute() }
                .onFailure {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
        }
    }
}