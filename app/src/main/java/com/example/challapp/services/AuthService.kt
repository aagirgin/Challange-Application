package com.example.challapp.services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


object AuthService {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    fun signUp(email: String, password: String, onComplete: (FirebaseUser?, Exception?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    onComplete(user, null)
                } else {
                    onComplete(null, task.exception)
                }
            }
    }

    fun signIn(email: String, password: String, onComplete: (FirebaseUser?, Exception?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    onComplete(user, null)
                } else {
                    onComplete(null, task.exception)
                }
            }
    }

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

}