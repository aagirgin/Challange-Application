package com.example.challapp.module

import com.example.challapp.repository.FirestoreUserRepository
import com.example.challapp.repository.FirestoreUserRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class FirestoreModule {

    @Provides
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    fun provideFirestoreUserRepository(repository: FirestoreUserRepositoryImpl): FirestoreUserRepository {
        return repository
    }
}