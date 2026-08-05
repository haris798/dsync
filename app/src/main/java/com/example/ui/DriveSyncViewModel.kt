package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.FolderPair
import com.example.data.SyncLog
import com.example.data.SyncRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DriveSyncViewModel(private val repository: SyncRepository) : ViewModel() {

    val folderPairs: StateFlow<List<FolderPair>> = repository.allFolderPairs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val syncLogs: StateFlow<List<SyncLog>> = repository.allLogs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun insertFolderPair(pair: FolderPair) {
        viewModelScope.launch {
            repository.insertFolderPair(pair)
        }
    }

    fun updateFolderPair(pair: FolderPair) {
        viewModelScope.launch {
            repository.updateFolderPair(pair)
        }
    }

    fun deleteFolderPair(pair: FolderPair) {
        viewModelScope.launch {
            repository.deleteFolderPair(pair)
        }
    }

    fun clearLogs() {
        viewModelScope.launch {
            repository.clearLogs()
        }
    }
}

class DriveSyncViewModelFactory(private val repository: SyncRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DriveSyncViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DriveSyncViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
