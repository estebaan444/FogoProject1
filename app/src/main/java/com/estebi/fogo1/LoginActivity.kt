package com.estebi.fogo1

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val firebaseAuth = FirebaseAuth.getInstance()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide();


        auth = Firebase.auth

        val loginBtn = findViewById<Button>(R.id.loginBtn)
        val signUp = findViewById<Button>(R.id.singUpButton)

        loginBtn.setOnClickListener {
            login()
        }

        signUp.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun login() {
        val email = findViewById<EditText>(R.id.EmailAddress)
        val password = findViewById<EditText>(R.id.TextPassword)
        val email1: String = email.text.toString().trim()
        val password1: String = password.text.toString().trim()

        if (password.text.toString().isEmpty() || email.text.toString().isEmpty()) {
            Toast.makeText(
                baseContext, "Please complete the text fields",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            auth.signInWithEmailAndPassword(email1, password1)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                       checkIfEmailVerified()
                    } else {
                        Log.w(TAG, "registerUserWithMail:failure", task.exception)
                        if (task.exception.toString() == "com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: " +
                            "The password is invalid or the user does not have a password."
                        ) {
                            Toast.makeText(
                                baseContext, "The password is invalid",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
        }
    }
    private fun checkIfEmailVerified() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user!!.isEmailVerified) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            Toast.makeText(this, "Successfully logged in", Toast.LENGTH_SHORT).show()
        } else {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, "Verify your account", Toast.LENGTH_SHORT).show()

        }
    }

}