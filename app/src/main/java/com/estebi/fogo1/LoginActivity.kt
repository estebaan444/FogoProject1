package com.estebi.fogo1

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val GOOGLE_SIGN_IN = 100



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide();


        auth = Firebase.auth

        val loginBtn = findViewById<Button>(R.id.loginBtn)
        val signUp = findViewById<Button>(R.id.singUpButton)
        val googleBtn = findViewById<Button>(R.id.googleButton)

        loginBtn.setOnClickListener {
            login()
        }

        googleBtn.setOnClickListener{

            // Configuration
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
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

    override fun onStart(){
        super.onStart()
    }
    private fun session(){
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null )
        val provider = prefs.getString("provider", null)

        if(email != null && provider != null){
            showHome(email, ProviderType.valueOf(provider))
        }
    }

    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("An error ocurred during the google login")
        builder.setPositiveButton("Acceept", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String, provider: ProviderType){
        val homeIntent = Intent(this, MainActivity::class.java).apply{
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }

    override fun onActivityResult(requestCode: Int, resulCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resulCode, data)

        if(requestCode == GOOGLE_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try{
                val account = task.getResult(ApiException::class.java)

                if(account != null){

                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener{
                        if(it.isSuccessful){
                            showHome(account.email ?: "", ProviderType.GOOGLE)
                        } else {
                            showAlert()
                        }
                    }
                }
            } catch (e: ApiException){
                showAlert()
            }

        }
    }


}