package com.astrobytes.thedevoapp.repositories.core

import com.astrobytes.thedevoapp.models.Quote
import com.astrobytes.thedevoapp.repositories.CoreRepository
import com.astrobytes.thedevoapp.repositories.QuoteListRepository
import com.astrobytes.thedevoapp.stores.QuoteStore
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class CoreQuoteListRepository @Inject constructor(
    private val store: QuoteStore
): CoreRepository<List<Quote>>(listOf()), QuoteListRepository {
    override suspend fun refresh(): List<Quote> {
        val quotes = store.fetch()
        _value.update { quotes }
        return quotes
    }

    override fun clear() {
        _value.update { listOf() }
    }
}