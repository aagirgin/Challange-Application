package com.challengerdaily.challenge.services

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await


object ImageUploadService {

    private val storageInstance: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }

    fun uploadImagetoUserDir(
        imageUri: Uri, challangeID: String,
        currentUserUid: String, onFailure: (String) -> Unit) {

        val storageRef: StorageReference =
            storageInstance.reference.child("Users").child(currentUserUid).child(challangeID)

        storageRef.putFile(imageUri)
            .addOnFailureListener { exception ->
                onFailure("Image upload failed: ${exception.message}")
            }
    }

    suspend fun getImageWithDocumentId(userId: String, documentId: String): String? {
        val storageRef: StorageReference =
            storageInstance.reference.child("Users").child(userId).child(documentId)

        return try {
            val downloadUri = storageRef.downloadUrl.await()
            downloadUri.toString()
        } catch (e: Exception) {
            "No Image"
        }
    }
}