package com.davi.dev.scannermlkit.presentation.screens.viewModel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        loadFiles()
    }

    fun loadFiles() {
        viewModelScope.launch(Dispatchers.IO) {
            val filesDir = getApplication<Application>().filesDir
            val pdfFiles = filesDir.listFiles { file ->
                file.name.endsWith(".pdf")
            }?.sortedByDescending { it.lastModified() } ?: emptyList()
            _files.value = pdfFiles
        }
    }

    fun deleteFile(file: File) {
        viewModelScope.launch(Dispatchers.IO) {
            if (file.exists()) {
                val deleted = file.delete()
                if (deleted) {
                    withContext(Dispatchers.Main) {
                        _files.value = _files.value.filter { it.absolutePath != file.absolutePath }
                    }
                }
            }
        }
    }

    fun renameFile(oldFile: File, newName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (oldFile.exists()) {
                val newFileExtension = if (newName.endsWith(".pdf", ignoreCase = true)) "" else ".pdf"
                val newFile = File(oldFile.parent, newName + newFileExtension)
                if (oldFile.renameTo(newFile)) {
                    withContext(Dispatchers.Main) {
                        _files.value = _files.value.map { currentFile ->
                            if (currentFile.absolutePath == oldFile.absolutePath) newFile else currentFile
                        }
                    }
                } else {
                    // Log an error if rename fails
                    // In a real app, you might want to show a Toast or update a UI state here
                    println("Failed to rename file: ${oldFile.name} to ${newName}")
                }
            }
        }
    }

    fun printPdf(context: Context, file: File, pdfUri: Uri) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
        val jobName = "${file.name} Document"

        // Create a WebView to generate the PrintDocumentAdapter
        val webView = WebView(context)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                val adapter = view.createPrintDocumentAdapter(jobName)
                printManager.print(jobName, adapter, null)
            }
        }
        // Load the PDF into the WebView
        webView.loadUrl(pdfUri.toString())
    }

    fun clearFilters() {
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