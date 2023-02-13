package com.estebi.fogo1

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.estebi.fogo1.models.User
import com.estebi.fogo1.repository.auth.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        supportActionBar?.hide();

        setup()
        val loginBtn = findViewById<Button>(R.id.loginBtnSig)
        loginBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
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

    private fun setup() {
        title = "Autenticación"
        val signInBtn = findViewById<Button>(R.id.signinBtn)
        val email = findViewById<EditText>(R.id.EmailAddressSig)
        val password = findViewById<EditText>(R.id.TextPasswordSig)
        val passwordCheck = findViewById<EditText>(R.id.textPasswordSig)

        signInBtn.setOnClickListener {
            if (email.text.isNotEmpty() && password.text.isNotEmpty() && passwordCheck.text.isNotEmpty()) {
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                            sendEmailVerification()
                            val user = User(
                                email.text.toString(),
                                "Name",
                                "Path"
                            )
                            AuthRepository.addUserCollection(user)
                        } else {
                            showAlert()
                        }
                    }
            }
        }

    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("An error ocurred during the google login")
        builder.setPositiveButton("Acceept", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String, provider: ProviderType) {
        val homeIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }
}