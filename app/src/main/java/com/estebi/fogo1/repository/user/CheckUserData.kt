package com.estebi.fogo1.repository.user

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CheckUserData {
    companion object {
        fun checkUserDataGoogle(): MutableLiveData<String> {
            val currentUse = FirebaseAuth.getInstance().currentUser?.email
            val mFireStore = FirebaseFirestore.getInstance()

            val docRef = mFireStore.collection("Users").document("$currentUse")
            val mutableData = MutableLiveData<String>()

            docRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null) {
                        if (document.exists()) {
                            Log.d("TAG", "Document already exists.")
                            mutableData.value = "true"
                        } else {
                            Log.d("TAG", "Document doesn't exist.")
                            mutableData.value = "false"
                        }
                    }
                } else {
                    Log.d("TAG", "Error: ", task.exception)
                }
            }
            return mutableData
        }
    }
}

