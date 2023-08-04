package com.example.challapp.repository

import android.net.Uri
import com.example.challapp.R
import com.example.challapp.services.ImageUploadService
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageRepositoryImpl  @Inject constructor(
    private val storageInstance: FirebaseStorage
) : StorageRepository {

    override suspend fun insertImageForUserAvatar(imageUri: Uri, email: String): String {
        val imageName = "${email}_avatar.jpg"
        val storageRef: StorageReference =
            storageInstance.reference.child("profileAvatars").child(imageName)

        return try {
            storageRef.putFile(imageUri).await()
            val downloadUrl = storageRef.downloadUrl.await()
            downloadUrl.toString()
        } catch (e: Exception) {
            throw StorageRepository.StorageException("Image upload failed: ${e.message}")
        }
    }
    
    override suspend fun insertImageToUserDir(imageUri: Uri, challengeID: String, currentUserUid: String) {
        val storageRef: StorageReference =
            storageInstance.reference.child("Users").child(currentUserUid).child(challengeID)

        try {
            storageRef.putFile(imageUri).await()
        } catch (e: Exception) {
            throw StorageRepository.StorageException("Image upload failed: ${e.message}")
        }
    }
    override suspend fun loadProfileImage(email: String): String? {
        val imageUrl = "${email}_avatar.jpg"
        val storageRef: StorageReference =
            storageInstance.reference.child("profileAvatars").child(imageUrl)

        return try {
            val downloadUri = storageRef.downloadUrl.await()
            downloadUri.toString()
        } catch (e: Exception) {
            "No Picture"
        }
    }

    override suspend fun getImageWithDocumentId(userId: String, documentId: String): String? {
        val storageRef: StorageReference =
            storageInstance.reference.child("Users").child(userId).child(documentId)

        return try {
            val downloadUri = storageRef.downloadUrl.await()
            downloadUri.toString()
        } catch (e: Exception) {
            "No Picture"
        }
    }


}