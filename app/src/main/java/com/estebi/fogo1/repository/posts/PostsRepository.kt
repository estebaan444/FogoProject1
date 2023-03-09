package com.estebi.fogo1.repository.posts

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.estebi.fogo1.R
import com.estebi.fogo1.models.Posts
import com.estebi.fogo1.models.User
import com.estebi.fogo1.repository.auth.AuthRepository.Companion.db
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class PostsRepository(val context: Context) {
    companion object {
        fun getPostsList(): LiveData<MutableList<Posts>> {
            val mutableData = MutableLiveData<MutableList<Posts>>()
            val db = Firebase.firestore
            db.collection("Posts")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e)
                        return@addSnapshotListener
                    }
                    val listDataPosts = mutableListOf<Posts>()
                    if (snapshot != null) {
                        for (document in snapshot) {
                            listDataPosts.add(
                                Posts(
                                    document["postId"].toString(),
                                    document["userEmailPosts"].toString(),
                                    document["userNamePosts"].toString(),
                                    document["userImgProfile"].toString(),
                                    document["userImgPosts"].toString(),
                                    document["postDesc"].toString(),
                                    document["titlePost"].toString(),
                                )
                            )
                        }
                    }
                    mutableData.value = listDataPosts
                }
            return mutableData
        }

        fun getMyPostsList(): LiveData<MutableList<Posts>> {
            val mutableData = MutableLiveData<MutableList<Posts>>()
            val db = Firebase.firestore
            db.collection("Posts").whereEqualTo("userEmailPosts", FirebaseAuth.getInstance().currentUser?.email.toString())
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e)
                        return@addSnapshotListener
                    }
                    val listDataPosts = mutableListOf<Posts>()
                    if (snapshot != null) {
                        for (document in snapshot) {
                            listDataPosts.add(
                                Posts(
                                    document["postId"].toString(),
                                    document["userEmailPosts"].toString(),
                                    document["userNamePosts"].toString(),
                                    document["userImgProfile"].toString(),
                                    document["userImgPosts"].toString(),
                                    document["postDesc"].toString(),
                                    document["titlePost"].toString(),
                                )
                            )
                        }
                    }
                    mutableData.value = listDataPosts
                }
            return mutableData
        }
    }
}