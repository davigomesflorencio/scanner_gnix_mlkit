package com.davi.dev.scannermlkit.presentation.screens.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.io.File

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive = _isSearchActive.asStateFlow()

    private val _files = MutableStateFlow<List<File>>(emptyList())

    val filteredFiles = combine(_files, _searchQuery) { files, query ->
        if (query.isEmpty()) files
        else files.filter { it.name.contains(query, ignoreCase = true) }
    }.stateIn(
        scope = kotlinx.coroutines.GlobalScope, // Usando GlobalScope para simplificar, idealmente seria viewModelScope
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        loadFiles()
    }

    fun loadFiles() {
        val filesDir = getApplication<Application>().filesDir
        val pdfFiles = filesDir.listFiles { file ->
            file.name.endsWith(".pdf")
        }?.sortedByDescending { it.lastModified() } ?: emptyList()
        _files.value = pdfFiles
    }

    fun clearFilters(){
        _searchQuery.value = ""
        _isSearchActive.value = false
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onSearchActiveChange(active: Boolean) {
        _isSearchActive.value = active
    }
}
