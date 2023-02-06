package com.estebi.fogo1

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        supportActionBar?.hide();
        auth = Firebase.auth

        val signInBtn = findViewById<Button>(R.id.signinBtn)
        val loginbtn = findViewById<Button>(R.id.loginBtnSig)

        signInBtn.setOnClickListener {
            signUp()
        }

        loginbtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signUp() {
        val email = findViewById<EditText>(R.id.EmailAddressSig)
        val password = findViewById<EditText>(R.id.TextPasswordSig)
        val passwordCheck = findViewById<EditText>(R.id.textPasswordSig)

        if (password.text.toString().isEmpty() || email.text.toString()
                .isEmpty() || passwordCheck.text.toString().isEmpty()
        ) {
            Toast.makeText(
                baseContext, "Please complete the text fields",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            if (password.text.toString() == passwordCheck.text.toString()) {
                val email1: String = email.text.toString().trim()
                val password1: String = password.text.toString().trim()

                auth.createUserWithEmailAndPassword(email1, password1)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            sendEmailVerification()
                        } else {
                            Log.w(ContentValues.TAG, "registerUserWithMail:failure", task.exception)
                            if (task.exception.toString() == "com.google.firebase.auth.FirebaseAuthUserCollisionException: " +
                                "The email address is already in use by another account."
                            ) {
                                Toast.makeText(
                                    baseContext,
                                    "The email address is already in use by another account.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else if (task.exception.toString() == "com.google.firebase.auth.FirebaseAuthWeakPasswordException: " +
                                "The given password is invalid. [ Password should be at least 6 characters ]"
                            ) {
                                Toast.makeText(
                                    baseContext, "The password must have a minimum of 6 characters",
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
            } else {
                val txt = "The passwords are not the same"
                Toast.makeText(baseContext, txt, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendEmailVerification() {
        //get instance of firebase auth
        val firebaseAuth = FirebaseAuth.getInstance()
        //get current user
        val firebaseUser = firebaseAuth.currentUser

        //send email verification
        firebaseUser!!.sendEmailVerification()
            .addOnSuccessListener {
                val intent = Intent(this, LoginActivity::class.java)
                Toast.makeText(
                    baseContext, "Registration successful, wait for the email verification",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error to send verify" + e.message, Toast.LENGTH_SHORT)
                    .show()
            }
    }
}