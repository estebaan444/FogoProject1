package com.estebi.fogo1.repository.posts

import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.estebi.fogo1.models.Posts
import com.estebi.fogo1.repository.auth.AuthRepository.Companion.db
import com.estebi.fogo1.repository.user.AddImg
import com.estebi.fogo1.ui.goToUpdatePost.GoToUpdatePostViewModel.Companion.userPostIdSVM
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class GoToUpdatePostRep(val context: Context) {
    companion object {

        fun getPostsDataOfSpecify(): LiveData<MutableList<Posts>> {
            val mutableData = MutableLiveData<MutableList<Posts>>()
            val db = Firebase.firestore
            db.collection("Posts").whereEqualTo("postId", userPostIdSVM)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w(ContentValues.TAG, "Listen failed.", e)
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

/*        fun deleteMyPost(postId: String){
            db.collection("Posts").document(postId).delete().addOnSuccessListener {
                Log.d(ContentValues.TAG, "DocumentSnapshot successfully deleted!")
            }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error deleting document", e)
            }
        }*/

        fun deleteMyPost(postId: String){
            val db = Firebase.firestore
            db.collection("Posts").whereEqualTo("postId", postId)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w(ContentValues.TAG, "Listen failed.", e)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        for (document in snapshot) {
                            db.collection("Posts").document(document.id).delete().addOnSuccessListener {
                                Log.d(ContentValues.TAG, "DocumentSnapshot successfully deleted!")
                            }
                                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error deleting document", e)
                                }
                        }
                    }
                }
        }

        fun updateMyPost(postId: String, titlePost: String, postDesc: String) {
            AddImg.db.collection("Posts").document(postId)
                .update("titlePost", titlePost, "postDesc", postDesc).addOnSuccessListener {
                    Log.d(ContentValues.TAG, "DocumentSnapshot successfully updated!")
                }.addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error updating document", e) }
        }

    }
}