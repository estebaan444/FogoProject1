package com.estebi.fogo1.repository.user
import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.estebi.fogo1.models.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class SearchUserRepository(val context: Context) {
    companion object {
        fun getUserNameList() : LiveData<MutableList<User>>{
            val mutableData = MutableLiveData<MutableList<User>>()
            val db = Firebase.firestore
            db.collection("Users")
                .addSnapshotListener  {snapshot, e ->
                    if (e != null) {
                        Log.w(ContentValues.TAG, "Listen failed.", e)
                        return@addSnapshotListener
                    }
                    val listData = mutableListOf<User>()
                    if (snapshot != null) {
                        for (document in snapshot) {
                            listData.add(
                                User(
                                    document["userEmail"].toString(),
                                    document["userName"].toString(),
                                    document["userImg"].toString()
                                )
                            )
                        }
                    }
                    mutableData.value = listData
                }
            return mutableData
        }
        fun getUserNameListFiltered(searchData: String) : LiveData<MutableList<User>>{
            val mutableData = MutableLiveData<MutableList<User>>()
            val db = Firebase.firestore
            db.collection("Users").whereEqualTo("userName", searchData)
                .addSnapshotListener  {snapshot, e ->
                    if (e != null) {
                        Log.w(ContentValues.TAG, "Listen failed.", e)
                        return@addSnapshotListener
                    }
                    val listData = mutableListOf<User>()
                    if (snapshot != null) {
                        for (document in snapshot) {
                            listData.add(
                                User(
                                    document["userEmail"].toString(),
                                    document["userName"].toString(),
                                    document["userImg"].toString()
                                )
                            )
                        }
                    }
                    mutableData.value = listData
                }
            return mutableData
        }
    }
}