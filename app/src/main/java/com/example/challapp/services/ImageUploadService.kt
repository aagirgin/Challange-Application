package com.example.challapp.services

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
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
    fun uploadImage(imageUri: Uri, email: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        val imageName = "${email}_avatar.jpg"
        val storageRef: StorageReference =
            FirebaseStorage.getInstance().reference.child("profileAvatars").child(imageName)

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
            .error(R.drawable.baseline_person_24) // Set a placeholder image if loading fails
            .into(imageView)
    }

    fun loadProfileImage(imageView: ShapeableImageView) {
        val imageUrl = "${AuthService.getCurrentUser()?.email}_avatar.jpg"
        val storageRef: StorageReference =
            FirebaseStorage.getInstance().reference.child("profileAvatars").child(imageUrl)

        storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
            loadImageIntoImageView(downloadUri.toString(), imageView)
        }.addOnFailureListener { exception ->
            imageView.setImageResource(R.drawable.baseline_person_24)
        }
    }
}