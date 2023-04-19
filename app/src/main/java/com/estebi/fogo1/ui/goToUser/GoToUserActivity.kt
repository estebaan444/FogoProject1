package com.estebi.fogo1.ui.goToUser

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.estebi.fogo1.R
import com.estebi.fogo1.models.Posts
import com.estebi.fogo1.repository.auth.AuthRepository.Companion.db
import com.estebi.fogo1.repository.posts.PostsRepository
import com.estebi.fogo1.ui.goToUser.adapter.GoToUserAdapter
import com.estebi.fogo1.ui.search.SearchViewModel.Companion.getUserEmailKey
import com.google.firebase.database.core.Context
import com.squareup.picasso.Picasso

class GoToUserActivity : AppCompatActivity() {
    private val goToUserAdapter = GoToUserAdapter()
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_go_to_user)
        var userNameTxt = findViewById<TextView>(R.id.userNameOfSelect)
        val userImgSelect = findViewById<ImageView>(R.id.userImgOfSelect)
        val context: GoToUserActivity = this

        val goBackButton = findViewById<ImageView>(R.id.goBackSearchFragment)
        goBackButton.setOnClickListener {
            finish()
        }
        db.collection("Users").document(getUserEmailKey)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    var userName = document.data?.get("userName").toString()
                    userNameTxt.text = userName
                    val userImg = document.data?.get("userImg").toString()
                    Picasso.get().load(userImg).into(userImgSelect)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                userNameTxt.text = ""
                userImgSelect.setImageResource(R.drawable.account)
            }

        val myPostsRv = findViewById<RecyclerView>(R.id.rvOfSelectedUser)
        myPostsRv.layoutManager = GridLayoutManager(this, 2)
        myPostsRv.setHasFixedSize(true)
        myPostsRv.adapter = goToUserAdapter
        goToUserAdapter.setItemListener(object : GoToUserAdapter.onItemClickListener {
            override fun onItemClick(posts: Posts) {
                val postDesc = posts.postDesc
                val builder = AlertDialog.Builder(context)
                with(builder)
                {
                    setTitle("The recipe of ${posts.titlePost}")
                    setMessage(postDesc)
                    setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    show()
                }
            }
        })
        observePosts()
    }
    private fun observePosts() {
        PostsRepository.getPostsListOfSelectedUser().observe(this@GoToUserActivity) { postsList ->
            goToUserAdapter.setListData(postsList)
            goToUserAdapter.notifyDataSetChanged()
        }
    }
}
