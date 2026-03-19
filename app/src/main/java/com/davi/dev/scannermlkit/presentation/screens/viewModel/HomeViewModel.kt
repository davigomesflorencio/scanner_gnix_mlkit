package com.davi.dev.scannermlkit.presentation.screens.viewModel

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.os.CancellationSignal
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.provider.MediaStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

val Context.dataStore by preferencesDataStore(name = "user_preferences")

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

    private val _saveStatus = MutableSharedFlow<String>()
    val saveStatus: SharedFlow<String> = _saveStatus.asSharedFlow()

    private val TOOLTIP_SHOWN_KEY = booleanPreferencesKey("has_shown_home_tooltip")

    val hasShownTooltip = application.dataStore.data.map { preferences ->
        preferences[TOOLTIP_SHOWN_KEY] ?: false
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    init {
        loadFiles()
    }

    fun markTooltipAsShown() {
        viewModelScope.launch(Dispatchers.IO) {
            getApplication<Application>().dataStore.edit { preferences ->
                preferences[TOOLTIP_SHOWN_KEY] = true
            }
        }
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

    fun printPdf(context: Context, file: File) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
        val jobName = "${file.name} Document"

        val pda = object : PrintDocumentAdapter() {
            override fun onWrite(
                pages: Array<out PageRange>?,
                destination: ParcelFileDescriptor?,
                cancellationSignal: CancellationSignal?,
                callback: WriteResultCallback?
            ) {
                try {
                    FileInputStream(file.absolutePath).use { input ->
                        FileOutputStream(destination?.fileDescriptor).use { output ->
                            input.copyTo(output)
                        }
                    }
                    callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                } catch (e: Exception) {
                    callback?.onWriteFailed(e.message)
                }
            }

            override fun onLayout(
                oldAttributes: PrintAttributes?,
                newAttributes: PrintAttributes?,
                cancellationSignal: CancellationSignal?,
                callback: LayoutResultCallback?,
                extras: Bundle?
            ) {
                if (cancellationSignal?.isCanceled == true) {
                    callback?.onLayoutCancelled()
                    return
                }

                val info = PrintDocumentInfo.Builder(jobName)
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .build()
                callback?.onLayoutFinished(info, true)
            }
        }

        printManager.print(jobName, pda, null)
    }

    fun savePdfToDownloads(context: Context, sourceFile: File, newFileName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, newFileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            var success = false
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

            uri?.let { destinationUri ->
                try {
                    context.contentResolver.openOutputStream(destinationUri)?.use { outputStream ->
                        sourceFile.inputStream().use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                    success = true
                } catch (e: Exception) {
                    e.printStackTrace()
                    success = false
                }
            }

            withContext(Dispatchers.Main) {
                if (success) {
                    _saveStatus.emit("PDF saved to Downloads")
                } else {
                    _saveStatus.emit("Failed to save PDF")
                }
            }
        }
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