package com.estebi.fogo1.repository.user

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.estebi.fogo1.models.Posts
import com.estebi.fogo1.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class GetUserData {
    companion object {
        fun getMyUserData(): LiveData<MutableList<User>> {
            val mutableData = MutableLiveData<MutableList<User>>()
            val db = Firebase.firestore
            db.collection("Users").document(
                FirebaseAuth.getInstance().currentUser?.email.toString()
            ).get().addOnSuccessListener { document ->
                val listDataUser = mutableListOf<User>()
                if (document != null) {
                    Log.d(ContentValues.TAG, "DocumentSnapshot data: ${document.data}")
                    listDataUser.add(
                        User(
                            document.data?.get("userEmail").toString(),
                            document.data?.get("userName").toString(),
                            document.data?.get("userImg").toString(),
                        )
                    )
                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
                mutableData.value = listDataUser
            }
                .addOnFailureListener { exception ->
                    Log.d(ContentValues.TAG, "get failed with ", exception)
                }
            return mutableData
        }
    }
}