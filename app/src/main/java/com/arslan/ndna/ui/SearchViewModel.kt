package com.arslan.ndna.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.arslan.ndna.data.GithubRepo
import com.arslan.ndna.data.TokenStore
import com.arslan.ndna.data.fdroid.FdroidRepo
import com.arslan.ndna.data.fdroid.FdroidSync
import com.arslan.ndna.model.AppItem
import com.arslan.ndna.model.Filters
import com.arslan.ndna.model.SearchState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel(app: Application) : AndroidViewModel(app) {

    val tokenStore = TokenStore(app)
    private val github = GithubRepo(tokenStore)
    val fdroidSync = FdroidSync(app)
    private val fdroid = FdroidRepo(fdroidSync)

    private val _filters = MutableStateFlow(Filters())
    val filters = _filters.asStateFlow()

    private val _state = MutableStateFlow(SearchState())
    val state = _state.asStateFlow()

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage = _syncMessage.asStateFlow()

    fun update(block: (Filters) -> Filters) = _filters.update(block)

    fun search() = run(page = 1)

    fun loadMore() = run(page = _state.value.page + 1)

    fun clearError() = _state.update { it.copy(error = null) }

    fun clearSyncMessage() { _syncMessage.value = null }

    fun syncFdroid() = viewModelScope.launch {
        _syncMessage.value = "Syncing catalog…"
        _syncMessage.value = runCatching { withContext(Dispatchers.IO) { fdroidSync.sync() } }
            .getOrElse { it.message ?: "Sync failed" }
    }

    private fun run(page: Int) = viewModelScope.launch {
        _state.update { it.copy(loading = true, error = null) }
        val result = runCatching { withContext(Dispatchers.IO) { collect(page) } }
        _state.update { current -> reduce(current, result, page) }
    }

    private fun reduce(
        current: SearchState,
        result: Result<List<AppItem>>,
        page: Int
    ): SearchState {
        val items = result.getOrNull() ?: return current.copy(
            loading = false,
            error = result.exceptionOrNull()?.message ?: "Search failed"
        )
        val merged = if (page == 1) items else current.items + items
        return SearchState(false, merged, null, page, items.isNotEmpty())
    }

    private fun collect(page: Int): List<AppItem> {
        val f = _filters.value
        val ghItems = if (f.github) github.search(f, page) else emptyList()
        val fdItems = if (f.fdroid && page == 1) fdroid.search(f) else emptyList()
        return ghItems + fdItems
    }
}
