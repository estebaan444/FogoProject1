package com.estebi.fogo1.repository.auth

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.estebi.fogo1.LoginActivity
import com.estebi.fogo1.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class AuthRepository(val context: Context) {
    companion object {
        val db = Firebase.firestore
        fun addUserCollection(user: User) {
            db.collection("Users").document(user.userEmail)
                .set(user)
                .addOnSuccessListener {
                    Log.d(
                        ContentValues.TAG,
                        "DocumentSnapshot successfully written!"
                    )
                }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }
        }
    }
}
