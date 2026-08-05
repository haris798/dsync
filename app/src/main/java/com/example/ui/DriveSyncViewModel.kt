package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.FolderPair
import com.example.data.SettingsRepository
import com.example.data.SyncLog
import com.example.data.SyncRepository
import com.example.data.ThemePreference
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DriveSyncViewModel(
    private val repository: SyncRepository,
    private val settingsRepository: SettingsRepository,
    val driveAuthRepository: com.example.data.DriveAuthRepository
) : ViewModel() {
    
    val signedInAccount: StateFlow<com.google.android.gms.auth.api.signin.GoogleSignInAccount?> = driveAuthRepository.signedInAccount


    val themePreference: StateFlow<ThemePreference> = settingsRepository.themePreference.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ThemePreference.SYSTEM
    )

    fun setThemePreference(theme: ThemePreference) {
        viewModelScope.launch {
            settingsRepository.saveThemePreference(theme)
        }
    }

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

    fun triggerManualSync(context: android.content.Context, pairId: Int) {
        com.example.sync.SyncManager.triggerSync(context, pairId)
    }

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

class DriveSyncViewModelFactory(
    private val repository: SyncRepository,
    private val settingsRepository: SettingsRepository,
    private val driveAuthRepository: com.example.data.DriveAuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DriveSyncViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DriveSyncViewModel(repository, settingsRepository, driveAuthRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
