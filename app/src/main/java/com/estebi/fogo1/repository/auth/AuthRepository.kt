package com.estebi.fogo1.repository.auth

import android.content.Context
import com.estebi.fogo1.models.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class AuthRepository(val context: Context) {
    companion object {
        val db = Firebase.firestore
        fun addUserCollection(user: User) {
            db.collection("Users").document(user.userEmail)
                .set(user)
        }
    }
}