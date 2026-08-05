package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.FolderPair
import com.example.data.SyncType
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(viewModel: DriveSyncViewModel, pairId: Int, onNavigateBack: () -> Unit) {
    val folderPairs by viewModel.folderPairs.collectAsStateWithLifecycle()
    val existingPair = folderPairs.find { it.id == pairId }
    val isEdit = existingPair != null

    var name by remember { mutableStateOf(existingPair?.name ?: "") }
    var localUri by remember { mutableStateOf(existingPair?.localUri ?: "") }
    var driveFolderId by remember { mutableStateOf(existingPair?.driveFolderId ?: "") }
    var driveFolderName by remember { mutableStateOf(existingPair?.driveFolderName ?: "") }
    var syncType by remember { mutableStateOf(existingPair?.syncType ?: SyncType.TWO_WAY) }
    var autoSync by remember { mutableStateOf(existingPair?.autoSync ?: true) }

    var syncTypeExpanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Task Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Local Folder", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = localUri.ifEmpty { "Not selected" }, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { /* Launch SAF to pick folder */ localUri = "content://path/to/local/folder" }) {
                    Text("Select Local Folder")
                }
            }
        }

        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Google Drive Folder", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = driveFolderName.ifEmpty { "Not selected" }, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { /* Launch Drive picker */ driveFolderName = "My Drive / Backup"; driveFolderId = "drive_id_123" }) {
                    Text("Select Drive Folder")
                }
            }
        }

        ExposedDropdownMenuBox(
            expanded = syncTypeExpanded,
            onExpandedChange = { syncTypeExpanded = !syncTypeExpanded }
        ) {
            OutlinedTextField(
                value = syncType.name.replace("_", " "),
                onValueChange = {},
                readOnly = true,
                label = { Text("Sync Type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = syncTypeExpanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = syncTypeExpanded,
                onDismissRequest = { syncTypeExpanded = false }
            ) {
                SyncType.values().forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.name.replace("_", " ")) },
                        onClick = {
                            syncType = type
                            syncTypeExpanded = false
                        }
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Auto Sync")
            Switch(checked = autoSync, onCheckedChange = { autoSync = it })
        }
        
        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onNavigateBack) {
                Text("Cancel")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    val pair = FolderPair(
                        id = if (isEdit) pairId else 0,
                        name = name.ifEmpty { "Untitled Task" },
                        localUri = localUri,
                        driveFolderId = driveFolderId,
                        driveFolderName = driveFolderName,
                        syncType = syncType,
                        autoSync = autoSync
                    )
                    if (isEdit) {
                        viewModel.updateFolderPair(pair)
                    } else {
                        viewModel.insertFolderPair(pair)
                    }
                    onNavigateBack()
                },
                enabled = localUri.isNotEmpty() && driveFolderId.isNotEmpty()
            ) {
                Text("Save")
            }
        }
    }
}
