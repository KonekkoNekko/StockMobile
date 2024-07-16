package com.bielcode.stockmobile.data.injection

import android.content.Context
import com.bielcode.stockmobile.data.preferences.UserPreference
import com.bielcode.stockmobile.data.preferences.dataStore
import com.bielcode.stockmobile.data.repository.Repository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

object Injection {
    fun provideRepository(context: Context): Repository{
        val dataStore = context.dataStore
        val userPreference = UserPreference.getInstance(dataStore)
        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseFirestore = FirebaseFirestore.getInstance()
        val firebaseStorage = FirebaseStorage.getInstance()
        return Repository.getInstance(userPreference, firebaseAuth, firebaseFirestore, firebaseStorage)
    }
}