package com.astrobytes.thedevoapp.ui.composables

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.astrobytes.thedevoapp.ui.Information
import com.astrobytes.thedevoapp.ui.activities.LiveDevotionalActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class TabRoute(val route: String) {
    object Testing : TabRoute("testing")
    object Devotionals : TabRoute("devotionals")
}

@Composable
fun TabView(
    modifier: Modifier = Modifier,
    model: TabViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val selectedTab by model.selectedTab
    val navController = rememberNavController()

    LaunchedEffect(context) {
        model.openLiveDevotional.collect {
            context.startActivity(Intent(context, LiveDevotionalActivity::class.java))
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            PrimaryTabRow(
                selectedTab,
                Modifier.navigationBarsPadding()
            ) {
                model.tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = {
                            model.onTabSelected(index)

                            navController.navigate(
                                when (index) {
                                    0 -> TabRoute.Testing.route
                                    else -> TabRoute.Devotionals.route
                                }
                            ) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        text = { Text(title) }
                    )
                }
            }

        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TabRoute.Testing.route
        ) {
            composable(TabRoute.Testing.route) {
                Box(modifier.padding(innerPadding)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Information()
                        Button(onClick = model::onOpenLiveDevotional) {
                            Text("Open Live Devotional")
                        }
                    }
                }
            }

            composable(TabRoute.Devotionals.route) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    DevotionalsTabView({
                        navController.navigate("devotional/${it.id}")
                    })
                }
            }

            composable(
                route = "devotional/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id")
                id?.let { DevotionalDetailView() }
            }
        }
    }
}

@HiltViewModel
class TabViewModel @Inject constructor() : ViewModel() {
    private val _openLiveDevotional = MutableSharedFlow<Unit>()
    val openLiveDevotional: SharedFlow<Unit> = _openLiveDevotional
    val tabs = listOf("Testing", "Devotionals")
    val selectedTab: MutableState<Int> = mutableIntStateOf(1)

    fun onOpenLiveDevotional() {
        viewModelScope.launch {
            _openLiveDevotional.emit(Unit)
        }
    }

    fun onTabSelected(index: Int) {
        selectedTab.value = index
    }
}