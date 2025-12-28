package com.astrobytes.thedevoapp.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.astrobytes.thedevoapp.models.Devotional
import com.astrobytes.thedevoapp.models.ViewState
import com.astrobytes.thedevoapp.repositories.DevotionalListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
fun DevotionalsTabView(
    onSelection: (Devotional) -> Unit,
    modifier: Modifier = Modifier,
    model: DevotionalsTabViewModel = hiltViewModel()
) {
    val state by model.state

    when (val s = state) {
        ViewState.Loading -> {
            LoadingView()
        }

        is ViewState.Ready -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(s.data, key = { it.id }) {
                    DevotionalListItem(it, onClick = { onSelection(it) })
                }
            }
        }

        is ViewState.Error -> {
            Text(s.message)
        }
    }
}

@HiltViewModel
class DevotionalsTabViewModel @Inject constructor(
    private val devotionalListRepository: DevotionalListRepository
) : ViewModel() {

    val state: MutableState<ViewState<List<Devotional>>> = mutableStateOf(ViewState.Loading)

    init {
        listenToDevotionals()
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            devotionalListRepository.refresh()
        }
    }

    private fun listenToDevotionals() {
        viewModelScope.launch {
            devotionalListRepository.value.collect { devotionals ->
                state.value = ViewState.Ready(devotionals)
            }
        }
    }
}

@Composable
private fun DevotionalListItem(
    devotional: Devotional,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = devotional.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Speaker: ${devotional.speaker.fullName}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            devotional.scripture?.let {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${it.book} ${it.chapter}:${it.verses.joinToString(",")}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            devotional.topics.takeIf { it.isNotEmpty() }?.let { topics ->
                Spacer(Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    topics.take(3).forEach {
                        AssistChip(
                            onClick = {},
                            label = { Text(it) }
                        )
                    }
                }
            }

            devotional.summary?.let {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}