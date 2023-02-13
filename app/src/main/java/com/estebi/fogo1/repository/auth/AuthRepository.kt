package com.estebi.fogo1.repository.auth

import android.content.ContentValues
import android.util.Log
import com.estebi.fogo1.models.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AuthRepository {
    companion object {
        private val db = Firebase.firestore
        fun addUserCollection(user: User) {
            db.collection("Users").document(user.userEmail)
                .set(user)
                .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }
        }
    }

}
