package com.example.challapp.di

import com.example.challapp.repository.FirestoreUserRepository
import com.example.challapp.repository.FirestoreUserRepositoryImpl
import com.example.challapp.repository.StorageRepository
import com.example.challapp.repository.StorageRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FirestoreModule {

    @Provides
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
    @Provides
    fun provideFirestoreUserRepository(repository: FirestoreUserRepositoryImpl): FirestoreUserRepository {
        return repository
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Provides
    fun provideStorageRepository(repository: StorageRepositoryImpl): StorageRepository {
        return repository
    }

    @Provides
    @Singleton
    fun provideStorageReference(firebaseStorage: FirebaseStorage): StorageReference {
        return firebaseStorage.reference
    }
}