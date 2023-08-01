package com.example.challapp.services

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat.startActivityForResult
import com.bumptech.glide.Glide
import com.example.challapp.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.rpc.context.AttributeContext.Auth


object ImageUploadService {

    private val storageInstance: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    fun getStorageReference(): StorageReference {
        return storageInstance.reference
    }
    fun uploadImage(imageUri: Uri, email: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        val imageName = "${email}_avatar.jpg"
        val storageRef: StorageReference =
            storageInstance.reference.child("profileAvatars").child(imageName)

        storageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    onSuccess(downloadUri.toString())
                }
            }
            .addOnFailureListener { exception ->
                onFailure("Image upload failed: ${exception.message}")
            }
    }

    fun uploadImagetoUserDir(imageUri: Uri, challangeID: String,currentUserUid: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {

        val storageRef: StorageReference =
            storageInstance.reference.child(currentUserUid).child(challangeID)

        storageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    onSuccess(downloadUri.toString())
                }
            }
            .addOnFailureListener { exception ->
                onFailure("Image upload failed: ${exception.message}")
            }
    }

    fun loadImageIntoImageView(imageUrl: String, imageView: ImageView) {
        Glide.with(imageView.context)
            .load(imageUrl)
            .centerCrop()
            .error(R.drawable.baseline_person_24)
            .into(imageView)
    }


    fun loadProfileImage(email: String, imageView: ShapeableImageView) {
        val imageUrl = "${email}_avatar.jpg"
        val storageRef: StorageReference =
            storageInstance.reference.child("profileAvatars").child(imageUrl)

        storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
            loadImageIntoImageView(downloadUri.toString(), imageView)
        }.addOnFailureListener { exception ->
            imageView.setImageResource(R.drawable.baseline_person_24)
        }
    }
}