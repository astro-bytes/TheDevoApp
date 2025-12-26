package com.astrobytes.thedevoapp.ui.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.astrobytes.thedevoapp.models.Devotional
import com.astrobytes.thedevoapp.repositories.core.CoreDevotionalListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
fun DevotionalsTab(
    modifier: Modifier = Modifier,
    model: DevotionalsTabViewModel = hiltViewModel()
) {
    val devotionals by model.devotionalListRepository.value.collectAsState(listOf())

    Text(devotionals.size.toString())
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(devotionals) { devotional ->
            DevotionalItem(devotional)
        }
    }
}

@Composable
fun DevotionalItem(devotional: Devotional) {
    Card(
        modifier = Modifier.fillMaxWidth(),
//        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = devotional.title)//, style = MaterialTheme.typography.h6)
            devotional.summary?.let {
                Text(text = it)//, style = MaterialTheme.typography.body2)
            }
        }
    }
}

@HiltViewModel
class DevotionalsTabViewModel @Inject constructor(
    val devotionalListRepository: CoreDevotionalListRepository
) : ViewModel() {

    init { refresh() }
    // Optionally, expose a refresh function
    fun refresh() {
        viewModelScope.launch {
            devotionalListRepository.refresh()
        }
    }
}