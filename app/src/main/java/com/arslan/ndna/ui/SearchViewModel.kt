package com.arslan.ndna.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.arslan.ndna.data.BlockStore
import com.arslan.ndna.data.FiltersStore
import com.arslan.ndna.data.GithubRepo
import com.arslan.ndna.data.TokenStore
import com.arslan.ndna.model.AppItem
import com.arslan.ndna.model.BlockedApp
import com.arslan.ndna.model.Filters
import com.arslan.ndna.model.Preview
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

    private val filtersStore = FiltersStore(app)

    private val _filters = MutableStateFlow(filtersStore.load())
    val filters = _filters.asStateFlow()

    private val blockStore = BlockStore(app)

    private val _blocked = MutableStateFlow(blockStore.load())
    val blocked = _blocked.asStateFlow()

    private val _preview = MutableStateFlow<Preview?>(null)
    val preview = _preview.asStateFlow()

    private val _state = MutableStateFlow(SearchState())
    val state = _state.asStateFlow()

    fun update(block: (Filters) -> Filters) {
        _filters.update(block)
        filtersStore.save(_filters.value)
    }

    fun search() = run(page = 1)

    fun loadMore() = run(page = _state.value.page + 1)

    fun openPreview(item: AppItem) {
        _preview.value = Preview(item)
        viewModelScope.launch {
            val result = runCatching { withContext(Dispatchers.IO) { github.readme(item.name) } }
            _preview.update { current ->
                if (current?.item?.id != item.id) return@update current
                current.copy(
                    loading = false,
                    readme = result.getOrDefault(""),
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun closePreview() {
        _preview.value = null
    }

    /** Hides an app everywhere and remembers it, so later searches skip it too. */
    fun block(item: AppItem) {
        updateBlocked(_blocked.value + BlockedApp(item.id, item.name))
        _state.update { it.copy(items = it.items.filterNot { app -> app.id == item.id }) }
    }

    fun unblock(id: String) = updateBlocked(_blocked.value.filterNot { it.id == id })

    private fun updateBlocked(blocked: List<BlockedApp>) {
        val sorted = blocked.distinctBy { it.id }.sortedBy { it.name.lowercase() }
        _blocked.value = sorted
        blockStore.save(sorted)
    }

    fun clearError() = _state.update { it.copy(error = null) }

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
        val blockedIds = _blocked.value.map { it.id }.toSet()
        return github.search(_filters.value, page).filterNot { it.id in blockedIds }
    }
}
