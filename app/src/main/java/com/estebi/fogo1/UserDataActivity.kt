package com.estebi.fogo1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.estebi.fogo1.models.User
import com.estebi.fogo1.repository.auth.AuthRepository
import com.google.firebase.auth.FirebaseAuth

class UserDataActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_data)
        supportActionBar?.hide();
        val gotItBtn = findViewById<Button>(R.id.gotItUserData)
        gotItBtn.setOnClickListener {
            addUserCollection()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun addUserCollection() {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email.toString()
        val userName = findViewById<EditText>(R.id.addUserNameData);
        val user = User(userEmail, userName.text.toString(), "path")
        AuthRepository.addUserCollection(user)
        Toast.makeText(this, "Welcome To Fogo $userName", Toast.LENGTH_SHORT).show()
    }
}