package com.astrobytes.thedevoapp.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.astrobytes.thedevoapp.models.Devotional
import com.astrobytes.thedevoapp.models.Music
import com.astrobytes.thedevoapp.models.Person
import com.astrobytes.thedevoapp.repositories.DevotionalRepositoryFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
fun DevotionalDetailView(
    modifier: Modifier = Modifier,
    model: DevotionalDetailViewModel = hiltViewModel()
) {
    val devotional by model.devotional.collectAsState(null)

    devotional?.let { devotional ->
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = devotional.title,
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = "Speaker: ${devotional.speaker.fullName}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            devotional.scripture?.let { scripture ->
                item {
                    Card {
                        Column(Modifier.padding(16.dp)) {
                            SectionHeader("Scripture")
                            Text(
                                text = "${scripture.book} ${scripture.chapter}:${
                                    scripture.verses.joinToString(
                                        ","
                                    )
                                }",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Read by ${scripture.reader.fullName}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            item {
                SectionHeader("Participants")
                PersonRow("Invocation", devotional.invocation)
                PersonRow("Benediction", devotional.benediction)
            }

            item {
                SectionHeader("Music")
                MusicRow("Prelude", devotional.prelude)
                MusicRow("Introit", devotional.introit)
                MusicRow("Postlude", devotional.postlude)
                MusicRow("Recessional", devotional.recessional)
            }

            devotional.topics.takeIf { it.isNotEmpty() }?.let {
                item {
                    SectionHeader("Topics")
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        it.forEach { topic ->
                            AssistChip(onClick = {}, label = { Text(topic) })
                        }
                    }
                }
            }

            devotional.quotes.takeIf { it.isNotEmpty() }?.let { quotes ->
                item {
                    SectionHeader("Quotes")
                }
                items(quotes) { quote ->
                    QuoteView(quote)
                }
            }

            devotional.summary?.let {
                item {
                    SectionHeader("Summary")
                    Text(text = it)
                }
            }

            devotional.transcript?.let {
                item {
                    SectionHeader("Transcript")
                    Text(text = it)
                }
            }
        }
    }
}

@HiltViewModel
class DevotionalDetailViewModel @Inject constructor(
    devotionalFactory: DevotionalRepositoryFactory,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val id: Int = checkNotNull(savedStateHandle["id"])
    private val devotionalRepository = devotionalFactory.create(id)
    val devotional: Flow<Devotional?> = devotionalRepository.value

    init {
        viewModelScope.launch {
            devotionalRepository.refresh()
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun PersonRow(label: String, person: Person?) {
    person ?: return
    Text(
        text = "$label: ${person.fullName}",
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
private fun MusicRow(label: String, music: Music?) {
    music ?: return

    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = music.title,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Performed by ${music.performer.fullName}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}