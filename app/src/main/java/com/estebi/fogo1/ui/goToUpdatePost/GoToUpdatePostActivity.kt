package com.estebi.fogo1.ui.goToUpdatePost

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.estebi.fogo1.MainActivity
import com.estebi.fogo1.R
import com.estebi.fogo1.repository.posts.GoToUpdatePostRep.Companion.deleteMyPost
import com.estebi.fogo1.repository.posts.GoToUpdatePostRep.Companion.getPostsDataOfSpecify
import com.estebi.fogo1.repository.posts.GoToUpdatePostRep.Companion.updateMyPost
import com.estebi.fogo1.ui.goToUpdatePost.GoToUpdatePostViewModel.Companion.userPostIdSVM
import com.estebi.fogo1.ui.user.UserFragment

class GoToUpdatePostActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_go_to_update_post)

        val goBackButton = findViewById<ImageView>(R.id.goBackUpdatePostFragment)
        goBackButton.setOnClickListener {
            finish()
        }

        supportActionBar?.hide()

        getDataToUpdate()
        updatePost()
        deletePost()
    }

    private fun getDataToUpdate() {
        val updateTitlePost = findViewById<EditText>(R.id.updateTitlePost)
        updateTitlePost.setText(getPostsDataOfSpecify() .value?.get(0)?.titlePost.toString())
        val updatePostDesc = findViewById<EditText>(R.id.updateDescPost)

        getPostsDataOfSpecify().observe(this@GoToUpdatePostActivity) { postsList ->
            updateTitlePost.setText(postsList[0].titlePost)
            updatePostDesc.setText(postsList[0].postDesc)
        }
    }

    private fun updatePost() {
        val updatePostBtn = findViewById<Button>(R.id.updatePostButton)
        val updateTitlePost = findViewById<EditText>(R.id.updateTitlePost)
        val updatePostDesc = findViewById<EditText>(R.id.updateDescPost)
        updatePostBtn.setOnClickListener {
            updateMyPost(userPostIdSVM, updateTitlePost.text.toString(), updatePostDesc.text.toString())
            val intent = Intent(this@GoToUpdatePostActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
            Toast.makeText(this, "Post updated!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deletePost() {
        val deletePostBtn = findViewById<TextView>(R.id.deletePostTextView)
        deletePostBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            with(builder)
            {
                setTitle("Delete post")
                setMessage("Are you sure you want to delete this post?")
                setPositiveButton("Yes") { _, _ ->
                    deleteMyPost(userPostIdSVM)
                    Intent(this@GoToUpdatePostActivity, MainActivity::class.java).also {
                        startActivity(it)
                    }
                    finish()
                    Toast.makeText(this@GoToUpdatePostActivity, "Post deleted!", Toast.LENGTH_SHORT).show()
                }
                setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                show()
            }
        }
    }
}

