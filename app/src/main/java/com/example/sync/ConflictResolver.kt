package com.example.sync

import java.io.File

enum class ConflictResolutionStrategy {
    KEEP_LATEST,
    SKIP,
    CREATE_DUPLICATE
}

enum class ResolutionAction {
    UPLOAD_LOCAL,
    DOWNLOAD_REMOTE,
    SKIP,
    CREATE_DUPLICATE
}

class ConflictResolver {

    fun resolve(
        localFileLastModified: Long,
        remoteFileLastModified: Long,
        strategy: ConflictResolutionStrategy
    ): ResolutionAction {
        return when (strategy) {
            ConflictResolutionStrategy.SKIP -> ResolutionAction.SKIP
            
            ConflictResolutionStrategy.KEEP_LATEST -> {
                if (localFileLastModified > remoteFileLastModified) {
                    ResolutionAction.UPLOAD_LOCAL
                } else if (remoteFileLastModified > localFileLastModified) {
                    ResolutionAction.DOWNLOAD_REMOTE
                } else {
                    ResolutionAction.SKIP
                }
            }
            
            ConflictResolutionStrategy.CREATE_DUPLICATE -> ResolutionAction.CREATE_DUPLICATE
        }
    }

    fun resolve(
        localFile: File,
        remoteFileLastModified: Long,
        strategy: ConflictResolutionStrategy
    ): ResolutionAction {
        return resolve(localFile.lastModified(), remoteFileLastModified, strategy)
    }
}
