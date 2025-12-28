package com.astrobytes.thedevoapp.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.astrobytes.thedevoapp.models.Quote
import com.astrobytes.thedevoapp.models.ViewState
import com.astrobytes.thedevoapp.repositories.QuoteListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
fun QuotesTab(
    onSelection: (Quote) -> Unit,
    modifier: Modifier = Modifier,
    model: QuotesViewModel = hiltViewModel()
) {
    val state by model.state

    when (val s = state) {
        is ViewState.Loading -> {
            LoadingView()
        }

        is ViewState.Ready -> {
            val quotes = s.data

            if (quotes.isEmpty()) {
                Box(
                    modifier = modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No quotes available.", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                LazyColumn(
                    modifier = modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(quotes, key = { it.id }) { quote ->
                        QuoteView(quote, onClick = { onSelection(quote) })
                    }
                }
            }
        }

        is ViewState.Error -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = s.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@HiltViewModel
class QuotesViewModel @Inject constructor(
    private val quotesRepository: QuoteListRepository
): ViewModel() {
    val state: MutableState<ViewState<List<Quote>>> = mutableStateOf(ViewState.Loading)

    init {
        listenToQuotes()
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            quotesRepository.refresh()
        }
    }

    private fun listenToQuotes() {
        viewModelScope.launch {
            quotesRepository.value.collect { quotes ->
                state.value = ViewState.Ready(quotes)
            }
        }
    }
}