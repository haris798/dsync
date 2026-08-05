package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.FolderPair
import com.example.sync.SyncManager
import com.example.ui.theme.*

@Composable
fun HomeScreen(viewModel: DriveSyncViewModel, onNavigateToEdit: (Int) -> Unit) {
    val folderPairs by viewModel.folderPairs.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        GlobalSyncStatusCard(onSyncAll = {
            folderPairs.forEach { viewModel.triggerManualSync(context, it.id) }
        })

        if (folderPairs.isEmpty()) {
            SyncAnalyticsCard()
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "No folder pairs configured.\nTap + to add one.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    SyncAnalyticsCard()
                }
                item {
                    Text(
                        text = "Active Folder Pairs",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
                items(folderPairs) { pair ->
                    FolderPairCard(pair = pair, onClick = { onNavigateToEdit(pair.id) }, onSync = {
                        viewModel.triggerManualSync(context, pair.id)
                    })
                }
            }
        }
    }
}

@Composable
fun GlobalSyncStatusCard(onSyncAll: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "CURRENT STATUS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ready to sync",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Button(
                onClick = onSyncAll,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary),
                shape = RoundedCornerShape(percent = 50),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text("Sync Now", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun FolderPairCard(pair: FolderPair, onClick: () -> Unit, onSync: () -> Unit) {
    // Determine colors based on status (simplified)
    val isError = pair.lastSyncStatus.contains("Failed", ignoreCase = true)
    val isSyncing = pair.lastSyncStatus.contains("Running", ignoreCase = true)
    
    val badgeColor = when {
        isError -> MaterialTheme.colorScheme.errorContainer
        isSyncing -> SyncingBlue
        else -> Success
    }
    val badgeTextColor = when {
        isError -> Error
        isSyncing -> OnSyncingBlue
        else -> OnSuccess
    }
    
    val iconBgColor = when {
        isError -> MaterialTheme.colorScheme.errorContainer
        pair.syncType.name.contains("UPLOAD") -> MaterialTheme.colorScheme.tertiaryContainer
        else -> SyncingBlue
    }
    val iconColor = when {
        isError -> MaterialTheme.colorScheme.errorContainer
        pair.syncType.name.contains("UPLOAD") -> MaterialTheme.colorScheme.tertiaryContainer
        else -> OnSyncingBlue
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(iconBgColor, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Folder,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = pair.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = pair.syncType.name.replace("_", " ").lowercase().replaceFirstChar { it.titlecase() },
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .background(badgeColor, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (isSyncing) "SYNCING..." else if (isError) "ERROR" else "SYNCED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeTextColor,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            if (isSyncing) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().height(6.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Local:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), modifier = Modifier.width(40.dp))
                            Text(pair.localUri, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = FontFamily.Monospace, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Cloud:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), modifier = Modifier.width(40.dp))
                            Text(pair.driveFolderName, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = FontFamily.Monospace, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                    
                    OutlinedButton(
                        onClick = onSync,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("Sync Now", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            if (isError) {
                Text(
                    text = pair.lastSyncStatus,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun SyncAnalyticsCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "7-Day Overview",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(label = "Transferred", value = "1.2 GB")
                StatItem(label = "Storage Used", value = "45%")
                StatItem(label = "Syncs", value = "24")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Bar Chart
            val data = listOf(2f, 4f, 1f, 6f, 3f, 5f, 7f)
            val primaryColor = MaterialTheme.colorScheme.primary
            
            androidx.compose.foundation.Canvas(modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
            ) {
                val maxVal = data.maxOrNull() ?: 1f
                val barWidth = size.width / (data.size * 2)
                val spacing = barWidth
                
                var startX = spacing / 2
                
                for (value in data) {
                    val barHeight = (value / maxVal) * size.height
                    drawRoundRect(
                        color = primaryColor,
                        topLeft = androidx.compose.ui.geometry.Offset(startX, size.height - barHeight),
                        size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                    )
                    startX += barWidth + spacing
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Mon", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Tue", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Wed", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Thu", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Fri", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Sat", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Sun", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
