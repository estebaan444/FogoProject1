package com.estebi.fogo1.repository.user

import android.annotation.SuppressLint
import android.content.Context
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddImg(val context: Context) {
    companion object {
        @SuppressLint("StaticFieldLeak")
        val db = Firebase.firestore
        fun addImgSignUp(email: String, url: String) {
            db.collection("Users").document(email).update("userImg", url)
        }
    }
}