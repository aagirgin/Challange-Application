package com.example.challapp.repository

import android.net.Uri
import com.google.android.material.imageview.ShapeableImageView

interface StorageRepository {
    suspend fun insertImageForUserAvatar(imageUri: Uri, email: String): String
    suspend fun insertImageToUserDir(imageUri: Uri, challengeID: String, currentUserUid: String)
    suspend fun loadProfileImage(email: String): String?
    suspend fun getImageWithDocumentId(userId: String, documentId: String): String?
}

